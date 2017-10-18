package com.bendilts.iftttcontrol4audiobridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class InputActivity extends ScreenSaverableActivity implements Control4Device.DeviceListener {

    AudioSystem system;
    AudioInput input;

    TextView inputName;
    ImageView inputIcon;
    ListView outputList;
    BaseAdapter outputListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_input);

        inputName = (TextView)findViewById(R.id.inputName);
        inputIcon = (ImageView)findViewById(R.id.inputIcon);
        outputList = (ListView)findViewById(R.id.outputList);

        findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int inputIndex = getIntent().getIntExtra("INPUT_INDEX", 1);
        system = AudioSystem.instance;
        input = system.receiver.getInput(inputIndex);

        View inputControls = input.getInputControls(getLayoutInflater());
        if(inputControls != null) {
            ((LinearLayout)findViewById(R.id.header)).addView(inputControls);
        }

        inputName.setText(input.name);
        inputIcon.setImageResource(input.iconResource);

        outputListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return system.receiver.outputs.size();
            }

            @Override
            public Object getItem(int position) {
                return system.receiver.outputs.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = getLayoutInflater().inflate(R.layout.output_volume, null);

                TextView name = (TextView)rowView.findViewById(R.id.outputName);
                SeekBar volumeBar = (SeekBar)rowView.findViewById(R.id.outputVolume);
                ImageView icon = (ImageView)rowView.findViewById(R.id.inputIcon);

                final AudioOutput output = system.receiver.outputs.get(position);

                name.setText(output.name);
                if(output.currentInput == input) {
                    volumeBar.setProgress(output.currentVolume);
                } else {
                    volumeBar.setProgress(0);
                }

                if(output.currentInput == null || output.currentVolume == 0) {
                    icon.setImageResource(android.R.color.transparent);
                } else {
                    icon.setImageResource(output.currentInput.iconResource);
                }

                volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    private int targetVolume = output.currentVolume;

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
                        new AsyncTask<Integer, Integer, Long>() {
                            @Override
                            protected Long doInBackground(Integer... params) {
                                try {
                                    system.receiver.setAudio(output, input, targetVolume);
                                } catch(Exception e) {}
                                return 0L;
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });

                return rowView;
            }
        };

        outputList.setAdapter(outputListAdapter);
        system.receiver.listeners.add(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        system.receiver.listeners.remove(this);
    }

    @Override
    public void onDeviceChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                outputListAdapter.notifyDataSetChanged();
            }
        });
    }

}
