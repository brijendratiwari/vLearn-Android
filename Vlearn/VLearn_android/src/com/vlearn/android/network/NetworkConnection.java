package com.vlearn.android.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vlearn.android.R;
import com.vlearn.android.util.VUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class NetworkConnection {

	private String url = "http://app.familyplaza.us/index.php/";
//	private String url = "http://192.168.10.126/iphone_project/index.php/";
	private String apiUrl = "";
	OnNetworkResult resultListener;
	Context context;
	int callingId;
	
	public NetworkConnection(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public AsyncTask<String, Void, String> getData(String urlSuffix,HttpEntity entity,OnNetworkResult resultListener,int callingId){
		if(isNetworkAvailable()){
			this.apiUrl = url+urlSuffix;
			this.resultListener = resultListener;
			return new GetDataFromServer(entity,callingId).execute(apiUrl);
		}else{
			Toast.makeText(context, context.getString(R.string.NO_NETWORK_), Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	
	public AsyncTask<String, Void, String> getData(String urlSuffix,String method,List<NameValuePair> al,OnNetworkResult resultListener,int callingId){
		if(isNetworkAvailable()){
			this.apiUrl = url+urlSuffix;
			this.resultListener = resultListener;
			return new GetDataFromServer(al,callingId).execute(apiUrl,method);
		}else{
			Toast.makeText(context, context.getString(R.string.NO_NETWORK_), Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	
	public AsyncTask<String, Void, String> getData(String urlSuffix,String method,List<NameValuePair> al,OnNetworkResult resultListener,int callingId,boolean ISJSON){
		if(isNetworkAvailable()){
			this.apiUrl = url+urlSuffix;
			this.resultListener = resultListener;
			return new GetDataFromServer(al,callingId,ISJSON).execute(apiUrl,method);
		}else{
			Toast.makeText(context, context.getString(R.string.NO_NETWORK_), Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public String connect(String url, String type,
			List<NameValuePair> al,boolean ISString) {

		URL mUrl;
		String mParameters;
		InputStream mStream;
		byte[] mData;
		String mResult = null;
		HttpURLConnection mConnection = null;
		if(al == null) al = new ArrayList<NameValuePair>();
//		 String[] srr = HttpUtility.sendHttpRequest(apiUrl + "import_file/", POST, data);
		
		
		try{
			mParameters = URLEncodedUtils.format(al, HTTP.UTF_8);
			if(type.equals("GET")){
				mUrl = new URL(url+"?"+mParameters);
				mConnection = (HttpURLConnection) mUrl.openConnection();
				mConnection.setDoOutput(false);
	            mConnection.setUseCaches(false);
	            mConnection.setRequestMethod("GET"); 
	            mConnection.setRequestProperty("Content-Type",
		                    "application/x-www-form-urlencoded;charset=UTF-8");
			}else if(type.equals("POST")){
		
				mUrl = new URL(url);
				mConnection = (HttpURLConnection) mUrl.openConnection();
//				mConnection.setRequestProperty("Accept-Charset", mParameters);
				mConnection.setDoOutput(true);
				mConnection.setDoInput(true);
	            mConnection.setUseCaches(false);
	            mConnection.setRequestMethod("POST");
	            mConnection.addRequestProperty("Accept", "*/*");
	            mConnection.addRequestProperty("Pragma", "no-cache");
	            mConnection.addRequestProperty("Connection", "Keep-Alive");
	            mConnection.addRequestProperty("Cache-Control", "no-cache");
	            
	            mConnection.setRequestProperty("Content-Type",
		                    "application/x-www-form-urlencoded;charset=UTF-8");
	            mData = mParameters.getBytes();
//	            mConnection.setFixedLengthStreamingMode(mData.length);

				OutputStream os = mConnection.getOutputStream();
//				os.write(mData);
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8"));
				if(ISString)
					writer.write(getQuery(al,""));
				else
					writer.write(getQuery(al));
					
				writer.flush();
				writer.close();
				os.close();
				
			}
			
			//mConnection.connect();
			
			int status = mConnection.getResponseCode();
			if(status == 200 || status == 201){
				mStream = new BufferedInputStream(mConnection.getInputStream());
				String result = convertStreamToString(mStream);
				mResult = result ;
				mStream.close();
			}else{
				
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}finally {
            if (mConnection != null) {
                mConnection.disconnect();
            }
		}
		
		
		return mResult;
	}

	public String connect(String url,HttpEntity reqEntity){
		String mResult = null;
		HttpClient httpClient = new DefaultHttpClient();
	    HttpContext localContext = new BasicHttpContext();
	    HttpPost httpPost = new HttpPost(url);

	    try {

	    	httpPost.setEntity(reqEntity);

	        HttpResponse response = httpClient.execute(httpPost, localContext);
			
			InputStream mStream = new BufferedInputStream(response.getEntity().getContent());
			String result = convertStreamToString(mStream);
			mResult = result;
			mStream.close();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return mResult;
	}
	
	private String getQuery(List<NameValuePair> params,String test) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
	
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{

		JSONObject object = new JSONObject();
		try{
		for (NameValuePair pair : params){
			if(pair.getValue().startsWith("[")){
				object.put(pair.getName(), new JSONArray(pair.getValue()));
			}else
			object.put(pair.getName(), pair.getValue());
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	    return object.toString();
	}
	
	private String convertStreamToString(InputStream is) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	class GetDataFromServer extends AsyncTask<String, Void, String>{
		List<NameValuePair> al;
		int callingId;
		HttpEntity entity;
		boolean isMultipart = false;
		boolean ISString = true;
		
		public GetDataFromServer(HttpEntity entity,int callingId) {
			// TODO Auto-generated constructor stub
			this.entity = entity;
			this.callingId = callingId;
			this.isMultipart = true;
			this.ISString = false;
		}
		
		public GetDataFromServer(List<NameValuePair> al,int callingId) {
			// TODO Auto-generated constructor stub
			this.callingId = callingId;
			this.al = al;
			this.ISString = false;
			this.isMultipart = false;
		}
		
		public GetDataFromServer(List<NameValuePair> al,int callingId,boolean ISString) {
			// TODO Auto-generated constructor stub
			this.al = al;
			this.callingId = callingId;
			this.ISString = ISString;
			this.isMultipart = false;
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(isMultipart)
				return connect(params[0], entity);
			else
				return connect(params[0], params[1], al, ISString);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			isMultipart = false;
			ISString = false;
			if(result == null){ 
				if(resultListener != null) resultListener.onError(context.getString(R.string.serverError),callingId);
				return;
			}
			if(result.startsWith("<div")){ 
				if(resultListener != null) resultListener.onError(result,callingId);
			}else{
				if(resultListener != null) resultListener.onResult(result,callingId);
			}
			
		}
		
	}
	
	public void NoNetworkAvailable(Context context) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context).setTitle("Error").setMessage(context.getString(R.string.NO_NETWORK_)).setPositiveButton("OK",null).create().show();
	}
	
}