package com.vlearn.android.setting.mysetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.ui.picker.DatePickerFragment;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class MyProfile extends DialogFragment implements OnNetworkResult{

	HomeActivity activity;
	Context context;

	Button dob;
	EditText firstName, LastName, Email, password, zip_code;
	Button cancel, save;

	String avtaarUrl,fileName;
	NetworkConnection connection;
	VUtil util;

	public static MyProfile Create(String fileName,String fileUrl) {
		MyProfile myprofile = new MyProfile();
		Bundle bundle = new Bundle();
		bundle.putString("fileName", fileName);
		bundle.putString("avtaarUrl", fileUrl);
		myprofile.setArguments(bundle);
		return myprofile;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		avtaarUrl = getArguments().getString("avtaarUrl");
		fileName = getArguments().getString("fileName");
		if(fileName == null){
			//fileUrl
			fileName = avtaarUrl.substring( avtaarUrl.lastIndexOf('/')+1, avtaarUrl.length() );
		}
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		// setStyle(style, theme)
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.myprofile, null, false);

		dob = (Button) view.findViewById(R.id.dob);

		firstName = (EditText) view.findViewById(R.id.firstName);
		LastName = (EditText) view.findViewById(R.id.lastName);
		Email = (EditText) view.findViewById(R.id.emailAddress);
		zip_code = (EditText) view.findViewById(R.id.zipcode);
		password = (EditText) view.findViewById(R.id.password);
		
		save = (Button) view.findViewById(R.id.save);
		cancel = (Button) view.findViewById(R.id.cancel);
		save.setOnClickListener(onClickListener);
		cancel.setOnClickListener(onClickListener);
		dob.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
				newFragment
						.setOnDateChangeListener(new DatePickerFragment.DateChangeListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int month, int day) {
								// TODO Auto-generated method stub
								dob.setText(year + "/" + (month + 1) + "/"
										+ day);
							}

						});
			}
		});

		setView();

		return view;
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.save:
				saveButton();
				break;
			case R.id.cancel:
				getDialog().dismiss();
				break;

			default:
				break;
			}
		}
	};

	AsyncTask<String, Void, String> task;
	private void saveButton() {
		if(checkForEmpty()){
			Toast.makeText(context, getString(R.string.All_fields_are_required), Toast.LENGTH_SHORT).show();
			return;
		}
		util.showLoading(context.getString(R.string.please_wait));
		al.clear();
		al.add(new BasicNameValuePair("avatar", fileName));
		al.add(new BasicNameValuePair("firstName", firstName.getText()
				.toString().trim()));
		al.add(new BasicNameValuePair("lastName", LastName.getText()
				.toString().trim()));
		al.add(new BasicNameValuePair("email", Email.getText().toString()
				.trim()));
		al.add(new BasicNameValuePair("passNew", password.getText().toString()
				.trim()));
		al.add(new BasicNameValuePair("dob", dob.getText().toString().trim()));
		al.add(new BasicNameValuePair("zipcode", zip_code.getText().toString()
				.trim()));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		
		task = connection.getData("user/editProfile", "POST", al, MyProfile.this, saveProfile);
	}

	public boolean checkForEmpty() {
		if (firstName.getText().toString().trim().isEmpty()
				|| LastName.getText().toString().trim().isEmpty()
				|| Email.getText().toString().trim().isEmpty()
				|| password.getText().toString().trim().isEmpty()
				|| dob.getText().toString().trim().isEmpty()
				|| zip_code.getText().toString().trim().isEmpty()) {
			return true;
		}
		return false;
	}

	List<NameValuePair> al = new ArrayList<>();

	private void setView() {
		String firstName;
		String lastLName;
		String Email;
		String passsword;
		String DOB;
		String zipCode;
		String userName;
		String userId;
		String avataar;

		firstName = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_FIRSTNAME);
		lastLName = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_LASTNAME);
		Email = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_EMAIL);
		passsword = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_PASSWORD);
		DOB = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_DOB);
		zipCode = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_ZIPCODE);
		userName = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_NAME);
		userId = (String) util.getFromSharedPreference(VariableType.STRING,
				VUtil.USER_ID);
		avataar = avtaarUrl;
		if(DOB.equalsIgnoreCase("null"))
			dob.setText("");
		else
			dob.setText(DOB);
		
		if(firstName.equalsIgnoreCase("null"))
			MyProfile.this.firstName.setText("");
		else
			MyProfile.this.firstName.setText(firstName);

		if(lastLName.equalsIgnoreCase("null"))
			MyProfile.this.LastName.setText("");
		else
			MyProfile.this.LastName.setText(lastLName);
		

		if(Email.equalsIgnoreCase("null"))
			MyProfile.this.Email.setText("");
		else
			MyProfile.this.Email.setText(Email);
		
		if(zipCode.equalsIgnoreCase("null"))
			MyProfile.this.zip_code.setText("");
		else
			MyProfile.this.zip_code.setText(zipCode);
		
		if(passsword.equalsIgnoreCase("null"))
			MyProfile.this.password.setText("");
		else
			MyProfile.this.password.setText(passsword);
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case saveProfile:
			util.hideLoading();
			onProfileSave(result);
			break;

		default:
			break;
		}
	}

	private void onProfileSave(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error = false;
		String errorMsg = "";
		String status = "";
		String message = "";
		
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			if(!error){
				message = object.getString("message");
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				
				HashMap<String, Object> map = new HashMap<>();
				map.put(VUtil.USER_FIRSTNAME, firstName.getText().toString().trim());
				map.put(VUtil.USER_LASTNAME, LastName.getText().toString().trim());
				map.put(VUtil.USER_EMAIL, Email.getText().toString().trim());
				map.put(VUtil.USER_DOB, dob.getText().toString().trim());
				map.put(VUtil.USER_PASSWORD, password.getText().toString().trim());
				map.put(VUtil.USER_ZIPCODE, zip_code.getText().toString().trim());
				map.put(VUtil.USER_AVTAAR, avtaarUrl);
				util.saveInPreference(map);
				
				getDialog().dismiss();
			}
			else
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case saveProfile:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.Unabletoupdateprofile), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

}
