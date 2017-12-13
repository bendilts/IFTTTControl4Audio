package com.bendilts.iftttcontrol4audiobridge;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bendilts.iftttcontrol4audiobridge.audio.AudioSystem;
import com.bendilts.iftttcontrol4audiobridge.audio.control4.Control4Device;
import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.OutputDevice;

import java.util.List;

public class PhoneInputActivity extends UpdateFromMasterActivity implements OutputDevice.DeviceListener {

    CommandExecutor executor = CommandExecutor.getInstance();
    AudioSystem system;
    AudioInput input;
    List<AudioOutput> outputs;

    TextView inputName;
    ImageView inputIcon;
    ListView outputList;
    BaseAdapter outputListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);

        inputName = (TextView)findViewById(R.id.inputName);
        inputIcon = (ImageView)findViewById(R.id.inputIcon);
        outputList = (ListView)findViewById(R.id.outputList);

        findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int inputId = getIntent().getIntExtra("INPUT_ID", 1);
        system = AudioSystem.getInstance();
        input = system.inputById(inputId);
        outputs = input.availableOuputs;

        View inputControls = input.getInputControls(getLayoutInflater(), 14);
        if(inputControls != null) {
            ((LinearLayout)findViewById(R.id.header2)).addView(inputControls);
        }

        inputName.setText(input.name);
        inputIcon.setImageResource(input.iconResource);

        outputListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return outputs.size();
            }

            @Override
            public Object getItem(int position) {
                return outputs.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = getLayoutInflater().inflate(R.layout.phone_output_volume, null);

                TextView name = (TextView)rowView.findViewById(R.id.outputName);
                SeekBar volumeBar = (SeekBar)rowView.findViewById(R.id.outputVolume);
                ImageView icon = (ImageView)rowView.findViewById(R.id.inputIcon);

                final AudioOutput output = outputs.get(position);

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
                        executor.setAudio(output, input, targetVolume);
                    }
                });

                return rowView;
            }
        };

        outputList.setAdapter(outputListAdapter);
        for(OutputDevice device : system.outputDevices) {
            device.listeners.add(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(OutputDevice device : system.outputDevices) {
            device.listeners.remove(this);
        }
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
