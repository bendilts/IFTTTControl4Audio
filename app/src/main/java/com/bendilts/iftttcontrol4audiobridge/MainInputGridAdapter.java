package com.bendilts.iftttcontrol4audiobridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainInputGridAdapter extends BaseAdapter {
    private AudioSystem system = AudioSystem.getInstance();
    private Activity activity;
    private Class inputActivityClass;
    private boolean autoSizeIcons;

    public MainInputGridAdapter(Activity a, Class inputActivity, boolean autoSize) {
        super();
        activity = a;
        inputActivityClass = inputActivity;
        autoSizeIcons = autoSize;
    }

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

        rowView = activity.getLayoutInflater().inflate(R.layout.input_main, null);

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

        if(autoSizeIcons) {
            Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point screenSize = new Point(0, 0);
            display.getSize(screenSize);
            int smallerDimension = Math.min(screenSize.x, screenSize.y);

            ViewGroup.LayoutParams params = img.getLayoutParams();
            params.width = (int) (smallerDimension * 0.25);
            params.height = (int) (smallerDimension * 0.25);
            img.setLayoutParams(params);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, inputActivityClass);
                intent.putExtra("INPUT_INDEX", input.index);
                activity.startActivity(intent);
            }
        });

        return rowView;
    }

    @Override
    public boolean isEmpty() {
        return system.receiver.inputs.isEmpty();
    }
}
