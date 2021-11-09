package org.waynezhou.libBluetooth.bt.connection;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import org.waynezhou.libUtil.looper.InputStreamLooper;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.schedule.ThreadSchedule;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BtConnectionServerHandler extends BtConnectionHandler {
    private final BluetoothServerSocket serverSocket;
    private final Thread thread;
    private final AtomicBoolean serverSwitch;
    private final AtomicReference<OutputStream> out;
    private final AtomicReference<InputStreamLooper> inLoop;
    private final AtomicReference<BluetoothSocket> acceptedSocket;
    protected BtConnectionServerHandler(
            BluetoothServerSocket serverSocket,
            AtomicReference<BluetoothSocket> acceptedSocket,
            Thread thread,
            AtomicBoolean serverSwitch,
            AtomicReference<OutputStream> out,
            AtomicReference<InputStreamLooper> inLoop) {
        this.serverSocket = serverSocket;
        this.acceptedSocket = acceptedSocket;
        this.thread = thread;
        this.serverSwitch = serverSwitch;
        this.out = out;
        this.inLoop = inLoop;
    }

    @Override
    public boolean IsConnecting() {
        return acceptedSocket.get()!=null;
    }

    @Override
    public void onSend(byte[] msg) {
        ThreadSchedule.run(() -> {
            try {
                out.get().write(msg);
            } catch (IOException e) {
                LogHelper.e("BtConnectionHandler throw Exception", e);
            }
        });
    }

    @Override
    public void stop() {
        try {
            inLoop.get().loopSwitch.set(false);
            serverSwitch.set(false);
            serverSocket.close();
            thread.join(5000);
            thread.interrupt();
        } catch (IOException | InterruptedException e) {
            LogHelper.e("BtConnectionHandler throw Exception", e);
        }
    }
}

