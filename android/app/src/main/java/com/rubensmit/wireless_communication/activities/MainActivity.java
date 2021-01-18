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

            mScanning = true;
//
//            List<ScanFilter> filters = new ArrayList<>();
//            filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(Device.UUID_HUMAN_INTERFACE_DEVICE_SERVICE)).build());
//            filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(Device.UUID_GENERIC_ATTRIBUTE_SERVICE)).build());
//
//            ScanSettings settings = new ScanSettings.Builder()
//                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                    .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
//                    .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
//                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
//                    .build();
//
//            bluetoothLeScanner.startScan(filters, settings, leScanCallback);
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.i(TAG, "Stopped scanning for devices");
        }
    }

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Log.i(TAG, "Found a bluetooth device!");
                    super.onScanResult(callbackType, result);
                    BluetoothDevice bluetoothDevice = result.getDevice();
                    if (!BluetoothDevicesProvider.contains(bluetoothDevice.getAddress())) {
                        Log.i(TAG, "Adding new device to list");
                        Device device = new Device(bluetoothDevice, getContext());
                        BluetoothDevicesProvider.addDevice(device);
                        device.addObserver(getObserver());
                    }
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Context getContext() {
        return (Context)this;
    }

    public Observer getObserver() {
        return this;
    }

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