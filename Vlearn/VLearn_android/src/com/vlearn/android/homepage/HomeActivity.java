package com.vlearn.android.homepage;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.flurry.android.FlurryAgent;
import com.vlearn.android.R;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.list.Navigationlist;
import com.vlearn.android.network.NetworkConnection;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.viedo.VideoActivity;
import com.vlearn.android.viedo.savevideo.SaveVideo;
import com.vlearn.android.viedo.savevideo.services.UploadService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	Context context;

	ImageView listButton, homeButton, videoButton;

	View headedView;
	Button backButton, forwardButton;
	TextView headerTitle;
	ImageLoader loader;

	NetworkConnection connection;
	VUtil util;

	FragmentManager manager;
	FragmentTransaction transaction;

	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.homeactivity);
		connection = new NetworkConnection(context);
		util = new VUtil(context);
		loader = new ImageLoader(context);
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}
		updateView();

		// init:Loader
		util.initLoader(HomeActivity.this);

		manager = getFragmentManager();

		headedView = findViewById(R.id.header);
		headerTitle = (TextView) findViewById(R.id.title);
		backButton = (Button) findViewById(R.id.backButton);
		forwardButton = (Button) findViewById(R.id.forwardButton);

		listButton = (ImageView) findViewById(R.id.sunButton);
		homeButton = (ImageView) findViewById(R.id.vcloudButton);
		videoButton = (ImageView) findViewById(R.id.videoButton);

		listButton.setOnClickListener(actionBarClickListener);
		homeButton.setOnClickListener(actionBarClickListener);
		videoButton.setOnClickListener(actionBarClickListener);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goBack();
			}
		});
		changePage(HomePage.Create());
	}
Fragment currentFragment;
	public void changePage(Fragment fragment) {
		// TODO Auto-generated method stub
		if(currentFragment!=null && fragment instanceof HomePage && currentFragment instanceof HomePage){
			return;
		}
		
		if(currentFragment!=null && fragment instanceof Navigationlist && currentFragment instanceof Navigationlist){
			return;
		}
		hideKeyboard();
		currentFragment = fragment;
		enableUs();
		String backStateName = fragment.getClass().getName();
		transaction = manager.beginTransaction();
		changePage(fragment, backStateName);
	}

	public void changePage(Fragment fragment, String backStateName) {
		transaction = manager.beginTransaction();

//		transaction.setCustomAnimations(R.animator.righttoleft_1,
//				R.animator.righttoleft_2, R.animator.lefttoright_1,
//				R.animator.lefttoright_2);
		
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);

		boolean fragmentPopped = manager
				.popBackStackImmediate(backStateName, 0);

		if (!fragmentPopped) { // fragment not in back stack, create it.

			transaction.replace(R.id.fragmentContainer, fragment);
			transaction.addToBackStack(backStateName);
			transaction.commit();
		}

	}

	public NetworkConnection getNetworkConnection() {
		if (connection == null) {
			connection = new NetworkConnection(context);
		}
		return connection;
	}

	public VUtil getVUtil() {
		if (util == null) {
			util = new VUtil(context);
		}
		return util;
	}

	public ImageLoader getImageLoader() {
		if (loader == null) {
			loader = new ImageLoader(context);
		}
		return loader;
	}
	
	public InputMethodManager getInputMethodManager() {
	
		return imm;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		goBack();
	}

	public void setHeader(boolean isHeader, int backButton, int forwardButton,
			String backButtonTitle, String forwardButtontitle,
			String headerTitle, View.OnClickListener listenerForForwardButton,
			int drawableLeft) {

		resetHeader();

		if (isHeader) {
			if (backButton != 0)
				this.backButton.setBackgroundResource(backButton);
			else
				this.backButton.setVisibility(View.INVISIBLE);

			if (forwardButton != 0)
				this.forwardButton.setBackgroundResource(forwardButton);
			else
				this.forwardButton.setVisibility(View.INVISIBLE);

			if (forwardButton == R.drawable.edit_button_selector_bg) {
				HomeActivity.this.forwardButton.setSelected(false);
				this.forwardButton.post(new Runnable() {
					public void run() {
						HomeActivity.this.forwardButton.setPadding(
								0,
								0,
								0,
								(int) context.getResources().getDimension(
										R.dimen.margintop_10));

					}
				});
			}

			if (backButtonTitle != null)
				this.backButton.setText(backButtonTitle);
			else
				this.backButton.setVisibility(View.INVISIBLE);

			if (forwardButtontitle != null)
				this.forwardButton.setText(forwardButtontitle);
			else
				this.forwardButton.setVisibility(View.INVISIBLE);

			if (headerTitle != null)
				this.headerTitle.setText(headerTitle);
			else
				this.headerTitle.setVisibility(View.INVISIBLE);

			if (listenerForForwardButton != null) {
				this.forwardButton.setOnClickListener(listenerForForwardButton);
			} else {
				this.forwardButton.setOnClickListener(null);
			}

			if (drawableLeft != 0) {
				this.headerTitle.setCompoundDrawablesWithIntrinsicBounds(
						drawableLeft, 0, 0, 0);
				this.headerTitle.setText("          "
						+ this.headerTitle.getText());
			} else {
				this.headerTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						0, 0);
			}

		} else {
			isHeader = false;
			headedView.setVisibility(View.GONE);
		}

	}

	private void resetHeader() {
		headedView.setVisibility(View.VISIBLE);
		backButton.setVisibility(View.VISIBLE);
		headerTitle.setVisibility(View.VISIBLE);
		forwardButton.setVisibility(View.VISIBLE);
	}

	int requestCodeForVideo = 1007;

	View.OnClickListener actionBarClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.sunButton:
				changePage(Navigationlist.Create());
				break;
			case R.id.vcloudButton:
				changePage(HomePage.Create());
				break;
			case R.id.videoButton:

				Intent i = new Intent(getBaseContext(), VideoActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(i, requestCodeForVideo);
				overridePendingTransition(R.anim.slidedwn1, R.anim.ideal);
				break;

			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == requestCodeForVideo) {
		
				Bundle bun = data.getExtras();
				String VideoPath;
				if (bun != null) {
					VideoPath = bun.getString("videoOutPut");
					changePage(SaveVideo.Create(SaveVideo.CurriculumVlearn,
							VideoPath));
				}
			
		}else{
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
	};

    public void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    public void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
	
	public void goBack() {
		// TODO Auto-generated method stub
		hideKeyboard();
		enableUs(); 
		if (manager.getBackStackEntryCount() > 1) {
			manager.popBackStack();
		} else {
			super.onBackPressed();
			finish();
		}
	}
	
	private void hideKeyboard(){
		imm.hideSoftInputFromWindow(backButton.getWindowToken(), 0);
	}

	Intent intent;

	public void doBindService(String dbid) {
		doBindService(dbid, "", "", false);

	}

	public void doBindService(String dbid, String id, String projectType,
			boolean isKid) {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).

		if (UploadService.isVideoUploading) {
			new AlertDialog.Builder(context)
					.setTitle("Alert!")
					.setMessage("Please wait for previous uploading done!")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).create().show();
			return;
		}

		Intent intent = new Intent(HomeActivity.this, UploadService.class);
		intent.putExtra("dbid", dbid);
		intent.putExtra("project_type", projectType);
		intent.putExtra("approver_id", id);
		intent.putExtra("iskid", isKid);
		startService(intent);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurrykey));
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
		FlurryAgent.onEndSession(this);
	}

	public void doUnbindService() {
		if (intent != null) {
			// Detach our existing connection.
			// unbindService(mConnection);
			stopService(intent);
			intent = null;
		}
	}
	
	public void disableUs(){
		backButton.setEnabled(false);
		forwardButton.setEnabled(false);
	}
	
	public void enableUs(){
		backButton.setEnabled(true);
		forwardButton.setEnabled(true);
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			updateView();
		}
	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
		//login
			util.saveInPreference(VUtil.PREF_KEY_FACEBOOK_LOGIN, true);
			util.saveInPreference(VUtil.PREF_KEY_ACCESSTOKEN, session.getAccessToken());
		} else {
			//logout
			util.saveInPreference(VUtil.PREF_KEY_FACEBOOK_LOGIN, false);
		}
	}
	
	
}
