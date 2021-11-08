package org.waynezhou.libBluetooth.eventGroup;

import org.waynezhou.libBluetooth.eventArgs.BleGattServerCharacteristicReadRequestEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerCharacteristicWriteRequestEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerConnectionStateChangeEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerDescriptorReadRequestEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerDescriptorWriteRequestEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerExecuteWriteEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerMtuChangedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerNotificationSentEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerPhyReadEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerPhyUpdateEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattServerServiceAddedEventArgs;
import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BleGattServerEventGroup extends EventGroup<BleGattServerEventGroup> {

    public final EventHolder<BleGattServerCharacteristicReadRequestEventArgs> characteristicReadRequest = new EventHolder<>();
    public final EventHolder<BleGattServerCharacteristicWriteRequestEventArgs> characteristicWriteRequest = new EventHolder<>();
    public final EventHolder<BleGattServerConnectionStateChangeEventArgs> connectionStateChanged = new EventHolder<>();
    public final EventHolder<BleGattServerDescriptorReadRequestEventArgs> descriptorReadRequest = new EventHolder<>();
    public final EventHolder<BleGattServerDescriptorWriteRequestEventArgs> descriptorWriteRequest = new EventHolder<>();

    public final EventHolder<BleGattServerExecuteWriteEventArgs> executeWrite = new EventHolder<>();
    public final EventHolder<BleGattServerMtuChangedEventArgs> mtuChanged = new EventHolder<>();
    public final EventHolder<BleGattServerNotificationSentEventArgs> notificationSent = new EventHolder<>();
    public final EventHolder<BleGattServerPhyReadEventArgs> phyRead = new EventHolder<>();
    public final EventHolder<BleGattServerPhyUpdateEventArgs> phyUpdate = new EventHolder<>();
    public final EventHolder<BleGattServerServiceAddedEventArgs> serviceAdded = new EventHolder<>();

}


