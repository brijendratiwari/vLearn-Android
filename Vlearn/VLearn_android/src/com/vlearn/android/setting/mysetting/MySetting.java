package com.vlearn.android.setting.mysetting;

import java.io.File;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

public class MySetting extends Fragment implements OnNetworkResult{

	Context context;
	HomeActivity activity;
	TextView inviteText,inviteEmail,termsOfService,privacyPolicy;
	Switch fallow_editor_picks;
	VUtil util;
	NetworkConnection connection;
	ImageView profilePic;
	ImageLoader loader;
	int GALLERY_PICK_PHOTO = 100;
	
	
	public static MySetting Create() {
		MySetting mySetting = new MySetting();
		return mySetting;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		util = activity.getVUtil();
		connection = activity.getNetworkConnection();
		fileUrl = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_AVTAAR);
		loader = new ImageLoader(context);
		MyProfile profile = MyProfile.Create(fileName,fileUrl);
		profile.show(getFragmentManager(), "my profile");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.my_settings, null, false);
		inviteText = (TextView) view.findViewById(R.id.inviteText);
		inviteEmail = (TextView) view.findViewById(R.id.inviteEmail);
		termsOfService = (TextView) view.findViewById(R.id.termsservice);
		privacyPolicy = (TextView) view.findViewById(R.id.privacypolicy);
		fallow_editor_picks = (Switch) view.findViewById(R.id.fallow_editor_picks);
		profilePic = (ImageView)view.findViewById(R.id.profilePic);
		profilePic.setOnClickListener(clickListener);
		loader.DisplayImage(fileUrl, profilePic);
		
		return view;
	}

	
	View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//ImageView imageView = (ImageView) v;
			switch (v.getId()) {
			case R.id.profilePic:
				Intent intent1 = new Intent();
				intent1.setType("image/*");
				if (Build.VERSION.SDK_INT < 19) {
					intent1.setAction(Intent.ACTION_GET_CONTENT);
				} else {
					intent1.addCategory(Intent.CATEGORY_OPENABLE);
				}

				startActivityForResult(
						Intent.createChooser(intent1, "Choose Pictures..."),
						GALLERY_PICK_PHOTO);
				break;

			default:
				break;
			}
		}
	};
	private String ImageFilePath;
	
	@SuppressLint("NewApi")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == GALLERY_PICK_PHOTO){
			Uri originalUri = null;

			if (Build.VERSION.SDK_INT < 19) {
				originalUri = data.getData();
			} else {
				originalUri = data.getData();
				final int takeFlags = data.getFlags()
						& (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				// Check for the freshest data.
				context.getContentResolver().takePersistableUriPermission(
						originalUri, takeFlags);

			}
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = context.getContentResolver().query(originalUri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			ImageFilePath = filePath;
			cursor.close();
			if(!connection.isNetworkAvailable()){
				Toast.makeText(context, context.getString(R.string.NO_NETWORK_), Toast.LENGTH_SHORT).show();
				return;
			}
			uploadPhoto();
			profilePic.setImageBitmap(VUtil.decodeFile(new File(filePath),
					70, 70));
		}
	};
	
	private void uploadPhoto() {
			
		String roletitle = (String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ROLE); 
		
		if (roletitle.toLowerCase().contains("teacher")) {
			roletitle = "teacher";
		} else if (roletitle.toLowerCase().contains("padrino")) {
			roletitle = "padrino";
		} else {
			roletitle = "parent";
		}
		
		util.showLoading();
		
		try{
		 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
         File file = new File (VUtil.getCompressFile(new File(ImageFilePath), 120, 120));
         entity.addPart("file", new FileBody(file));
         entity.addPart("access", new StringBody(roletitle));
         connection.getData("user/uploadProfilePhoto", entity, MySetting.this, uploadPhotoSetting);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		 		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		activity.setHeader(true, R.drawable.button_selector_round_blue, R.drawable.button_selector_round_green, context.getString(R.string.back), context.getString(R.string.My_Profile), context.getString(R.string.mysetting), forward, 0);
		
	}

	View.OnClickListener forward = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MyProfile profile = MyProfile.Create(fileName,fileUrl);
			profile.show(getFragmentManager(), "my profile");
		}
	};
	private String fileUrl;
	private String fileName;


	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case uploadPhotoSetting:
			onPhotoUpload(result);
			break;

		default:
			break;
		}
	}

	private void onPhotoUpload(String result) {
		// TODO Auto-generated method stub
		util.hideLoading();
		JSONObject object = null;
		boolean error;
		String errorMsg;
		String status;
		
		try{
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			status = object.getString("status");
			if(!error){
				fileName = object.getString("fileName");
				fileUrl = object.getString("fileUrl");
				util.saveInPreference(VUtil.USER_AVTAAR, fileUrl);
			}else{
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub
		switch (callingId) {
		case uploadPhotoSetting:
			Toast.makeText(context, activity.getString(R.string.serverError)+" "+activity.getString(R.string.unabletochangeprofilepic), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
	
}
