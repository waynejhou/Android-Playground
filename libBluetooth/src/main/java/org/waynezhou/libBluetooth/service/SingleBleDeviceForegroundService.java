package org.waynezhou.libBluetooth.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.waynezhou.libBluetooth.BleDiscover;
import org.waynezhou.libBluetooth.BleDiscoverHandler;
import org.waynezhou.libBluetooth.BleGattClient;
import org.waynezhou.libBluetooth.BleGattClientHandle;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SpinWait;
import org.waynezhou.libUtil.StandardKt;
import org.waynezhou.libUtil.ThreadUtils;

import java.util.UUID;

public abstract class SingleBleDeviceForegroundService extends Service {
    
    protected abstract int getNotificationSmallIcon();
    
    private final static String NOTIFICATION_CHANNEL_ID = "SingleBleDeviceService";
    
    protected String getNotificationChannelId() {
        return NOTIFICATION_CHANNEL_ID;
    }
    
    private final static String NOTIFICATION_CHANNEL_NAME = "Single Ble Device Connection";
    
    protected String getNotificationChannelName() {
        return NOTIFICATION_CHANNEL_NAME;
    }
    
    protected abstract String getDeviceName();
    
    protected abstract String getBroadcastActionNameForReceive();
    
    protected abstract String getBroadcastExtraDataNameForReceiveData();
    
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder foregroundNotificationBuilder;
    private int foregroundNotificationId = 0;
    
    private void createForegroundNotification() {
        notificationManager = NotificationManagerCompat.from(this);
        
        notificationManager.createNotificationChannel(
          new NotificationChannelCompat.Builder(
            getNotificationChannelId(), NotificationManager.IMPORTANCE_MAX)
            .setName(getNotificationChannelName())
            .build());
        
        foregroundNotificationBuilder = new NotificationCompat.Builder(this, getNotificationChannelId())
          .setSmallIcon(getNotificationSmallIcon())
          .setPriority(NotificationCompat.PRIORITY_MAX)
          .setCategory(Notification.CATEGORY_SERVICE)
          .setAutoCancel(false)
          .setVibrate(new long[0])
          .setContentTitle(getNotificationChannelName())
          .setContentText("Initialing");
        
        foregroundNotificationId = foregroundNotificationBuilder.hashCode();
        startForeground(foregroundNotificationId, foregroundNotificationBuilder.build());
    }
    
    // region Connection
    
    private BleDiscoverHandler bleDiscoverHandler;
    private BleGattClientHandle controllerHandler;
    
    private Context connectionContext;
    
    protected abstract String getGattServiceUUID();
    
    protected abstract String getReceiverCharacteristicUUID();
    
    private BluetoothDevice controllerDevice;
    
    private final BleGattClient controllerGattClient = StandardKt.apply(new BleGattClient(), it -> {
        it.getEventGroup()
          .on(g -> g.connectionStateChanged, e -> {
              LogHelper.i("controller connected");
              LogHelper.ie(() -> e.status != BluetoothGatt.GATT_SUCCESS, BleGattClient.explainConnectionStatus(e.status));
              LogHelper.i(BleGattClient.explainConnectionNewState(e.newState));
              if (e.newState == BluetoothGatt.STATE_CONNECTED) {
                  notificationManager.notify(foregroundNotificationId,
                    foregroundNotificationBuilder.setContentText(String.format("%s Connected, Discovering Services", controllerDevice.getName())).build());
                  e.gatt.discoverServices();
              } else if (e.newState == BluetoothGatt.STATE_DISCONNECTED) {
                  notificationManager.notify(foregroundNotificationId,
                    foregroundNotificationBuilder.setContentText(String.format("%s Disconnected, Retrying", controllerDevice.getName())).build());
                  ThreadUtils.run(() -> {
                      if (controllerHandler != null) {
                          controllerHandler.close();
                          controllerHandler = null;
                          foundController();
                      }
                  });
              }
          })
          .on(g -> g.servicesDiscovered, e -> {
              LogHelper.i("controller ServicesDiscovered");
              notificationManager.notify(foregroundNotificationId,
                foregroundNotificationBuilder.setContentText(String.format("%s Connected, All ok", controllerDevice.getName())).build());
              BluetoothGattService groveService = e.gatt.getService(UUID.fromString(getGattServiceUUID()));
              BluetoothGattCharacteristic rx = groveService.getCharacteristic(UUID.fromString(getReceiverCharacteristicUUID()));
              LogHelper.i("groveService: " + groveService.getUuid());
              LogHelper.i("Rx: " + rx.getUuid());
              e.gatt.setCharacteristicNotification(rx, true);
              LogHelper.i("Ask initial rotation command");
              rx.setValue("I");
              e.gatt.writeCharacteristic(rx);
              //ThreadUtils.run(this::foundCastingApp);
              
          })
          .on(g -> g.characteristicWrite, e -> {
              LogHelper.i("getCharacteristicWrite: " + e.characteristic.getUuid());
              // e.gatt.readCharacteristic(e.characteristic);
              //e.gatt.writeCharacteristic(e.characteristic);
          })
          .on(g -> g.characteristicRead, e -> {
              LogHelper.i("getCharacteristicRead: " + e.characteristic.getUuid());
          })
          .on(g -> g.characteristicChanged, e -> {
              LogHelper.d("getCharacteristicChanged: " + e.characteristic.getUuid());
              byte[] val = e.characteristic.getValue();
              sendBroadcast(StandardKt.apply(new Intent(getBroadcastActionNameForReceive()), intent -> {
                  intent.putExtra(getBroadcastExtraDataNameForReceiveData(), new String(val).trim());
              }));
              //eventGroup.getInvoker().invoke(g -> g.gotCmd, new GotCmdEventArgs(new String(val).trim()));
              //eventPack.getControlMessageIncome().invoke(new String(val, StandardCharsets.UTF_8));
          })
          .on(g -> g.descriptorRead, e -> {
              LogHelper.d("descriptorRead: " + e.descriptor.getUuid());
              e.gatt.readDescriptor(e.descriptor);
          })
          .on(g -> g.descriptorWrite, e -> {
              LogHelper.d("descriptorWrite: " + e.descriptor.getUuid());
              e.gatt.readDescriptor(e.descriptor);
          });
        
    });
    
    private boolean isBleDiscoverTerminated = true;
    private final BleDiscover controllerDiscover = StandardKt.apply(new BleDiscover(true), it -> {
        it.getEventGroup()
          .on(g -> g.failed, e -> {
              LogHelper.e("Ble Discover Failed: " + e.errorCode);
          })
          .on(g -> g.gotResult, e -> {
              BluetoothDevice device = e.result.getDevice();
              LogHelper.i("random device found: %s %s", device.getName(), device.getAddress());
              if (this.controllerDevice == null && device.getName().equals(getDeviceName())) {
                  LogHelper.i("controller found");
                  notificationManager.notify(foregroundNotificationId,
                    foregroundNotificationBuilder.setContentText(String.format("Found device: %s, Connecting", device.getName())).build());
                  this.controllerDevice = device;
              }
              if (!isBleDiscoverTerminated && this.controllerDevice != null) {
                  SpinWait.spinUntil(() -> bleDiscoverHandler != null);
                  this.bleDiscoverHandler.stop();
                  bleDiscoverHandler = null;
                  isBleDiscoverTerminated = true;
                  foundController();
              }
          });
    });
    
    private void foundController() {
        LogHelper.i();
        controllerHandler = controllerGattClient.startOn(
          this.controllerDevice, connectionContext, false, BluetoothDevice.TRANSPORT_LE);
    }
    
    public void connectOn(Context context) {
        this.connectionContext = context;
        LogHelper.i("start scan controller");
        notificationManager.notify(foregroundNotificationId, foregroundNotificationBuilder.setContentText("Scanning").build());
        isBleDiscoverTerminated = false;
        bleDiscoverHandler = controllerDiscover.startOn(this, btManager);
    }
    
    public void disconnect() {
        if (bleDiscoverHandler != null) {
            bleDiscoverHandler.stop();
            bleDiscoverHandler = null;
        }
        if (controllerHandler != null) {
            controllerHandler.close();
            controllerHandler = null;
        }
    }
    // endregion
    
    
    @NonNull
    private BluetoothManager btManager;
    
    @Override
    @CallSuper
    public void onCreate() {
        btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        createForegroundNotification();
        connectOn(this);
    }
    
    @Override
    @CallSuper
    public void onDestroy() {
        disconnect();
        notificationManager.cancel(foregroundNotificationId);
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
