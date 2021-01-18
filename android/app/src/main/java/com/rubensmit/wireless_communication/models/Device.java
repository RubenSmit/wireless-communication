package com.rubensmit.wireless_communication.models;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.Observable;
import java.util.UUID;

public class Device extends Observable {
    private final static String TAG = "DEVICE";

    private BluetoothDevice device;
    BluetoothGatt bluetoothGatt;
    private int connectionState = STATE_DISCONNECTED;
    private int deviceType = TYPE_SENSOR;

    public final static UUID UUID_HUMAN_INTERFACE_DEVICE_SERVICE =
            UUID.fromString("00001812-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_GENERIC_ATTRIBUTE_SERVICE =
            UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private static final int TYPE_SENSOR = 0;
    private static final int TYPE_SERVO = 1;

    public Device(BluetoothDevice bluetoothDevice, Context context) {
        this.device = bluetoothDevice;

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
                setChanged();
                notifyObservers();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                setChanged();
                notifyObservers();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Gatt services discovered!");
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Characteristic read!");
            } else {
                Log.w(TAG, "onCharacteristicRead received: " + status);
            }
        }
    };

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
}
