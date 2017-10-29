package com.bendilts.iftttcontrol4audiobridge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class RadioInput extends AudioInput {
    private int tunerIndex;
    RadioInput(int i, String n, String s, int g, int _tunerIndex) {
        super(i, n, s, g, R.drawable.radio);
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
