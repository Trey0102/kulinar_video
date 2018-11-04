package com.aristocrat.cooking.vkgroup;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Media implements Parcelable{
	protected Long duration;
	protected Long size;
	protected String title;
	protected String url;
	protected String id;
	protected String ownerId;
	  
	public Media(){		  
	}

	public Media(Long duration, Long size, String title, String url) {
		super();
		this.duration = duration;
		this.size = size;
		this.title = title;
		this.url = url;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int describeContents() {		
		return 0;
	}

	
	 @Override
	 public void writeToParcel(Parcel dest, int flags) {
		 dest.writeLong(duration);
		 dest.writeLong(size);
		 dest.writeString(title);
		 dest.writeString(url);
	 }
	 
	 public void readFromParcel(Parcel source){
		 duration = source.readLong();
		 size = source.readLong();
		 title = source.readString();
		 url = source.readString();
	 }
	 
	 public static String getMediaName(Media media){
			return ((media instanceof Song) ? 
					((Song)media).getArtist() + " - " : ((Video)media).parseUrlForExtention()+"p.") + media.getTitle();		    
	 }
	 
	 
	 public String getTypeExtension(){
		 return getTypeExtension(this.url);
	 }
	 
	 public static String getTypeExtension(String url){
		 if(url.startsWith("http://") || url.startsWith("https://")){
			 Log.d("Media", url.substring(url.lastIndexOf(".")));
			 String temp = url.substring(url.lastIndexOf("."));
			 if(!temp.contains("?")){
				 return temp;
			 }
			 return temp.substring(0, temp.indexOf("?"));			 
		 }
		 //Log.d("Media", url.substring(url.length()-4));
		 return url.substring(url.length()-4);
	 }

	 public String getId() {
		 return id;
	 }

	 public void setId(String id) {
		 this.id = id;
	 }

	 public String getOwnerId() {
		 return ownerId;
	 }

	 public void setOwnerId(String ownerId) {
		 this.ownerId = ownerId;
	 }

	public String getCombinedId() {
		return this.ownerId + "_" + this.id;
	}

	public String getDownloadId() {
		return getCombinedId();
	}
}
