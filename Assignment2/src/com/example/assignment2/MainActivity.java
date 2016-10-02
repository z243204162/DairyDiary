package com.example.assignment2;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.example.assignment2.bean.Data;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	
	public static final int SHOW_RESPONSE = 0;
	public static final int SHOW_ERROR = 1;
	public static final String URL_STRING ="http://www.wincoredata.com/php/get_dealinfo.php?did=6&count=0";
	private String requestMethod="GET";
	private int connectTimeout =8000;
	private int readTimeout =8000;
	private Message message = new Message();
	private HttpURLConnection connection = null;
	private ImageView imageView;
	private Button sendRequest;
	private ListView listView;
	private ProgressBar progressBar;
	
	//use a handler to receive massages and conduct actions on UI 
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SHOW_RESPONSE:
					Log.i("info", msg.obj+"");
					// use handler to show the result on the main activity							
					showData(parseData(msg.obj.toString()));
					progressBar.setVisibility(View.GONE);
					break;			
				case SHOW_ERROR:			
					Toast.makeText(MainActivity.this, "sorry, there is an error", 1).show();
					break;
				default:	
					break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sendRequest = (Button) findViewById(R.id.button1);
		imageView=(ImageView) findViewById(R.id.imageView1);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		
		sendRequest.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			sendRequestWithHttpURLConnection();
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	
	private void sendRequestWithHttpURLConnection() {
		// start a thread to send Internet request
		new Thread(new Runnable() {
		@Override
			public void run() {
			getData(URL_STRING);
			}		
		}).start();
	}
 
	//get string data from url and send massage to handler 
	private void getData(String url_string){
		try {
			URL url = new URL(url_string);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			InputStream in = connection.getInputStream();
			
            //read input stream got from request
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			//Message message = new Message();
			message.what = SHOW_RESPONSE;
			// save the result into a massage
			message.obj = response.toString();
			handler.sendMessage(message);
		} catch (NullPointerException e) {
			message.what = SHOW_ERROR;
		} catch (Exception e) {
			message.what = SHOW_ERROR;
		}finally {
			progressBar.setVisibility(View.VISIBLE);
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	//use gson to parse data and return a list
	private  Data[]  parseData(String result) {
		// TODO Auto-generated method stub
	    Gson gson = new Gson();
	    try {
	    	Data[] processedData = gson.fromJson(result, Data[].class);
	    	return processedData;
	    } catch (Exception e){
	    	return null;}	    
	}

	//use Picasso to load image, use array_adaper to put data into a listview  
	private void showData(Data[] data){
		if (data!=null){
		    Picasso.with(MainActivity.this).load(data[0].getImagePath()+data[0].getImageFileName()).into(imageView);
		    
		    String[] resultText={("Deal Id: "+data[0].getDealId()),("Retailer Id: "+data[0].getRetailerId()),
		    		("Location Id: "+data[0].getLocationId()),("Cat Code: "+data[0].getCatCode()),
		    		("Item Name: "+data[0].getItemName()),("Original Price: "+data[0].getOriginalPrice()),
		    		("Discount Price: "+data[0].getDiscountPrice()),("Percentage: "+data[0].getPercentage()+"%"),
		    		("Units: "+data[0].getUnits()),("Quantity: "+data[0].getQuantity()),
		    		("Effective Date: "+data[0].getEffectiveDate()),("Expiry Date: "+data[0].getExpiryDate()),
		    		("Active: "+data[0].getActive()),("Count Of Views: "+data[0].getCountOfViews()),
		    		("Description: \n"+data[0].getDescription())};

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, 
				android.R.layout.simple_list_item_1, resultText);
			listView = (ListView) findViewById(R.id.listView1);
			listView.setAdapter(adapter);
		}
		else
			Toast.makeText(MainActivity.this, "sorry, there is no data", 1).show();
	}	
}