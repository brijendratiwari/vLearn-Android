package com.vlearn.android.viedo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.boxes.Container;
import com.flurry.android.FlurryAgent;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.netcompss.loader.LoadJNI;
import com.vlearn.android.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

@SuppressLint("SimpleDateFormat")
public class VideoActivity extends Activity {

	private static final String TAG = VideoActivity.class.getName();
	private boolean isRecording = false;
	// private boolean isFirstTouchOnRecording = false;
	private boolean isRecordingLimitReach = false;
	private Camera mCamera;
	private VideoPreview mPreview;
	private MediaRecorder mMediaRecorder;
	int count = 0;
	private Button pausenPlay, next, gallerychooser;
	private boolean isGalleryChooser = false;
	private int progress = 20000;
	private ProgressBar bar;
	private TextView crossBtn;
	private long videoDuration = 0;
	// private Thread thread;
	String videoName;

	private final int buttonRed = R.drawable.ma_red_record_button;
	private final int buttonBlue = R.drawable.ma_blue_record_button;

	static File mediaStorageDir;
	Handler handler = new Handler();
	private Runnable update_runnable;
	private long start_time = System.currentTimeMillis();
	protected long duration;
	private ProgressDialog progressBar;

	final String workFolder = "/sdcard";
	final String vkLogPath = workFolder + "/vk.log";

	private String _durationOfCurrent;
	private long _lastVklogSize = -1;
	private int _vkLogNoChangeCounter = 0;
	private SimpleDateFormat _simpleDateFormat;
	long _timeRef = -1;
	int _prevProgress = 0;
	LoadJNI vk;
	private String videoFilePath = "";

	private String vidoOutPath = "";

	VideoView videoView;
	MediaController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_recorder);

		initCamera();
		videoName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		count = 0;
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
				"Vlearn");
		vidoOutPath = mediaStorageDir.getAbsolutePath() + "/VID_"
				+ new Date().getTime() + ".mp4";
		pausenPlay = (Button) findViewById(R.id.pausenplayButton);
		bar = (ProgressBar) findViewById(R.id.progressBar);
		next = (Button) findViewById(R.id.next);
		gallerychooser = (Button) findViewById(R.id.galleryChooser);
		crossBtn = (TextView) findViewById(R.id.crossbtn);

		videoView = (VideoView) findViewById(R.id.videoPlayer);
		controller = new MediaController(VideoActivity.this);
		controller.setAnchorView(videoView);
		videoView.setMediaController(controller);
		videoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				goToNext();
			}
		});
		videoView.setVisibility(View.INVISIBLE);
		videoView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (isVideoPlaying)
						videoView.pause();
					else
						videoView.resume();
					isVideoPlaying = !isVideoPlaying;
				}
				return false;
			}
		});

		initProgressBar();
		// initThread();
		// thread.start();

		update_runnable = new Runnable() {
			public void run() {
				handler.postDelayed(this, 1);
				if (isRecordingLimitReach)
					return;
				if (isRecording) {
					duration = (int) ((System.currentTimeMillis() - start_time))
							+ videoDuration;

					if (duration <= progress) {
						runOnUiThread(new Runnable() {
							public void run() {
								bar.setProgress((int) duration);
							}
						});
					} else {
						recordVideo();
						isRecordingLimitReach = true;
						MergeVideos();
					}
				}

			}
		};

		handler.postDelayed(update_runnable, 1);

		crossBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(!isVideoPlaying)
					finishSelf();
				else
					goToNext();
			}
		});
		pausenPlay.setOnTouchListener(onTouchListener);
		pausenPlay.setBackgroundResource(buttonBlue);
		next.setEnabled(false);
		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!isVideochoosed) {
					isRecordingLimitReach = true;
					MergeVideos();
				} else {
					goToNext();
				}

			}
		});

		gallerychooser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vidoOutPath = mediaStorageDir.getAbsolutePath() + "/VID_"
						+ new Date().getTime() + ".mp4";
				pickAVideo();
			}
		});

	}

	boolean isVideoPlaying = false;
	boolean isVideochoosed = false;

	private void proceedToNext() {
		new AlertDialog.Builder(VideoActivity.this)
				.setTitle("" + getString(R.string.Done))
				.setMessage(
						""
								+ getString(R.string.Thevideohasbeensavedtoyourcameraroll))
				.setPositiveButton("" + getString(R.string.play),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								isVideochoosed = true;
								isVideoPlaying = true;
								videoView.setVideoPath(vidoOutPath);
								videoView.start();
								videoView.setVisibility(View.VISIBLE);
							}
						})
				.setNegativeButton("" + getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								goToNext();
							}
						}).create().show();
	}

	private void pickAVideo() {
		isGalleryChooser = true;
		Intent intent = new Intent();
		intent.setType("video/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(Intent.createChooser(intent, "Select a Video"),
				1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			Uri uri = data.getData();
			String mimeType = getContentResolver().getType(uri);
			if(!mimeType.equalsIgnoreCase("video/mp4"))
			{
				Toast.makeText(this, R.string.shouldselectmp4video, Toast.LENGTH_SHORT).show();
				return;
			}
			String[] filePathColumn = { MediaStore.Video.Media.DATA };
			Cursor cursor = getContentResolver().query(uri, filePathColumn,
					null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			
			videoFilePath = filePath;
			runTranscoding();
			next.setEnabled(true);
			// ImageFilePath = filePath;
			cursor.close();

		} else {
			Toast.makeText(getBaseContext(),
					"" + getString(R.string.Youshouldtoselectavideo),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void initProgressBar() {
		bar.setProgress(0);
	}

	private Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(Prefs.TAG, "Handler got message");
			if (progressBar != null) {
				progressBar.dismiss();

				// stopping the transcoding native
				if (msg.what == -1) {
					Log.i(Prefs.TAG, "Got cancel message, calling fexit");
					vk.fExit(getApplicationContext());
				}

				if (msg.what == 0) {
					proceedToNext();
				}

			}
		}
	};

	public void runTranscoding() {
		progressBar = new ProgressDialog(VideoActivity.this);
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setTitle("" + getString(R.string.CompressingVideo));
		// progressBar.setMessage("Press the cancel button to end the operation");
		progressBar.setMax(100);
		progressBar.setProgress(0);

		progressBar.setCancelable(false);
		/*
		 * progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * handler1.sendEmptyMessage(-1); } });
		 */
		progressBar.show();

		// progressBar = ProgressDialog.show(MainActivity.this,
		// "","Scanning Please Wait",true);
		new Thread() {
			public void run() {

				try {
					// sleep(5000);
					runTranscodingUsingLoader();
					handler1.sendEmptyMessage(0);

				} catch (Exception e) {
					Log.e("threadmessage", e.getMessage());
				}
			}
		}.start();

		// Progress update thread
		new Thread() {
			public void run() {
				Log.d(Prefs.TAG, "Progress update started");
				int i = 0;
				int progress = -1;
				try {
					while (true) {
						sleep(300);
						progress = calcProgress();
						if (progress != 0 && progress < 100) {
							progressBar.setProgress(progress);
						} else if (progress == 100) {
							Log.i(Prefs.TAG,
									"==== progress is 100, exiting Progress update thread");
							initCalcParamsForNextInter();
							break;
						}
					}

				} catch (Exception e) {
					Log.e("threadmessage", e.getMessage());
				}
			}
		}.start();
	}

	private void initCalcParamsForNextInter() {
		Log.i(Prefs.TAG, "initCalcParamsForNextInter");
		Log.i(Prefs.TAG, "Init _lastVklogSize");
		_lastVklogSize = -1;

	}

	private void runTranscodingUsingLoader() {
		Log.i(Prefs.TAG, "runTranscodingUsingLoader started...");

		_simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SS");
		try {
			Date ref = _simpleDateFormat.parse("00:00:00.00");
			ref.setYear(112);
			_timeRef = ref.getTime();
		} catch (ParseException e) {
			Log.w(Prefs.TAG, "failed to set _timeRef");
		}

		// delete previous log
		GeneralUtils.deleteFileUtil(vkLogPath);

		PowerManager powerManager = (PowerManager) VideoActivity.this
				.getSystemService(VideoActivity.this.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
		Log.d(Prefs.TAG, "Acquire wake lock");
		wakeLock.acquire();

		// String[] complexCommand = {"ffmpeg","-threads", "0", "-y" ,"-i",
		// "/sdcard/videokit/in.mp4","-strict","experimental", "-vf",
		// "crop=iw/2:ih:0:0,split[tmp],pad=2*iw[left]; [tmp]hflip[right]; [left][right] overlay=W/2",
		// "-vcodec", "mpeg4", "-vb", "20M", "-r", "23.956",
		// "/sdcard/videokit/out.mp4"};
		String commandStr = "ffmpeg -y -i "
				+ videoFilePath
				+ " -strict experimental -s 320x240 -r 30 -aspect 3:4 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 "
				+ "/" + vidoOutPath;
		// String[] complexCommand = {"ffmpeg","-threads", "0", "-y" ,"-i",
		// "/sdcard/videokit/sample.mp4","-strict","experimental", "-vf",
		// "crop=iw/2:ih:0:0,split[tmp],pad=2*iw[left]; [tmp]hflip[right]; [left][right] overlay=W/2",
		// "-vcodec", "mpeg4", "-vb", "20M", "-r", "23.956",
		// "/sdcard/videokit/out.mp4"};

		// EditText commandText = (EditText)findViewById(1);
		// String commandStr = commandText.getText().toString();

		vk = new LoadJNI();
		try {

			// vk.run(complexCommand, workFolder, getApplicationContext());
			vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder,
					getApplicationContext());

		} catch (Throwable e) {
			Log.e(Prefs.TAG, "vk run exeption.", e);
		} finally {
			if (wakeLock.isHeld())
				wakeLock.release();
			else {
				Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
			}
		}

		// finished Toast
		final String status = GeneralUtils.getReturnCodeFromLog(vkLogPath);
		VideoActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(VideoActivity.this, status, Toast.LENGTH_LONG)
						.show();
				if (status.equals("Transcoding Status: Failed")) {
					Toast.makeText(VideoActivity.this,
							"Check: " + vkLogPath + " for more information.",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private int calcProgress() {
		// Log.i(Prefs.TAG, "========calc progress=======");
		int progress = 0;
		if (_durationOfCurrent == null) {
			_durationOfCurrent = GeneralUtils
					.getDutationFromVCLogRandomAccess(vkLogPath);
			Log.i(Prefs.TAG, "Got real duration: " + _durationOfCurrent);
		}

		if (_durationOfCurrent != null) {
			long currentVkLogSize = -1;

			currentVkLogSize = GeneralUtils.getVKLogSizeRandomAccess(vkLogPath);
			Log.i(Prefs.TAG, "currentVkLogSize: " + currentVkLogSize
					+ " _lastVklogSize: " + _lastVklogSize);

			if (currentVkLogSize > _lastVklogSize) {
				_lastVklogSize = currentVkLogSize;
				_vkLogNoChangeCounter = 0;
			} else {
				Log.w(Prefs.TAG, "Looks like Vk log is not increasing in size");
				_vkLogNoChangeCounter++;
			}

			String currentTimeStr = GeneralUtils
					.readLastTimeFromVKLogUsingRandomAccess(vkLogPath);
			Log.d(Prefs.TAG, "currentTimeStr: " + currentTimeStr);
			if (currentTimeStr.equals("exit")) {
				Log.d(Prefs.TAG,
						"============Found one of the exit tokens in the log============");
				return 100;
			} else if (currentTimeStr.equals("error") && _prevProgress == 0) {
				Log.d(Prefs.TAG,
						"============Found error in the log============");
				return 100;
			} else if (_vkLogNoChangeCounter > 16) {
				Log.e(Prefs.TAG,
						"VK log is not changing in size, and no exit token found");
				return 100;
			} 
			try {
				Date durationDate = _simpleDateFormat.parse(_durationOfCurrent);
				Date currentTimeDate = _simpleDateFormat.parse(currentTimeStr);
				currentTimeDate.setYear(112);
				durationDate.setYear(112);
				// Log.d(Prefs.TAG, " durationDate: " + durationDate +
				// " currentTimeDate: " + currentTimeDate);

				long durationLong = durationDate.getTime() - _timeRef;
				long currentTimeLong = currentTimeDate.getTime() - _timeRef;
				// Log.d(Prefs.TAG, " durationLong: " + durationLong +
				// " currentTimeLong: " + currentTimeLong + " diff: " +
				// (durationLong - currentTimeLong));
				progress = Math
						.round(((float) currentTimeLong / durationLong) * 100);
				if (progress >= 100) {
					Log.w(Prefs.TAG,
							"progress is 100, but can't find exit in the log, probably fake progress, still running...");
					progress = 99;
				}
				_prevProgress = progress;

			} catch (ParseException e) {
				Log.w(Prefs.TAG, e.getMessage());
			}
		} else {
			_durationOfCurrent = "00:03:00.00";
			Log.w(Prefs.TAG, "_durationOfCurrent is null, force setting: "
					+ _durationOfCurrent);
		}
		return progress;
	}

	private void initCamera() {
		mCamera = getCameraInstance();

		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + 270) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - 270 + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);

		Camera.Parameters params = mCamera.getParameters();

		List<String> focusModes = params.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			// Autofocus mode is supported
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}

		if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
			params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

		if (params.getMaxNumMeteringAreas() > 0) { // check that metering areas
													// are supported
			List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();

			Rect areaRect1 = new Rect(-100, -100, 100, 100); // specify an area
																// in center of
																// image
			meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to
																// 60%
			Rect areaRect2 = new Rect(800, -1000, 1000, -800); // specify an
																// area in upper
																// right of
																// image
			meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to
																// 40%
			params.setMeteringAreas(meteringAreas);
		}

		// mCamera.setFaceDetectionListener(new MyFaceDetectionListener());

		mCamera.setParameters(params);
		// Create our Preview view and set it as the content of our activity.
		if (mCamera != null) {
			mPreview = new VideoPreview(this, mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);
		}
	}

	View.OnTouchListener onTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (!isRecordingLimitReach)
					recordVideo();
				break;

			default:
				break;
			}

			return false;
		}
	};

	private void MergeVideos() {
		// TODO Auto-generated method stub
		MergeVideos(true);
	}

	private void MergeVideos(boolean isNext) {
		try {
			Movie[] inMovies = new Movie[count];
			File[] toDel = new File[count];
			for (int i = 0; i < count; i++) {
				toDel[i] = new File(mediaStorageDir.getPath() + File.separator
						+ "VID_" + videoName + i + ".mp4");
				inMovies[i] = MovieCreator.build(toDel[i].getAbsolutePath()
						.toString());

			}

			List<Track> videoTracks = new LinkedList<Track>();
			List<Track> audioTracks = new LinkedList<Track>();

			for (Movie m : inMovies) {
				for (Track t : m.getTracks()) {
					if (t.getHandler().equals("soun")) {
						audioTracks.add(t);
					}
					if (t.getHandler().equals("vide")) {
						videoTracks.add(t);
					}
				}
			}

			Movie result = new Movie();

			if (audioTracks.size() > 0) {
				result.addTrack(new AppendTrack(audioTracks
						.toArray(new Track[audioTracks.size()])));
			}
			if (videoTracks.size() > 0) {
				result.addTrack(new AppendTrack(videoTracks
						.toArray(new Track[videoTracks.size()])));
			}

			Container out = new DefaultMp4Builder().build(result);

			File file = new File(mediaStorageDir, "VID_" + videoName + ".mp4");
			file.createNewFile();
			file.setWritable(true);
			file.setReadable(true);
			FileChannel fc = new RandomAccessFile(file.getAbsolutePath(), "rw")
					.getChannel();
			out.writeContainer(fc);
			fc.close();
			vidoOutPath = file.getAbsolutePath();
			for (int i = 0; i < toDel.length; i++)
				toDel[i].delete();

			if (isNext) {
				proceedToNext();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block     
			e.printStackTrace();
		}
	}

	private void recordVideo() {
		next.setEnabled(true);
		if (isRecording) {
			try {
				pausenPlay.setEnabled(false);
				Thread.sleep(1000);
				pausenPlay.setBackgroundResource(buttonBlue);
				pausenPlay.setEnabled(true);
				// stop recording and release cameras
				mMediaRecorder.stop(); // stop the recording
				videoDuration = duration;
				releaseMediaRecorder(); // release the MediaRecorder object
				mCamera.lock(); // take camera access back from MediaRecorder

				isRecording = false;
			} catch (Exception e) {
				// TODO: handle exception
				if (count != 0)
					count -= 1;
				releaseMediaRecorder(); // release the MediaRecorder object
			}
		} else {
			// initialize video camera
			if (prepareVideoRecorder()) {
				// Camera is available and unlocked, MediaRecorder is prepared,
				// now you can start recording
				pausenPlay.setBackgroundResource(buttonRed);
				mMediaRecorder.start();
				start_time = System.currentTimeMillis();
				count += 1;
				// isFirstTouchOnRecording = true;
				isRecording = true;
			} else {
				// prepare didn't work, release the camera
				releaseMediaRecorder();
				// inform user
			}
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private boolean prepareVideoRecorder() {

		mMediaRecorder = new MediaRecorder();

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_480P));

		// Step 4: Set o file
		mMediaRecorder.setOutputFile(getOutputMediaFile().toString());

		// Step 5: Set the preview output
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

		// mMediaRecorder.setCaptureRate(20);
		// mMediaRecorder.setAudioSamplingRate(20);
		mMediaRecorder.setOrientationHint(90);

		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d(TAG,
					"IllegalStateException preparing MediaRecorder: "
							+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private File getOutputMediaFile() {

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled

		// Create the storage directory if it does not exists
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Vlearn", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "VID_" + videoName + count + ".mp4");

		return mediaFile;
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurrykey));
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isGalleryChooser) {
			isGalleryChooser = false;
			return;
		} else {
			releaseMediaRecorder(); // if you are using MediaRecorder, release
									// it
									// first
			releaseCamera(); // release the camera immediately on pause event
		}
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	private void goToNext() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("videoOutPut", vidoOutPath);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finishSelf();
	}

	private void finishSelf() {
		finish();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("Error", "" + getString(R.string.cancel));
		intent.putExtras(bundle);
		setResult(RESULT_CANCELED, intent);
		overridePendingTransition(R.anim.ideal, R.anim.slideup2);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finishSelf();
	}

}
