package com.dariydiary.newdiary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DownloadTool extends AsyncTask<String, Integer, Uri>{
	private Context context;
	private File img_file;
	private Uri imgUri;
	private ProgressDialog progressDialog;
	
	public DownloadTool(Context context, File img_file ) {
		this.img_file= img_file;
	    this.context = context;   
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub			
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("downloading");
		progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(100);
		progressDialog.setProgress(0);
		progressDialog.show();
	}
	
	@Override
	protected Uri doInBackground(String... params) {
		// TODO Auto-generated method stub
		String path = params[0];
		
		try {
			
			URL url = new URL(path);
			InputStream inputStream = url.openStream();
			FileOutputStream outputStream = new FileOutputStream(img_file);
			
			byte[] data = new byte[2048];			
			int dataSize;
			int loadedSize = 0;
			    while ((dataSize = inputStream.read(data)) != -1) {
			        loadedSize += dataSize;
			        publishProgress(loadedSize);
			        outputStream.write(data, 0, dataSize);
			    }
			    
			inputStream.close();
			outputStream.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		} 
		
		return imgUri;
	}
	protected void onProgressUpdate(Integer... values) {
		progressDialog.setProgress(values[0]);
		
	}
	@Override
    protected void onPostExecute(Uri result) {
		Log.i("info", "download finished");
		Toast.makeText(context, "background image is downloaded", Toast.LENGTH_SHORT).show();
		progressDialog.hide();		
    }
}
