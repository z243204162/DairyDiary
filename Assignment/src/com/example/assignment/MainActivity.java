package com.example.assignment;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment.bean.Data;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String[] detail=new String[15];
	private String url="http://www.wincoredata.com/php/get_dealinfo.php?did=6&count=0";
	private ImageView iv;
	private ListView lv;
	private Button Btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv=(ImageView) findViewById(R.id.imageView1);
		Btn=(Button) findViewById(R.id.button1);
		
		Btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				try{ 
					getData();
				} catch (ArrayIndexOutOfBoundsException e) {
					Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
				} catch (NullPointerException e){
					Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
				} finally{
					//Toast.makeText(MainActivity.this,"error", Toast.LENGTH_LONG);
				}
			}
		});	
	}
	
	//sent Internet request to get json
	public void getData(){
		StringRequest request=new StringRequest(url,new Response.Listener<String>() {
			
			@Override
			//when request success 
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub		
				Log.i("info", arg0);	
				dealData(arg0);		
			}
			
			//when request fail 
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this,"fail to access to data", Toast.LENGTH_LONG).show();
			}
		});
		new Volley().newRequestQueue(getApplicationContext()).add(request);
	}
	
	//deal data with gson
	private void dealData(String result) {
		// TODO Auto-generated method stub
	    Gson gson = new Gson();
	    Data[] data = gson.fromJson(result, Data[].class);
	    
	    //load image from url 
	    Picasso.with(MainActivity.this).load(data[0].getImagePath()+data[0].getImageFileName()).into(iv);
	    
	    //put data into an array
	    detail[0]= "Deal Id: "+data[0].getDealId();
		detail[1]= "Retailer Id: "+data[0].getRetailerId();
		detail[2]= "Location Id: "+data[0].getLocationId();
		detail[3]= "Cat Code: "+data[0].getCatCode();
		detail[4]= "Item Name: "+data[0].getItemName();
		detail[5]= "Original Price: "+data[0].getOriginalPrice();
		detail[6]= "Discount Price: "+data[0].getDiscountPrice();
		detail[7]= "Percentage: "+data[0].getPercentage()+"%";
		detail[8]= "Units: "+data[0].getUnits();
		detail[9]= "Quantity: "+data[0].getQuantity();
		detail[10]= "Effective Date: "+data[0].getEffectiveDate();
		detail[11]= "Expiry Date: "+data[0].getExpiryDate();
		detail[12]= "Active: "+data[0].getActive();
		detail[13]= "Count Of Views: "+data[0].getCountOfViews();
		detail[14]= "Description: \n"+data[0].getDescription();
	    
	    //use adapter to put data into listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, 
			android.R.layout.simple_list_item_1, detail);
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(adapter);
    
	}
}
