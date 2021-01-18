package com.rubensmit.wireless_communication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rubensmit.wireless_communication.R;
import com.rubensmit.wireless_communication.models.Device;
import com.rubensmit.wireless_communication.providers.BluetoothDevicesProvider;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BluetoothDevicesListAdapter extends RecyclerView.Adapter implements Observer {
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
    }

    /**
     * Bind the view and add values to the view
     * @param holder
     * @param device
     */
    private void bindServoView(ServoViewHolder holder, final Device device) {
        holder.tvName.setText(device.getDeviceName());
        holder.tvStatus.setText(device.getConnectionStatus());
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

    @Override
    public void update(Observable o, Object arg) {
        notifyDataSetChanged();
    }

    /**
     * SensorViewHolder
     * Holder of the servo view
     */
    private class SensorViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvStatus;

        public SensorViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvSensorName);
            tvStatus = itemView.findViewById(R.id.tvSensorStatus);
        }
    }

    /**
     * ServoViewHolder
     * Holder of the sensor view
     */
    private class ServoViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvStatus;

        public ServoViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvServoName);
            tvStatus = itemView.findViewById(R.id.tvServoStatus);
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
