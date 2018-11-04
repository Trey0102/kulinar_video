package com.aristocrat.cooking.vkgroup;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

public class Document {
	public static String TYPE = "doc";
	private String id;
	private String ownerId;
	private String title;
	private Long size;
	private String extension;
	private String url;
	private String accessKey;
	
	public Document(){
		
	}
	
	public static Document parse(JSONObject json) throws JSONException  {
		Document doc = new Document();
		if(json.has("id")) 		doc.setId(Html.fromHtml(json.getString("id")).toString());
		if(json.has("owner_id")) doc.setOwnerId(json.getString("owner_id"));
		if(json.has("title")) 	doc.setTitle(json.getString("title"));
		if(json.has("size")) 	doc.setSize(json.getLong("size"));
		if(json.has("extension"))doc.setExtension(json.getString("extension"));
		if(json.has("url")) 	doc.setUrl(json.getString("url"));
		if(json.has("access_key"))doc.setAccessKey(json.getString("access_key"));
		

		return doc;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	@Override
	public String toString() {
		return "File: " + title;
	}
	
	
	
}
