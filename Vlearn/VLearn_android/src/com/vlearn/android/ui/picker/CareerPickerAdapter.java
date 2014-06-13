package com.vlearn.android.ui.picker;

import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.imageload.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CareerPickerAdapter extends BaseAdapter{

	Context context;
	List<CareerPickerCollection> list;
	private LayoutInflater inflater;
	private ImageLoader loader;
	
	
	public CareerPickerAdapter(Context context,List<CareerPickerCollection> list, ImageLoader loader) {
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
	public CareerPickerCollection getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		CareerPickerHolder holder = null;
		CareerPickerCollection col = list.get(position);
		
		if(view == null){
			
			view = inflater.inflate(R.layout.career_picker_row, null, false);
			holder = new CareerPickerHolder();
			holder.kidName = (TextView) view.findViewById(R.id.kidName);
			holder.profilePic = (ImageView) view.findViewById(R.id.profilePic);
			holder.rightTick = (ImageView) view.findViewById(R.id.rightTick);
			view.setTag(holder);
		}else{
			holder = (CareerPickerHolder) view.getTag();
		}
		
		if(col.isChecked){
			holder.rightTick.setVisibility(View.VISIBLE);
		}else{
			holder.rightTick.setVisibility(View.INVISIBLE);
		}
		
		loader.DisplayImage(col.imgUrl, holder.profilePic);
		holder.kidName.setText(col.name); 
		
		return view;
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		}
	};
	
	class CareerPickerHolder{
		TextView kidName;
		ImageView profilePic;
		ImageView rightTick;
		
	}
	
}
