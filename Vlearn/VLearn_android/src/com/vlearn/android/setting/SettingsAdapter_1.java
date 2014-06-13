package com.vlearn.android.setting;

import java.util.Date;
import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.FileCache;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.imageload.MemoryCache;
import com.vlearn.android.myCommunity.MyCommunity;
import com.vlearn.android.videoPlayer.VideoPlayerFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsAdapter_1 extends BaseAdapter {

	Context context;
	HomeActivity activity;
	List<SettingsCollection> list;
	LayoutInflater inflater;
	FileCache fileCache;
	MemoryCache memoryCache = new MemoryCache();
	ImageLoader loader;

	int currentRunningviewId = -1;
	
	FragmentManager manager;
	FragmentTransaction transaction;
	VideoPlayerFragment fragment;
	
	public SettingsAdapter_1(Context context, List<SettingsCollection> list,
			ImageLoader loader) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.activity = (HomeActivity) context;
		this.inflater = LayoutInflater.from(context);
		this.loader = loader;
		this.manager = activity.getFragmentManager();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public SettingsCollection getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View view = convertView;
		SettingsCollection col = list.get(position);
		SettingsHolder holder = null;

		if (view == null) {

			view = inflater.inflate(R.layout.settings_row_1, null, false);
			holder = new SettingsHolder();
			holder.videoImageView = (ImageView) view.findViewById(R.id.videoPreview);
			holder.videoTitle = (TextView) view.findViewById(R.id.videoTitle); 
			holder.bar = (ImageView) view.findViewById(R.id.ratingbar);
			holder.noOfRating = (TextView) view.findViewById(R.id.noofRating);
			holder.videoDescription = (TextView) view.findViewById(R.id.description);
			holder.ratingcontainer1 = (LinearLayout) view.findViewById(R.id.ratingcontainer1);
			holder.videoDescription.setMovementMethod(new ScrollingMovementMethod());
			holder.videoDescription.setOnTouchListener(onTouchListener);
			holder.videoThumbContainer = (FrameLayout)view.findViewById(R.id.videoThumbContainer);
			int viewId = (int) new Date().getTime();
			viewId = Math.abs(viewId);
			holder.videoThumbContainer.setId(viewId);
			holder.videoThumbContainer.setOnClickListener(onClickListener);
			holder.ratingcontainer1.setOnClickListener(onClickListener1);
			view.setTag(holder);
 
		} else {

			holder = (SettingsHolder) view.getTag();

		}
		loader.DisplayImage(col.videopreview, holder.videoImageView, (int) context.getResources().getDimension(R.dimen.margintop_150), (int) context.getResources().getDimension(R.dimen.margintop_150));
		holder.videoTitle.setText(col.videoTitle);
		holder.videoDescription.setText(col.description);
		holder.noOfRating.setText("(" + col.noofrating + ")");

		int starRes = 0;
		if (col.star_no == 0) {
			starRes = R.drawable.stars_0;
		} else if (col.star_no == 1) {
			starRes = R.drawable.stars_1;
		} else if (col.star_no == 2) {
			starRes = R.drawable.stars_2;
		} else if (col.star_no == 3) {
			starRes = R.drawable.stars_3;
		} else if (col.star_no == 4) {
			starRes = R.drawable.stars_4;
		} else if (col.star_no == 5) {
			starRes = R.drawable.stars_5;
		}

		holder.bar.setImageResource(starRes);
		holder.videoThumbContainer.setTag(position);
		
		return view;
	}
	
	View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

        	TextView tv = (TextView) v;
			if(tv.getLineCount()>2)
				v.getParent().requestDisallowInterceptTouchEvent(true);

            return false;
        }
    };
	
    View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = (int) v.getTag();
			if(currentRunningviewId != position){//doPauseResume
				stopPreviousvideo();
				currentRunningviewId = position;
				SettingsCollection col = list.get(position);
				transaction = manager.beginTransaction();
				fragment = VideoPlayerFragment.Create(col.videoUrl);
				transaction.add(v.getId(), fragment); 
				transaction.commit();
			}else{
				fragment.doPauseResume();  
			}
		}
	};
	
	View.OnClickListener onClickListener1 = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			activity.changePage(MyCommunity.Create());
		
		}
	};

	
	void stopPreviousvideo() {
		if(fragment != null){
			transaction = manager.beginTransaction();
			transaction.remove(fragment);
			transaction.commit();
		}
	}

	class SettingsHolder {

		ImageView videoImageView;
		TextView videoTitle; 
		TextView videoDescription;
		TextView noOfRating;
		ImageView bar;
		FrameLayout videoThumbContainer;
		LinearLayout ratingcontainer1;

	}

}
