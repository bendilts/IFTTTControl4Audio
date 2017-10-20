package com.bendilts.iftttcontrol4audiobridge;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.INVISIBLE;

public class MainActivity extends ScreenSaverableActivity implements Control4Device.DeviceListener {

    HTTPServer server;
    AudioSystem system;

    GridView mainGrid;
    BaseAdapter mainGridAdapter;

    AudioOutput localOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        system = AudioSystem.getInstance();

        if(CommandExecutor.isMaster) {
            server = HTTPServer.getInstance();
        }

        system.receiver.listeners.add(this);

        mainGrid = (GridView)findViewById(R.id.mainInputGrid);
        mainGridAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return system.receiver.inputs.size();
            }

            @Override
            public Object getItem(int position) {
                return system.receiver.inputs.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView;

                rowView = getLayoutInflater().inflate(R.layout.input_main, null);

                TextView name = (TextView)rowView.findViewById(R.id.inputName);
                ImageView img = (ImageView)rowView.findViewById(R.id.inputIcon);

                final AudioInput input = system.receiver.inputs.get(position);

                if(system.receiver.inputUsers(input).isEmpty()) {
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);  //0 means grayscale
                    ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
                    img.setColorFilter(cf);
                    img.setImageAlpha(80);   // 128 = 0.5
                }

                name.setText(input.name);
                img.setImageResource(input.iconResource);

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, InputActivity.class);
                        intent.putExtra("INPUT_INDEX", input.index);
                        startActivity(intent);
                    }
                });

                return rowView;
            }

            @Override
            public boolean isEmpty() {
                return system.receiver.inputs.isEmpty();
            }
        };

        mainGrid.setAdapter(mainGridAdapter);

        Spinner outputSpinner = (Spinner)findViewById(R.id.outputSpinner);
        outputSpinner.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return system.receiver.outputs.size();
            }

            @Override
            public Object getItem(int position) {
                return system.receiver.outputs.get(position).name;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = new TextView(MainActivity.this);
                tv.setText(system.receiver.outputs.get(position).name);
                tv.setTextSize(25);
                tv.setPadding(5,5,5,5);
                return tv;
            }
        });

        localOutput = system.receiver.getOutput(0);
        outputSpinner.setSelection(0);
        outputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                localOutput = system.receiver.outputs.get(position);
                updateControls();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ((VerticalSeekBar)findViewById(R.id.outputVolume)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int targetVolume = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                if(fromUser) {
                    targetVolume = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(localOutput != null && localOutput.currentInput != null) {
                    CommandExecutor.getInstance().setAudio(localOutput, localOutput.currentInput, targetVolume);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateControls();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        system.receiver.listeners.remove(this);
    }

    private void updateControls() {
        mainGridAdapter.notifyDataSetChanged();

        ImageView icon = (ImageView) findViewById(R.id.inputIcon2);
        VerticalSeekBar volume = (VerticalSeekBar)findViewById(R.id.outputVolume);
        if(localOutput == null || localOutput.currentInput == null) {
            icon.setImageResource(android.R.color.transparent);
            volume.setVisibility(View.INVISIBLE);
            volume.setProgress(0);
        } else {
            icon.setImageResource(localOutput.currentInput.iconResource);
            volume.setVisibility(View.VISIBLE);
            volume.setProgress(localOutput.currentVolume);

            if(localOutput.currentVolume == 0) {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);  //0 means grayscale
                ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
                icon.setColorFilter(cf);
                icon.setImageAlpha(80);   // 128 = 0.5
            } else {
                icon.setColorFilter(null);
                icon.setImageAlpha(255);
            }
        }
    }

    @Override
    public void onDeviceChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateControls();
            }
        });
    }
}
