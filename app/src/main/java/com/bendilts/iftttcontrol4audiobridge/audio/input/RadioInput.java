package com.bendilts.iftttcontrol4audiobridge.audio.input;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.bendilts.iftttcontrol4audiobridge.CommandExecutor;
import com.bendilts.iftttcontrol4audiobridge.audio.control4.Control4Radio;
import com.bendilts.iftttcontrol4audiobridge.R;

public class RadioInput extends AudioInput {
    public int tunerIndex;
    public RadioInput(int id, String n, String s, int g, int _tunerIndex) {
        super(id, n, s, g, R.drawable.radio);
        tunerIndex = _tunerIndex;
    }

    public View getInputControls(final LayoutInflater inflater, final int textSize) {
        View view = inflater.inflate(R.layout.radio_controls, null);

        GridView stationGrid = (GridView)view.findViewById(R.id.stationGrid);
        stationGrid.setNumColumns(Control4Radio.stations.length);

        stationGrid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return Control4Radio.stations.length;
            }

            @Override
            public Object getItem(int position) {
                return Control4Radio.stations[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                Button button = new Button(inflater.getContext());
                button.setText(Control4Radio.stations[position]);
                button.setTextSize(textSize);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommandExecutor.getInstance().tune(tunerIndex, Control4Radio.stations[position]);
                    }
                });
                return button;
            }
        });

        return view;
    }
}
