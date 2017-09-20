package org.opencv.samples.facedetect;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class OpeningActivity extends Activity {
    private View.OnTouchListener gestureListener;
    private TextView toNextActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(android.R.style.Theme_Material_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening);

        toNextActivity = (TextView) findViewById(R.id.toNextActivity);
        toNextActivity.setOnTouchListener(gestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        	finish();
            return true;
    }
    
    @Override
    public void onBackPressed() {
    }

}