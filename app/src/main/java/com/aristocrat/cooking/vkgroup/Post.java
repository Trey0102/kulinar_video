package com.aristocrat.cooking.vkgroup;

import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class Post {
	private String id;
	private String fromId;
	private String toId;
	private Long date;
	private PostType postType;
	private String text;
	private ArrayList<Photo> photos;
	private ArrayList<Document> docs;
	private Video video;
	private boolean favorited;

    public void setFavorited(boolean favorited){
        this.favorited = favorited;
    }
    public boolean isFavorited(){
        return this.favorited;
    }
	
	public static enum PostType{
		POST, COPY,	REPLY, POSTPONE, SUGGEST
	}	
	
	@Override
	public String toString() {
		  
		  String attachments = "";
		  if(null != photos){
		   for(int i = 0; i< photos.size();++i){
		    attachments+="{ type: '"+Photo.TYPE+"', photo:" + photos.get(i)+((i+1 == photos.size())?"}":"},");
		   }   
		  }
		  
		  return "{id:" + id + "," +
		      "from_id:" + fromId + "," +
		      "to_id:" + toId+ "," +
		      "date:" + date + "," +
		      "post_type:'" + postType + "'," +
		      "text:'" + text + "'," +
		      "attachments:["+attachments+"]"+
		    "}";
	}

	public static Post parse(JSONObject json) throws JSONException  {
		if(json.has("post_type") && PostType.POST != PostType.valueOf(json.getString("post_type").toUpperCase(Locale.ENGLISH))){
			return null;
		}

					
		Post post = new Post();
		if(json.has("id")) post.setId(Html.fromHtml(json.getString("id")).toString());
		if(json.has("from_id")) post.setFromId(Html.fromHtml(json.getString("from_id")).toString());
		if(json.has("to_id"))post.setToId(json.getString("to_id"));
		if(json.has("date"))post.setDate(json.getLong("date"));
		if(json.has("post_type"))post.setPostType(PostType.valueOf(json.getString("post_type").toUpperCase(Locale.ENGLISH)));
		if(json.has("text")){
            post.setText(json.getString("text").replace("<br>", "\n"));
        }else {
            return null;
        }
        if(post.getText().length()<2){
            return null;
        }
		if( null != post.getText() && (post.getText().contains("[club") || post.getText().contains("http://") ||post.getText().contains("https://") || post.getText().contains("www.")))
			return null;
		
		if(json.has("attachments")){
			if(isPageSubsription(json)){
				return null;
			}
			if(null != getVideoFromAttachment(json))
				return null;
			post.setPhotos(getPhotoFromAttachments(json));		
			post.setDocs(getDocsFromAttachments(json));
			
		}
		return post;
	}
	private static Boolean isPageSubsription(JSONObject json) throws JSONException{
		JSONArray localJSONArray = json.getJSONArray("attachments");
		for (int i = 0; i < localJSONArray.length(); i++){
	    	if( localJSONArray.getJSONObject(i).has("type") && 
	    		localJSONArray.getJSONObject(i).getString("type").equals("page")){
	    		return true;   		
	    	}
	    }

		return false;

	}


    strictfp

	private static ArrayList<Photo> getPhotoFromAttachments(JSONObject json) throws JSONException{
		ArrayList<Photo> photos = new ArrayList<Photo>();
		
		JSONArray localJSONArray = json.getJSONArray("attachments");
	    
	    for (int i = 0; i < localJSONArray.length(); i++){
	    	if( localJSONArray.getJSONObject(i).has("type") && 
	    		localJSONArray.getJSONObject(i).getString("type").equals(Photo.TYPE)){
	    		photos.add(Photo.parse(localJSONArray.getJSONObject(i).getJSONObject(Photo.TYPE)));		    		
	    	}
	    }
		return photos;
	}
	
	private static Video getVideoFromAttachment(JSONObject json) throws JSONException{		
		JSONArray localJSONArray = json.getJSONArray("attachments");
		for (int i = 0; i < localJSONArray.length(); i++){
		    if(localJSONArray.getJSONObject(0).has("type") && localJSONArray.getJSONObject(0).getString("type").equals(Video.TYPE)){
		    	return new Video();    		
		    }	    
		}
		return null;
	}
	
	
	
	private static ArrayList<Document> getDocsFromAttachments(JSONObject json) throws JSONException{
		ArrayList<Document> docs = new ArrayList<Document>();
		
		JSONArray localJSONArray = json.getJSONArray("attachments");
	    for (int i = 0; i < localJSONArray.length(); i++){
	    	if( localJSONArray.getJSONObject(i).has("type") && 
	    		localJSONArray.getJSONObject(i).getString("type").equals(Document.TYPE)){
	    		docs.add(Document.parse(localJSONArray.getJSONObject(i).getJSONObject(Document.TYPE)));		    		
	    	}
	    }
		return docs;
	}
	
	public Post(){

	}

	public String getId() {
		return id;
	}

	public String getFromId() {
		return fromId;
	}

	public String getToId() {
		return toId;
	}

	public Long getDate() {
		return date;
	}

	public PostType getPostType() {
		return postType;
	}

	public String getText() {
		return text;
	}

	public ArrayList<Photo> getPhotos() {
		return photos;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public void setPostType(PostType postType) {
		this.postType = postType;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setPhotos(ArrayList<Photo>  photos) {
		this.photos = photos;
	}

	public ArrayList<Document> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<Document> docs) {
		this.docs = docs;
	}

	public void setVideo(Video video){
		this.video = video;
	}
	
	public Video getVideo(){
		return this.video;
	}

	
	
	
}
