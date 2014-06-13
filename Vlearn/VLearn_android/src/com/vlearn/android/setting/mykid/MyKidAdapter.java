package com.vlearn.android.setting.mykid;

import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyKidAdapter extends BaseAdapter{

	HomeActivity activity;
	Context context;
	List<MyKidCollection> list;
	LayoutInflater inflater;
	ImageLoader loader;
	Mykid mykid;
	
	public MyKidAdapter(Context context,Mykid mykid, List<MyKidCollection> list, ImageLoader loader) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.activity = (HomeActivity) context;
		this.inflater = LayoutInflater.from(context);
		this.loader = loader;
		this.mykid = mykid;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public MyKidCollection getItem(int position) {
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
		
		MyKidHolder holder = null;
		MyKidCollection col = list.get(position);
		
		if(view == null){
			
			view = inflater.inflate(R.layout.mykid_row, null, false);
			holder = new MyKidHolder();
			holder.crossBtn = (Button) view.findViewById(R.id.crossbtn);
			holder.delBtn = (Button) view.findViewById(R.id.delButton);
			holder.kidName = (TextView) view.findViewById(R.id.kidName);
			holder.profilePic = (ImageView) view.findViewById(R.id.profilePic);
			
			holder.crossBtn.setOnClickListener(onClickListener);
			holder.delBtn.setOnClickListener(onClickListener);
			
			holder.profilePic.setOnClickListener(onClickListener);
			holder.kidName.setOnClickListener(onClickListener);
			
			view.setTag(holder);
			
		}else{
			holder = (MyKidHolder) view.getTag();
		}
		
		holder.kidName.setText(col.first_name+" "+col.last_name);
//		holder.profilePic.setImageBitmap(col.profilePic);
		loader.DisplayImage(col.imgUrl, holder.profilePic);
		holder.crossBtn.setTag(position);
		holder.delBtn.setTag(position);
		holder.profilePic.setTag(position);
		holder.kidName.setTag(position);
		if(col.isCross){
			holder.crossBtn.setVisibility(View.VISIBLE);
			holder.profilePic.setVisibility(View.INVISIBLE);
		}else{
			holder.profilePic.setVisibility(View.VISIBLE);
			holder.crossBtn.setVisibility(View.INVISIBLE);
		}
		
		if(col.isDelete){
			holder.delBtn.setVisibility(View.VISIBLE);
			
		}else{
			holder.delBtn.setVisibility(View.INVISIBLE);
		}
		
		return view;
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = (int)v.getTag();
			MyKidCollection col = list.get(position);
			switch (v.getId()) {
			case R.id.delButton:
				mykid.deleteKid(col.id, position);
				break;
			case R.id.crossbtn:
				col.isDelete = !col.isDelete;
				notifyDataSetChanged();
				break;
			case R.id.profilePic:
			case R.id.kidName:
				Mykid.col = col;
				activity.changePage(AddKidSpecial.Create());
				break;
			default:
				break;
			}
		}
	};
	
	class MyKidHolder{
		TextView kidName;
		ImageView profilePic;
		Button crossBtn;
		Button delBtn;
	}
	
}
