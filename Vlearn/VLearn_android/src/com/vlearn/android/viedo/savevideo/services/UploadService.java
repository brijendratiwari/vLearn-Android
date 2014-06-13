package com.vlearn.android.viedo.savevideo.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.vlearn.android.R;
import com.vlearn.android.RegistrationActivity;
import com.vlearn.android.database.VlearnDataBase;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.network.OnNetworkResult;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;

public class UploadService extends Service implements OnNetworkResult {
	public static final String INPUTDATA = "response";
	public static final String USERID = "SUCCESS";
	public static final String COLLECTIONID = "CANCEL";

	private NotificationManager mNM;
	Context context = UploadService.this;

	public static boolean isVideoUploading = false;

	NetworkConnection connection;
	AsyncTask<String, Void, String> task;

	private int NOTIFICATION = (int) new Date().getTime();

	private boolean isCareer = false;

	String id = ""; 
	String video_type = "";
	String video_user_id = "";
	String video_language = "";
	String video_title = "";
	String video_thumb = "";
	String video_file = "";
	String career_id = "";
	String tell_us = "";
	String tell_us_id = "";
	String stage = "";
	String stageid = "";
	String description = "";
	String grade = "";
	String grade_id = "";
	String subject = "";
	String subject_id = "";
	String domain = "";
	String domain_id = "";
	String standard = "";
	String standard_id = "";
	String skill = "";
	String skill_id = "";
	String video_status = "";
	String videoFileNameServer;
	String videoFilePathServer;
	String videoThumbnailPathServer;
	String videoThumnailNameServer;

	List<NameValuePair> al = new ArrayList<>();
	String dbid = "";

	public class LocalBinder extends Binder {
		public UploadService getService() {
			return UploadService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	}

	String project_type;
	String approver_id;
	boolean isKid = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		dbid = intent.getStringExtra("dbid");
		project_type = intent.getStringExtra("project_type");
		approver_id = intent.getStringExtra("approver_id");
		isKid = intent.getBooleanExtra("iskid", false);
		initDataBase();
		connection = new NetworkConnection(context);
		util = new VUtil(context);

		showNotification();
		loadData();
		return START_STICKY;
	}

	private void loadData() {
		// TODO Auto-generated method stub
		int length = 0;
		cursor = database.rawQuery("SELECT * FROM video_table WHERE id=\""
				+ dbid + "\"", null);
		if (cursor == null)
			killServiceWithoudDelete("No Entry in DataBase");
		cursor.moveToFirst();
		length = cursor.getCount();
		if (length <= 0) {
			killServiceWithoudDelete("No Entry in DataBase");
		}
		int index = 0;

		while (index < length) {
			index += 1;
			id = cursor.getString(cursor.getColumnIndex("id"));
			video_type = cursor.getString(cursor.getColumnIndex("video_type"));
			video_user_id = cursor.getString(cursor
					.getColumnIndex("video_user_id"));
			video_language = cursor.getString(cursor
					.getColumnIndex("video_language"));
			video_title = cursor
					.getString(cursor.getColumnIndex("video_title"));
			video_thumb = cursor
					.getString(cursor.getColumnIndex("video_thumb"));
			video_file = cursor.getString(cursor.getColumnIndex("video_file"));
			career_id = cursor.getString(cursor.getColumnIndex("career_id"));
			tell_us = cursor.getString(cursor.getColumnIndex("tell_us"));
			tell_us_id = cursor.getString(cursor.getColumnIndex("tell_us_id"));
			stage = cursor.getString(cursor.getColumnIndex("stage"));
			stageid = cursor.getString(cursor.getColumnIndex("stageid"));
			description = cursor
					.getString(cursor.getColumnIndex("description"));
			grade = cursor.getString(cursor.getColumnIndex("grade"));
			grade_id = cursor.getString(cursor.getColumnIndex("grade_id"));
			subject = cursor.getString(cursor.getColumnIndex("subject"));
			subject_id = cursor.getString(cursor.getColumnIndex("subject_id"));
			domain = cursor.getString(cursor.getColumnIndex("domain"));
			domain_id = cursor.getString(cursor.getColumnIndex("domain_id"));
			standard = cursor.getString(cursor.getColumnIndex("standard"));
			standard_id = cursor
					.getString(cursor.getColumnIndex("standard_id"));
			skill = cursor.getString(cursor.getColumnIndex("skill"));
			skill_id = cursor.getString(cursor.getColumnIndex("skill_id"));
			video_status = cursor.getString(cursor
					.getColumnIndex("video_status"));

			isCareer = !video_type.equalsIgnoreCase("curriculum");

			if (video_file.isEmpty()) {
				killServiceWithoudDelete("Database Error");
				return;
			}

			if (!new File(video_file).exists()) {
				killServiceWithoudDelete("Video is not exit or Deleted from sdcard.");
				return;
			}

			if (video_thumb.isEmpty()) {

				Bitmap thumb = ThumbnailUtils.createVideoThumbnail(video_file,
						MediaStore.Images.Thumbnails.MINI_KIND);

				File file = new File(Environment.getExternalStorageDirectory(),
						"Vlearn");

				file = new File(file.getAbsoluteFile() + "/img_"
						+ new Date().getTime() + ".png");
				try {
					file.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(file.getAbsoluteFile());
					thumb.compress(Bitmap.CompressFormat.PNG, 90, out);
					video_thumb = file.getAbsolutePath();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						out.close();
					} catch (Throwable ignore) {
					}
				}

			}

			if (connection.isNetworkAvailable())
				uploadVideo();
			else
				killServiceWithoudDelete("No Network");

			break;
		}
	}

	private void uploadVideo() {
		// TODO Auto-generated method stub

		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			File file = new File(video_file);
			entity.addPart("file", new FileBody(file));
			entity.addPart("fileName", new StringBody(file.getName()));
			task = connection.getData("user/uploadVideo", entity,
					UploadService.this, uploadVideo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void deleteEntry() {
		database.rawQuery("DELETE FROM video_table WHERE id=\"" + dbid + "\"",
				null);
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

	private Builder mBuilder;

	private void showNotification() {
		final CharSequence text = getText(R.string.Virtua_Dollars);

		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("video Uploading...")
				.setContentText("Uploading is in progress...").setOngoing(true)
				.setSmallIcon(R.drawable.stat_sys_upload);

		mBuilder.setAutoCancel(false);
		isVideoUploading = true;
		mBuilder.setTicker("video is uploading...");
		mBuilder.setContentText("Uploading is in progress...");
		mNM.notify(NOTIFICATION, mBuilder.build());
	}

	public void notificationMessage(String message) {
		mBuilder.setAutoCancel(true);
		mBuilder.setOngoing(false);
		mBuilder.setTicker(message);
		mBuilder.setContentText(message);
		isVideoUploading = false;
		mBuilder.setContentTitle("Vlearn Android");
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mNM.cancel(NOTIFICATION);
		mNM.notify((int) new Date().getTime(), mBuilder.build());
		stopSelf();
	}

	public void changeNotificationMessage(String message) {
		mBuilder.setAutoCancel(false);
		mBuilder.setOngoing(true);
		mBuilder.setTicker(message);
		mBuilder.setContentText(message);

		mBuilder.setContentTitle("Vlearn Android");

		mNM.notify(NOTIFICATION, mBuilder.build());

	}

	private void updateDB(String id, String video_server_id) {
		// TODO Auto-generated method stub
		database.delete("video_table", "id=" + id, null);

	}

	int isSuccess;
	boolean isReceivedData;
	private VUtil util;
	boolean isUploadingCompete = false;

	@Override
	public void onResult(String result, int callingId) {
		// TODO Auto-generated method stub
		if (result == null) {
			killServiceWithoudDelete(context.getString(R.string.serverError));
			return;
		}
		switch (callingId) {
		case uploadVideo:
			OnUploadVideo(result);
			changeNotificationMessage("Video Thumbnail is uploading...");
			break;
		case uploadVideoThumbnail:
			isUploadingCompete = true;
			changeNotificationMessage("Video is saving");
			OnUploadVideoThumbnail(result);
			break;
		case uploadCareerVideoDetails:
		case uploadCurriculumVideoDetails:
			// updateProgress(9);
			OnUploadVideoDetails(result, callingId);
			break;
		case upCategoryStatus:
			// updateProgress(10);
			updateDB(dbid, video_server_id);
			killServiceWithDelete();
			break;
		default:
			break;
		}
	}

	private String video_server_id;

	private void killServiceWithDelete() {
		// deleteEntry();
		notificationMessage("Video uploaded SuccessFull...");
	}

	private void killServiceWithoudDelete(String message) {
		notificationMessage(message);
	}

	private void OnUploadVideoDetails(String result, int callingId) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error;
		String errorMsg;
		String status;
		String message;
		String id;

		try {
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if (!error) {
				message = object.getString("message");
				status = object.getString("status");
				if (callingId == uploadCareerVideoDetails)
					id = object.getString("id");
				else
					id = object.getString("setId");
				Intent intent = new Intent(UploadBroadCast.ONVIDEOUPLOAD);
				intent.putExtra("dbid", dbid);
				intent.putExtra("id", id);
				video_server_id = id;

				UploadService.this.sendBroadcast(intent);
				if (connection.isNetworkAvailable())
					updateCategoryStatus(id);
				else
					killServiceWithoudDelete("No Network");

			} else {
				killServiceWithoudDelete(errorMsg);
			}
		} catch (JSONException e) {
			// TODO: handle exception
			killServiceWithoudDelete("Video details are not updated. Failed");
		}

	}

	private void updateCategoryStatus(String id2) {
		// TODO Auto-generated method stub
		al.clear();
		al.add(new BasicNameValuePair("user", (String) util
				.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util
				.getFromSharedPreference(VariableType.STRING,
						VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("app_type", "video"));
		al.add(new BasicNameValuePair("app_name", "vlearn"));
		al.add(new BasicNameValuePair("categoryId", id2));
		al.add(new BasicNameValuePair("status", "2"));
		task = connection.getData("user/updateCategoryStatus", "POST", al,
				UploadService.this, upCategoryStatus);
	}

	private void OnUploadVideoThumbnail(String result) {
		// TODO Auto-generated method stub
		JSONObject object = null;
		boolean error;
		String errorMsg;
		String status;

		try {
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if (!error) {
				status = object.getString("status");
				videoThumnailNameServer = object.getString("fileName");
				videoThumbnailPathServer = object.getString("fileUrl");
				if (connection.isNetworkAvailable())
					uploadVideoDetails();
				else
					killServiceWithoudDelete("No Network");

			} else {
				killServiceWithoudDelete(errorMsg);
			}
		} catch (JSONException e) {
			// TODO: handle exception
			killServiceWithoudDelete("Video Thumbnail Upload failed");
		}

	}

	private void uploadVideoDetails() {
		// TODO Auto-generated method stub

		al.clear();

		al.add(new BasicNameValuePair("user", (String) util
				.getFromSharedPreference(VariableType.STRING, VUtil.USER_NAME)));
		al.add(new BasicNameValuePair("pass", (String) util
				.getFromSharedPreference(VariableType.STRING,
						VUtil.USER_PASSWORD)));
		al.add(new BasicNameValuePair("description", "" + description));
		al.add(new BasicNameValuePair("language", video_language
				.equalsIgnoreCase("english") ? "0" : "1"));

		if (isCareer) {

			al.add(new BasicNameValuePair("aboutyou", "" + tell_us_id));
			al.add(new BasicNameValuePair("career", "" + career_id));
			al.add(new BasicNameValuePair("uid",
					(String) util.getFromSharedPreference(VariableType.STRING,
							VUtil.USER_ID)));
			al.add(new BasicNameValuePair("videofile", videoFileNameServer));
			al.add(new BasicNameValuePair("videoicon", videoThumnailNameServer));
			al.add(new BasicNameValuePair("videoname", video_title));
			al.add(new BasicNameValuePair("videostage", stageid));
			al.add(new BasicNameValuePair("videotype", "1"));
			task = connection.getData("user/addVideoCareer", "POST", al,
					UploadService.this, uploadCareerVideoDetails);

		} else {

			al.add(new BasicNameValuePair("app_type", "video"));
			al.add(new BasicNameValuePair("app_name", "vlearn"));
			al.add(new BasicNameValuePair("domainId", domain_id));
			al.add(new BasicNameValuePair("gradeId", grade_id));
			al.add(new BasicNameValuePair("name", video_title));
			al.add(new BasicNameValuePair("private", "0"));
			al.add(new BasicNameValuePair("skillId", skill_id));
			al.add(new BasicNameValuePair("stageId", stageid));
			al.add(new BasicNameValuePair("subjectId", subject_id));
			al.add(new BasicNameValuePair("thumbnail", videoThumnailNameServer));
			al.add(new BasicNameValuePair("video", video_file));
			al.add(new BasicNameValuePair("videofile", videoFileNameServer));
			if (isKid) {
				al.add(new BasicNameValuePair("project_type", project_type));
				al.add(new BasicNameValuePair("approver_id", approver_id));
			}

			task = connection.getData("user/addQuestionSet", "POST", al,
					UploadService.this, uploadCurriculumVideoDetails);
		}

	}

	private void OnUploadVideo(String result) {
		// TODO Auto-generated method stub

		JSONObject object = null;
		boolean error;
		String errorMsg;
		String status;

		try {
			object = new JSONObject(result);
			error = object.getBoolean("error");
			errorMsg = object.getString("errorMsg");
			if (!error) {
				status = object.getString("status");
				videoFileNameServer = object.getString("fileName");
				videoFilePathServer = object.getString("fileUrl");
				if (connection.isNetworkAvailable())
					uploadVideoThumbnail();
				else
					killServiceWithoudDelete("No Network");

			} else {
				killServiceWithoudDelete(errorMsg);
			}
		} catch (JSONException e) {
			// TODO: handle exception
			killServiceWithoudDelete("Video Upload failed");
		}

	}

	private void uploadVideoThumbnail() {
		// TODO Auto-generated method stub

		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			File file = new File(VUtil.getCompressFile(new File(video_thumb),
					120, 120));
			entity.addPart("file", new FileBody(file));
			entity.addPart("fileName", new StringBody(file.getName()));
			task = connection.getData("user/uploadVideoThumbnail", entity,
					UploadService.this, uploadVideoThumbnail);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			killServiceWithoudDelete("Video Thumbnail Upload failed");
		}

	}

	private VlearnDataBase helper;
	private Cursor cursor;
	private SQLiteDatabase database;

	private void initDataBase() {

		helper = new VlearnDataBase(context, VlearnDataBase.dbName, null,
				VlearnDataBase.version);
		database = helper.getWritableDatabase();
	}

	@Override
	public void onError(String result, int callingId) {
		// TODO Auto-generated method stub

		switch (callingId) {
		case uploadVideo:
			killServiceWithoudDelete(context.getString(R.string.serverError)
					+ " " + context.getString(R.string.unabletouplaodvideo));
			break;
		case uploadVideoThumbnail:
			killServiceWithoudDelete(context.getString(R.string.serverError)
					+ " "
					+ context.getString(R.string.unabletouploadVideoThumbnail));
			break;
		case uploadCareerVideoDetails:
		case uploadCurriculumVideoDetails:
			killServiceWithoudDelete(context.getString(R.string.serverError)
					+ " " + context.getString(R.string.uploadtoupdatedetails));
			break;
		case upCategoryStatus:
			killServiceWithoudDelete(context.getString(R.string.serverError)
					+ " "
					+ context.getString(R.string.unabletoupdatecategogrystatus));
			break;
		default:
			break;
		}

	}

}
