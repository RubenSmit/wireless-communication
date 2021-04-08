package com.rubensmit.wireless_communication.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.rubensmit.wireless_communication.R;
import com.rubensmit.wireless_communication.adapters.BluetoothDevicesListAdapter;
import com.rubensmit.wireless_communication.models.Device;
import com.rubensmit.wireless_communication.providers.BluetoothDevicesProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * BluetoothDevicesProvider
 *
 * Handles displaying of a list of connected bluetooth devices.
 * It initiates scanning for new devices and stores the found devices in a provider.
 * It initiates displaying of the list and notifies the list of changes to the devices.
 *
 * @author Ruben
 */
public class MainActivity extends AppCompatActivity implements Observer {
    private final static String TAG = "MAIN_ACTIVITY";

    BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    public BluetoothDevicesListAdapter bluetoothDevicesListAdapter;

    // BLE scanner
    private BluetoothLeScanner bluetoothLeScanner =
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private boolean mScanning;
    private Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    /**
     * Starts the bluetooth service, initiates the list of devices and adds a listener to the fab
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        bluetoothDevicesListAdapter = new BluetoothDevicesListAdapter(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice();
            }
        });
    }

    /**
     * Scans for available BLE peripherals during the scanning period
     */
    private void scanLeDevice() {
        Log.i(TAG, "Started scanning for devices");
        BluetoothDevicesProvider.clear();
        if (!mScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    Log.i(TAG, "Stopped scanning for devices");
                }
            }, SCAN_PERIOD);

            // Scan for devices matching the filter and use the callback for found devices
            mScanning = true;
            List<ScanFilter> filters = deviceFilters();
            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bluetoothLeScanner.startScan(filters, settings, leScanCallback);
        } else {
            // Stop scanning if the button is pressed again
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.i(TAG, "Stopped scanning for devices");
        }
    }

    /**
     * Provides the filters used to limit the scan results to the wanted peripherals
     * @return device filters
     */
    private List<ScanFilter> deviceFilters() {
        List<ScanFilter> filters = new ArrayList<>();

        // Filter for FyPi
        ScanFilter humanInterfaceFilter = new ScanFilter.Builder()
                .setDeviceName("FyPi")
                .build();
        filters.add(humanInterfaceFilter);

        // Filter for NRF52
        ScanFilter genericAttributeFilter = new ScanFilter.Builder()
                .setDeviceName("NRF52")
                .build();
        filters.add(genericAttributeFilter);

        return filters;
    }

    /**
     * Initiates a connection with found peripherals, creates a device model and adds it to the
     * provider
     */
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    // Get the bluetooth device
                    Log.i(TAG, "Found a bluetooth device!");
                    super.onScanResult(callbackType, result);
                    BluetoothDevice bluetoothDevice = result.getDevice();

                    if (!BluetoothDevicesProvider.contains(bluetoothDevice.getAddress())) {
                        // Add the device to the list and observe it
                        Log.i(TAG, "Adding new device to list");
                        Device device = new Device(bluetoothDevice, getContext());
                        BluetoothDevicesProvider.addDevice(device);
                        device.addObserver(getObserver());
                    }
                }
            };

    public Context getContext() {
        return (Context)this;
    }

    public Observer getObserver() {
        return this;
    }

    /**
     * Notifies the list adapter to changes in a observed device in the ui thread
     * @param observable Device with changed properties
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = BluetoothDevicesProvider.deviceList.indexOf((Device) observable);
                bluetoothDevicesListAdapter.notifyItemChanged(index);
            }
        });
    }
}