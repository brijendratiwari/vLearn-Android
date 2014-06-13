package com.vlearn.android.setting.myvlearn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.vlearn.android.R;
import com.vlearn.android.homepage.HomeActivity;
import com.vlearn.android.imageload.ImageLoader;
import com.vlearn.android.util.VUtil;
import com.vlearn.android.util.VariableType;
import com.vlearn.android.videoPlayer.VideoPlayerActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyVlearnAdapter extends BaseAdapter {

	Context context;
	HomeActivity activity;
	LayoutInflater inflater;
	List<MyVlearnCollection> list;
	ImageLoader loader;
	MyVlearn myVlearn;

	boolean isKid;
	public MyVlearnAdapter(Context context, List<MyVlearnCollection> list,
			ImageLoader loader, MyVlearn myVlearn) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.activity = (HomeActivity) context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
		this.loader = loader;
		this.myVlearn = myVlearn;
		
		isKid = ((String) new VUtil(context).getFromSharedPreference(VariableType.STRING, VUtil.USER_ACCESS)).contains("student");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public MyVlearnCollection getItem(int position) {
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

		MyVlearnCollection col = list.get(position);
		MyVlearnHolder holder = null;

		if (view == null) {

			view = inflater.inflate(R.layout.myvlearn_row, null, false);
			holder = new MyVlearnHolder();
			holder.videoTitle = (TextView) view.findViewById(R.id.videoTitle);
			holder.videoLang = (TextView) view.findViewById(R.id.videoLanguage);
			holder.status = (TextView) view.findViewById(R.id.statusText);
			holder.linearlayout = (LinearLayout) view
					.findViewById(R.id.linearlayout);
			holder.approve = (Button) view.findViewById(R.id.aproveButton);
			holder.del = (Button) view.findViewById(R.id.delButton);
			holder.edit = (Button) view.findViewById(R.id.editButton);
			holder.profilePic = (ImageView) view.findViewById(R.id.profilePic);
			holder.videoThumbContainer = (FrameLayout)view.findViewById(R.id.videoThumbContainer);

			holder.del.setOnClickListener(onClickListener);
			holder.edit.setOnClickListener(onClickListener);
			holder.approve.setOnClickListener(onClickListener);
			holder.videoThumbContainer.setOnClickListener(onClickListener);
			view.setTag(holder);

		} else {
			holder = (MyVlearnHolder) view.getTag();
		}

		if (col.isLocal) {
			if (!col.icon.isEmpty()){
				holder.profilePic.setImageBitmap(BitmapFactory
						.decodeFile(col.icon));
			}
			else {

				Bitmap thumb = ThumbnailUtils.createVideoThumbnail(
						col.videofile, MediaStore.Images.Thumbnails.MINI_KIND);

				File file = new File(Environment.getExternalStorageDirectory(),
						"Vlearn");

				file = new File(file.getAbsoluteFile()+"/img_" + new Date().getTime() + ".png");
				try {
					file.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(file.getAbsoluteFile());
					thumb.compress(Bitmap.CompressFormat.PNG, 90, out);
					col.icon = file.getAbsolutePath();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						out.close();
					} catch (Throwable ignore) {
					}
				}

				holder.profilePic.setImageBitmap(thumb);
			}
		} else {
			loader.DisplayImage(col.icon, holder.profilePic);
		}

		if(col.approval.length()>1){
			
			holder.status.setText(col.approval);
			holder.approve.setVisibility(View.INVISIBLE);
			holder.del.setVisibility(View.INVISIBLE);
			holder.edit.setVisibility(View.INVISIBLE);
			
		}else if (col.isDraft) {
			holder.status.setText("Draft");
			holder.approve.setVisibility(View.VISIBLE);
			holder.del.setVisibility(View.VISIBLE);
			holder.edit.setVisibility(View.VISIBLE);
			holder.approve.setText("Approve");
			if (col.isLocal)
				holder.approve.setText("Submit");

		} else {
			holder.status.setText("Pending");
			holder.approve.setVisibility(View.INVISIBLE);
			holder.del.setVisibility(View.INVISIBLE);
			holder.edit.setVisibility(View.INVISIBLE);
		}

		if(col.language.equalsIgnoreCase("3")){
			holder.videoLang.setVisibility(View.GONE);
		}else if (col.language.equalsIgnoreCase("0")) {
			holder.videoLang.setText("(Language: English)");
		} else {
			holder.videoLang.setText("(Language: Spanish)");
		}

		holder.videoTitle.setText(col.name);
		holder.del.setTag(position);
		holder.approve.setTag(position);
		holder.edit.setTag(position);
		holder.videoThumbContainer.setTag(position);
	
		return view;
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final int pos = (int) v.getTag();
			final MyVlearnCollection col = list.get(pos);
			switch (v.getId()) {
			case R.id.aproveButton:
				Button button = (Button) v;
				if (button.getText().toString().trim()
						.equalsIgnoreCase("Submit")) {
					if(!isKid)
						submitVideo(col);
					else
						myVlearn.showDialog(col);
				}else{
					myVlearn.updateStatus(col.id, pos);
				}
				break;
			case R.id.delButton:
				new AlertDialog.Builder(context)
						.setTitle("Alert!")
						.setMessage("Are You sure want to delete this video.")
						.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										myVlearn.deleteVideo(pos);
										dialog.dismiss();
									}
								})
						.setNegativeButton("NO",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).create().show();
				break;
			case R.id.editButton:
				MyVlearn.collectionBackUp = col;
				if(col.VideoType == 0){
					//curriculum
					activity.changePage(EditCareerVideo.Create());
				}else{
					//career
					activity.changePage(EditCurriculumVideo.Create());
				}
				break;
			case R.id.videoThumbContainer:
				Intent intent = new Intent(context, VideoPlayerActivity.class);
				intent.putExtra("videoUrl", col.videofile);
				activity.startActivity(intent);
				break;
			default:
				break;
			}
		}

		private void submitVideo(MyVlearnCollection col) {
			// TODO Auto-generated method stub
			myVlearn.submitVideo(col);
		}

	};

	class MyVlearnHolder {
		ImageView profilePic;
		FrameLayout videoThumbContainer;
		TextView videoTitle;
		TextView videoLang;
		TextView status;
		Button approve;
		Button edit;
		Button del;
		LinearLayout linearlayout;
	}

}
