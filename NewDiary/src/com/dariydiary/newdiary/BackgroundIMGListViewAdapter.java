package com.dariydiary.newdiary;



import com.squareup.picasso.Picasso;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BackgroundIMGListViewAdapter extends BaseAdapter{
	private Context context;
	private String[] urlList;
	private LinearLayout layout;
	
	public BackgroundIMGListViewAdapter(Context context,String[] urlList){
		this.context=context;
		this.urlList=urlList;
	}
	 	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
	    if(urlList!=null)
		return urlList.length;
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return urlList[position];
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
		layout = (LinearLayout) inflater.inflate(R.layout.cell_new_diary_background_listview, null);
		ImageView background_listview_imageview = (ImageView)layout
				.findViewById(R.id.imageview_cell_new_diary_choose_background);
		Picasso
			.with(context)
			.load(urlList[position].toString())
			.resize(850, 850)
			.centerCrop()
			.into(background_listview_imageview);
		
		return layout;
	}	
}
