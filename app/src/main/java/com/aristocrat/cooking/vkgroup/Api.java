package com.aristocrat.cooking.vkgroup;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class  Api
{
	public static final String BASE_URL = "https://api.vk.com/method/";

	public static enum Groups{ 
		WISDOMS("-23170931", "-43208313", "-34520223", "-36181626", "-55876962", "-37371171","-53261322", "-49459445", "-47159402"),
		TOST("-27653829", "-26727967","-53304912","-53321346","-33631833"),
//		http://vk.com/pritci -27653829
//      http://vk.com/toasts -26727967
//      http://vk.com/toastland -53304912
//      http://vk.com/club44864960 -44864960
//      http://vk.com/tap4you -53321346
//      http://vk.com/pozdrav1 -33631833
		
		HEALTH_LIFE("-16945011", "-34414185", "-18189633", "-14088672", "-18422366", "-54146284"),
		
//		http://vk.com/zdorovoe_telo -16945011
//		http://vk.com/zdravalab -34414185
//		http://vk.com/diets -18189633
//		http://vk.com/zdorovie_i_krasota -14088672
//		http://vk.com/club18422366 -18422366

		SALADS("-34749428","-45604232","-26305436","-16320785","-39841268","-37968454","-43549297","-43253633","-55871062"),
		
		
//      http://vk.com/vkusnye_salaty -34749428
//      http://vk.com/bigcookbook -45604232
//      http://vk.com/club26305436 -26305436
//      http://vk.com/club16320785 -16320785 
//      http://vk.com/mirsalata -39841268
//      http://vk.com/salat_12rus -37968454
//      http://vk.com/ovsal -43549297
//      http://vk.com/receptysalatov -43253633
//      http://vk.com/salad96 -55871062
		HAND_MADE("-18371622", "-21445524", "-47170588","-34131214"),
		
//		https://vk.com/handmademore -18371622
//		https://vk.com/vseinstrumenti -21445524
//		https://vk.com/pl_hm -47170588
//		https://vk.com/mnogodelki -34131214
		
		LIFEHACK("-58414397", "-34300769", "-40567146", "-39341390", "-24738928"),
		//https://vk.com/public24738928
//		http://vk.com/lifehacks -58414397
//		http://vk.com/lifehackforyou -34300769
//		http://vk.com/secret_4_u -40567146
//		https://vk.com/lfhck -39341390
		
		DIETS("-36263341","-43259590","-18189633","-15811158","-35395355"),
//		https://vk.com/tvoya_figura_klass -36263341
//		https://vk.com/femininestyle -43259590
//		https://vk.com/diets -18189633
//		https://vk.com/club15811158 -15811158
//		https://vk.com/dieta.mira -35395355
//todo asd
		//combined
		DISHES("-37584963","-43722178","-47980470","-43697759","-48219356","-63919922","-18318978","-55809968","-35781804"),
		//СУПЫ И ЗАВТРАКИ
		//DISHES("-37584963","-43722178"),

		//СОУСЫ
		//DISHES("-47980470","-43697759","-48219356"),

		//НАПИТКИ
		//DISHES("-63919922","-18318978","-55809968","-35781804",),
//todo asd
		//DISHES("-4806561","-2315986","-32231484", "-32509740", "-40828279" ),
		//DISHES("-41554252", "-40095494", "-39009769" ),
		//DISHES("-4806561","-2315986","-43879004","-39051301","-54136595","-40168190","-35067856","-36888803","-47113831"),
//	    http://vk.com/art.cookery -39051301
//      http://vk.com/vkusnoe_bludo -54136595
//      http://vk.com/speso -39945294
//      http://vk.com/vkusnaya_straniza -40168190
//      http://vk.com/edimkrasivo -35067856
//      http://vk.com/recepty_s_foto -36888803
//      http://vk.com/holidayrecept -47113831

		
		HAIR("-39708850","-34468726","-24673049","-40234699","-42233561"),
		
//		http://vk.com/simple_hairstile_for_every_day -39708850
//		http://vk.com/escanlee -34468726
//		http://vk.com/d115021648 -24673049
//		http://vk.com/club40234699 -40234699
//		http://vk.com/club42233561 -42233561
		
		ILLUSTRATIONS("-45267255","-53513849","-30388285","-40934356"),		
//		  http://vk.com/illustration_cat -45267255
//        http://vk.com/illustrated_beautifully -53513849
//        http://vk.com/printpatternillustrations -30388285
//        http://vk.com/iloveillustration -40934356
		
		COCKTAILS("-46491980", "-50307866", "-38445293", "-42084652", "-39114853", "-32061121", "-39431457"),
		
//		http://vk.com/nice_drinks -46491980
//		http://vk.com/liqueurs -50307866
//		http://vk.com/shooter_cocktails -38445293
//		http://vk.com/non_alcoholic_drinks -42084652
//		http://vk.com/koktejli -42825208
//		http://vk.com/shaker_club -39114853
//		http://vk.com/nonalcoholic -32061121
//		http://vk.com/alcoholic.cocktails.public -39431457
		
		PAINTING("-31786047", "-100889", "-45807548", "-40368555", "-35259113"),
//		http://vk.com/ipainting -31786047
//		http://vk.com/art_of_painting -100889
//		http://vk.com/kartiny_mira -45807548
//		http://vk.com/world_of_painting -40368555
//		http://vk.com/painting_russian -35259113
		
		DESSERT("-55273553","-40084675","-46732649","-45383707","-34234362","-65043155","-47340424","-41841473 ","-44565810","-37133845"),
		
//      http://vk.com/desserrer -55273553
//      http://vk.com/deserty_recepty_s_foto -40084675
//      http://vk.com/academy_dessert -46732649
//      http://vk.com/sladkiy_recept -45383707
//      http://vk.com/public34234362 -34234362
//      http://vk.com/vegdessert -65043155
//      http://vk.com/desertu -47340424
//      http://vk.com/mydessert -41841473 
//      http://vk.com/a_na_desert -44565810
//      http://vk.com/nadesert -37133845

		
		 GREAT_WORDS("-55589493","-50395167"),		
		 
//		   http://vk.com/zitati_velikih_ludej -55589493
//         http://vk.com/tsitaty_of_the_greatest_men -50395167
		 
		 GREATWORDS2("-60873758","-57019283"),
		 
//		   http://vk.com/citaty_best -60873758
//         http://vk.com/tobesexy -57019283
		 
		 GREAT_WORDS3("-66306899","-51979077"),
		 
		 
//		   http://vk.com/public66306899 -66306899
//         http://vk.com/quotesofgreat -51979077
		 
		 WISE_THOUGHTS("-48772640","-50868227"),
		 
//		   http://vk.com/putanisa  -48772640
//         http://vk.com/aphorisms_quote_ideas -50868227
		 
		 
		 GREATWORDS3("-50314827","-28981879"),
		 
//		   http://vk.com/vk_mudrslova -50314827
//         http://vk.com/cvl2012 -28981879 
		 
		 WISE_THOUGHTS2("-31551855","-23170931","-55876962"),
		 
//		 http://vk.com/greatwords -31551855
//			 -23170931
//		    -55876962
		
		 GREATWORDS4("-43208313","34520223","-36181626"),
		 
//		 -43208313
//		 -34520223
//		 -36181626
		 
		 GREAT_WORDS4("-37371171"," -53261322 ","-49459445 "," -47159402"),
		 
//		 -37371171
//		 -53261322 
//		 -49459445 
//		 -47159402
		 
		 LIFESTYLE1("-72769162","-49024973"),
		 
//		   http://vk.com/krasota.zdorovie.fitness -72769162
//         http://vk.com/health.lifestyle -49024973
		 
		 
		 LIFESTYLE6("-65440509","-6262639"),
		 
//		   http://vk.com/public65440509 -65440509
//         http://vk.com/slimdown_ru -6262639
		 
		 
		 LIFESTYLE5("-43259590","-35395355"),
//		 https://vk.com/femininestyle -43259590
//			https://vk.com/dieta.mira -35395355
		 
		 LIFESTYLE4("-46352648","-60082279"),
		 
//		   http://vk.com/lady906090 -46352648
//         http://vk.com/energija_zdorovja -60082279
		 
		 LIFESTYLE2("-16945011","-49411707"),
		 
//		   http://vk.com/zdorovoe_telo -16945011
//         http://vk.com/public49411707 -49411707

		 
		 
		 LIFESTYLE7("-47525482","-44760860"),
//		 http://vk.com/club47525482   -47525482
//		 http://vk.com/fitness_diety -44760860
		 
		 
		 LIFESTYLE3("-38324009","-14088672"),
		 
//		   http://vk.com/dietamnet -38324009
//		 http://vk.com/zdorovie_i_krasota -14088672
		 
		 
		 LIFESTYLE8("-35665206","-72457870","-51256722"),
		 
//         http://vk.com/loveorgainc -72457870
//         http://vk.com/fitnessdiety -51256722
		 
		 
//			http://vk.com/zdorovoe_telo -16945011
//			
//			
//			http://vk.com/zdorovie_i_krasota -14088672
//			http://vk.com/club18422366 -18422366
//			http://vk.com/healthwm -54146284
		 
		 HORROR2("-47243331","-28760271"),
//		   http://vk.com/obosris_ot_straha -47243331
//         http://vk.com/panicsu -28760271
		 
		 HORRORS("-40529013","-24742811"),
		 
//		   http://vk.com/horror_tales -40529013
//         http://vk.com/horroroff -24742811
		 
		 
		 SCARY_STORIES("-29286546","-56523430"),
		 
//		   http://vk.com/uuuua -29286546
//         http://vk.com/horror_memoirs -56523430
		 
		 
		 HORRORS2(" -39062459","-62915596"),
		
//		  http://vk.com/vsemk_ru -39062459
//        http://vk.com/ht_kz -62915596
		 
		 SCARY_STORIES2("-41088092","-68572794"),
		 
//		   http://vk.com/club41088092 -41088092
//         http://vk.com/ctpax01 -68572794
		 
		 HORROR3("-46487209","-29919347"),
		 
//		   http://vk.com/public46487209 -46487209
//         http://vk.com/strashulki -29919347
		 
		 HORROR("-45063326"," -23424999"),
		 
//		   http://vk.com/straxno -45063326
//         http://vk.com/ssikatno_com -23424999
		 
		 HORRORS3("-40612718","-51928856"),
		 
//		   http://vk.com/theblackskull -40612718
//         http://vk.com/public51928856 -51928856
		 
		 
		 DIY("-21445524","-32008648","-43688579"),
		 
//		   http://vk.com/vseinstrumenti -21445524
//         http://vk.com/club32008648 -32008648
//         http://vk.com/h.made -43688579
		 
		 SDELAI_SAM("-65647630","-26169184","-28943265"),
		 
//		   http://vk.com/be.kreativ -65647630
//         http://vk.com/rukikryki -26169184
//         http://vk.com/hands.make -28943265
		 
		 
		  DIY2("-42763971","-45244683"),
		 
//		   http://vk.com/kreaaativ -42763971
//         http://vk.com/yourself_do_itt -45244683
		 
		 SDELAI_SAM2("-41886576","-60116310"),
		 
//		   http://vk.com/sdelai__sam -41886576
//         http://vk.com/public_mdiy_step_by_step -60116310
		 
		 DIY3("-28009600","-40937739","-72948083"),
		 
		 
//          http://vk.com/home_hm -28009600
//          http://vk.com/u_will -40937739 
//          http://vk.com/magiya_bisera -72948083
		 
		 SDELAI_SAM3("-51624724","-41427996"),
		 
//		   http://vk.com/kreativnooo -51624724
//         http://vk.com/the_diy -41427996
		 
		 DIY4("-53079952","-68867777","-71610730"),
		 
//		   http://vk.com/club53079952 -53079952
//         http://vk.com/public68867777 -68867777
//         http://vk.com/forever_hand_made -71610730
		 
		 SDELAI_SAM4("-43236700","-68055626","-23662548"),
		 
//		  http://vk.com/ideya101 -43236700
//        http://vk.com/diywed -68055626
//        http://vk.com/fashion_style_ideas -23662548 
		 
		 MAKE_UP("-17146774","-36208551"),
		 
//		   http://vk.com/vse_dlya_devushki -17146774
//         http://vk.com/yourkrasota -36208551
		 
		 
		 MAKEUP("-25375935","-29383904 "),
		 
//		   http://vk.com/kosmeticka -25375935
//         http://vk.com/magazin.kosmetiki -29383904 
		 
		 MAKE_UP2("-57797738","-5020366"),
		 
//		   http://vk.com/makeup_now -57797738
//         http://vk.com/annbeautystore -5020366
		 
		 MAKEUP2("-12501265"," -53383003"),
		 
//		   http://vk.com/makiyazh24 -12501265
//         http://vk.com/club53383003 -53383003
		 
		 MAKE_UP3("-55302861","-29892672"),
		  
//		    http://vk.com/paletkin -55302861
//          http://vk.com/makeup_yours -29892672
		 
		 MAKEUP3("-30877952","-47730935"),
		 
//		   http://vk.com/style__2014 -30877952
//         http://vk.com/makeup_ideas -47730935
		 
		 MAKE_UP4("-39594572","-11136719"),
		 
		 
//           http://vk.com/artmake_up -39594572
//           http://vk.com/club11136719 -11136719
		 
		 MAKEUP4("-46903912","-42768859");
		 
		 
//          http://vk.com/public46903912 -46903912
//          http://vk.com/public.phpmakeup12 -42768859

		 
		private String[] ids;
		Groups(String...ids){
			this.ids = ids;
		}
		
		public String[] getIds(){
			return this.ids;
		}
	}
		
	private static Api INSTANCE;
	
	private Api(){
	}
	
	public static Api getINSTANCE(){
		if(null == INSTANCE){
			INSTANCE = new Api();
		}
		
		return INSTANCE;
	}
	
	public ArrayList<Post> getWallPostsByIds(String postCombinedIds) throws JSONException, IOException, VKException{
		Params params = new Params("wall.getById");
		
		params.put("posts",postCombinedIds);
		getJson(params);
		
		JSONObject jsonObj = getJson(params);
	    JSONArray json = jsonObj.getJSONArray("response");
	    
	    ArrayList<Post> posts = new ArrayList<Post>();
	    
	    for (int i = 0; i < json.length(); i++){
	    	posts.add(Post.parse((JSONObject)json.get(i)));	    	
	    }
	    
	    return posts;
	}
	
	
	private void checkError(JSONObject paramJSONObject) throws JSONException, VKException
	{
	    if (!paramJSONObject.isNull("error"))
	    {
	      JSONObject localJSONObject = paramJSONObject.getJSONObject("error");
	      int i = localJSONObject.getInt("error_code");
	      String str = localJSONObject.getString("error_msg");
	      throw new VKException(i, str);
	    }
	}

	public JSONObject getJson(Params paramParams)   throws IOException, JSONException, VKException
	{
		paramParams.put("v", "5.0");		
	    URLConnection localURLConnection = new URL(paramParams.getRequestUrl()).openConnection();
	    localURLConnection.setConnectTimeout(30000);
	    localURLConnection.setReadTimeout(30000);
	    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream(), "UTF8"));
	    String str = localBufferedReader.readLine();
	    localBufferedReader.close();
	    if (str == null)
	      throw new JSONException("JSON string is null");
	    JSONObject localJSONObject = new JSONObject(str);
	    checkError(localJSONObject);
	    return localJSONObject;	    
	}
	
	public void postJson(Params params)   throws IOException, JSONException, VKException
	{
		params.put("v", "5.0");		
		String[] reguestParams = params.getRequestUrl().split("\\?");
		String urlParameters = reguestParams[1];
		String request = reguestParams[0];
		URL url = new URL(request); 
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches (false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		System.out.println(connection.getResponseCode()); 
		
		connection.disconnect();
	}	
	
	private Params getRequestParams(String ownerId, int offset, int count){
		Params localParams = new Params("wall.get");
		localParams.put("owner_id", ownerId);
		localParams.put("count", count);
		localParams.put("offset", offset);
		localParams.put("filter", "owner");
		return localParams;
		
	}
	
	public ArrayList<Post> getPosts(Groups group, int offset, int count) throws JSONException, IOException, VKException{
		ArrayList<Post> posts = new ArrayList<Post>();		
		count /= group.getIds().length;
		offset /= group.getIds().length;
		
		for(int j = 0; j< group.getIds().length; ++j){		
			try{

			    JSONObject jsonObj = getJson(getRequestParams(group.getIds()[j], offset, count));
			    JSONArray json = jsonObj.getJSONObject("response").getJSONArray("items");
			    
			    for (int i = 1; i < json.length(); i++){
			    	Post p = Post.parse((JSONObject)json.get(i));
			    	if (null != p){
			    		int randomIndex = posts.size()/2;//(posts.size() == 0) ? 0 : (int) (Math.random() * (posts.size()-1));	
			    		posts.add(randomIndex, p);
			    	}
			    }
                Log.e("VK", String.valueOf(posts.size()));
			}catch (VKException e) {
				e.printStackTrace();
			} catch (SocketTimeoutException socketex){
                socketex.printStackTrace();
                return null;
            }
		}		
	    return posts;
	}	

	public void postToWall(Post post, String accessToken, String userId) throws IOException, JSONException, VKException{
		Params params = new Params("wall.post");
		
		String message = post.getText();
//		if(700 <  message.length()){
//			message = message.substring(0, 699);
//		}
		params.put("message",message);
		params.put("access_token", accessToken);		
		
		String attachments = "";
		for(Photo p : post.getPhotos()){
			attachments += Photo.TYPE+p.getOwnerId()+"_"+p.getId()+",";
		}
		if(0 != attachments.length())
			attachments = attachments.substring(0, attachments.length()-1);
		params.put("attachments", attachments);
		
//		JSONObject json = new JSONObject();
//		json.put("message",message);
//		
		postJson(params);
	}

	public static String getStringWithNoChars(String s){
		return s.replace('/', ' ')
				.replace('\\', ' ')
				.replace('\'', ' ')
				.replace("\"", "")
				.replace(':', ' ')
				.replace('*', ' ')
				.replace('?', ' ')
				.replace('<', ' ')
				.replace('>', ' ')
				.replace('|', ' ')
				.replace('.', ' ')
				.replace(',', ' ')
				.replace(';', ' ')
				.replace('\000', ' ').trim();
	}	
}