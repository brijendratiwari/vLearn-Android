package com.vlearn.android.ui.picker;

import org.json.JSONObject;

import com.vlearn.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class Picker extends Activity{

	String[] entries;
	String okButtonString;
	String cancelButtonString;
	String titleString;
	
	NumberPicker mPicker;
	Button ok;
	Button cancel;
	TextView title;
	
	public static final String _OKButton = "ok_Button";
	public static final String _CANCELButton = "cancel_Button";
	public static final String _PICKERTITLE = "pickerTitle";
	public static final String _ListEntries = "ListEntries";
	
	private final int okButton = R.id.ok;
	private final int cancelButton = R.id.cancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picker);
		
		Bundle intent = getIntent().getExtras();
		try{
			entries 		= intent.getStringArray(_ListEntries);
			okButtonString 	= intent.getString(_OKButton);
			cancelButtonString = intent.getString(_CANCELButton);
			titleString 	= intent.getString(_PICKERTITLE);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		mPicker = (NumberPicker)findViewById(R.id.numberPicker);
		ok 		= (Button) 		findViewById(okButton);
		cancel 	= (Button) 		findViewById(cancelButton);
		title   = (TextView) 	findViewById(R.id.title);
		
		if(titleString != null){
			title.setText(titleString); 
		}
		if(okButtonString != null){
			ok.setText(okButtonString);
		}
		if(cancelButtonString != null){
			cancel.setText(cancelButtonString);
		}
		if(entries == null){
			Intent returnIntent = new Intent();
			returnIntent.putExtra("result", "You didn't send any string array.");
			setResult(RESULT_CANCELED, returnIntent);   
			finishSelf();
		}else{
			mPicker.setMinValue(0);
			mPicker.setMaxValue(entries.length-1);
			mPicker.setDisplayedValues(entries);
		}
		
		
		mPicker.setWrapSelectorWheel(false);
		mPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	
		ok.setOnClickListener(clickListener);
		cancel.setOnClickListener(clickListener);
		
	}
	
	int newVal = 0;;
	
	View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case okButton:
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", entries[mPicker.getValue()]);
				returnIntent.putExtra("position", mPicker.getValue());
				setResult(RESULT_OK, returnIntent);      
				break;
			case cancelButton:
				setResult(RESULT_CANCELED, null);      
				break;

			default:
				break;
			}
			
			finishSelf();
		}
	};
	
	public void onBackPressed() {
		super.onBackPressed();
		finishSelf();
	};
	
	private void finishSelf(){
		finish();
	
		setResult(RESULT_CANCELED, null);   
		overridePendingTransition(R.anim.slideright1, R.anim.slideright2);
	}
	
}
