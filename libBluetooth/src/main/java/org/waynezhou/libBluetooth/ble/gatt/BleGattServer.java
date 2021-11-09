package org.waynezhou.libBluetooth.ble.gatt;

import static org.waynezhou.libUtil.standard.StandardKt.checkNullRet;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

import java.util.List;

public class BleGattServer {
    
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<BleGattServerCharacteristicReadRequestEventArgs> characteristicReadRequest = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerCharacteristicWriteRequestEventArgs> characteristicWriteRequest = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerConnectionStateChangeEventArgs> connectionStateChanged = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerDescriptorReadRequestEventArgs> descriptorReadRequest = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerDescriptorWriteRequestEventArgs> descriptorWriteRequest = new EventHolder<>();
        @NonNull
        
        public final EventHolder<BleGattServerExecuteWriteEventArgs> executeWrite = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerMtuChangedEventArgs> mtuChanged = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerNotificationSentEventArgs> notificationSent = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerPhyReadEventArgs> phyRead = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerPhyUpdateEventArgs> phyUpdate = new EventHolder<>();
        @NonNull
        public final EventHolder<BleGattServerServiceAddedEventArgs> serviceAdded = new EventHolder<>();
        
        @NonNull
        Invoker getPrivateInvoker() {
            return getInvoker();
        }
    }
    
    @NonNull
    private final EventGroup eventGroup = new EventGroup();
    
    @NonNull
    private final BaseEventGroup<EventGroup>.Invoker invoker;
    
    
    public BaseEventGroup<EventGroup> getEvents() {
        return eventGroup;
    }
    
    
    //private final BleGattServerEventPack eventPack;
    private final List<BleGattService> services;
    private final BluetoothGattServerCallbackClass callback = new BluetoothGattServerCallbackClass();
    
    public BleGattServer(List<BleGattService> services) {
        this.invoker = eventGroup.getPrivateInvoker();
        this.services = services;
    }
    
    public BleGattServerHandler startOn(
      BluetoothManager btManager,
      Context context
    ) {
        LogHelper.d("lib open gatt server");
        BluetoothGattServer gattServer = btManager.openGattServer(context, callback);
        callback.setGattServer(gattServer);
        for (BleGattService ser : services) {
            BluetoothGattService service = new BluetoothGattService(ser.uuid, ser.serviceType);
            for (BleGattCharacteristic chr : ser.characteristics) {
                BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(chr.uuid, chr.properties, chr.permissions);
                for (BleGattDescriptor descpt : chr.descriptors) {
                    BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(descpt.uuid, descpt.permissions);
                    characteristic.addDescriptor(descriptor);
                }
                characteristic.setWriteType(chr.writeType);
                service.addCharacteristic(characteristic);
            }
            gattServer.addService(service);
        }
        return new BleGattServerHandler(gattServer);
    }
    
    private class BluetoothGattServerCallbackClass extends BluetoothGattServerCallback {
        private BluetoothGattServer gattServer;
        
        public void setGattServer(BluetoothGattServer gattServer) {
            this.gattServer = gattServer;
        }
        
        @Override
        public void onConnectionStateChange(
          BluetoothDevice device,
          int status,
          int newState
        ) {
            LogHelper.d("onConnectionStateChange");
            invoker.invoke(g -> g.connectionStateChanged,
              new BleGattServerConnectionStateChangeEventArgs(gattServer, device, status, newState));
        }
        
        @Override
        public void onServiceAdded(
          int status,
          BluetoothGattService service
        ) {
            LogHelper.d("onServiceAdded " + status + " " + checkNullRet(service, s -> s.getUuid()));
            invoker.invoke(g -> g.serviceAdded,
              new BleGattServerServiceAddedEventArgs(
                gattServer, status, service
              )
            );
        }
        
        @Override
        public void onCharacteristicReadRequest(
          BluetoothDevice device,
          int requestId,
          int offset,
          BluetoothGattCharacteristic characteristic
        ) {
            LogHelper.d("onCharacteristicReadRequest");
            invoker.invoke(g -> g.characteristicReadRequest,
              
              new BleGattServerCharacteristicReadRequestEventArgs(
                gattServer, device, requestId, offset, characteristic
              )
            );
        }
        
        @Override
        public void onCharacteristicWriteRequest(
          BluetoothDevice device,
          int requestId,
          BluetoothGattCharacteristic characteristic,
          boolean preparedWrite,
          boolean responseNeeded,
          int offset,
          byte[] value
        ) {
            invoker.invoke(g -> g.characteristicWriteRequest,
              
              new BleGattServerCharacteristicWriteRequestEventArgs(
                gattServer, device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            );
        }
        
        @Override
        public void onDescriptorReadRequest(
          BluetoothDevice device,
          int requestId,
          int offset,
          BluetoothGattDescriptor descriptor
        ) {
            invoker.invoke(g -> g.descriptorReadRequest,
              
              new BleGattServerDescriptorReadRequestEventArgs(
                gattServer, device, requestId, offset, descriptor
              )
            );
        }
        
        @Override
        public void onDescriptorWriteRequest(
          BluetoothDevice device,
          int requestId,
          BluetoothGattDescriptor descriptor,
          boolean preparedWrite,
          boolean responseNeeded,
          int offset,
          byte[] value
        ) {
            invoker.invoke(g -> g.descriptorWriteRequest,
              
              new BleGattServerDescriptorWriteRequestEventArgs(
                gattServer, device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
            );
        }
        
        @Override
        public void onExecuteWrite(
          BluetoothDevice device,
          int requestId,
          boolean execute
        ) {
            invoker.invoke(g -> g.executeWrite,
              
              new BleGattServerExecuteWriteEventArgs(
                gattServer, device, requestId, execute
              )
            );
        }
        
        @Override
        public void onNotificationSent(
          BluetoothDevice device,
          int status
        ) {
            invoker.invoke(g -> g.notificationSent,
              
              new BleGattServerNotificationSentEventArgs(
                gattServer, device, status
              )
            );
        }
        
        @Override
        public void onMtuChanged(
          BluetoothDevice device,
          int mtu
        ) {
            invoker.invoke(g -> g.mtuChanged,
              
              new BleGattServerMtuChangedEventArgs(
                gattServer, device, mtu
              )
            );
        }
        
        @Override
        public void onPhyUpdate(
          BluetoothDevice device,
          int txPhy,
          int rxPhy,
          int status
        ) {
            invoker.invoke(g -> g.phyUpdate,
              
              new BleGattServerPhyUpdateEventArgs(
                gattServer, device, txPhy, rxPhy, status
              )
            );
        }
        
        @Override
        public void onPhyRead(
          BluetoothDevice device,
          int txPhy,
          int rxPhy,
          int status
        ) {
            invoker.invoke(g -> g.phyRead,
              
              new BleGattServerPhyReadEventArgs(
                gattServer, device, txPhy, rxPhy, status
              )
            );
        }
    }
    
}

