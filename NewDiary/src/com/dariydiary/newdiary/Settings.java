package com.dariydiary.newdiary;


import com.dariydiary.newdiary.R.drawable;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Settings extends Activity implements OnSeekBarChangeListener , OnClickListener{
	
	private int background_opacity_progress;
	private int location_img_radius;
	private int location_img_size;
	private int search_img_size_progress;
	private float background_opacity;
	private ImageView background;
	private SeekBar	location_img_size_seekBar;
	private SeekBar	location_img_radius_seekBar;
	private SeekBar	search_img_size_seekBar;
	private SeekBar	background_opacity_seekBar;
	private Button confirm;
	private Button cancel;
	private String search_img_size;
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_settings);
		getSettingData();
		initView();
	}
	
	public void getSettingData(){
		
		SharedPreferences sharedprefs = getSharedPreferences("settings", MODE_PRIVATE);
		search_img_size=sharedprefs.getString("search_img_size", "medium");
		location_img_size=sharedprefs.getInt("location_img_size", 1000);
		location_img_radius=sharedprefs.getInt("location_img_radius", 500);	
		background_opacity=sharedprefs.getFloat("background_opacity", 0.66f);
		
		switch (search_img_size) {
		case "small" :
			search_img_size_progress=1;
			break;
		case "medium":
			search_img_size_progress=2;
			break;
		case "large":
			search_img_size_progress=3;
			break;
		case "huge":
			search_img_size_progress=4;
			break;
		default:
			search_img_size_progress=2;
			break;
		}
		
		background_opacity_progress=(int)(background_opacity*100);
		
	}
	
	public void initView(){
		// Set up action bar.
        final ActionBar actionBar = getActionBar();
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(drawable.color));
        
		location_img_radius_seekBar=(SeekBar)findViewById(R.id.location_img_radius_seekBar);
		location_img_size_seekBar=(SeekBar)findViewById(R.id.location_img_size_seekBar);
		search_img_size_seekBar=(SeekBar)findViewById(R.id.search_img_size_seekBar);
		background_opacity_seekBar=(SeekBar)findViewById(R.id.background_opacity_seekBar);
		
		location_img_radius_seekBar.setProgress(location_img_radius);
		location_img_size_seekBar.setProgress(location_img_size);
		search_img_size_seekBar.setProgress(search_img_size_progress);
		background_opacity_seekBar.setProgress(background_opacity_progress);
		
		confirm=(Button)findViewById(R.id.setting_confirm);
		cancel=(Button)findViewById(R.id.setting_cancel);
		
		location_img_radius_seekBar.setOnSeekBarChangeListener(this);
		location_img_size_seekBar.setOnSeekBarChangeListener(this);
		search_img_size_seekBar.setOnSeekBarChangeListener(this);
		background_opacity_seekBar.setOnSeekBarChangeListener(this);
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_confirm:
			saveSettings();
			finish();
			break;
		case R.id.setting_cancel:
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		switch (seekBar.getId()) {
		case R.id.location_img_radius_seekBar:
			if (progress==100) {
				Toast.makeText(Settings.this, 
						"only a few pictures can be found when radius is too small", 
						Toast.LENGTH_SHORT)
					.show();
			}else if (progress<1){
				progress=1;
			}
			location_img_radius=progress;
			break;
			
		case R.id.location_img_size_seekBar:
			if (progress==400) {
				Toast.makeText(Settings.this, 
						"only a few pictures can be found when quality is too low", 
						Toast.LENGTH_SHORT)
					.show();
			}else if (progress<1){
				progress=1;
			}
			location_img_size=progress;
			break;
			
		case R.id.search_img_size_seekBar:
			switch (progress) {
			case 1:
				search_img_size="small";
				break;
			case 2:
				search_img_size="medium";
				break;
			case 3:
				search_img_size="large";
				break;
			case 4:
				search_img_size="huge";
				break;
			default:
				search_img_size="medium";
				break;
			}	
			break;
			
		case R.id.background_opacity_seekBar:
			background_opacity=(float)progress/100;
			break;
			
		default:
			break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}
	
	public void saveSettings(){
		SharedPreferences  sharedprefs = getSharedPreferences("settings", MODE_PRIVATE);
		SharedPreferences.Editor editor;
		editor = sharedprefs.edit();
		editor.putInt("location_img_radius", location_img_radius);
		editor.putInt("location_img_size", location_img_size);
		editor.putString("search_img_size", search_img_size);
		editor.putFloat("background_opacity", background_opacity);
		editor.commit();

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
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}