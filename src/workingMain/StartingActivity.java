package org.opencv.samples.facedetect;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StartingActivity extends Activity implements OnItemSelectedListener {
	private static final String    DCDEBUG             = "darrynStartingDebug";
	private EditText			   userView;
	private Spinner				   exerciseSpinner;
	private EditText			   weightInput;
	private	Button 				   startOpenCV;
	private String				   returnUser;
	private String				   returnExercise;
	private int				       returnWeight;
	private ArrayList<String>	   exerciseList;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(android.R.style.Theme_Material_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appstart);
        
	    Intent appOpening = new Intent(StartingActivity.this, OpeningActivity.class);
	    startActivity(appOpening);
        
        MyDbHelper myDB = new MyDbHelper(this);
        
        userView = (EditText) findViewById(R.id.userView);
        exerciseSpinner = (Spinner) findViewById(R.id.exerciseSpinner);
        weightInput = (EditText) findViewById(R.id.weightInput);
        
        startOpenCV = (Button) findViewById(R.id.startOpenCV);   
        startOpenCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(userView.getText().toString().equals("") || weightInput.getText().toString().equals("")) {
            		Toast.makeText(getApplicationContext(), "You must enter values for both the User and Weight being lifted!", Toast.LENGTH_LONG).show();
            	}
            	else {
            		storeValuesToPass();
            		finish();
            	}
            }
        });  
        
        exerciseList = myDB.returnStoredExercises();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exerciseList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(spinnerAdapter); 
        exerciseSpinner.setOnItemSelectedListener(this);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
    	returnExercise = exerciseSpinner.getSelectedItem().toString();
    	Log.i(DCDEBUG, "Exercise value stored: " + returnExercise);
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    	Toast.makeText(getBaseContext(), "Nothing Selected", Toast.LENGTH_LONG).show();
    }
    
    public void storeValuesToPass() {
    	returnUser = userView.getText().toString();
    	Log.i(DCDEBUG, "Exercise value stored: " + returnUser);
    	returnWeight = Integer.valueOf(weightInput.getText().toString());
    	Log.i(DCDEBUG, "Exercise value stored: " + returnWeight);
    	
    	Intent myIntent = getIntent();
    	myIntent.putExtra("user", returnUser);
    	myIntent.putExtra("exer", returnExercise);
    	myIntent.putExtra("weight", returnWeight);
    	setResult(Activity.RESULT_OK,myIntent);
    }
    
    @Override
    public void onBackPressed() {
    }
    
}