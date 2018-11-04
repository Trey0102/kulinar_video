package com.aristocrat.cooking.vkgroup;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Params extends HashMap<String, String>
{
  
	private static final long serialVersionUID = 3121679067245034776L;
	private String methodName;

	public Params(String paramString)
	{
		this.methodName = paramString;
	}
	
	public String getUrlWithoutParams(){
		return "https://api.vk.com/method/" + this.methodName;
	}
	
	public JSONObject getJson() throws JSONException{
		JSONObject json = new JSONObject();
		
		Iterator<Entry<String, String>> localIterator = entrySet().iterator();
	    while (localIterator.hasNext()){
	    	Entry<String, String> localEntry = (Entry<String, String>)localIterator.next();
	      	json.put((String)localEntry.getKey(), Uri.encode((String)localEntry.getValue()));
	    }
	    return json;
	}

	public String getRequestUrl()
	{
	    String str1 = "https://api.vk.com/method/" + this.methodName + "?";
	    String str2 = "";
	    Iterator<Entry<String, String>> localIterator = entrySet().iterator();
	    while (localIterator.hasNext())
	    {
	      Entry<String, String> localEntry = (Entry<String, String>)localIterator.next();
	      if (str2.length() != 0){
              str2 += "&";
          }
	      str2 += (String)localEntry.getKey() + "=" + Uri.encode((String)localEntry.getValue());
	    }
	    return str1 + str2;

	}
	
	public String put(String paramString, Object param)
	{
	    return (String)super.put(paramString, param.toString());
	}

}