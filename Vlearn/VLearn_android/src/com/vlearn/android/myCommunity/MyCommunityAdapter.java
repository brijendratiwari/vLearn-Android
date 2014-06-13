package com.vlearn.android.myCommunity;

import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;

import android.content.Context;
import android.media.Image;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MyCommunityAdapter extends BaseAdapter {

	Context context;
	HomeActivity activity;
	List<MyCommunityCollection> list;
	LayoutInflater inflater;
	ImageLoader loader;

	public MyCommunityAdapter(Context context,
			List<MyCommunityCollection> list, ImageLoader loader) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.activity = (HomeActivity) context;
		this.inflater = LayoutInflater.from(context);
		this.loader = loader;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
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
		MyCommunityCollection col = list.get(position);
		MyCommunityHolder holder = null;

		if (view == null) {

			view = inflater.inflate(R.layout.mycommunity_row, null, false);
			holder = new MyCommunityHolder();
			holder.profilePic1 = (ImageView) view
					.findViewById(R.id.profilepic1);
			holder.profilePic2 = (ImageView) view
					.findViewById(R.id.profilepic2);
			holder.comment = (TextView) view.findViewById(R.id.comment);
			holder.description = (TextView) view.findViewById(R.id.description);
			holder.bar = (ImageView) view.findViewById(R.id.ratingbar);
			holder.description.setMovementMethod(new ScrollingMovementMethod());
			holder.description.setOnTouchListener(onTouchListener);
			view.setTag(holder);
		} else {
			holder = (MyCommunityHolder) view.getTag();
		}

		// holder.profilePic1.setImageBitmap(col.profilepic);
		// holder.profilePic2.setImageBitmap(col.VideoPic);

		holder.description.setText(col.description);
		holder.comment.setText(col.comment);
		loader.DisplayImage(col.profilepic, holder.profilePic1);
		loader.DisplayImage(col.VideoPic, holder.profilePic2);

		int starRes = 0;
		if (col.starValue == 0) {
			starRes = R.drawable.stars_0;
		} else if (col.starValue == 1) {
			starRes = R.drawable.stars_1;
		} else if (col.starValue == 2) {
			starRes = R.drawable.stars_2;
		} else if (col.starValue == 3) {
			starRes = R.drawable.stars_3;
		} else if (col.starValue == 4) {
			starRes = R.drawable.stars_4;
		} else if (col.starValue == 5) {
			starRes = R.drawable.stars_5;
		}

		holder.bar.setImageResource(starRes);

		return view;
	}

	View.OnTouchListener onTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			v.getParent().requestDisallowInterceptTouchEvent(true);

			return false;
		}
	};

	class MyCommunityHolder {
		TextView comment;
		TextView description;
		ImageView profilePic1;
		ImageView profilePic2;
		ImageView bar;
	}

}
