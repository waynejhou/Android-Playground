package org.waynezhou.androidplayground.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.waynezhou.androidplayground.R;
import org.waynezhou.libBluetooth.BleDiscover;
import org.waynezhou.libBluetooth.BleDiscoverHandler;
import org.waynezhou.libBluetooth.BleGattClient;
import org.waynezhou.libBluetooth.BleGattClientHandle;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SpinWait;
import org.waynezhou.libUtil.StandardKt;
import org.waynezhou.libUtil.ThreadUtils;

import java.util.Date;
import java.util.UUID;

public class SingleBleDeviceService extends Service {
    public SingleBleDeviceService() {
    }
    
    private Date createDate;
    private NotificationManagerCompat notificationManager;
    private Notification foregroundNotification;
    private int foregroundNotificationId = 0;
    private final static String NOTIFICATION_CHANNEL_ID_SINGLE_BLE_DEVICE_CONNECTION = "SingleBleDeviceService";
    private final static String NOTIFICATION_CHANNEL_ID_SINGLE_BLE_DEVICE_CONNECTION_NAME = "Single Ble Device Connection";
    @NonNull
    private BluetoothManager btManager;
    @Override
    public void onCreate() {
        createDate = java.util.Calendar.getInstance().getTime();
        LogHelper.i(createDate);
        createForegroundNotification();
    
        btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        connectOn(this);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogHelper.i(java.util.Calendar.getInstance().getTime());
        disconnect();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private void createForegroundNotification(){
        notificationManager = NotificationManagerCompat.from(this);
    
        notificationManager.createNotificationChannel(
          new NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_ID_SINGLE_BLE_DEVICE_CONNECTION, NotificationManager.IMPORTANCE_MAX)
            .setName(NOTIFICATION_CHANNEL_ID_SINGLE_BLE_DEVICE_CONNECTION_NAME)
            .build());
    
        foregroundNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID_SINGLE_BLE_DEVICE_CONNECTION)
          .setChannelId(NOTIFICATION_CHANNEL_ID_SINGLE_BLE_DEVICE_CONNECTION)
          .setSmallIcon(R.mipmap.ic_launcher)
          .setContentTitle("Single Ble Device Initializing")
          .setSettingsText("Initializing")
          .setPriority(NotificationCompat.PRIORITY_MAX)
          //.setContentIntent(PendingIntent.getActivity(this, 0,getPackageManager().getLaunchIntentForPackage(getPackageName()), 0))
          .setCategory(Notification.CATEGORY_SERVICE)
          .setAutoCancel(false)
          .setVibrate(null)
          .build();
        foregroundNotificationId = foregroundNotification.hashCode();
        startForeground(foregroundNotificationId, foregroundNotification);
    }
    

    
    // region Connection
    private static final String GROVE_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_RX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private BleDiscoverHandler bleDiscoverHandler;
    private BleGattClientHandle controllerHandler;
    private static final String CONTROLLER_NAME = "Rotation_Test";
    private Context connectionContext;
    public static final String BROADCAST_GOT_CMD = "GOT_CMD";
    private final BleGattClient controllerGattClient = StandardKt.apply(new BleGattClient(), it -> {
        it.getEventGroup()
          .on(g -> g.connectionStateChanged, e -> {
              LogHelper.i("controller connected");
              LogHelper.ie(() -> e.status != BluetoothGatt.GATT_SUCCESS, BleGattClient.explainConnectionStatus(e.status));
              LogHelper.i(BleGattClient.explainConnectionNewState(e.newState));
              if (e.newState == BluetoothGatt.STATE_CONNECTED) {
                  e.gatt.discoverServices();
              }else if(e.newState == BluetoothGatt.STATE_DISCONNECTED){
                  ThreadUtils.run(()->{
                      if(controllerHandler!=null){
                          controllerHandler.close();
                          controllerHandler = null;
                          foundController();
                      }
                  });
              }
          })
          .on(g -> g.servicesDiscovered, e -> {
              LogHelper.i("controller ServicesDiscovered");
              BluetoothGattService groveService = e.gatt.getService(UUID.fromString(GROVE_SERVICE));
              BluetoothGattCharacteristic rx = groveService.getCharacteristic(UUID.fromString(CHARACTERISTIC_RX));
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
              sendBroadcast(StandardKt.apply(new Intent(BROADCAST_GOT_CMD), intent->{
                  intent.putExtra("cmd", new String(val).trim());
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
          .on(g->g.failed, e->{
              LogHelper.e("Ble Discover Failed: " + e.errorCode);
          })
          .on(g -> g.gotResult, e -> {
              BluetoothDevice device = e.result.getDevice();
              LogHelper.i("random device found: %s %s", device.getName(), device.getAddress());
              if (this.controllerDevice == null && device.getName().equals(CONTROLLER_NAME)) {
                  LogHelper.i("controller found");
                  this.controllerDevice = device;
              }
              if( !isBleDiscoverTerminated && this.controllerDevice!=null){
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
    
    private BluetoothDevice controllerDevice;
    
    public void connectOn(Context context) {
        this.connectionContext = context;
        LogHelper.i("start scan controller");
        isBleDiscoverTerminated = false;
        bleDiscoverHandler = controllerDiscover.startOn(this, btManager);
    }
    
    public void disconnect() {
        if (bleDiscoverHandler != null) {
            bleDiscoverHandler.stop();
            bleDiscoverHandler = null;
        }
        if(controllerHandler!=null){
            controllerHandler.close();
            controllerHandler = null;
        }
    }
    // endregion
    
}