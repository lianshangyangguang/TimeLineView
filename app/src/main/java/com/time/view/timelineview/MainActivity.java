package com.time.view.timelineview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.time.view.library.TimeLineView;

public class MainActivity extends AppCompatActivity {

    private TextView txt_time;
    private TimeLineView timeLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_time = (TextView)findViewById(R.id.txt_time);
        timeLineView = (TimeLineView)findViewById(R.id.timeline);
        timeLineView.setTextSize(10).setCenterNum(10).setMultiple(16).setMarkColor(Color.RED);

        timeLineView.setNumberListener(new TimeLineView.NumberListener() {
            @Override
            public void onChanged(String time) {
                txt_time.setText(time);
            }
        });
    }
}