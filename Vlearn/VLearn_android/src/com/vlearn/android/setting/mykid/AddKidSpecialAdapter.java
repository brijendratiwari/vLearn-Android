package com.vlearn.android.setting.mykid;

import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.util.VUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddKidSpecialAdapter extends BaseAdapter{

	List<AddKidSpecialCollection> list;
	LayoutInflater inflater;
	Context context;
	ImageLoader loader;

	public AddKidSpecialAdapter(Context context,List<AddKidSpecialCollection> list,ImageLoader loader) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
		this.loader = loader;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public AddKidSpecialCollection getItem(int position) {
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
		
		AddKidSpecialCollection col = list.get(position);
		AddKidSpecialHolder holder = null;
		View view = convertView;
		
		if(view == null){
			
			view = inflater.inflate(R.layout.add_kid_special_row, null, false);
			holder = new AddKidSpecialHolder();
			holder.date = (TextView) view.findViewById(R.id.date);
			holder.dollar= (TextView) view.findViewById(R.id.dollarSysm);
			holder.grade= (TextView) view.findViewById(R.id.grade);
			holder.imageView = (ImageView) view.findViewById(R.id.photoIcon);
			holder.standard = (TextView) view.findViewById(R.id.standard);
			holder.score = (TextView) view.findViewById(R.id.score);
			holder.level = (TextView) view.findViewById(R.id.level);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.kidTitle = (TextView) view.findViewById(R.id.kidTitle);
			
			view.setTag(holder);
		}else{
			holder = (AddKidSpecialHolder) view.getTag();
		}
		
		loader.DisplayImage(col.badge_image_filename, holder.imageView);
		holder.date.setText(col.date);
		holder.dollar.setText(col.badge_value);
		holder.grade.setText(col.grade);
		if(VUtil.IS_APP_LANG_ENG){
			holder.kidTitle.setText(col.title);
			holder.standard.setText(col.standard);
		}
		else{
			holder.standard.setText(col.standard_spanish);
			holder.kidTitle.setText(col.title_spanish);
		}
		holder.score.setText(col.score);
		holder.level.setText(col.level);
		holder.time.setText(col.time_taken);

		
		return view;
	}

	class AddKidSpecialHolder{
		TextView level;
		TextView date;
		TextView grade;
		TextView kidTitle;
		TextView standard;
		TextView time;
		TextView score;
		TextView dollar;
		ImageView imageView;
	}
	
}
