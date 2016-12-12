package com.dariydiary.newdiary;

import java.util.Locale;
import java.util.Random;

import com.dariydiary.newdiary.R.drawable;
import com.squareup.picasso.Picasso;
import android.Manifest;
import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener , OnClickListener{
	
	private static final String PERMISSION_FOR_ACCESS_FINE_LOCATION= Manifest.permission.ACCESS_FINE_LOCATION;
	private static final String PERMISSION_FOR_READ_EXTERNAL_STORAGE= Manifest.permission.READ_EXTERNAL_STORAGE;
	private static final int  MY_PERMISSIONS_REQUEST=0;
	private DrawerLayout mDrawerLayout;
	private LinearLayout main_gallery_layout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	private Button newDiary_Button;
	private ImageView main_imageView;
	private TextView main_date_textView;
	private Database database;
	private Cursor cursor;
	private SQLiteDatabase databaseReader;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main);
		getPermission(PERMISSION_FOR_ACCESS_FINE_LOCATION);
		getPermission(PERMISSION_FOR_READ_EXTERNAL_STORAGE);
		initSettings();
		initView ();
	}
	
	public void initSettings(){
		SharedPreferences sharePrefs = getSharedPreferences("settings", MODE_PRIVATE);
		SharedPreferences.Editor editor;
		editor = sharePrefs.edit();
		if (!sharePrefs.contains("initialized")) {
			editor.putInt("location_img_radius", 500);
			editor.putInt("location_img_size", 1600);
			editor.putString("search_img_size", "large");
			editor.putFloat("background_opacity", 0.50f);
			editor.putBoolean("initialized", true);
			editor.commit();
			
			//main_gallery_layout.setVisibility(View.INVISIBLE);
		} 
	}
	
	public void initView (){
		String[] MainMenuOptions = {" ","New Diary","My Diary","Setting","Exit"};
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, MainMenuOptions));
        mDrawerList.setOnItemClickListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("Dairy Diary");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Menu");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(drawable.color));
        main_gallery_layout=(LinearLayout)findViewById(R.id.main_gallery);
        main_imageView=(ImageView)findViewById(R.id.main_imageview);
        main_date_textView=(TextView)findViewById(R.id.main_date_textView);
        
        newDiary_Button=(Button) findViewById(R.id.main_new_button);
        newDiary_Button.setOnClickListener(this);
        showGallery ();       
	}
	
	public void showGallery (){
		database = new Database(this);
		databaseReader = database.getReadableDatabase();
		cursor = databaseReader.query(Database.TABLE_NAME, null, null, null, null, null, null);
		Random random = new Random();
		
        try {
        	for (int i = 0; i <100; i++) {
        		int position = random.nextInt(cursor.getCount());
            	cursor.moveToPosition(position);
            	if (cursor.getString(cursor.getColumnIndex(Database.IMAGE_PATH))!=null) { 		
            		Picasso.with(MainActivity.this).load(cursor.getString(cursor.getColumnIndex(Database.IMAGE_PATH))).resize(850, 600).centerCrop().into(main_imageView);
            		main_date_textView.setText(cursor.getString(cursor.getColumnIndex(Database.TIME)));
            		main_gallery_layout.setVisibility(View.VISIBLE);
            	break;
    			} 
    		}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
        main_imageView.setOnClickListener(this);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
		switch (position) {
		case 0:
			break;
		case 1:
			Intent intent_new = new Intent(MainActivity.this,NewDiary.class);
			startActivity(intent_new);
			mDrawerLayout.closeDrawers();
			break;
		case 2:
			Intent intent_old = new Intent(MainActivity.this,OldDiary.class);
			startActivity(intent_old);
			mDrawerLayout.closeDrawers();
			break;
		case 3:
			Intent intent_settings = new Intent(MainActivity.this,Settings.class);
			startActivity(intent_settings);
			mDrawerLayout.closeDrawers();
			break;
		case 4:
			finish();
			break;
		default:
			break;
		}	
	}

	public void getPermission(String PERMISSION_TYPE){
		if (ContextCompat.checkSelfPermission(this,
				PERMISSION_TYPE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					PERMISSION_TYPE)) {
				// Show an expanation to the user *asynchronously* -- 
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{PERMISSION_TYPE},
						MY_PERMISSIONS_REQUEST);
				
				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		} else {
		Log.i("info", PERMISSION_TYPE+" permission granted");
		}
	}
		
	@Override
	public void onRequestPermissionsResult(int requestCode,
	        String permissions[], int[] grantResults) {
		if (requestCode==MY_PERMISSIONS_REQUEST) {
			if (grantResults.length > 0
	                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
	                Toast.makeText(MainActivity.this, "Permission Granted",Toast.LENGTH_SHORT).show();
				
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            	Toast.makeText(MainActivity.this, "Permission denied, this app requires permission to run.",Toast.LENGTH_SHORT).show();
            	finish();
            }  
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_new_button:
			Intent newDiary = new Intent(MainActivity.this,NewDiary.class);
			startActivity(newDiary);
			break;
		case R.id.main_imageview:
			Intent i = new Intent(MainActivity.this,ReviewDiary.class);
			i.putExtra(Database.ID, cursor.getInt(cursor.getColumnIndex(Database.ID)));
			i.putExtra(Database.CONTENT, cursor.getString(cursor.getColumnIndex(Database.CONTENT)));
			i.putExtra(Database.TIME, cursor.getString(cursor.getColumnIndex(Database.TIME)));
			i.putExtra(Database.IMAGE_PATH, cursor.getString(cursor.getColumnIndex(Database.IMAGE_PATH)));
			i.putExtra(Database.BACKGROUNDIMG_PATH, cursor.getString(cursor.getColumnIndex(Database.BACKGROUNDIMG_PATH)));
			startActivity(i);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}
}
