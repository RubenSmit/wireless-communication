package com.rubensmit.wireless_communication.providers;

import android.bluetooth.BluetoothDevice;

import com.rubensmit.wireless_communication.adapters.BluetoothDevicesListAdapter;
import com.rubensmit.wireless_communication.models.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BluetoothDevicesProvider
 *
 * Provides the bluetooth devices list
 *
 * @author Ruben
 */
public class BluetoothDevicesProvider {

    //List with al the devices
    public static List<Device> deviceList;
    public static Map<String, Device> deviceMap;
    public static BluetoothDevicesListAdapter adapter;

    /**
     * Initiates the devices list and map
     */
    static {
        deviceList = new ArrayList<>();
        deviceMap = new HashMap<>();
    }

    /**
     * Adding device to the list and hashes
     * @param device
     */
    public static void addDevice(Device device) {
        if (!contains(device.getDeviceAddress())) {
            deviceList.add(device);
            deviceMap.put(device.getDeviceAddress(), device);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Clears the list of devices
     */
    public static void clear() {
        for(Device device: deviceList) {
            device.disconnect();
        }

        deviceList.clear();
        deviceMap.clear();
        adapter.notifyDataSetChanged();
    }

    public static boolean contains(String deviceAddress) {
        return deviceMap.containsKey(deviceAddress);
    }
}
