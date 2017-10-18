package com.bendilts.iftttcontrol4audiobridge;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ScreenSaverableActivity implements Control4Device.DeviceListener {

    HTTPServer server;
    AudioSystem system;

    GridView mainGrid;
    BaseAdapter mainGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        system = new AudioSystem();
        server = new HTTPServer(system);
        server.listen(8080);

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
                mainGridAdapter.notifyDataSetChanged();
            }
        });
    }
}
