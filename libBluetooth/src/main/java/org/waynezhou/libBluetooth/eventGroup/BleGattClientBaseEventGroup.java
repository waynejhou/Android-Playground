package org.waynezhou.libBluetooth.eventGroup;

import org.waynezhou.libBluetooth.eventArgs.BleGattClientCharacteristicChangedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientCharacteristicReadEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientCharacteristicWriteEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientConnectionStateChangeEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientDescriptorReadEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientDescriptorWriteEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientMtuChangedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientPhyReadEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientPhyUpdateEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientReadRemoteRssiEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientReliableWriteCompletedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleGattClientServicesDiscoveredEventArgs;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BleGattClientBaseEventGroup extends BaseEventGroup<BleGattClientBaseEventGroup> {
    public final EventHolder<BleGattClientCharacteristicWriteEventArgs> characteristicWrite = new EventHolder<>();
    public final EventHolder<BleGattClientCharacteristicReadEventArgs> characteristicRead = new EventHolder<>();
    public final EventHolder<BleGattClientCharacteristicChangedEventArgs> characteristicChanged = new EventHolder<>();
    public final EventHolder<BleGattClientDescriptorReadEventArgs> descriptorRead = new EventHolder<>();
    public final EventHolder<BleGattClientDescriptorWriteEventArgs> descriptorWrite = new EventHolder<>();
    public final EventHolder<BleGattClientConnectionStateChangeEventArgs> connectionStateChanged = new EventHolder<>();
    public final EventHolder<BleGattClientPhyReadEventArgs> phyRead = new EventHolder<>();
    public final EventHolder<BleGattClientPhyUpdateEventArgs> phyUpdate = new EventHolder<>();
    public final EventHolder<BleGattClientReadRemoteRssiEventArgs> readRemoteRssi = new EventHolder<>();
    public final EventHolder<BleGattClientReliableWriteCompletedEventArgs> reliableWriteCompleted = new EventHolder<>();
    public final EventHolder<BleGattClientServicesDiscoveredEventArgs> servicesDiscovered = new EventHolder<>();
    public final EventHolder<BleGattClientMtuChangedEventArgs> mtuChanged = new EventHolder<>();

}

