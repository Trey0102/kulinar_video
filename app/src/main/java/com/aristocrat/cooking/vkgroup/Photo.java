package com.aristocrat.cooking.vkgroup;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Photo {
	public static String TYPE = "photo";
	private String id;
	private String albumId;
	private String ownerId;
	private String userId;
	private ArrayList<String> photoUrls = new ArrayList<String>();
	private String width;
	private String height;
	private String text;
	private Long date;
	private String postId;
	
	public Photo(){
		
	}
	


	@Override
	public String toString() {
		String photosStr = "";
		for(int i = 0; i < photoUrls.size(); ++i){
			switch(i){
			case 0:
				photosStr+="photo_75";
				break;
			case 1:
				photosStr+="photo_130";
				break;
			case 2:
				photosStr+="photo_604";
				break;
			case 3:
				photosStr+="photo_807";
				break;
			case 4:
				photosStr+="photo_1280";
				break;
			case 5:
				photosStr+="photo_2560";
				break;
			}
			photosStr+=":'"+photoUrls.get(i)+"'"+((i+1==photoUrls.size())?"":",");
		}
		
		
		
		return "{" +
				"id:" + id + "," +
				"album_id:" + albumId + "," +
				"owner_id:"+ ownerId + "," +
				"user_id:" + userId + ","+ 
				 photosStr	+ "," +
				"width:" + width + "," +
				"height:" + height + "," +
				"text:'" + text+"'," +
				"date:" + date + "," +
				"post_id:" + postId + 
				"}";
	}
	
	public static Photo parse(JSONObject json) throws JSONException  {
		Photo photo = new Photo();
		if(json.has("id")) 			photo.setId(Html.fromHtml(json.getString("id")).toString());
		if(json.has("album_id")) 	photo.setAlbumId(Html.fromHtml(json.getString("album_id")).toString());
		if(json.has("owner_id"))	photo.setOwnerId(json.getString("owner_id"));
		if(json.has("user_id"))		photo.setUserId(json.getString("user_id"));
		
		ArrayList<String> urls = new ArrayList<String>();		
		
		if(json.has("photo_75"))	urls.add(json.getString("photo_75"));	
		if(json.has("photo_130"))	urls.add(json.getString("photo_130"));
		if(json.has("photo_604"))	urls.add(json.getString("photo_604"));
		if(json.has("photo_807"))	urls.add(json.getString("photo_807"));
		if(json.has("photo_1280"))	urls.add(json.getString("photo_1280"));
		if(json.has("photo_2560"))	urls.add(json.getString("photo_2560"));		
		
		photo.setPhotoUrls(urls);
		
		
		if(json.has("width"))	photo.setWidth(json.getString("width"));
		if(json.has("height"))	photo.setHeight(json.getString("height"));
		if(json.has("text"))	photo.setText(json.getString("text").replace("<br>", "\n"));
		if(json.has("date"))	photo.setDate(json.getLong("date"));
		if(json.has("post_id"))	photo.setPostId(json.getString("post_id"));
		

		return photo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ArrayList<String> getPhotoUrls() {
		return photoUrls;
	}

	public void setPhotoUrls(ArrayList<String> photoUrls) {
		this.photoUrls = photoUrls;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}	
}
