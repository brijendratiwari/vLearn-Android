package com.vlearn.android.viedolist.share;

import java.util.HashMap;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;


public class ShareVideoLink extends Fragment {

	public static ShareVideoLink Create(String hashTag, String videoThumb,String videoId) {
		ShareVideoLink ShareVideoLink = new ShareVideoLink();
		Bundle bun = new Bundle();
		bun.putString("thumbUrl", videoThumb);
		bun.putString("videoId", videoId);
		bun.putString("tags", hashTag);
 
		ShareVideoLink.setArguments(bun);
		return ShareVideoLink;
	}
	
	String videoUrlPrefix = "http://dev.plazafamilia.com/videos/play_video/";
	HomeActivity activity;
	Context context;
	ImageLoader loader;
	VUtil util;
	NetworkConnection connection;

	
	boolean doSwitch = true;
	
	Switch facebookSwitch,twitterSwitch;
	Button shareButton;
	EditText messageView;
	TextView hashTag;
	ImageView videoThumb;
	//http://vlearnandroidapp.com
//	static String TWITTER_CONSUMER_KEY = "buNgQoqEdlHYsHryuh0V6VllG";
//    static String TWITTER_CONSUMER_SECRET = "6fWpIgHk5cp5jqxIKnRzNJrRQa1crnWfu6H2BGZqCBrQcy8cmK";
    
    static String TWITTER_CONSUMER_KEY = "PTUIDoZ0PnWtuTZtkYPxe468s";
    static String TWITTER_CONSUMER_SECRET = "1K7fINUyWTJ7wjjdNgRrCPb1R5Hg0nyLFtsOwx7AsvmKFZQBRz";
	
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
   
 
    static final String TWITTER_CALLBACK_URL = "outh://vlearnandroidapp.com";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    
	String thumbUrl = "",tags = "";
	
	 // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = activity = (HomeActivity) getActivity();
		util = activity.getVUtil();
		loader = activity.getImageLoader();
		connection = activity.getNetworkConnection();
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Bundle bundle = getArguments();
		if(bundle!=null){
			if(bundle.containsKey("tags")) tags = bundle.getString("tags");
			if(bundle.containsKey("thumbUrl")) thumbUrl = bundle.getString("thumbUrl");
			if(bundle.containsKey("videoId"))videoUrlPrefix+=bundle.getString("videoId");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.share_video_link, null, false);
		
		facebookSwitch = (Switch) view.findViewById(R.id.facebookSwitch);
		twitterSwitch = (Switch) view.findViewById(R.id.twitterSwitch);
		
		checkForTwitter();
		cheeckForFacebook();
		
		facebookSwitch.setOnCheckedChangeListener(listener);
		twitterSwitch.setOnCheckedChangeListener(listener);
		
		shareButton = (Button) view.findViewById(R.id.shareButton);
		shareButton.setOnClickListener(onClickListener);
		
		messageView = (EditText) view.findViewById(R.id.messageView);
		
		hashTag = (TextView) view.findViewById(R.id.hashTag);
		hashTag.setText(tags);
		messageView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.getParent().requestDisallowInterceptTouchEvent(true);
				
				return false;
			}
		});
		videoThumb = (ImageView) view.findViewById(R.id.videoThumb);
		loader.DisplayImage(thumbUrl, videoThumb);
		return view;
	}
	
	OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			boolean iscon = connection.isNetworkAvailable();
			if(!iscon){
				if(!iscon) Toast.makeText(context, ""+context.getString(R.string.pleasecheckyournetworkconnection), Toast.LENGTH_SHORT).show();
				doSwitch = true;
				return;
			}
			
			switch (buttonView.getId()) {
			case R.id.facebookSwitch:
				if(isChecked){
					//facebook login save to prefrence
					loginToFaceboook();
				}else{
					// do expire session
					logoutFromFacebook();
				}
				break;
			case R.id.twitterSwitch:
				if(isChecked){
					//twitter login save to prefrence
					loginOnTwitter();
				}else{
					// do expire session 
					logoutFromTwitter();
				}
				break;

			default:
				break;
			}
		}

	};
	
	private void loginOnTwitter() {
		// TODO Auto-generated method stub
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        //builder.setDebugEnabled(true);
        Configuration configuration = builder.build();
        
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        
        try {
            requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
            Intent intent = new Intent(context,TwitterWebviewActivity.class);
            intent.putExtra("url", Uri.parse(requestToken.getAuthenticationURL()).toString());
            intent.putExtra("callback", TWITTER_CALLBACK_URL);
            //activity.startActivity(intent);
            startActivityForResult(intent, 1356);
        } catch (TwitterException e) {
        	Toast.makeText(context, ""+context.getString(R.string.unabletologinontwitter), Toast.LENGTH_SHORT).show();
        	new AlertDialog.Builder(context).setTitle(""+context.getString(R.string.unabletologinontwitter)).setMessage(context.getString(R.string.twittersolution)).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					twitterSwitch.setChecked(false);
				}
			}).show();
            e.printStackTrace();
        }
	}
	
	protected void logoutFromFacebook() {
		// TODO Auto-generated method stub
		activity.onClickLogout();
		util.saveInPreference(VUtil.PREF_KEY_FACEBOOK_LOGIN, false);
	}

	protected void loginToFaceboook() {
		// TODO Auto-generated method stub
		activity.onClickLogin();
		util.saveInPreference(VUtil.PREF_KEY_FACEBOOK_LOGIN, true);
	}

	private void logoutFromTwitter(){
		twitter.setOAuthAccessToken(null);
        util.saveInPreference(PREF_KEY_TWITTER_LOGIN, false);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == Activity.RESULT_OK && requestCode == 1356){
			
				checkForTwitterLogin(data.getData());

		}else{
//		    Session.getActiveSession().onActivityResult(activity, requestCode, resultCode, data);
		}
	};
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			share();
		}

	};

	
	private void share() {
		// TODO Auto-generated method stub
		String messageView = ShareVideoLink.this.messageView.getText().toString();
		if(twitterSwitch.isChecked()){
			shareOnTwitter(messageView);
		}
		if(facebookSwitch.isChecked()){
			sharOnFacebook();
		}
		activity.goBack();
	}
	
	private void sharOnFacebook() {
		// TODO Auto-generated method stub
		Bundle params = new Bundle();
		params.putString("caption", tags);
		params.putString("description", ""+messageView.getText().toString().trim());
		params.putString("name", "Vlearn");
		params.putString("link", videoUrlPrefix);
		params.putString("picture", thumbUrl);

		Request request = new Request(Session.getActiveSession(), "me/feed", params, HttpMethod.POST);
		request.setCallback(new Request.Callback() {
		    @Override
		    public void onCompleted(Response response) {
		        if (response.getError() == null) {
		            // Tell the user success!
		        	Toast.makeText(context, ""+context.getString(R.string.facebooksuccess), Toast.LENGTH_SHORT).show();
		        }else{
		        	Toast.makeText(context, ""+context.getString(R.string.facebookerror), Toast.LENGTH_SHORT).show();
		        }
		    }
		});
		request.executeAsync();
	}

	private void shareOnTwitter(String message) {
		// TODO Auto-generated method stub
		message+=" "+tags+" "+videoUrlPrefix;
		try {
			Status status = twitter.updateStatus(message);
			Toast.makeText(context, ""+context.getString(R.string.twitterSuccess), Toast.LENGTH_SHORT).show();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, ""+context.getString(R.string.twitterError)+" "+e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activity.setHeader(true, R.drawable.button_background_round_blue, 0, context.getString(R.string.back), null, "Share", null, 0);
	}

	private void cheeckForFacebook() {
		facebookSwitch.setChecked(isFacebookLoggedInAlready());
	}

	private void checkForTwitter() {
		// TODO Auto-generated method stub
		doSwitch = false;
        twitterSwitch.setChecked(isTwitterLoggedInAlready());
	}

	private void checkForTwitterLogin(Uri uri) {
		// TODO Auto-generated method stub
		 /** This if conditions is tested once is
         * redirected from twitter page. Parse the uri to get oAuth
         * Verifier
         * */
        
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
 
                try {
                    // Get the access token
                    AccessToken accessToken = twitter.getOAuthAccessToken(
                            requestToken, verifier);
 
                   HashMap<String, Object> map = new HashMap<>();
                   map.put(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                   map.put(PREF_KEY_OAUTH_SECRET,
                            accessToken.getTokenSecret());
                    // Store login status - true
                   map.put(PREF_KEY_TWITTER_LOGIN, true);
                   
                   doSwitch = false;
				   twitterSwitch.setChecked(true);
					
                   
                   util.saveInPreference(map);
 
                    Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                }
            }
       
	}

	private boolean isFacebookLoggedInAlready() {
		// TODO Auto-generated method stub
		boolean bool = (boolean) util.getFromSharedPreference(VariableType.BOOLEAN, VUtil.PREF_KEY_FACEBOOK_LOGIN);
		
		return bool;
	}
	
	private boolean isTwitterLoggedInAlready() {
		// TODO Auto-generated method stub
		boolean bool = (boolean) util.getFromSharedPreference(VariableType.BOOLEAN, PREF_KEY_TWITTER_LOGIN);
		if(bool){
		 try {

		        ConfigurationBuilder confbuilder  = new ConfigurationBuilder();
		        confbuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY) 
		        .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET) 
		        .setOAuthAccessToken((String) util.getFromSharedPreference(VariableType.STRING, PREF_KEY_OAUTH_TOKEN)) 
		        .setOAuthAccessTokenSecret((String) util.getFromSharedPreference(VariableType.STRING, PREF_KEY_OAUTH_SECRET)); 
		        twitter = new TwitterFactory(confbuilder.build()).getInstance(); 

		        //Status status = twitter.updateStatus("Working lunch today");
		        User user = twitter.verifyCredentials();
		        doSwitch = false;
				   twitterSwitch.setChecked(true);
				   util.saveInPreference(PREF_KEY_TWITTER_LOGIN, true);
		    } catch (TwitterException e) {
		    	bool = false;
		    	doSwitch = false;
				   twitterSwitch.setChecked(false);
				   util.saveInPreference(PREF_KEY_TWITTER_LOGIN, false);
		    }
		}
		return bool;
	}
}
