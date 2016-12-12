package com.dariydiary.newdiary;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Environment;

public class FileCreater {
	
	public File creatFileForImage(String FileName){
		
		File new_folder =new File(Environment.getExternalStorageDirectory()+"/"+FileName);
		if (!new_folder.exists()) {
			new_folder.mkdir();
		}
		File img_file = new File(new_folder,getTime()+".jpg");
		return img_file;
	}
	public String getTime(){
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-HH:mm:ss,yyyy");
		Date curDate = new Date();
		String str = format.format(curDate);
		return str;
	}
	
}
