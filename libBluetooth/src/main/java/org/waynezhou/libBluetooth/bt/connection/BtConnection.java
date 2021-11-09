package org.waynezhou.libBluetooth.bt.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import androidx.annotation.NonNull;

import org.waynezhou.libBluetooth.eventGroup.BluetoothConnectionBaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.looper.InputStreamLooper;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.event.BaseEventGroup;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BtConnection {
    
    private final String name;
    private final UUID uuid;
    
    public BtConnection(
      String name,
      UUID uuid
    ) {
        this.name = name;
        this.uuid = uuid;
        this.invoker = eventGroup.getPrivateInvoker();
    }
    
    private int extraSizeInHeader(InputStream in) throws IOException {
        byte[] size = new byte[4];
        int readSize = in.read(size);
        if (readSize != 4) throw new IOException();
        ByteBuffer wrapped = ByteBuffer.allocate(size.length);
        wrapped.mark();
        wrapped.put(size);
        wrapped.reset();
        return wrapped.getInt(0);
    }
    
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<BluetoothConnectionAcceptedEventArgs> accepted = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothConnectionWaitingAcceptedEventArgs> waitingAccepted = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothConnectionConnectedEventArgs> connected = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothConnectionGotMessageEventArgs> gotMessage = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothConnectionStartedEventArgs> started = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothConnectionClosedEventArgs> closed = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothConnectionErrorEventArgs> error = new EventHolder<>();
        
        @NonNull
        Invoker getPrivateInvoker() {
            return getInvoker();
        }
    }
    
    @NonNull
    private final EventGroup eventGroup = new EventGroup();
    
    @NonNull
    private final BaseEventGroup<EventGroup>.Invoker invoker;
    
    @NonNull
    public BaseEventGroup<EventGroup> getEvents() {
        return eventGroup;
    }
    
    
    public BtConnectionServerHandler startServer(
      BluetoothManager btManager,
      Context context
    ) {
        final BluetoothAdapter adapter = btManager.getAdapter();
        BluetoothServerSocket serverSocket;
        try {
            serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(name, uuid);
        } catch (IOException e) {
            LogHelper.e("BtConnection throw Exception", e);
            invoker.invoke(g -> g.error, new BluetoothConnectionErrorEventArgs(null, e));
            return null;
        }
        final AtomicBoolean serverSwitch = new AtomicBoolean(true);
        final BluetoothServerSocket finalServerSocket = serverSocket;
        final AtomicReference<OutputStream> out = new AtomicReference<>();
        final AtomicReference<InputStreamLooper> inLoop = new AtomicReference<>();
        final AtomicReference<BluetoothSocket> acceptSocket = new AtomicReference<>();
        Thread thd = new Thread(() -> {
            invoker.invoke(g -> g.started, new BluetoothConnectionStartedEventArgs());
            while (serverSwitch.get()) {
                try {
                    invoker.invoke(g -> g.waitingAccepted, new BluetoothConnectionWaitingAcceptedEventArgs());
                    BluetoothSocket socket = finalServerSocket.accept();
                    acceptSocket.set(socket);
                    invoker.invoke(g -> g.accepted, new BluetoothConnectionAcceptedEventArgs(socket.getRemoteDevice()));
                    out.set(socket.getOutputStream());
                    inLoop.set(
                      new InputStreamLooper(socket.getInputStream())
                        .setInputLengthGetter(this::extraSizeInHeader)
                        .setLoopCondition(socket::isConnected)
                        .setAvailableListener(data -> {
                            invoker.invoke(g -> g.gotMessage, new BluetoothConnectionGotMessageEventArgs(socket.getRemoteDevice(), data));
                        })
                        .setErrorListener(e -> {
                            invoker.invoke(g -> g.error, new BluetoothConnectionErrorEventArgs(socket.getRemoteDevice(), e));
                        })
                    );
                    inLoop.get().loop();
                    socket.getInputStream().close();
                    socket.getOutputStream().close();
                    socket.close();
                    acceptSocket.set(null);
                } catch (IOException e) {
                    LogHelper.e("BtConnection throw Exception", e);
                    invoker.invoke(g -> g.error, new BluetoothConnectionErrorEventArgs(null, e));
                }
            }
            invoker.invoke(g -> g.closed, new BluetoothConnectionClosedEventArgs());
        });
        thd.start();
        return new BtConnectionServerHandler(finalServerSocket, acceptSocket, thd, serverSwitch, out, inLoop);
    }
    
    public BtConnectionClientHandler startConnection(
      BluetoothDevice device,
      BluetoothConnectionBaseEventGroup eventPack
    ) {
        BluetoothSocket _socket = null;
        try {
            _socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            LogHelper.e("BtConnection throw Exception", e);
            return null;
        }
        final AtomicReference<BluetoothSocket> socket = new AtomicReference<>(_socket);
        final AtomicReference<OutputStream> out = new AtomicReference<>();
        final AtomicReference<InputStreamLooper> inLoop = new AtomicReference<>();
        final Thread thd = new Thread(() -> {
            try {
                invoker.invoke(g -> g.started, new BluetoothConnectionStartedEventArgs());
                socket.get().connect();
                invoker.invoke(g -> g.connected, new BluetoothConnectionConnectedEventArgs(device));
                out.set(socket.get().getOutputStream());
                inLoop.set(new InputStreamLooper(socket.get().getInputStream())
                  .setInputLengthGetter(this::extraSizeInHeader)
                  .setLoopCondition(socket.get()::isConnected)
                  .setAvailableListener(data -> {
                      invoker.invoke(g -> g.gotMessage, new BluetoothConnectionGotMessageEventArgs(device, data));
                  })
                  .setErrorListener(e -> {
                      LogHelper.e("BtConnection throw Exception", e);
                      invoker.invoke(g -> g.error, new BluetoothConnectionErrorEventArgs(device, e));
                  }));
                inLoop.get().loop();
                socket.get().getInputStream().close();
                socket.get().getOutputStream().close();
                socket.get().close();
                socket.set(null);
            } catch (IOException e) {
                LogHelper.e("BtConnection throw Exception", e);
                invoker.invoke(g -> g.error, new BluetoothConnectionErrorEventArgs(device, e));
            }
            invoker.invoke(g -> g.closed, new BluetoothConnectionClosedEventArgs());
        });
        thd.start();
        return new BtConnectionClientHandler(socket, thd, out, inLoop);
    }
}
