package com.dariydiary.newdiary;




import com.dariydiary.newdiary.R.drawable;
import com.squareup.picasso.Picasso;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewDiary extends Activity implements OnClickListener {
	private float opacity;
	private ImageView imageView_reiview_photo;
	private ImageView imageView_reiview_background;
	private TextView textView_review_diary_content;
	private TextView textView_review_date;
	private Button button_review_delete;
	private Button button_review_share;
	private Database database;
	private SQLiteDatabase database_Reader;
	private Uri uri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_review_diary);
		getSettingData();
		initView();		
	}
	public void getSettingData(){
		SharedPreferences sharedprefs = getSharedPreferences("settings", MODE_PRIVATE);
		opacity=sharedprefs.getFloat("background_opacity", 0.55f);
	}
	
	public void initView(){
		// Set up action bar.
        final ActionBar actionBar = getActionBar();
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(drawable.color));
		database = new Database(this);
		database_Reader = database.getReadableDatabase();
		imageView_reiview_photo=(ImageView)findViewById(R.id.imageView_review_photo);
		imageView_reiview_background=(ImageView)findViewById(R.id.imageView_review_background);
		imageView_reiview_background.setAlpha(opacity);
		textView_review_date=(TextView)findViewById(R.id.textView_review_date);
		textView_review_diary_content=(TextView)findViewById(R.id.textView_review_diary_content);
		button_review_share=(Button)findViewById(R.id.button_review_share);
		button_review_delete=(Button)findViewById(R.id.button_review_delete);
		button_review_share.setOnClickListener(this);
		button_review_delete.setOnClickListener(this);
		
		try {
			showDiary();
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(ReviewDiary.this, 
					"something wrong, can not display the content", 
					Toast.LENGTH_SHORT)
				.show();
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_review_share:
			try {
				uri=uri.parse(getIntent().getStringExtra(Database.IMAGE_PATH));
				createInstagramIntent(uri);
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(ReviewDiary.this, "Oops, there is no picture to share",Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.button_review_delete:
			deleteData();
			finish();
			break;

		default:
			break;
		}
	}
	
	public void showDiary(){
		
		Picasso.with(ReviewDiary.this)
			.load(getIntent()
			.getStringExtra(Database.BACKGROUNDIMG_PATH))
			.resize(1080, 1702).centerCrop()
			.into(imageView_reiview_background);
		
		Picasso.with(ReviewDiary.this)
			.load(getIntent()
			.getStringExtra(Database.IMAGE_PATH))
			.resize(850, 850).centerCrop()
			.into(imageView_reiview_photo);
		
		textView_review_diary_content.setText(getIntent().getStringExtra(Database.CONTENT));
		textView_review_date.setText(getIntent().getStringExtra(Database.TIME));
		
	}
	public void deleteData() {
		database_Reader.delete(Database.TABLE_NAME, "_id="+getIntent().getIntExtra(Database.ID, 0), null);
		Toast.makeText(ReviewDiary.this, "this diary has been deleted", Toast.LENGTH_SHORT).show();
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void createInstagramIntent(Uri uri){

	    // Create the new Intent using the 'Send' action.
	    Intent share = new Intent(Intent.ACTION_SEND);

	    // Set the MIME type
	    share.setType("image/*");

	    // Add the URI to the Intent.
	    share.putExtra(Intent.EXTRA_STREAM, uri);

	    // Broadcast the Intent.
	    startActivity(Intent.createChooser(share, "Share to"));
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
