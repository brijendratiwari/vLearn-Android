package com.vlearn.android.viedo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import android.util.Log;

public class GeneralUtils {
	
	public static long getVKLogSizeRandomAccess(String vkLogpath) {
		RandomAccessFile f = null;
		long ret = -1;
		try {
			f = new RandomAccessFile(vkLogpath, "r");
			ret = f.length();
		} catch (FileNotFoundException e) {
			Log.w(Prefs.TAG, e.getMessage());

		} catch (IOException e) {
			Log.w(Prefs.TAG, e.getMessage());
		}
		return ret;
	}

	public static boolean checkIfFileExistAndNotEmpty(String fullFileName) {
		File f = new File(fullFileName);
		long lengthInBytes = f.length();
		Log.d(Prefs.TAG, fullFileName + " length in bytes: " + lengthInBytes);
		if (lengthInBytes > 100)
			return true;
		else {
			return false;
		}

	}

	public static void deleteFileUtil(String fullFileName) {
		File f = new File(fullFileName);
		boolean isdeleted = f.delete();
		Log.d(Prefs.TAG, "deleteing: " + fullFileName + " isdeleted: " + isdeleted);
	}

	public static String getReturnCodeFromLog(String filePath)  {
		String status = "Transcoding Status: Unknown";

		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(filePath, "r");


			long endLocation  = f.length();
			long seekLocation = -1;
			if (( seekLocation = endLocation - 100) < 0) {
				seekLocation = 0;
			}
			f.seek(seekLocation);

			String line;
			while ((line = f.readLine()) != null){ 
				if (line.startsWith("ffmpeg4android: 0")) {
					status = "Transcoding Status: Finished OK";
					break;
				}
				else if (line.startsWith("ffmpeg4android: 1") ) {
					status = "Transcoding Status: Failed";
					break;
				}
				
				else if (line.startsWith("ffmpeg4android: 2") ) {
					status = "Transcoding Status: Stopped";
					break;
				}

			}
		} catch (Exception e) {
			Log.e(Prefs.TAG, e.getMessage());
		}

		return status;
	}
	
	public static String[] utilConvertToComplex(String str) {
		 String[] complex = str.split(" ");
		 return complex;
	 }
	
	public static String getDutationFromVCLogRandomAccess(String vkLogpath) {
		String duration = null;
		try {
			RandomAccessFile f = new RandomAccessFile(vkLogpath, "r");
			String line;
			//f.seek(0);
			
			while ((line = f.readLine()) != null){ 
				//Log.d(Prefs.TAG, line);
				// Duration: 00:00:04.20, start: 0.000000, bitrate: 1601 kb/s 
				int i1 = line.indexOf("Duration:");
				int i2 = line.indexOf(", start");
				if (i1 != -1 && i2 != -1) {
					duration = line.substring(i1 + 10, i2 );
					break;
				}
			}
		} catch (FileNotFoundException e) {
			Log.w(Prefs.TAG, e.getMessage());

		} catch (IOException e) {
			Log.w(Prefs.TAG, e.getMessage());
		}
		return duration;
	}
	
	public static String readLastTimeFromVKLogUsingRandomAccess(String vkLogPath) {
		String timeStr = "00:00:00.00";
		try {
			// TODO elih 26.9.2013 changed to vk from ffmpeg log
			RandomAccessFile f = null;
			f = new RandomAccessFile(vkLogPath, "r");
			
			
			String line;
			long endLocation  = f.length();
			long seekLocation = -1;
			if (( seekLocation = endLocation - 100) < 0) {
				seekLocation = 0;
			}
			f.seek(seekLocation);
			//Log.d(Prefs.TAG, "Starting while loop seekLocation: " + seekLocation);

			while ((line = f.readLine()) != null){ 
				// old
				// frame=   26 fps=  0 q=2.0 size=      11kB time=00:00:01.73 bitrate=  50.2kbits/s dup=1 drop=17 
				
				// new
				// frame=  114 fps= 45 q=2.0 size=     190kB time=00:00:03.80 bitrate= 409.3kbits/s dup=24 drop=0    
				Log.i("line", line);
				int i1 = line.indexOf("time=");
				int i2 = line.indexOf("bitrate=");
				if (i1 != -1 && i2 != -1) {
					timeStr = line.substring(i1 + 5, i2 - 1);
				}
				else if (line.startsWith("ffmpeg4android: 0")) {
					timeStr = "exit";
				}
				else if (line.startsWith("ffmpeg4android: 1") ) {
						Log.w(Prefs.TAG, "error line: " + line);
						Log.w(Prefs.TAG, "Looks like error in the log");
						timeStr = "error";
				}
			}
		} catch (FileNotFoundException e) {
			Log.w(Prefs.TAG, e.getMessage());

		} catch (IOException e) {
			Log.w(Prefs.TAG, e.getMessage());
		}
		return timeStr.trim();
		
	}

}
