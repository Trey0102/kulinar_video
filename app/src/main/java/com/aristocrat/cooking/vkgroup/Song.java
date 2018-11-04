package com.aristocrat.cooking.vkgroup;

import android.text.Html;
import org.json.JSONException;
import org.json.JSONObject;

public class Song extends Media{
	  private String artist;
	  private String lyricsId;
	  private String lyricsText;
	  
	  public Song(){
		  super();
	  }
	  
	  public Song(Song song){
	    super(song.duration, song.size, song.title, song.url);
	    this.artist = song.artist;
	    this.lyricsId = song.lyricsId;
	    this.lyricsText = song.lyricsText;
	    this.id = song.id;
	    this.ownerId = song.ownerId;
	  }
	
	  public static Song parse(JSONObject paramJSONObject) throws JSONException  {
	    Song localSong = new Song();
	    if(paramJSONObject.has("artist")) localSong.setArtist(Html.fromHtml(paramJSONObject.getString("artist")).toString());
	    if(paramJSONObject.has("title")) localSong.setTitle(Html.fromHtml(paramJSONObject.getString("title")).toString());
	    if(paramJSONObject.has("duration"))	localSong.setDuration(paramJSONObject.getLong("duration"));
	    if(paramJSONObject.has("url"))	localSong.setUrl(paramJSONObject.getString("url"));
	    if(paramJSONObject.has("lyrics_id"))localSong.setLyricsId(paramJSONObject.getString("lyrics_id"));
	    if(paramJSONObject.has("aid"))localSong.setId(paramJSONObject.getString("aid"));
	    if(paramJSONObject.has("id"))localSong.setId(paramJSONObject.getString("id"));
	    if(paramJSONObject.has("owner_id"))localSong.setOwnerId(paramJSONObject.getString("owner_id"));
	   
	    return localSong;
	  }
	
	  public String getArtist(){
		  return this.artist;
	  }
	  
	  public void setArtist(String paramString){
		  this.artist = paramString;
	  }

	public String getLyricsId() {
		return lyricsId;
	}

	public void setLyricsId(String lyricsId) {
		this.lyricsId = lyricsId;
	}

	public String getLyricsText() {
		return lyricsText;
	}

	public void setLyricsText(String lyricsText) {
		this.lyricsText = lyricsText;
	}	  
}