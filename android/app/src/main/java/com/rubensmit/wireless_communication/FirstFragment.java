package com.rubensmit.wireless_communication;

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

public class FirstFragment extends Fragment {

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
        SeekBar seekBar = view.findViewById(R.id.sb_servo_angle);
        TextView servoAngle = view.findViewById(R.id.tv_servo_angle);
        ProgressBar progressBar = view.findViewById(R.id.pb_sensor_angle);
        TextView sensorAngle = view.findViewById(R.id.tv_sensor_angle);

        servoAngle.setText(seekBar.getProgress() + " degrees");
        sensorAngle.setText(seekBar.getProgress() + " degrees");
        progressBar.setProgress(seekBar.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                servoAngle.setText(seekBar.getProgress() + " degrees");
                sensorAngle.setText(seekBar.getProgress() + " degrees");
                progressBar.setProgress(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}