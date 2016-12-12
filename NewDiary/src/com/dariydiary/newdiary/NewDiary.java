package com.dariydiary.newdiary;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.dariydiary.newdiary.R.drawable;
import com.dariydiary.newdiary.bean.LocationData;
import com.dariydiary.newdiary.bean.SearchData;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public  class NewDiary extends  FragmentActivity {
	
	private static final String API_KEY = "&key=AIzaSyCEXI-R8OEKt2qW2rYn5XRNp7ssZ-lKxww";
	private static final String url_location_fragment_1="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
	private static final String url_location_fragment_2="&radius=";
	private static final String url_location_photorefer_fragment_1="https://maps.googleapis.com/maps/api/place/photo?maxwidth=";
	private static final String url_location_photorefer_fragment_2="&photoreference=";
	private static final String url_search_fragment_1="https://www.googleapis.com/customsearch/v1?q=";
	private static final String url_search_fragment_2="&cx=016692671182788828750%3Aejrfgmzyzki&fileType=jpg&imgSize=";
	private static final String url_search_fragment_3="&searchType=image";
	private static final int SHOW_RESPONSE = 0;
	private static final int SHOW_ERROR = 1;
	private static final int fragment_new_diary_choose_background_number = 0;
    private static final int fragment_new_diary_take_photo_number= 1;
	private static final int fragment_new_diary_text_number = 2;
	private static final int camera_request_code = 2;
	private static final int REQUEST_CODE_LOCATION = 1;
	private static final int REQUEST_CODE_SEARCH = 2;
	private int REQUEST_CODE;
	
	private double longitude;
	private double latitude;
	private String requestUrl;
	private String search_img_size;
	private String provider;
	private int location_radius;
	private int location_img_size;
	private String[] photoUrl;
	
	private ViewPager mViewPager;
	private Button button_choose_background_search;
	private Button button_choose_background_location;
	private Button button_take_photo;
	private Button button_save;
	private Button button_cancel;
	private TextView textView_choose_background_loading;
	private EditText editText_diarytext;
	private EditText editText_choose_background;
	private ListView listView_choose_background;
	private ImageView imageView_take_photo;
	
	private BackgroundIMGListViewAdapter backgroundIMGListViewAdapter;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private LocationManager locationManager;
	private DownloadTool downloadTool;
	private FileCreater fileCreater;
	private File imgFile_background=null;
	private File imgFile_photo=null;
	private Database database;
	private SQLiteDatabase database_writer;
	
	private  Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SHOW_RESPONSE:
				String response = (String) msg.obj;
				if (response!=null) {
					try {
						photoUrl=parseLocationData(response, REQUEST_CODE);
						showImg(photoUrl);
						textView_choose_background_loading.setText("swipe & click to select");
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(NewDiary.this, "sorry, can not display the images", Toast.LENGTH_SHORT).show();
					}		
				} else {
					Toast.makeText(NewDiary.this, "sorry, can not get the anything from Internet", Toast.LENGTH_SHORT).show();
					textView_choose_background_loading.setText("error");
				}
				break;
				case SHOW_ERROR:
					Toast.makeText(NewDiary.this, "sorry, can not get the images from Internet", Toast.LENGTH_SHORT).show();
					textView_choose_background_loading.setText("error");
				break;
				default:
				break;
			}
		}
	};
 
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	       getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
	       setContentView(R.layout.activity_new_diary);
	       getSettingData();
	       initView();        
	}
	
	public void getSettingData(){
		SharedPreferences sharedprefs = getSharedPreferences("settings", MODE_PRIVATE);
		search_img_size=sharedprefs.getString("search_img_size", "medium");
		location_img_size=sharedprefs.getInt("location_img_size", 1000);
		location_radius=sharedprefs.getInt("location_img_radius", 500);	
	}
	
	public void initView(){
		 myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		 // Set up action bar.
         final ActionBar actionBar = getActionBar();
         // Specify that the Home button should show an "Up" caret, indicating that touching the
         // button will take the user one step up in the application's hierarchy.
         actionBar.setDisplayHomeAsUpEnabled(true);
         getActionBar().setBackgroundDrawable(new ColorDrawable(drawable.color));
         // Set up the ViewPager, attaching the adapter.
         mViewPager = (ViewPager) findViewById(R.id.new_diary_pager);
         mViewPager.setAdapter(myFragmentPagerAdapter);
	 }
	 	 
	public class MyFragmentPagerAdapter extends  FragmentStatePagerAdapter{

		public  MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int i) {
			// TODO Auto-generated method stub
			NewDiaryFragment newDiaryFragment;
			switch (i) {
			case 0:
				newDiaryFragment = new NewDiaryFragment(fragment_new_diary_choose_background_number);
				return newDiaryFragment;
			case 1:
				newDiaryFragment = new NewDiaryFragment(fragment_new_diary_take_photo_number);
				return newDiaryFragment;
			case 2:
				newDiaryFragment = new NewDiaryFragment(fragment_new_diary_text_number);
				return newDiaryFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}
		@Override
        public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return"GET BACKGROUND";
			case 1:
				return"TAKE PHOTO";
			case 2:
				return"WRITE DIARY";
			default:
				return"";	
			}
        }
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
                    TaskStackBuilder. from(this)
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
    
    public  class NewDiaryFragment extends Fragment {
    	private int newDiaryFragmentNum;
    	NewDiaryFragment(int newDiaryFragmentNum){
    		this.newDiaryFragmentNum=newDiaryFragmentNum;
    	}
          	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View rootView ;
        	switch (newDiaryFragmentNum) {
			case fragment_new_diary_choose_background_number:
	            rootView = inflater.inflate(R.layout.fragment_new_diary_choose_background, container, false);
	            textView_choose_background_loading= (TextView)rootView.findViewById(R.id.textView_choose_background_loading);
	            editText_choose_background = (EditText)rootView.findViewById(R.id.editText_choose_background);
	            listView_choose_background = (ListView)rootView.findViewById(R.id.listview_choose_background);
	            button_choose_background_search=(Button)rootView.findViewById(R.id.button_choose_background_search);
	            button_choose_background_location=(Button)rootView.findViewById(R.id.button_choose_background_location);
	            
	            button_choose_background_search.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (editText_choose_background.getVisibility()!=View.VISIBLE) {
							editText_choose_background.setVisibility(View.VISIBLE);
						}else{
							textView_choose_background_loading.setText("It's just loading..");
							REQUEST_CODE=REQUEST_CODE_SEARCH;
							try {
									
								requestUrl=url_search_fragment_1
										+editText_choose_background.getText().toString().replace(" ", "+").replace("\n", "+")
										+url_search_fragment_2
										+search_img_size.toString()
										+url_search_fragment_3
										+API_KEY;
			
								sendRequestWithHttpURLConnection();
								editText_choose_background.setText("");
								editText_choose_background.setVisibility(View.GONE);
							} catch (Exception e) {
								// TODO: handle exception
								Toast.makeText(NewDiary.this, 
										"search funtion is not available currently, please try the location funtion", 
										Toast.LENGTH_SHORT)
									.show();
							}
						}						
					}
				});
        
	            button_choose_background_location.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						textView_choose_background_loading.setText("It's just loading..");
						REQUEST_CODE=REQUEST_CODE_LOCATION;
						getGeoLoction();
						try {					
							requestUrl=url_location_fragment_1
									+latitude+","+longitude
									+url_location_fragment_2
									+location_radius
									+API_KEY;
							Log.i("info", "1111111111111111111"+requestUrl)	;
							sendRequestWithHttpURLConnection();
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(NewDiary.this, 
									"location funtion is not available currently, please try the search funtion", 
									Toast.LENGTH_SHORT)
								.show();
						}
					}
				});
	            
	            listView_choose_background.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						if (locationManager != null) {
							locationManager.removeUpdates(locationListener);
						}
						fileCreater= new FileCreater();
						imgFile_background=fileCreater.creatFileForImage("DOWNLOAD_IMG");
						downloadTool = new DownloadTool(NewDiary.this,imgFile_background);
						downloadTool.execute(photoUrl[position].toString());						
					}
				});
	            return rootView;
	            
			case fragment_new_diary_take_photo_number:
				rootView = inflater.inflate(R.layout.fragment_new_diary_take_photo, container, false);
				imageView_take_photo=(ImageView)rootView.findViewById(R.id.imageView_take_photo);
				button_take_photo=(Button)rootView.findViewById(R.id.button_take_photo);
				button_take_photo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub	
						fileCreater= new FileCreater();
						imgFile_photo = fileCreater.creatFileForImage("DIARY_PHOTO");
						Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						open_camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile_photo));
						startActivityForResult(open_camera, camera_request_code);
					}
				});
				return rootView;
				
			case fragment_new_diary_text_number:
				rootView = inflater.inflate(R.layout.fragment_new_diary_text, container, false);
				editText_diarytext=(EditText)rootView.findViewById(R.id.editText_diarytext);
				button_save=(Button)rootView.findViewById(R.id.button_save);
				button_cancel=(Button)rootView.findViewById(R.id.button_cancel);
				
				button_save.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (locationManager != null) {
							locationManager.removeUpdates(locationListener);
						}
						saveToDatabase();
						finish();
					}
				});
				
				button_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (locationManager != null) {
							locationManager.removeUpdates(locationListener);
						}
						finish();
					}
				});		
	            return rootView;

			default:
				return null;
				
			}    	
        }
    	
    	@Override
    	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    		// TODO Auto-generated method stub
    		super.onActivityResult(requestCode, resultCode, data);
    		if (requestCode==camera_request_code) {
    			try {
    				Picasso.with(NewDiary.this)
    					.load(Uri.fromFile(imgFile_photo)
    					.toString()).resize(1080, 1080)
    					.centerCrop()
    					.into(imageView_take_photo);
    			} catch (Exception e) {
    				// TODO: handle exception
    				Toast.makeText(NewDiary.this, "take a picture if you like to",Toast.LENGTH_SHORT);   				
    			} 			
    		}
    	}  
    }

	private void sendRequestWithHttpURLConnection(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				HttpURLConnection connection = null;
				try {
					URL url = new URL(requestUrl);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);	
					connection.connect();
					InputStream in = connection.getInputStream();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
					response.append(line);
					}
					Message message = new Message();
					message.what = SHOW_RESPONSE;
					message.obj = response.toString();
					handler.sendMessage(message);
				} catch (Exception e) {
					// TODO: handle exception
					Message message = new Message();
					message.what = SHOW_ERROR;
					handler.sendMessage(message);
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}		
			}
		}).start();
	}
	
	private String[] parseLocationData(String jsonString, int requestCode){
		int photoUrl_size=0;
		int photoUrlListIndex=0;
		String[] photoRefer;
		String[] photoUrlList;
				
		switch (requestCode) {
		case REQUEST_CODE_LOCATION:{
			Gson gson = new Gson();
			LocationData processedData = gson.fromJson(jsonString, LocationData.class);
			int photoRefer_size =processedData.getResults().size();	
			photoRefer = new String[photoRefer_size];
			for (int i = 0; i < photoRefer_size; i++) {
				try {
					photoRefer[i]=url_location_photorefer_fragment_1
							+location_img_size+""
							+url_location_photorefer_fragment_2
							+processedData.getResults().get(i).getPhotos().get(0).getPhoto_reference()
							+API_KEY;
				} catch (Exception e) {
					// TODO: handle exception
					photoRefer[i]=null;
				}
				if (photoRefer[i]!=null) {
					photoUrl_size+=1;
				}
			}
			photoUrlList = new String[photoUrl_size];
			for (int i = 0; i < photoRefer_size; i++) {
				if (photoRefer[i]!=null) {
					photoUrlList[photoUrlListIndex]=photoRefer[i].toString();
					photoUrlListIndex+=1;		
				}	
			}
			return photoUrlList;
		}
		case REQUEST_CODE_SEARCH:{
			Gson gson = new Gson();
			SearchData processedData = gson.fromJson(jsonString, SearchData.class);
			int photoRefer_size =processedData.getItems().size();
			photoRefer = new String[photoRefer_size];
			for (int i = 0; i < photoRefer_size; i++) {
				try {
					photoRefer[i]=processedData.getItems().get(i).getLink();
				} catch (Exception e) {
					// TODO: handle exception
					photoRefer[i]=null;
				}
				if (photoRefer[i]!=null) {
					photoUrl_size+=1;
				}
			}
			photoUrlList = new String[photoUrl_size];
			for (int i = 0; i < photoRefer_size; i++) {
				if (photoRefer[i]!=null) {
					photoUrlList[photoUrlListIndex]=photoRefer[i].toString();
					photoUrlListIndex+=1;		
				}	
			}
			return photoUrlList;
		}		
		default:
			return  null;
		}
	}
	
	public void showImg(String[] urlList){

		backgroundIMGListViewAdapter = new BackgroundIMGListViewAdapter(NewDiary.this,urlList);			
		listView_choose_background.setAdapter(backgroundIMGListViewAdapter);
	}
	
	public void getGeoLoction() {
		locationManager = (LocationManager) getSystemService(Context. LOCATION_SERVICE);
		List<String> providerList = locationManager.getProviders(true);
		
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {			
			provider = null;	
		}
		
		if (provider!=null) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				// 显示当�?设备的�?置信�?�
				longitude = location.getLongitude();
				latitude = location.getLatitude();
			}else {
				Toast.makeText(NewDiary.this, "trying to detect your location", Toast.LENGTH_SHORT).show();
			}
			//accuracy is 1 meter, refresh frquency is 5 seconds
			locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
		} else {
			Toast.makeText(NewDiary.this, "location funtion is not available currently, please try the search funtion", 
					Toast.LENGTH_SHORT).show();
		}		
	}
	
	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub			
		}};
    
	public void saveToDatabase(){
		fileCreater= new FileCreater();
		database = new Database(this);
		database_writer = database.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(database.CONTENT,editText_diarytext.getText().toString());
		contentValues.put(database.TIME,fileCreater.getTime());
		try {
			contentValues.put(database.IMAGE_PATH, Uri.fromFile(imgFile_photo).toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			contentValues.put(database.BACKGROUNDIMG_PATH,Uri.fromFile(imgFile_background).toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		database_writer.insert(database.TABLE_NAME, null, contentValues);
	}	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}
}



