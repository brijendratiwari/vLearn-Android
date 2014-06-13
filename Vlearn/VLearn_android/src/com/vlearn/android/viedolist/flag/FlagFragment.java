package com.vlearn.android.viedolist.flag;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import com.vlearn.android.viedolist.VideoList;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FlagFragment extends Fragment implements OnNetworkResult {

	HomeActivity activity;
	Context context;
	CheckedTextView[] textviews;
	LayoutInflater inflater;
	
	Button cancel,Submit;
	
	NetworkConnection connection;
	VUtil util;
	LinearLayout linearLayout;
	
	AsyncTask<String, Void, String> task;
	
	String videoid;
	
	public static FlagFragment Create(String videoid){
		FlagFragment flagFragment = new FlagFragment();
		Bundle bun = new Bundle();
		bun.putString("videoid", videoid);
		flagFragment.setArguments(bun);
		return flagFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = (HomeActivity) (context = getActivity());
		videoid = getArguments().getString("videoid");
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.flag_layout, null, false);
		
		linearLayout = (LinearLayout) view.findViewById(R.id.container);
		
		Submit = (Button) view.findViewById(R.id.sumbit);
		cancel = (Button) view.findViewById(R.id.cancel);
		
		Submit.setOnClickListener(clickListenerAction);
		cancel.setOnClickListener(clickListenerAction);
		loadCheckedView();
		return view;
	}
	
	View.OnClickListener clickListenerAction = new View.OnClickListener() {
        public void onClick(View v)
        {
        	switch (v.getId()) {
	    		case R.id.sumbit:
	    			SubmitFlagDetails(videoid);
	    			break;
	    		case R.id.cancel:
	    			activity.goBack();
	    			break;
    		default:
    			break;
    		}
        }

    };
	
    private void SubmitFlagDetails(String id) {
		// TODO Auto-generated method stub
    	al.clear();
		util.showLoading(""+context.getString(R.string.please_wait));
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("feedback", ""));
		al.add(new BasicNameValuePair("categoryId", id));
		boolean isDone = false;
		for(int i=0;i<textviews.length;i++){
			String status =  textviews[i].isChecked()?"on":"off";
			al.add(new BasicNameValuePair((String) textviews[i].getTag(), status));
			if(!isDone){
				if(status.equalsIgnoreCase("on")){
					VideoList.isActiviate = true;
					isDone = true;
				}else{
					VideoList.isActiviate = false;
				}
			}
		}
		
		task = connection.getData("user/flagCategoryStatus", "POST", al, FlagFragment.this, submitFlag);
	}
    
	List<NameValuePair> al = new ArrayList<>();
	private void loadCheckedView(){
		
		al.clear();
		util.showLoading(""+context.getString(R.string.please_wait));
		task = connection.getData("user/getParams/flag_options", "GET", al, FlagFragment.this, loadFlag);
		
	}
	
	View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v)
        {
            ((CheckedTextView) v).toggle();
        }
    };
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onResult(final String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadFlag:
			new Handler().post(new Runnable() {
				public void run() {
					onCheckedViewLoad(result);
				}
			});
			
			break;
		case submitFlag:
			onFlagSubmit(result);
			break;
		default:
			break;
		}
	}

	private void onFlagSubmit(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error = false;
		String errorMsg = "";
		String status = "";
		
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("errorMsg");
			if(!error){
				
				Toast.makeText(context, ""+context.getString(R.string.flagupdate), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		activity.goBack();
	}

	private void onCheckedViewLoad(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		JSONArray jsonArray = null;
		boolean error = false;
		String errorMsg = "";
		String id = "";
		String name="";
		int length = 0;
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if(!error){
				jsonArray = object.getJSONArray("data");
				length = jsonArray.length();
				if(length<=0) return;
				textviews = new CheckedTextView[length];
				for(int i=0;i<length;i++){
					object = jsonArray.getJSONObject(i);
					id = object.getString("id");
					name = object.getString("name");
					
					textviews[i] = (CheckedTextView) inflater.inflate(R.layout.flag_layout_row, null, false);
					textviews[i].setTag(id);
					textviews[i].setText(name);
					textviews[i].setOnClickListener(clickListener);
					linearLayout.addView(textviews[i],(i+1));
					linearLayout.invalidate();
				}
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}
			
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadFlag:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToLoadAvailFlag), Toast.LENGTH_SHORT).show();
			break;
		case submitFlag:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.failedToSubmitFlag), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		
	}
	
}
