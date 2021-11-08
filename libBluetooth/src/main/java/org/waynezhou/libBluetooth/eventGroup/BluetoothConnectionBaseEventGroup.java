package org.waynezhou.libBluetooth.eventGroup;


import org.waynezhou.libBluetooth.eventArgs.BluetoothConnectionAcceptedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothConnectionClosedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothConnectionConnectedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothConnectionErrorEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothConnectionGotMessageEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothConnectionStartedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothConnectionWaitingAcceptedEventArgs;
import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BluetoothConnectionEventGroup extends EventGroup<BluetoothConnectionEventGroup> {

    public final EventHolder<BluetoothConnectionAcceptedEventArgs> accepted = new EventHolder<>();
    public final EventHolder<BluetoothConnectionWaitingAcceptedEventArgs> waitingAccepted = new EventHolder<>();
    public final EventHolder<BluetoothConnectionConnectedEventArgs> connected = new EventHolder<>();
    public final EventHolder<BluetoothConnectionGotMessageEventArgs> gotMessage = new EventHolder<>();
    public final EventHolder<BluetoothConnectionStartedEventArgs> started = new EventHolder<>();
    public final EventHolder<BluetoothConnectionClosedEventArgs> closed = new EventHolder<>();
    public final EventHolder<BluetoothConnectionErrorEventArgs> error = new EventHolder<>();

}
