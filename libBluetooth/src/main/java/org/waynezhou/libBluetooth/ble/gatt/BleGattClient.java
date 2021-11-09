package org.waynezhou.libBluetooth.ble.gatt.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;

import androidx.annotation.NonNull;

import org.waynezhou.libBluetooth.eventArgs.BleGattClientCharacteristicChangedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientCharacteristicReadEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientCharacteristicWriteEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientConnectionStateChangeEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientDescriptorReadEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientDescriptorWriteEventArgs;
import org.waynezhou.libBluetooth.eventGroup.BleGattClientBaseEventGroup;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientMtuChangedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientPhyReadEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientPhyUpdateEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientReadRemoteRssiEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientReliableWriteCompletedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientServicesDiscoveredEventArgs;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.event.BaseEventGroup;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BleGattClient {

    private final _BleGattClientBaseEventGroup eventGroup = new _BleGattClientBaseEventGroup();
    private final BaseEventGroup<BleGattClientBaseEventGroup>.Invoker invoker;

    private static class _BleGattClientBaseEventGroup extends BleGattClientBaseEventGroup {
        @NonNull
        public BaseEventGroup<BleGattClientBaseEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public BleGattClientBaseEventGroup getEventGroup() {
        return eventGroup;
    }
    
    private final BluetoothGattCallbackClass callback = new BluetoothGattCallbackClass();

    public BleGattClient() {
        this.invoker = eventGroup.getInvoker();
    }

    public BleGattClientHandle startOn(BluetoothDevice device, Context context,
                                       boolean autoConnect, int transport) {
        BluetoothGatt gatt = device.connectGatt(context, autoConnect, callback, transport);
        LogHelper.i(gatt);
        return new BleGattClientHandle(gatt);
    }

    private class BluetoothGattCallbackClass extends BluetoothGattCallback {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            invoker.invoke(g->g.phyUpdate,
                    new BleGattClientPhyUpdateEventArgs(
                            gatt,
                            txPhy,
                            rxPhy,
                            status
                    )
            );
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            invoker.invoke(g->g.phyRead,
                    new BleGattClientPhyReadEventArgs(
                            gatt,
                            txPhy,
                            rxPhy,
                            status
                    )
            );
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            invoker.invoke(g->g.connectionStateChanged,
                    new BleGattClientConnectionStateChangeEventArgs(
                            gatt,
                            status,
                            newState
                    )
            );
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            invoker.invoke(g->g.servicesDiscovered,
                    new BleGattClientServicesDiscoveredEventArgs(
                            gatt,
                            status
                    )
            );
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            invoker.invoke(g->g.characteristicRead,
                    new BleGattClientCharacteristicReadEventArgs(
                            gatt,
                            characteristic,
                            status
                    )
            );
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            invoker.invoke(g->g.characteristicWrite,
                    new BleGattClientCharacteristicWriteEventArgs(
                            gatt,
                            characteristic,
                            status
                    )
            );
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            invoker.invoke(g->g.characteristicChanged,
                    new BleGattClientCharacteristicChangedEventArgs(
                            gatt,
                            characteristic
                    )
            );
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            invoker.invoke(g->g.descriptorRead,
                    new BleGattClientDescriptorReadEventArgs(
                            gatt,
                            descriptor,
                            status
                    )
            );
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            invoker.invoke(g->g.descriptorWrite,
                    new BleGattClientDescriptorWriteEventArgs(
                            gatt,
                            descriptor,
                            status
                    )
            );
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            invoker.invoke(g->g.reliableWriteCompleted,
                    new BleGattClientReliableWriteCompletedEventArgs(
                            gatt,
                            status
                    )
            );
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            invoker.invoke(g->g.readRemoteRssi,
                    new BleGattClientReadRemoteRssiEventArgs(
                            gatt,
                            rssi,
                            status
                    )
            );
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            invoker.invoke(g->g.mtuChanged,
                    new BleGattClientMtuChangedEventArgs(
                            gatt,
                            mtu,
                            status
                    )
            );
        }
    }

    private static final Map<Integer, String> connectionStatusExplain = new HashMap<Integer, String>(){{
        put(BluetoothGatt.GATT_SUCCESS, "Success");
        put(BluetoothGatt.GATT_READ_NOT_PERMITTED, "Read Not Permitted");
        put(BluetoothGatt.GATT_WRITE_NOT_PERMITTED, "Write Not Permitted");
        put(BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION, "Insufficient Authentication");
        put(BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, "Request Not Supported");
        put(8/*GATT_CONN_TIMEOUT*/, "Connection Timeout");
        put(BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION, "Insufficient Encryption");
        put(BluetoothGatt.GATT_INVALID_OFFSET, "Invalid Offset");
        put(BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH, "Invalid Attribute Length");
        put(19/*GATT_CONN_TERMINATE_PEER_USER*/, "Connection Terminate by Peer User");
        put(133/*GATT_ERROR*/, "Low-Level Error in Communication or Stack never managed to connect in the first place");
        put(BluetoothGatt.GATT_CONNECTION_CONGESTED, "Connection Congested");
        put(BluetoothGatt.GATT_FAILURE, "Failure");

    }};
    public static String explainConnectionStatus(int status){
        if(connectionStatusExplain.containsKey(status)){
            return String.format(Locale.TRADITIONAL_CHINESE,
                    "Status Code: %d, Explain: %s"
                    , status, connectionStatusExplain.get(status));
        }
        return String.format(Locale.TRADITIONAL_CHINESE,
                "Status Code: %d, Explain: Unknown Status"
                , status);
    }

    private static final Map<Integer, String> connectionNewStateExplain = new HashMap<Integer, String>(){{
        put(BluetoothGatt.STATE_DISCONNECTED, "Disconnected");
        put(BluetoothGatt.STATE_CONNECTING, "Connecting");
        put(BluetoothGatt.STATE_CONNECTED, "Connected");
        put(BluetoothGatt.STATE_DISCONNECTING, "Disconnecting");
    }};
    public static String explainConnectionNewState(int newState){
        if(connectionNewStateExplain.containsKey(newState)){
            return String.format(Locale.TRADITIONAL_CHINESE,
                    "State Code: %d, Explain: %s"
                    , newState, connectionNewStateExplain.get(newState));
        }
        return String.format(Locale.TRADITIONAL_CHINESE,
                "State Code: %d, Explain: Unknown State"
                , newState);
    }
}
