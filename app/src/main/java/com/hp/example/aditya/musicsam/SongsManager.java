package com.hp.example.aditya.musicsam;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {

	final String MEDIA_PATH = new String("/sdcard1/");
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	

	public SongsManager(){
		File root=new File(MEDIA_PATH);
		getPlayList(root);
		
	}
	

	public void getPlayList(File root){

		File[] files=root.listFiles();
		for(File singleFile : files){
			if(singleFile.isDirectory()&& !singleFile.isHidden()){
				getPlayList(singleFile);
			}
			else{
				if(singleFile.getName().endsWith(".mp3")|| singleFile.getName().endsWith(".wav")){

					HashMap<String, String> song = new HashMap<String, String>();
					song.put("songTitle", singleFile.getName().substring(0, (singleFile.getName().length() - 4)));
					song.put("songPath", singleFile.getPath());


					songsList.add(song);
				}
			}

		}


		/*if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
				song.put("songPath", file.getPath());
				
				// Adding each song to SongList
				songsList.add(song);
			}
		}*/


	}
	


}
