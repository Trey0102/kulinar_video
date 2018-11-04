package com.sharelib;

import com.vk.sdk.VKScope;

public class Constants {
    public static final String TAG = "ShareLibAuth";

    public static final String TWI_API_KEY = "uRKZePL5am8oHecHDkGmIeqR2";
    public static final String TWI_API_SECRET = "I2eJDkFiMWCJvGELMjwnubXLGlLmt8mHIbJ4SpofLk7B15yPPg";

    public static final String TWI_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    public static final String TWI_ACCESS_URL = "https://api.twitter.com/oauth/access_token";
    public static final String TWI_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

    public static final String TWI_OAUTH_CALLBACK_URL = "http://vkgroups.com/";

    public static final String VK_APP_ID = "4018150";

    public static final String[] VK_SCOPE = new String[]{ VKScope.WALL, VKScope.PHOTOS, VKScope.OFFLINE, VKScope.NOHTTPS };

    public static final String VK_TOKEN_KEY = "VK_ACCESS_TOKEN";

    public static final String DEVELOPER_KEY = "AIzaSyA4sGFhoTso0QbicIWxroSFXG09ksOHsGg";
}

