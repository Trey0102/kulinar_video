package com.aristocrat.cooking;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aristocrat.cooking.ImageLoader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ListFragment extends android.support.v4.app.Fragment {
    private int categoryId=0;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.gc();
        categoryId=getArguments().getInt("KEY_CATEGORY");
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();

        InfoLoader.getInstance(getActivity()).saveFavorites();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        MainActivity.getInstance().hideFavButton();
        listView=(ListView)getActivity().findViewById(R.id.list);
        listView.setEmptyView(getActivity().findViewById(android.R.id.empty));
        //listView.setAdapter(new RecipeAdapter(getActivity(), R.layout.single_list, InfoLoader.getInstance(getActivity()).loadRecipes(categoryId)));
        listView.setAdapter(new RecipeAdapter(getActivity(),R.layout.single_list, SearchMapCreator.getInstance(getActivity()).allRecipeMap.get(categoryId)));
        listView.setDividerHeight(0);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecipeFragment recipeFragment=new RecipeFragment();
                ((MainActivity)getActivity()).setSentObj((RecipeObj)listView.getAdapter().getItem(position));
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, recipeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    public class RecipeAdapter extends ArrayAdapter<RecipeObj> {
        private ArrayList<RecipeObj> recipeObjs = new ArrayList<RecipeObj>();

        RecipeAdapter(Context context,int layoutResourceId,ArrayList<RecipeObj> recipeObjs){
            super(context,layoutResourceId,recipeObjs);
            this.recipeObjs =recipeObjs;
        }

        public RecipeObj getItem(int position){
            return this.recipeObjs.get(position);
        }

        public int getCount(){
            return this.recipeObjs.size();

        }

        @Override
        public View getView(final int i, View convertview, ViewGroup viewGroup) {
            ViewHolder holder;
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
                holder.badge=(ImageView)convertview.findViewById(R.id.badge);
                convertview.setTag(holder);
            }
            else {
                holder= (ViewHolder)convertview.getTag();
            }
            holder.badge.setVisibility(View.GONE);
            holder.singletxt.setTypeface(MainActivity.getInstance().typeface);
            holder.singletxt.setText(getItem(i).getRecipeName());
            final ViewHolder clickholder=holder;
            holder.singlefavbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getItem(i).getFileName()==i+1){
                        if(getItem(i).isFavorited()){
                            clickholder.singlefavbtn.setImageResource(R.drawable.favico);
                            getItem(i).setFavorited(false);
                            InfoLoader.getInstance(getActivity()).setFavorited(getItem(i).getCategoryName(),String.valueOf(getItem(i).getFileName()),false);
                        }else {
                            clickholder.singlefavbtn.setImageResource(R.drawable.favicoenabled);
                            getItem(i).setFavorited(true);
                            InfoLoader.getInstance(getActivity()).setFavorited(getItem(i).getCategoryName(),String.valueOf(getItem(i).getFileName()),true);
                        }
                    }
                }
            });

            if(getItem(i).isFavorited()){
                holder.singlefavbtn.setImageResource(R.drawable.favicoenabled);
            }
            else {
                holder.singlefavbtn.setImageResource(R.drawable.favico);
            }

            holder.singlevk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getItem(i).getFileName() == i + 1) {
                        if (MainActivity.getInstance().haveNetworkConnection()) {
                            MainActivity.getInstance().postImgToVKWall("Опубликовано через приложение Кулинарная Книга для Android \n https://play.google.com/store/apps/details?id=com.aristocrat.cooking \n"+getItem(i).getRecipeName() + "\n" + getItem(i).getRecipeText(), getItem(i).getCategoryName(), String.valueOf(getItem(i).getFileName()));
                        }
                        else {
                            ((Activity) MainActivity.getInstance()).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            holder.singletweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getItem(i).getFileName()==i+1) {
                        if (MainActivity.getInstance().haveNetworkConnection()) {
                            MainActivity.getInstance().tweetWithImages(getItem(i).getRecipeName(), getItem(i).getRecipeText(), getItem(i).getCategoryName(), String.valueOf(getItem(i).getFileName()));
                        }else {
                            Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });




            holder.singlefb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getItem(i).getFileName()==i+1){
                        if(MainActivity.getInstance().haveNetworkConnection()){
                            MainActivity.getInstance().postToFB(getItem(i).getRecipeName(),getItem(i).getRecipeText());
                        }else {
                            Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            holder.singlemail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getItem(i).getFileName()==i+1){
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = getItem(i).getRecipeName()+"\n"+getItem(i).getRecipeText();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getItem(i).getRecipeName());
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Cooking"));
                    }
                }
            });

            if(getItem(i).getPicUrl()!=null){
                ImageLoader.getInstance().displayImage(getItem(i).getPicUrl(), holder.singlepic, getActivity());
            }else {
                ImageLoader.getInstance().displayImage("assets://" + getItem(i).getCategoryName() + "/" + getItem(i).getFileName() + ".jpg", holder.singlepic, getActivity());
            }
            return convertview;
        }
        private class ViewHolder{
            public ImageView badge;
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


}
