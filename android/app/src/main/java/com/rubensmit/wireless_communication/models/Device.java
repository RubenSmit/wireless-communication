package com.rubensmit.wireless_communication.models;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class Device extends Observable implements Observer {
    private final static String TAG = "DEVICE";

    private BluetoothDevice device;
    private Context context;
    BluetoothGatt bluetoothGatt;
    private int connectionState = STATE_DISCONNECTED;
    private int deviceType = TYPE_UNKNOWN;
    private int angle = -1;
    private Device source;
    private boolean use_source;

    public final static UUID UUID_HUMAN_INTERFACE_DEVICE_SERVICE =
            UUID.fromString("00001812-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_GENERIC_ATTRIBUTE_SERVICE =
            UUID.fromString("00001843-0000-1000-8000-00805f9b34fb");

    public final static UUID UUID_PLANE_ANGLE_CHARACTERISTIC =
            UUID.fromString("00002763-0000-1000-8000-00805f9b34fb");

    public final static UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private static final int TYPE_UNKNOWN = 0;
    private static final int TYPE_SENSOR = 1;
    private static final int TYPE_SERVO = 2;

    public Device(BluetoothDevice bluetoothDevice, Context context) {
        this.device = bluetoothDevice;
        this.context = context;

        Log.i(TAG, "Attempt connecting to gatt service.");
        bluetoothGatt = device.connectGatt(context, false, gattCallback);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        bluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server for device: " + device.getName());
            }

            setChanged();
            notifyObservers();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Gatt services discovered!");
                setServiceType(gatt);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG, "Characteristic changed!");
            bluetoothGatt.readCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Characteristic read!");
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16;
                    Log.d(TAG, "Angle format UINT16.");
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    Log.d(TAG, "Angle format UINT8.");
                }
                final int sensorAngle = characteristic.getIntValue(format, 0);
                Log.i(TAG, "Data raw: " + sensorAngle);
                setAngle(sensorAngle);
            } else {
                Log.w(TAG, "onCharacteristicRead received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG, "Characteristic written!");
        }
    };

    private void setServiceType(BluetoothGatt gatt) {
        for (BluetoothGattService service: gatt.getServices()) {
            UUID uuid = service.getUuid();
            Log.i(TAG, "Found service: " + uuid + " for device: " + device.getName());
            if (uuid.equals(UUID_HUMAN_INTERFACE_DEVICE_SERVICE)) {
                Log.i(TAG, "This is a sensor!");
                deviceType = TYPE_SENSOR;
                subscribeToSensorData(service);
            } else if (uuid.equals(UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                Log.i(TAG, "This is a servo!");
                deviceType = TYPE_SERVO;
            }
        }
    }

    private void subscribeToSensorData(BluetoothGattService service) {
        for (BluetoothGattCharacteristic characteristic: service.getCharacteristics()) {
            Log.i(TAG, "Found characteristic: " + characteristic.getUuid() + " for device: " + device.getName());
        }

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_PLANE_ANGLE_CHARACTERISTIC);
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);

        Log.i(TAG, "Subscribed to sensor characteristic: " + characteristic);

        bluetoothGatt.readCharacteristic(characteristic);
    }

    private void setAngle(int angle) {
        if (angle != this.angle) {
            this.angle = angle;
            setChanged();
            notifyObservers();
        }
    }

    public void writeAngle(int angle, boolean notify) {
        if (angle != this.angle) {
            this.angle = angle;
            if (notify) {
                setChanged();
                notifyObservers();
            }
            Log.i(TAG, "Set angle to: " + angle);

            BluetoothGattService service = bluetoothGatt.getService(UUID_GENERIC_ATTRIBUTE_SERVICE);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_PLANE_ANGLE_CHARACTERISTIC);
            characteristic.setValue(angle, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            bluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getDeviceName() {
        return device.getName();
    }

    public String getDeviceAddress() {
        return device.getAddress();
    }

    public int getDeviceType() {
        return deviceType;
    }

    public String getConnectionStatus() {
        switch (connectionState) {
            case STATE_CONNECTING:
                return "Connecting";
            case STATE_CONNECTED:
                return "Connected";
            default :
                return "Disconnected";
        }
    }

    public int getAngle() {
        return angle;
    }

    public void disconnect() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt.disconnect();
        bluetoothGatt = null;
    }

    public void setSource(Device source) {
        if (source.getDeviceType() == TYPE_SENSOR) {
            this.source = source;
            use_source = true;
            source.addObserver(this);
        }
    }

    public void unsetSource() {
        use_source = false;
        if (source != null) {
            source.deleteObserver(this);
        }
    }

    public boolean usesSource() {
        return use_source;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (deviceType == TYPE_SERVO && use_source && source != null) {
            writeAngle(source.getAngle(), true);
        }
    }

    @Override
    public String toString() {
        return device.getName();
    }

    public Device getSource() {
        return source;
    }
}
