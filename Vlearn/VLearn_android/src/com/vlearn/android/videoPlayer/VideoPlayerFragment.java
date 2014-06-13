package com.vlearn.android.videoPlayer;

import java.io.IOException;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class VideoPlayerFragment extends Fragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, VideoControllerView.MediaPlayerControl {

    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;
    String videoUrl;
    ProgressBar progressBar;
    
    HomeActivity activity;
    Context context;
    public static VideoPlayerFragment Create(String videoUrl){
    	VideoPlayerFragment fragment = new VideoPlayerFragment();
    	Bundle bun = new Bundle();
    	bun.putString("videoUrl", videoUrl);
    	fragment.setArguments(bun);
    	return fragment;
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = activity = (HomeActivity) getActivity();
        Bundle bun = getArguments();
        if(bun != null){
        	if(bun.containsKey("videoUrl")) videoUrl = bun.getString("videoUrl");
        }
        
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	 view = inflater.inflate(R.layout.activity_video_player, null, false);
    	
    	  videoSurface = (SurfaceView) view.findViewById(R.id.videoSurface);
          progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
          SurfaceHolder videoHolder = videoSurface.getHolder();
          videoHolder.addCallback(this);

          player = new MediaPlayer();
          controller = new VideoControllerView(activity);

         
          try {
//        	  Toast.makeText(context, videoUrl, Toast.LENGTH_SHORT).show();
              player.setAudioStreamType(AudioManager.STREAM_MUSIC);
              player.setOnInfoListener(this);
              player.setOnBufferingUpdateListener(this);
              player.setDataSource(context, Uri.parse(videoUrl));
              player.setOnPreparedListener(this);
          } catch (IllegalArgumentException e) {
              e.printStackTrace();
          } catch (SecurityException e) {
              e.printStackTrace();
          } catch (IllegalStateException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
          
          view.setOnTouchListener(onTouchListener);
          
    	return view;
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			controller.show();
	        return false;
		}
	};
    
	public void doPauseResume(){
		if(controller!=null)
			controller.doPauseResume();
	}
	
    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
    	progressBar.setVisibility(View.INVISIBLE);
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) view.findViewById(R.id.videoSurfaceContainer));
        player.start();
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
    	try{
    		return player.getCurrentPosition();
    	}catch (Exception e) {
			// TODO: handle exception
        	return 0;
		}
    }

    @Override
    public int getDuration() {
    	try{
        return player.getDuration();
    	}catch (Exception e) {
			// TODO: handle exception
    		return 0;
		}
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
    	progressBar.setVisibility(View.INVISIBLE);
    	player.start();
    }

    
    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
    	
    }
    // End VideoMediaController.MediaPlayerControl
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	player.release();
    }
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
			progressBar.setVisibility(View.VISIBLE);
		}else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
			progressBar.setVisibility(View.INVISIBLE);
		}else if(what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING ){
			new AlertDialog.Builder(context).setTitle("Alert!").setMessage("sorry can't play video").setPositiveButton("OK", null).create().show();
		}else if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ){
			progressBar.setVisibility(View.INVISIBLE);
		}
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		controller.setSecondaryProgress(percent);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mp.seekTo(0);
		controller.setseek(0);
		mp.stop();
	}
	
}
