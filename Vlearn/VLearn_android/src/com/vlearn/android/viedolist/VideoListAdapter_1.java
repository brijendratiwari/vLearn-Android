package com.vlearn.android.viedolist;

import java.util.Date;
import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.myCommunity.MyCommunity;
import com.vlearn.android.setting.Settings;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import com.vlearn.android.videoPlayer.VideoPlayerFragment;
import com.vlearn.android.viedolist.assignchildren.AssignChildren;
import com.vlearn.android.viedolist.feedback.FeedBack;
import com.vlearn.android.viedolist.flag.FlagFragment;
import com.vlearn.android.viedolist.share.ShareVideoLink;

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

public class VideoListAdapter_1 extends BaseAdapter{

	protected int currentRunningviewId = -1;
	List<VideoListCollection> list;
	LayoutInflater inflater;
	Context context;
	HomeActivity activity;
	ImageLoader loader;
	VUtil util;
	boolean isKid = false;

	FragmentManager manager;
	FragmentTransaction transaction;
	VideoPlayerFragment fragment;
	
	public VideoListAdapter_1(List<VideoListCollection> list,Context context,ImageLoader loader) {
		// TODO Auto-generated constructor stub
		
		this.list = list;
		this.context = context;
		this.activity = (HomeActivity) this.context;
		this.inflater = LayoutInflater.from(context);
		this.loader = loader;
		this.util = activity.getVUtil();
		isKid = ((String) util.getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).equalsIgnoreCase("student");
		this.manager = activity.getFragmentManager();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public VideoListCollection getItem(int position) {
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
		VideoListHolder holder = null;
		VideoListCollection collection = list.get(position);
		
		if(view == null){
			
			holder= new VideoListHolder();
			view = inflater.inflate(R.layout.video_list_row_1, null, false);
			holder.title = (TextView) view.findViewById(R.id.videotitle);
			holder.noOfRating = (TextView) view.findViewById(R.id.startindicator);
			holder.description = (TextView) view.findViewById(R.id.videodesciption);
			holder.profileView = (ImageView) view.findViewById(R.id.profilePic);
			holder.addFav = (ImageView) view.findViewById(R.id.addfriend);
			holder.flagView = (ImageView) view.findViewById(R.id.flag);
			holder.feedbackButton = (ImageView) view.findViewById(R.id.feedbackButton);
			holder.shareButton = (ImageView) view.findViewById(R.id.shareBtn);
			holder.ratingIndicator = (ImageView) view.findViewById(R.id.ratingImageView);
			holder.ratinLayout = (LinearLayout) view.findViewById(R.id.startindicatorLayout);
			
			holder.description.setMovementMethod(new ScrollingMovementMethod());
			holder.description.setOnTouchListener(onTouchListener);
			
			holder.videoThumbContainer = (FrameLayout)view.findViewById(R.id.videoThumbContainer);
			
			holder.videoImageView = (ImageView) view.findViewById(R.id.videoImageView);
			int viewId = (int) new Date().getTime();
			viewId = Math.abs(viewId);
			holder.videoThumbContainer.setId(viewId);
			holder.videoThumbContainer.setOnClickListener(onClickListenerImageView);
			
			holder.profileView.setOnClickListener(onClickListener);
			holder.addFav.setOnClickListener(onClickListener);
			holder.flagView.setOnClickListener(onClickListener);
			holder.feedbackButton.setOnClickListener(onClickListener);
			holder.shareButton.setOnClickListener(onClickListener);
			holder.ratinLayout.setOnClickListener(onClickListener);

			if(isKid){
				holder.flagView.setVisibility(View.INVISIBLE);
				holder.addFav.setVisibility(View.INVISIBLE);
			}
			
			view.setTag(holder);
			
		}else{
			holder = (VideoListHolder) view.getTag(); 
		}
		
		holder.videoThumbContainer.setTag(position);
		holder.videoImageView.setTag(position);
		holder.profileView.setTag(position);
		holder.addFav.setTag(position);
		holder.flagView.setTag(position);
		holder.feedbackButton.setTag(position);
		holder.shareButton.setTag(position);
		holder.ratinLayout.setTag(position);

		loader.DisplayImage(collection.thumbnail, holder.videoImageView, (int) context.getResources().getDimension(R.dimen.margintop_150), (int) context.getResources().getDimension(R.dimen.margintop_150));
		loader.DisplayImage(collection.avatar, holder.profileView);
		holder.title.setText(collection.fullname);
		holder.description.setText(collection.description);

		holder.noOfRating.setText("("+collection.user_rating.length()+")");
		try{
			setRatingPhoto(holder.ratingIndicator,Integer.parseInt(collection.avg_rating));
		}catch (NumberFormatException e) {
			// TODO: handle exception
			setRatingPhoto(holder.ratingIndicator,0);
		}

		holder.videoThumbContainer.setTag(position);
		 
		if(collection.isFlagActivate){
			holder.flagView.setImageResource(R.drawable.flagactive);
		}else{
			holder.flagView.setImageResource(R.drawable.flaginactive);
		}
		
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
			VideoListCollection col = list.get(position);
			VideoList.currentPostion = position;
			VideoList.isActiviate  = col.isFlagActivate;
			VideoList.kidAssign = col.kids_assigned;
			switch (v.getId()) {
			case R.id.profilePic:
				//my community with this people
				activity.changePage(Settings.Create(col.bio, col.avatar, col.fullname, col.userId));
				break;
			case R.id.addfriend:
				activity.changePage(AssignChildren.Create(col.id));
				break;
			case R.id.flag:
				activity.changePage(FlagFragment.Create(col.id));
				break;
			case R.id.feedbackButton:
				// set feedback
				activity.changePage(FeedBack.Create(col.id,col.thumbnail));
				break;
			case R.id.shareBtn:
				// share this on social
				activity.changePage(ShareVideoLink.Create(col.hashTag, col.thumbnail,col.id));
				break;
			case R.id.startindicatorLayout:
				// my community
				activity.changePage(MyCommunity.Create());
				break;
			default:
				break;
			}
		}
	};
	
	private void setRatingPhoto(ImageView imageView, int ratinNo){
		switch(ratinNo){
		case 0:
			imageView.setImageResource(R.drawable.stars_0);
			break;
		case 1:
			imageView.setImageResource(R.drawable.stars_1);
			break;
		case 2:
			imageView.setImageResource(R.drawable.stars_2);
			break;
		case 3:
			imageView.setImageResource(R.drawable.stars_3);
			break;
		case 4:
			imageView.setImageResource(R.drawable.stars_4);
			break;
		case 5:
			imageView.setImageResource(R.drawable.stars_5);
			break;
		}
	}

	
	View.OnClickListener onClickListenerImageView = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = (int) v.getTag();
			if(currentRunningviewId != position){//doPauseResume
				stopPreviousvideo();
				currentRunningviewId = position;
				VideoListCollection col = list.get(position);
				transaction = manager.beginTransaction();
				fragment = VideoPlayerFragment.Create(col.url);
				transaction.add(v.getId(), fragment); 
				transaction.commit();
			}else{
				fragment.doPauseResume();  
			}
		}
	};


	public void stopPreviousvideo() { 
		// TODO Auto-generated method stub
		if(fragment != null){
			transaction = manager.beginTransaction();
			transaction.remove(fragment);
			transaction.commit();
		}
	}
	
	class VideoListHolder{
		ImageView videoImageView;
		ImageView profileView;
		TextView title;
		TextView noOfRating;
		TextView description;
		ImageView addFav;
		ImageView flagView;
		ImageView feedbackButton;
		ImageView shareButton;
		ImageView ratingIndicator;
		LinearLayout ratinLayout;
		FrameLayout videoThumbContainer;
	}

}
