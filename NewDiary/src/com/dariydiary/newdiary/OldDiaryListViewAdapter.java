package com.dariydiary.newdiary;


import com.squareup.picasso.Picasso;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OldDiaryListViewAdapter extends BaseAdapter {
	private Context context;
	private Cursor cursor;
	private LinearLayout layout;
	
	public OldDiaryListViewAdapter (Context context, Cursor cursor){
		this.context = context;
		this.cursor = cursor;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return cursor.getPosition();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		layout = (LinearLayout) inflater.inflate(R.layout.cell_old_diary_listview, null);
		TextView content_textView_cell_old_diary_listview =(TextView) layout.
				findViewById(R.id.content_textView_cell_old_diary_listview);
		TextView data_textView_cell_old_diary_listview=(TextView) layout.
				findViewById(R.id.data_textView_cell_old_diary_listview);
		ImageView imageView_cell_old_diary_listview = (ImageView)layout.
				findViewById(R.id.imageView_cell_old_diary_listview);
		
		cursor.moveToPosition(position);
		String diary_text = cursor.getString(cursor.getColumnIndex("content"));
		String diary_time = cursor.getString(cursor.getColumnIndex("time"));
		String diary_photo = cursor.getString(cursor.getColumnIndex("image_path"));
		//String background_image = cursor.getString(cursor.getColumnIndex("background_image_path"));
		content_textView_cell_old_diary_listview.setText(diary_text);
		data_textView_cell_old_diary_listview.setText(diary_time);
		Picasso.with(context)
			.load(diary_photo)
			.resize(250, 250)
			.centerCrop()
			.into(imageView_cell_old_diary_listview);
		return layout;
	}
	

}