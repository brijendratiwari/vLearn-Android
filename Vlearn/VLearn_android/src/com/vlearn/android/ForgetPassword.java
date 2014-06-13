package com.vlearn.android;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPassword extends Activity implements OnNetworkResult{

	EditText forgetPassword;
	Button okButton,cancelButton;
	VUtil util;
	NetworkConnection connection;
	InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.forget_password);
		forgetPassword 	= (EditText) findViewById(R.id.forgetPassword);
		okButton 		= (Button) findViewById(R.id.okButton);
		cancelButton 	= (Button) findViewById(R.id.cancelButton);
		
		util = new VUtil(this);
		connection = new NetworkConnection(this);
		cancelButton.setOnClickListener(onClickListener);
		okButton.setOnClickListener(onClickListener);
		
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.okButton:
				//api calling the success message then finish();
				forgetPassword();
				break;
			case R.id.cancelButton:
				finishSelf();
				break;

			default:
				break;
			}
			imm.hideSoftInputFromWindow(okButton.getWindowToken(), 0);
		}
	};

	List<NameValuePair> al = new ArrayList<>();
	AsyncTask<String, Void, String> task;
	private void forgetPassword(){
		String email = forgetPassword.getText().toString().trim();
		if(email.isEmpty()){
			Toast.makeText(this,getString(R.string.enteremail), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!VUtil.isEmailValid(email)){
			Toast.makeText(this,getString(R.string.vailedemail), Toast.LENGTH_SHORT).show();
			return;
		}
		al.clear();
		al.add(new BasicNameValuePair("email", email));
		task = connection.getData("user/forgotPassword", "POST", al, this, forgetpasswordapi);
	}
	
	
	
	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case forgetpasswordapi:
			onPasswordReset(result);
			break;

		default:
			break;
		}
	}

	private void finishSelf() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.ideal, R.anim.slideup2);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finishSelf();
	}

	private void onPasswordReset(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error;
		String errorMsg;
		try{
			
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if(error){
				Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, ""+getString(R.string.Success), Toast.LENGTH_SHORT).show();
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart()
	{
	super.onStart();
	FlurryAgent.onStartSession(this, getString(R.string.flurrykey));
	}
	@Override
	protected void onStop()
	{
	super.onStop();	
	FlurryAgent.onEndSession(this);
	}


	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case forgetpasswordapi:
			util.hideLoading();
			Toast.makeText(getBaseContext(), getString(R.string.serverError)+" "+getString(R.string.failedToResetPassword), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
	
}
