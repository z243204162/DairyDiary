package com.dariydiary.newdiary;



import com.dariydiary.newdiary.R.drawable;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class OldDiary extends Activity {
	
	private ListView listView_old_diary;
	private Database database;
	private Cursor cursor;
	private SQLiteDatabase databaseReader;
	private OldDiaryListViewAdapter oldDiaryListViewAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_old_diary_list);
		initView();
	}
	
	public void initView(){
		final ActionBar actionBar = getActionBar();
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(drawable.color));
		database = new Database(this);
		databaseReader = database.getReadableDatabase();
		listView_old_diary = (ListView)findViewById(R.id.listView_old_diary);
		cursor = databaseReader.query(Database.TABLE_NAME, null, null, null, null, null, null);
		oldDiaryListViewAdapter = new OldDiaryListViewAdapter(this, cursor);
		listView_old_diary.setAdapter(oldDiaryListViewAdapter);
		listView_old_diary.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(position);
				Intent i = new Intent(OldDiary.this,ReviewDiary.class);
				i.putExtra(Database.ID, cursor.getInt(cursor.getColumnIndex(Database.ID)));
				i.putExtra(Database.CONTENT, cursor.getString(cursor.getColumnIndex(Database.CONTENT)));
				i.putExtra(Database.TIME, cursor.getString(cursor.getColumnIndex(Database.TIME)));
				i.putExtra(Database.IMAGE_PATH, cursor.getString(cursor.getColumnIndex(Database.IMAGE_PATH)));
				i.putExtra(Database.BACKGROUNDIMG_PATH, cursor.getString(cursor.getColumnIndex(Database.BACKGROUNDIMG_PATH)));
				startActivity(i);
			}
		});
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
}
