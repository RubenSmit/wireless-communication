package com.rubensmit.wireless_communication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rubensmit.wireless_communication.R;
import com.rubensmit.wireless_communication.models.Device;
import com.rubensmit.wireless_communication.providers.BluetoothDevicesProvider;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDevicesListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Device> devicesList;

    private final int UNKNOWN = 0;
    private final int SENSOR = 1;
    private final int SERVO = 2;

    /**
     * Constructor
     * @param context
     */
    public BluetoothDevicesListAdapter(Context context) {
        this.context = context;
        this.devicesList = BluetoothDevicesProvider.deviceList;
        BluetoothDevicesProvider.adapter = this;
    }

    /**
     * On create of the viewholder
     * Determine device type and inflate the correct view
     * @param parent
     * @param viewType
     * @return holder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the correct view if locked or not
        switch (viewType) {
            case SENSOR :
                View sensorView = LayoutInflater.from(context).inflate(R.layout.devices_list_item_sensor, parent,  false);
                return new SensorViewHolder(sensorView);
            case SERVO :
                View servoView = LayoutInflater.from(context).inflate(R.layout.devices_list_item_servo, parent,  false);
                return new ServoViewHolder(servoView);
            case UNKNOWN :
                View unknownView = LayoutInflater.from(context).inflate(R.layout.devices_list_item_unknown, parent,  false);
                return new UnknownViewHolder(unknownView);
            default :
                return null;
        }
    }

    /**
     * Bind the the views to the holder
     * Determine device type and bind the correct view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Bind the correct view if locked or not
        switch (holder.getItemViewType()) {
            case SENSOR :
                bindSensorView((SensorViewHolder) holder, devicesList.get(position));
                break;
            case SERVO :
                bindServoView((ServoViewHolder) holder, devicesList.get(position));
                break;
            case UNKNOWN :
                bindUnknownView((UnknownViewHolder) holder, devicesList.get(position));
                break;
        }
    }

    /**
     * Bind the view and add values to the view
     * @param holder
     * @param device
     */
    private void bindSensorView(SensorViewHolder holder, final Device device) {
        holder.tvName.setText(device.getDeviceName());
        holder.tvStatus.setText(device.getConnectionStatus());

        if (device.getAngle() >= 0) {
            holder.tvAngle.setText(String.valueOf(device.getAngle()));
            holder.pbAngle.setProgress(device.getAngle());
        } else {
            holder.tvAngle.setText("unknown");
        }
    }

    /**
     * Bind the view and add values to the view
     * @param holder
     * @param device
     */
    private void bindServoView(ServoViewHolder holder, final Device device) {
        holder.tvName.setText(device.getDeviceName());
        holder.tvStatus.setText(device.getConnectionStatus());
        if (device.getAngle() >= 0) {
            holder.tvAngle.setText(String.valueOf(device.getAngle()));
            holder.sbAngle.setProgress(device.getAngle());
        } else {
            holder.tvAngle.setText("unset");
        }

        holder.sbAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!device.usesSource()) {
                    device.writeAngle(seekBar.getProgress(), false);
                    holder.tvAngle.setText(String.valueOf(seekBar.getProgress()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        List<Device> sources = new ArrayList<>();
        sources.add(device);
        for (Device sourceDevice: BluetoothDevicesProvider.deviceList) {
            if (!sourceDevice.equals(device) && sourceDevice.getDeviceType() == SENSOR) {
                sources.add(sourceDevice);
            }
        }
        ArrayAdapter<Device> spinnerAdapter = new ArrayAdapter(context, R.layout.source_list_item, R.id.tvSourceName, sources.toArray());
        holder.spSource.setAdapter(spinnerAdapter);
        if (device.usesSource()) {
            holder.spSource.setSelection(sources.indexOf(device.getSource()));
        }

        holder.spSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Device source = sources.get(i);
                if (source.equals(device)) {
                    device.unsetSource();
                } else {
                    device.setSource(source);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Bind the view and add values to the view
     * @param holder
     * @param device
     */
    private void bindUnknownView(UnknownViewHolder holder, final Device device) {
        holder.tvName.setText(device.getDeviceName());
        holder.tvStatus.setText(device.getConnectionStatus());
    }

    /**
     * SensorViewHolder
     * Holder of the servo view
     */
    private class SensorViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvStatus;
        public TextView tvAngle;
        public ProgressBar pbAngle;

        public SensorViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvSensorName);
            tvStatus = itemView.findViewById(R.id.tvSensorStatus);
            tvAngle = itemView.findViewById(R.id.tvSensorAngle);
            pbAngle = itemView.findViewById(R.id.pbSensorAngle);
        }
    }

    /**
     * ServoViewHolder
     * Holder of the sensor view
     */
    private class ServoViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvStatus;
        public TextView tvAngle;
        public SeekBar sbAngle;
        public Spinner spSource;

        public ServoViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvServoName);
            tvStatus = itemView.findViewById(R.id.tvServoStatus);
            tvAngle = itemView.findViewById(R.id.tvServoAngle);
            sbAngle = itemView.findViewById(R.id.sbServoAngle);
            spSource = itemView.findViewById(R.id.spServoSource);
        }
    }

    /**
     * UnknownViewHolder
     * Holder of the servo view
     */
    private class UnknownViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvStatus;

        public UnknownViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvUnknownName);
            tvStatus = itemView.findViewById(R.id.tvUnknownStatus);
        }
    }


    /**
     * Get the amount of devices
     * @return amount of devices
     */
    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    /**
     * Returns the view type of the device
     * @param position
     * @return view type of the device
     */
    @Override
    public int getItemViewType(int position) {
        Device device = BluetoothDevicesProvider.deviceList.get(position);

        if(device.getDeviceType() == SENSOR){
            return SENSOR;
        } else if (device.getDeviceType() == SERVO) {
            return SERVO;
        } else {
            return UNKNOWN;
        }
    }
}
