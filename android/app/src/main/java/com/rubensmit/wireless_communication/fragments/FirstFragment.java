package com.rubensmit.wireless_communication.fragments;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rubensmit.wireless_communication.R;
import com.rubensmit.wireless_communication.activities.MainActivity;
import com.rubensmit.wireless_communication.adapters.BluetoothDevicesListAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class FirstFragment extends Fragment {

    MainActivity activity;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();

        RecyclerView rvDevicesList = view.findViewById(R.id.rvDevicesList);
        rvDevicesList.setHasFixedSize(false);
        rvDevicesList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvDevicesList.setAdapter(activity.bluetoothDevicesListAdapter);
        activity.bluetoothDevicesListAdapter.notifyDataSetChanged();
    }
}