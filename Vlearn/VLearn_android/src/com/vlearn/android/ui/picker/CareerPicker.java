package com.vlearn.android.ui.picker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.setting.mykid.MyKidAdapter;
import com.vlearn.android.util.VUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class CareerPicker extends Activity implements OnNetworkResult {
	
	Context context = this;
	ListView listView;
	CareerPickerAdapter adapter;
	Button closeButton;
	
	NetworkConnection connection;
	VUtil util;
	List<CareerPickerCollection> list = new ArrayList<>();
	
	ImageLoader loader;
	private AsyncTask<String, Void, String> taskCareer;
	
	public static final String CHECKTOCARRERR = "chektocareer";
	
	private String careerId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			careerId = bundle.getString(CHECKTOCARRERR);
		}else{
			careerId = "";
		}
		
		setContentView(R.layout.career_picker);
		
		closeButton = (Button) findViewById(R.id.closeButton);
		closeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bun = new Bundle();
				bun.putString("data", "usercancelled");
				intent.putExtras(bun);
				setResult(RESULT_CANCELED, intent);
				finishSelf();
			}
		});

		connection = new NetworkConnection(context);
		util = new VUtil(context);
		util.initLoader(CareerPicker.this, "Load Career");
		loader = new ImageLoader(context);

		listView = (ListView) findViewById(R.id.listView);
		list.clear();
		adapter = new CareerPickerAdapter(context, list, loader);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CareerPickerCollection col = (CareerPickerCollection) parent.getAdapter().getItem(position);
				Intent intent = new Intent();
				Bundle bun = new Bundle();
				bun.putString("id", col.id);
				bun.putString("careers_id", col.careers_id);
				bun.putString("imgUrl", col.imgUrl);
				bun.putString("name", col.name);
				intent.putExtras(bun);
				setResult(RESULT_OK, intent);
				finishSelf();
			}
		});
		
		initDataBase();
		LoadList();
	}
	
	private void LoadList(){
		///db.execSQL("CREATE TABLE IF NOT EXISTS career(id INTEGER PRIMARY KEY , careers_id TEXT, name TEXT, imgUrl TEXT);");
		
				
		try{
			cursor = database.rawQuery("SELECT * FROM career", null);
			if(cursor.getCount() == 0){
				//load from server and save to db 
				util.showLoading();
				taskCareer = connection.getData("user/getCareers", "GET", null,
						this, loadCareerPicker);
			}else{
				loadListFromDB(cursor);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void loadListFromDB(Cursor cursor) {
		// TODO Auto-generated method stub
		int i = 0;
		int length = cursor.getCount();
		if(length<=0) {
			Intent intent = new Intent();
			Bundle bun = new Bundle();
			bun.putString("data", "no list yet");
			intent.putExtras(bun);
			setResult(RESULT_CANCELED, intent);
			finishSelf();
			return;
		}
		cursor.moveToFirst();
		list.clear();
		while(i<length){
			 i++;
			 String id;
			 String careers_id;
			 String name;
			 String imgUrl;
			 id = cursor.getString(cursor.getColumnIndex("id"));
			 careers_id = cursor.getString(cursor.getColumnIndex("careers_id"));
			 name = cursor.getString(cursor.getColumnIndex("name"));
			 imgUrl = cursor.getString(cursor.getColumnIndex("imgUrl"));
			 list.add(new CareerPickerCollection(id, careers_id, name, imgUrl, careerId.equalsIgnoreCase(careers_id)));
			 cursor.moveToNext();
		}
		
		adapter.notifyDataSetChanged();
		listView.invalidate();
	}

	
	
	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;
	
	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getWritableDatabase();
	}
	
	public void loadCareer(String result) {

		JSONObject object = null;
		JSONArray array = null;

		boolean error;
		String errorMsg;
		String status;
		int length = 0;

		try {

			object = new JSONObject(result);
			error = Boolean.parseBoolean(object.getString("error"));
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			array = object.getJSONArray("careers");
			length = array.length();

			if(error){
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}

		try {

			if (length <= 0) {
				return;
			}
			object = null;
			String id;
			String title;
			String imgUrl;
			DelCareers();
			for (int i = 0; i < length; i++) {
				object = array.getJSONObject(i);

				id = object.getString("id");
				title = object.getString("career_name");
				imgUrl = object.getString("career_img");
				insertORUpdateroleCareers(i, id, title, imgUrl);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return;
		}
		
		//
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadListFromDB(database.rawQuery("SELECT * FROM career", null));
	}

	private void DelCareers() {
		// TODO Auto-generated method stub
		try {
			database.execSQL("DELETE FROM career");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void insertORUpdateroleCareers(int index, String id, String title,
			String imgUrl) {
		// TODO Auto-generated method stub
		index += 1;
		database.execSQL("INSERT INTO career (id,careers_id,name,imgUrl) VALUES ( "
				+ index + ",\"" + id + "\", \"" + title + "\", \"" + imgUrl + "\");");
	}

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadCareerPicker:
			loadCareer(result);
			break;

		default:
			break;
		}
	}
	
	public void onBackPressed() {
		super.onBackPressed();
		finishSelf();
	};
	
	private void finishSelf(){
		finish();
	
		setResult(RESULT_CANCELED, null);   
		overridePendingTransition(R.anim.slideright1, R.anim.slideright2);
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		util.hideLoading();
		switch (callingId) {
		case loadCareerPicker:
			Toast.makeText(context, getString(R.string.serverError)+" "+getString(R.string.failedToLoadCareer), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
	
	
}
