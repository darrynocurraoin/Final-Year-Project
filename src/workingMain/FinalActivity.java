package org.opencv.samples.facedetect;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FinalActivity extends Activity {
	private static final String    DCDEBUG             = "darrynFinishingDebug";
	private String 				   passedUserName;
	private String 				   passedDate;
	private TextView 			   finalDayTextView;
	private Button 				   historyButton;
	private Button 				   newSession;
	private ListView 			   lvDay;
	private ArrayList<String> 	   currentDayInfo;
	private FragmentTransaction    ft;
	private HistoricalInfoFragment hInfoFrag;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(android.R.style.Theme_Material_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finishpage);
        finalDayTextView = (TextView) findViewById(R.id.finalDayTextView);
        historyButton = (Button) findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		ft = getFragmentManager().beginTransaction();
        		hInfoFrag = HistoricalInfoFragment.newInstance(passedUserName);
        		ft.replace(R.id.finalPageLayout, hInfoFrag);
        		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        		ft.addToBackStack(null);
        		ft.commit();
            }
        }); 
        
        newSession = (Button) findViewById(R.id.newSession);
        newSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	finish();
            }
        }); 
        
        MyDbHelper myDB = new MyDbHelper(this);
        Intent thisIntent = getIntent();
        Bundle passedData = thisIntent.getExtras();
        if(passedData != null){
        	passedUserName = passedData.getString("uName");
        	passedDate = passedData.getString("uDate");
        	Log.d(DCDEBUG, "User name received: " + passedUserName + " and the date " + passedDate);
        }
        
        finalDayTextView.setText(passedUserName + "'s summary for today(" + passedDate + ").");
        lvDay = (ListView) findViewById(R.id.dayList);
        lvDay.setBackgroundColor(Color.LTGRAY);
        currentDayInfo = myDB.returnCurrentDayDbData(passedUserName, passedDate);
        ArrayAdapter<String> todayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currentDayInfo);
        lvDay.setAdapter(todayAdapter);
        Log.d(DCDEBUG, "Current day data set successfully");

    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    @Override
    public void onBackPressed() {
    }
}