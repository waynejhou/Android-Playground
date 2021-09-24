package org.waynezhou.libBluetooth;

import android.bluetooth.BluetoothSocket;

import org.waynezhou.libUtil.InputStreamLooper;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.ThreadUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

public class BtConnectionClientHandler extends BtConnectionHandler {
    private final AtomicReference<BluetoothSocket> socket;
    private final Thread thd;
    private final AtomicReference<OutputStream> out;
    private final AtomicReference<InputStreamLooper> inLoop;

    protected BtConnectionClientHandler(AtomicReference<BluetoothSocket> socket, Thread thd, AtomicReference<OutputStream> out, AtomicReference<InputStreamLooper> inLoop) {
        super();
        this.socket = socket;
        this.thd = thd;
        this.out = out;
        this.inLoop = inLoop;
    }

    @Override
    public boolean IsConnecting() {
        return socket.get() != null;
    }

    @Override
    public void onSend(byte[] msg) {

        ThreadUtils.run(() -> {
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
            socket.get().getOutputStream().close();
            socket.get().getInputStream().close();
            socket.get().close();
            socket.set(null);
            thd.join(5000);
            thd.interrupt();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
