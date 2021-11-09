package org.waynezhou.libBluetooth.bt.connection;

import org.waynezhou.libUtil.log.LogHelper;

import java.nio.ByteBuffer;

public abstract class BtConnectionHandler {
    private byte[] patchMsg(byte[] msg){
        int len = msg.length;
        ByteBuffer buf = ByteBuffer.allocate(4+msg.length);
        LogHelper.d("buf len: "+len);
        buf.putInt(len);
        buf.put(msg);
        LogHelper.d("patchMsg end");
        return buf.array();
    }
    public void send(byte[] msg){
        onSend(patchMsg(msg));
    }
    public abstract boolean IsConnecting();
    abstract protected void onSend(byte[] msg);
    abstract public void stop();
}
