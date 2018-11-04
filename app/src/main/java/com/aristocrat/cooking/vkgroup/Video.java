package com.aristocrat.cooking.vkgroup;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class Video extends Media  {
	
	private String description;
	private String thumb;
	private String imageMedium;
	private String playerUrl;
	private String accessKey;
	private Bitmap bitmap;
	private LinkedHashMap<String, String> urls;
	
	public static String TYPE = "video";
	
	public Video() {
		super();
	}
	
	public static final Creator<Video> CREATOR =
	   new Creator<Video>(){
	
	    @Override
	    public Video createFromParcel(Parcel source) {
	    	return new Video(source);
	    }
	
	    @Override
	    public Video[] newArray(int size) {
	    	return new Video[size];
	    }
	 };
	 
	 public Video(Parcel source){
		 readFromParcel(source);
	 }
	
	 @Override
	 public int describeContents() {
		 return 0;
	 }
	
	 @Override
	 public void writeToParcel(Parcel dest, int flags) {
		 super.writeToParcel(dest, flags);
		 dest.writeString(id);
		 dest.writeString(description);
		 dest.writeString(imageMedium);
		 dest.writeString(ownerId);
		 dest.writeString(playerUrl);
		 dest.writeString(thumb);
	 }
	 
	 public void readFromParcel(Parcel source){
		 super.readFromParcel(source);
		 id = source.readString();
		 description = source.readString();
		 imageMedium = source.readString();
		 ownerId = source.readString();
		 playerUrl = source.readString();
		 thumb = source.readString();
	 }
	 
	 public Video(String id, String title, String description, String ownerId,
				Long duration, String thumb, String imageMedium, String url, Long size, String playerUrl, LinkedHashMap<String, String> urls) {
		super(duration, size, title, url);
		this.id = id;
		this.description = description;
		this.ownerId = ownerId;
		this.thumb = thumb;
		this.imageMedium = imageMedium;
		this.playerUrl = playerUrl;
		this.urls = urls;
	 }
	
	public Video(Video video){
		this(video.getId(), video.getTitle(), video.getDescription(), video.getOwnerId(), 
				video.getDuration(), video.getThumb(), video.getImageMedium(), video.getUrl(), 
				video.getSize(), video.getPlayerUrl(), video.getUrls());
	}

	public static Video parse(JSONObject paramJSONObject)  throws JSONException {
	    Video video = new Video();
	    
	    if(paramJSONObject.has("id")) video.setId(Html.fromHtml(paramJSONObject.getString("id")).toString());
	    if(paramJSONObject.has("vid")) video.setId(Html.fromHtml(paramJSONObject.getString("vid")).toString());
	    if(paramJSONObject.has("title")) video.setTitle(Html.fromHtml(paramJSONObject.getString("title")).toString());	    
	    if(paramJSONObject.has("duration")) video.setDuration(paramJSONObject.getLong("duration"));
	    if(paramJSONObject.has("description")) video.setDescription(Html.fromHtml(paramJSONObject.getString("description")).toString());
	    if(paramJSONObject.has("owner_id")) video.setOwnerId(Html.fromHtml(paramJSONObject.getString("owner_id")).toString());
	    if(paramJSONObject.has("thumb")) video.setThumb(Html.fromHtml(paramJSONObject.getString("thumb")).toString());
	    if(paramJSONObject.has("photo_130")) video.setThumb(Html.fromHtml(paramJSONObject.getString("photo_130")).toString());	
	    if(paramJSONObject.has("photo_320")) video.setImageMedium(Html.fromHtml(paramJSONObject.getString("photo_320")).toString());
	    if(paramJSONObject.has("player")) video.setPlayerUrl(Html.fromHtml(paramJSONObject.getString("player")).toString());
	    if(paramJSONObject.has("access_key")) video.setAccessKey(Html.fromHtml(paramJSONObject.getString("access_key")).toString());

	    return video;
	}
	
	
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getThumb() {
		return thumb;
	}


	public void setThumb(String thumb) {
		this.thumb = thumb;
	}


	public String getImageMedium() {
		return imageMedium;
	}


	public void setImageMedium(String imageMedium) {
		this.imageMedium = imageMedium;
	}


	public Bitmap getBitmap() {
		return bitmap;
	}


	public void setBitmap(Bitmap image) {
		this.bitmap = image;
	}


	public String getPlayerUrl() {
		return playerUrl;
	}

	public void setPlayerUrl(String playerUrl) {
		this.playerUrl = playerUrl;
	}

	public LinkedHashMap<String, String> getUrls() {
		return urls;
	}

	public void setUrls(LinkedHashMap<String, String> urls) {
		this.urls = urls;
	}		
	public String parseUrlForExtention(){
		if(url.startsWith("http://") || url.startsWith("https://")){
			String quality = this.url.substring(0, this.url.lastIndexOf('.'));					
			quality = quality.substring(quality.lastIndexOf('.')+1);
			return quality;
		}
		String quality = url.replace(Api.getStringWithNoChars(this.title), "");
		quality = quality.replace(".mp4", "").trim();
		quality = quality.substring(quality.length()-4, quality.length()-1);
		//Log.d("Video", quality);
		return quality;
	}

	
	
	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	@Override
	public String toString() {
		return "Video [description=" + description + ", thumb=" + thumb
				+ ", imageMedium=" + imageMedium + ", playerUrl=" + playerUrl
				+ ", bitmap=" + bitmap + ", urls=" + urls + "]";
	}
	
	
}