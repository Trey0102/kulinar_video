package com.aristocrat.cooking.vkgroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aristocrat.cooking.ImageLoader.core.ImageLoader;
import com.aristocrat.cooking.InfoLoader;
import com.aristocrat.cooking.MainActivity;
import com.aristocrat.cooking.R;
import com.aristocrat.cooking.RecipeObj;
import com.aristocrat.cooking.SearchMapCreator;

import com.google.gson.Gson;
import com.sharelib.TwitterShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class VKListFragment extends Fragment{

    private ListView listView;
    private GetTask getPostsTask;
    private ProgressDialog loading;
    private Boolean loadingStarted = false;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private VKListFragment vklistgragment=null;


    public VKListFragment(){
        vklistgragment=this;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        System.out.println("ON_CREATE "+ savedInstanceState);
        setRetainInstance(true);
        if(null != savedInstanceState){
            try {

                posts = new ArrayList<Post>();
                String[] strs = savedInstanceState.getStringArray("posts");
                for (String str : strs) {
                    posts.add(Post.parse(new JSONObject(str)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).hideFavButton();
    }



    @Override
    public void onPause() {
        super.onPause();
        InfoLoader.getInstance(getActivity()).saveFavorites();
        vklistgragment=null;
        if((getPostsTask!=null) && (getPostsTask.getStatus()== AsyncTask.Status.RUNNING)){
            getPostsTask.cancel(true);
        }

        /*if (null != loading && loading.isShowing()){
            loading.dismiss();
        }
        loadingStarted=false;
        //getPostsTask.cancel(true);



       /* if (!getPostsTask.isCancelled()){
            taskwasrunning=true;
            getPostsTask.cancel(true);
            loadingStarted=false;
            loading.cancel();
        }else {
            taskwasrunning=false;
        }*/

    }






    @Override
    public void onSaveInstanceState(Bundle outState) {
        String[] c = new String[posts.size()];
        for(int i = 0; i< posts.size();++i){
            c[i] = posts.get(i).toString();
        }
        outState.putStringArray("posts", c);


        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vklist, null);
        RelativeLayout loadingbox=(RelativeLayout)v.findViewById(R.id.loading);
        listView = (ListView)v.findViewById(R.id.list);
        listView.setDividerHeight(0);



        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView lw, final int firstVisibleItem,
                                 final int visibleItemCount, final int totalItemCount) {

                if (loadingStarted || totalItemCount <= 1) {
                    return;
                }
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    // I load the next page of gigs using a background task,
                    // but you can call any function here.
                    loading = new ProgressDialog(getActivity());
                    loading.show();
                    loading.setCancelable(true);
                    loading.setCanceledOnTouchOutside(false);
                    Integer offset = getPostsTask.getOffset();

                    getPostsTask = new GetTask();
                    getPostsTask.setOffset(offset + GetTask.count);
                    switch (getScreenOrientation()) {
                        case Configuration.ORIENTATION_PORTRAIT:
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            break;
                        case Configuration.ORIENTATION_LANDSCAPE:
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            break;
                    }
                    getPostsTask.execute();
                    loadingStarted = true;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int row,
                                    long arg3) {
                Post post = ((Post) listView.getAdapter().getItem(row));

                VKRecipeFragment vkRecipeFragment=new VKRecipeFragment();
                vkRecipeFragment.setInfo(post);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, vkRecipeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        if(posts.isEmpty()){
            switch (getScreenOrientation()){
                case Configuration.ORIENTATION_PORTRAIT: getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break;
                case Configuration.ORIENTATION_LANDSCAPE: getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break;
            }
            getPostsTask = new GetTask();
            getPostsTask.execute();
        }else{
            loadingbox.setVisibility(View.GONE);
        }
        listView.setAdapter(new ParentPostAdapter(posts, getActivity()));
        return v;
    }

    public class GetTask extends AsyncTask<Void, Void, ArrayList<Post>>{

        private Integer offset = 0;
        public static final int count = 60;

        public void setOffset(Integer offset){
            this.offset = offset;
        }

        public Integer getOffset(){
            return this.offset;
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            Api api = Api.getINSTANCE();
            try {
                ArrayList<Post> allPosts=api.getPosts(Api.Groups.DISHES, offset, count);
                Gson gson=new Gson();
                for(Post post:allPosts){
                    RecipeObj recipeObj=new RecipeObj();
                    if(null != post.getText() && 0 != post.getText().length()){
                        recipeObj.setRecipeName(getCuttedText(post.getText()));
                    }
                    recipeObj.setFileName(Integer.valueOf(post.getId()));
                    recipeObj.setRecipeText(post.getText());

                    if(null != post.getPhotos() && !post.getPhotos().isEmpty()){
                        recipeObj.setPhotos(post.getPhotos());
//                        int index = (post.getPhotos().get(0).getPhotoUrls().size() >= 3)? 2 : post.getPhotos().get(0).getPhotoUrls().size();
//                        String url = post.getPhotos().get(0).getPhotoUrls().get(index);
                        //recipeObj.setPicurl(url);
                    }
                    SearchMapCreator.getInstance(getActivity()).writeRecipe(recipeObj,gson);
                }
                return  allPosts;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (VKException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Post> result) {



            if(vklistgragment!=null) {
                getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
            }


                if (null == result) {
                    try {


                        new AlertDialog.Builder(getActivity()).setTitle("Ой").setMessage("Не удалось получить рецепты, попробуйте еще раз").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        if (posts.isEmpty())
                                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                                    }
                                }).create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

               Integer index = posts.size();
               posts.addAll(index, result);
            Log.d("posts","size "+posts.size());

                listView.setAdapter(new ParentPostAdapter(posts, getActivity()));
                listView.setSelection((index > 0) ? index - 1 : index);
                //banner.setVisibility(View.VISIBLE);
			/*
			try{
				if(index == 0)
					((ParentMainActivity)getActivity()).showMainBg();
			}catch(Exception e){
				
			}*/
                if (null != loading && loading.isShowing()){
                    loading.dismiss();
                }



                loadingStarted = false;

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                super.onPostExecute(result);

        }


    }

    public class ParentPostAdapter extends ArrayAdapter<Post> {

        protected ArrayList<Post> posts;
        protected Activity context;


        public ParentPostAdapter(ArrayList<Post> posts, Activity context){
            super(context,R.layout.single_list,posts);
            this.posts = posts;
            this.context = context;
            Log.d("PArentPostAdapter", posts.size() + "");
        }


        @Override
        public int getCount() {
            if(null == posts)
                return 0;
            return posts.size();
        }
        @Override
        public Post getItem(int pos) {
            return posts.get(pos);
        }
        @Override
        public long getItemId(int pos) {
            return Long.parseLong(posts.get(pos).getId());
        }



        @Override
        public View getView(final int pos, View convertview, ViewGroup viewGroup) {

            final ViewHolder holder;
            if(convertview==null){
                LayoutInflater inflater= LayoutInflater.from(viewGroup.getContext());
                convertview=inflater.inflate(R.layout.single_list,viewGroup,false);
                holder=new ViewHolder();
                holder.cardinfo=(LinearLayout)convertview.findViewById(R.id.cardinfo);
                holder.singlecard=(RelativeLayout)convertview.findViewById(R.id.singlecard);
                holder.singlepic=(ImageView)convertview.findViewById(R.id.singlepic);
                holder.singletxt=(TextView)convertview.findViewById(R.id.singletxt);
                holder.singlebottom=(RelativeLayout)convertview.findViewById(R.id.singlebottom);
                holder.singlefavbtn=(ImageView)convertview.findViewById(R.id.singlefavbtn);
                holder.cardshadow=(RelativeLayout)convertview.findViewById(R.id.cardshadow);
                holder.singlemail=(ImageView)convertview.findViewById(R.id.singlemail);
                holder.singlefb=(ImageView)convertview.findViewById(R.id.singlefb);
                holder.singlevk=(ImageView)convertview.findViewById(R.id.singlevk);
                holder.singletweet=(ImageView)convertview.findViewById(R.id.singletweet);
                holder.singlepiccontainer=(RelativeLayout)convertview.findViewById(R.id.singlepiccontainer);
                holder.singlepicframe=(ImageView)convertview.findViewById(R.id.singlepicframe);
                convertview.setTag(holder);
            }
            else {
                holder= (ViewHolder)convertview.getTag();
            }
            holder.singletxt.setTypeface(MainActivity.getInstance().typeface);
            if(null != posts.get(pos).getText() && 0 != posts.get(pos).getText().length()){
                holder.singletxt.setText(getCuttedText(posts.get(pos).getText()));
            }
            if(null != posts.get(pos).getPhotos() && !posts.get(pos).getPhotos().isEmpty()){
                int index = (posts.get(pos).getPhotos().get(0).getPhotoUrls().size() >= 3)? 2 : posts.get(pos).getPhotos().get(0).getPhotoUrls().size();
                String url = posts.get(pos).getPhotos().get(0).getPhotoUrls().get(index);

                    ImageLoader.getInstance().displayImage(url, holder.singlepic, getActivity());

            }

            holder.singlefavbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Long.parseLong(getItem(pos).getId()) == getItemId(pos)){
                        if(!getItem(pos).isFavorited()){
                            RecipeObj tempObj=new RecipeObj();
                            tempObj.setFavorited(true);
                            tempObj.setRecipeText(getItem(pos).getText());
                            tempObj.setRecipeName( holder.singletxt.getText().toString());
                            tempObj.setFileName(((int) getItemId(pos)));

                            if(null != posts.get(pos).getPhotos() && !posts.get(pos).getPhotos().isEmpty()){
                                int index = (posts.get(pos).getPhotos().get(0).getPhotoUrls().size() >= 3)? 2 : posts.get(pos).getPhotos().get(0).getPhotoUrls().size();
                                String url = posts.get(pos).getPhotos().get(0).getPhotoUrls().get(index);
                                tempObj.setPicurl(url);
                                tempObj.setPhotos(posts.get(pos).getPhotos());
                            }

                            holder.singlefavbtn.setImageResource(R.drawable.favicoenabled);
                            getItem(pos).setFavorited(true);
                            InfoLoader.getInstance(getActivity()).setForumFavorited(String.valueOf(getItemId(pos)),tempObj);
                        }
                        else {
                            holder.singlefavbtn.setImageResource(R.drawable.favico);
                            getItem(pos).setFavorited(false);
                            RecipeObj tempObj=new RecipeObj();
                            tempObj.setFavorited(false);
                            tempObj.setRecipeText(getItem(pos).getText());
                            tempObj.setRecipeName( holder.singletxt.getText().toString());
                            tempObj.setFileName(((int) getItemId(pos)));
                            if(null != posts.get(pos).getPhotos() && !posts.get(pos).getPhotos().isEmpty()){
                                int index = (posts.get(pos).getPhotos().get(0).getPhotoUrls().size() >= 3)? 2 : posts.get(pos).getPhotos().get(0).getPhotoUrls().size();
                                String url = posts.get(pos).getPhotos().get(0).getPhotoUrls().get(index);
                                tempObj.setPicurl(url);
                                tempObj.setPhotos(posts.get(pos).getPhotos());
                            }
                            InfoLoader.getInstance(getActivity()).setForumFavorited(String.valueOf(getItem(pos).getId()),tempObj,false);
                        }
                    }
                }
            });

            if (getItem(pos).isFavorited()){
                holder.singlefavbtn.setImageResource(R.drawable.favicoenabled);
            }else {
                holder.singlefavbtn.setImageResource(R.drawable.favico);
            }

            holder.singlevk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Long.parseLong(getItem(pos).getId()) == getItemId(pos)) {
                        if (MainActivity.getInstance().haveNetworkConnection()) {
                            MainActivity.getInstance().postImgToVKWallforum(getItem(pos).getText(), holder.singlepic.getDrawable());
                        }
                        else {
                            ((Activity) MainActivity.getInstance()).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
            holder.singletweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Long.parseLong(getItem(pos).getId()) == getItemId(pos))  {
                        if (MainActivity.getInstance().haveNetworkConnection()) {
                            String[] picurl=new String[1];
                            picurl[0]=  getItem(pos).getPhotos().get(0).getPhotoUrls().get(getItem(pos).getPhotos().get(0).getPhotoUrls().size() - 1);
                            TwitterShare.makeTweet(getActivity(), holder.singletxt.getText().toString()+"\n https://play.google.com/store/apps/details?id="+getActivity().getPackageName(), picurl);
                        }else {
                            Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            holder.singlefb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Long.parseLong(getItem(pos).getId()) == getItemId(pos)) {
                        if(MainActivity.getInstance().haveNetworkConnection()){
                            MainActivity.getInstance().postToFBwithImage(holder.singletxt.getText().toString(),getItem(pos).getText(),getItem(pos).getPhotos().get(0).getPhotoUrls().get(getItem(pos).getPhotos().get(0).getPhotoUrls().size() - 1));
                        }else {
                            Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            holder.singlemail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Long.parseLong(getItem(pos).getId()) == getItemId(pos)) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = getItem(pos).getText();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getCuttedText(getItem(pos).getText()));
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Cooking"));
                    }
                }
            });
            return convertview;
        }

        private class ViewHolder{
            public RelativeLayout singlecard;
            public LinearLayout cardinfo;
            public ImageView singlepic;
            public TextView singletxt;
            public RelativeLayout singlebottom;
            public ImageView singlefavbtn;
            public RelativeLayout cardshadow;
            public ImageView singlemail;
            public ImageView singletweet;
            public ImageView singlefb;
            public ImageView singlevk;
            public RelativeLayout singlepiccontainer;
            public ImageView singlepicframe;
        }
    }

    public  String getCuttedText(String originalText){
        try{
            int dotindex=originalText.indexOf(".");
            int breakindex=originalText.indexOf("\n");
            if ((breakindex<dotindex)&&(breakindex>0)){
                return (originalText.substring(0,breakindex));
            }
            if((breakindex>dotindex)&&(dotindex>0)){
                return originalText.substring(0,dotindex);
            }
            return originalText;

        }catch (StringIndexOutOfBoundsException e){
            return originalText;
        }
    }

    public int getScreenOrientation()
    {
        Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }



}
