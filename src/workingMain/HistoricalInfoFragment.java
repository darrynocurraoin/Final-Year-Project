package org.opencv.samples.facedetect;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class HistoricalInfoFragment extends Fragment {
	private FinalActivity finalAct;
	private Context context = null;
	private TextView finalHistoryTextView;
	private Button returnButton;
	private static String passedUser;
	private ArrayList<String> historicalInfo;	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		LinearLayout fragLayout = (LinearLayout) inflater.inflate(R.layout.historyfragment, null);

		finalHistoryTextView = (TextView) fragLayout.findViewById(R.id.finalHistoryTextView);
		finalHistoryTextView.setText("Historical Entries for " + passedUser +":");
		
		ListView historyList = (ListView) fragLayout.findViewById(R.id.historyList);
		historyList.setBackgroundColor(Color.LTGRAY);
		MyDbHelper myDB = new MyDbHelper(finalAct);
		historicalInfo = myDB.returnHistoricalDbData(passedUser);
		ArrayAdapter<String> historyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, historicalInfo);
		historyList.setAdapter(historyAdapter);
		
		returnButton = (Button) fragLayout.findViewById(R.id.returnButton);
		returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		getView().setVisibility(View.GONE);
            }
        }); 		
		return fragLayout;
	}
	
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); 
    }

	public static HistoricalInfoFragment newInstance(String string) {
		HistoricalInfoFragment fragment = new HistoricalInfoFragment();
		passedUser = string;
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			context = getActivity();
			finalAct = (FinalActivity) getActivity();
			
		} catch (IllegalStateException e) {
			throw new IllegalStateException("ILLEGAL STATE EXCEPTION");
		}
	}
}