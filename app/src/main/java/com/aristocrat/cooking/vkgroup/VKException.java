package com.aristocrat.cooking.vkgroup;

import java.util.List;

public class VKException extends Exception{
	private static final long serialVersionUID = 8790009540950704707L;
	
	private int errorCode;

	public VKException(int paramInt, String paramString)
	{
		super(paramString);
		this.errorCode = paramInt;
		//Log.d("VKException", "errorCode = " + errorCode);
	}

    public int getCode()
	{
		return this.errorCode;
	}


}

