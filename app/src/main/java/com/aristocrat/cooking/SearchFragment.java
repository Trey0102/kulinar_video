package com.aristocrat.cooking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aristocrat.cooking.ImageLoader.core.ImageLoader;
import com.sharelib.SocialShareActivity;
import com.squareup.picasso.Picasso;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;

/**
 * Created by Nursultan on 01.10.2015.
 */
public class SearchFragment extends Fragment implements TokenCompleteTextView.TokenListener{

    private int categoryId=0;
    private ListView listView;
    AutoBoxView autoBoxView;
    RadioGroup searchTypeSwitch;
    EditText editText;
    TextView autoCounter;
    ArrayAdapter<String> ingredientAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_searchlist, container, false);
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
        MainActivity.getInstance().changeTitle("Поиск");
        ArrayList<String> usedIngredients= SearchMapCreator.getInstance(getActivity()).getUsedIngredients();
        ingredientAdapter=new FilteredArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, usedIngredients) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(android.R.layout.simple_list_item_1, parent, false);
                }
                String p = getItem(position);
                ((TextView) convertView).setText(p);
                ((TextView) convertView).setTextColor(Color.BLACK);
                //((TextView)convertView.findViewById(R.id.name)).setText(p);
                //((TextView)convertView.findViewById(R.id.email)).setText(p.getEmail());
                return convertView;
            }

            @Override
            protected boolean keepObject(String s, String mask) {
                mask = mask.toLowerCase();
                s=s.toLowerCase();
                if(s.toLowerCase().startsWith(mask)){
                    return true;
                }else if(s.contains(" ")) {
                    String[] separate=s.split(" ");
                    for(String word:separate){
                        if(word.startsWith(mask)){
                            return true;
                        }
                    }
                }
                return false;
            }
        };


        searchTypeSwitch= (RadioGroup) getActivity().findViewById(R.id.search_type_radiogroup);
        searchTypeSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.search_by_name_button:{
                        hideKeyboard();
                        editText.setText("");
                        editText.setVisibility(View.VISIBLE);
                        autoBoxView.clearFocus();
                        autoBoxView.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.search_by_ingredient_button: {
                        hideKeyboard();
                        //autoBoxView.clear();
                        updateTokenConfirmation();
                        autoBoxView.setVisibility(View.VISIBLE);
                        editText.clearFocus();
                        editText.setVisibility(View.GONE);
                        break;
                    }
                }
            }
        });

        editText= (EditText) getActivity().findViewById(R.id.search_by_name_edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<RecipeObj> result=new ArrayList<RecipeObj>();
                if(s.length()>0) {
                     result = SearchMapCreator.getInstance(getActivity()).findByName(s.toString(), 0);
                    if (result.size() > 0) {
                        autoCounter.setVisibility(View.VISIBLE);
                        autoCounter.setText("Найдено рецептов: " + result.size());
                    } else {
                        autoCounter.setVisibility(View.GONE);
                    }
                }else {
                    autoCounter.setVisibility(View.GONE);
                }
                listView.setAdapter(new RecipeAdapter(getActivity(),R.layout.single_list,result));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        autoBoxView=(AutoBoxView)getActivity().findViewById(R.id.autoboxview);
        autoCounter=(TextView)getActivity().findViewById(R.id.autoboxview_count);
        autoBoxView.setVisibility(View.VISIBLE);
        autoBoxView.setAdapter(ingredientAdapter);
        autoBoxView.setTokenListener(this);
        autoBoxView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
        if (savedInstanceState == null) {
//            autoBoxView.setPrefix("Введите ингредиенты: "); //нужен для префикса
        }

        listView = (ListView) getActivity().findViewById(R.id.list);

        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
                try {
                    RecipeFragment recipeFragment = new RecipeFragment();
                    ((MainActivity) getActivity()).setSentObj((RecipeObj) listView.getAdapter().getItem(position));
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, recipeFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                catch (NullPointerException npe){
                    npe.printStackTrace();
                }

            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                hideKeyboard();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        searchTypeSwitch.check(R.id.search_by_ingredient_button);
        if(autoBoxView.getObjects().size()>0){
            updateTokenConfirmation();
        }
    }

    private void hideKeyboard(){
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(autoBoxView.getWindowToken(), 0);
    }

    private void updateTokenConfirmation() {
        ArrayList<Object> items= (ArrayList<Object>)autoBoxView.getObjects();

        ArrayList<String> strings=new ArrayList<>();
        for(Object o: items){
            strings.add((String)o);
        }
        ArrayList<RecipeObj> result= SearchMapCreator.getInstance(getActivity()).findByIngredients(strings, 0);
        if(items.size()>0){
            autoCounter.setVisibility(View.VISIBLE);
            autoCounter.setText("Найдено рецептов: "+result.size());
        }else {
            autoCounter.setVisibility(View.GONE);
        }
        listView.setAdapter(new RecipeAdapter(getActivity(),R.layout.single_list,result));
    }


    @Override
    public void onTokenAdded(Object token) {
        updateTokenConfirmation();
    }

    @Override
    public void onTokenRemoved(Object token) {
        updateTokenConfirmation();
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
                        if (getItem(i).isFavorited()) {
                            Log.e("SearchFragment", "unfavorited");
                            clickholder.singlefavbtn.setImageResource(R.drawable.favico);
                            getItem(i).setFavorited(false);
                            InfoLoader.getInstance(getActivity()).setFavorited(getItem(i).getCategoryName(), String.valueOf(getItem(i).getFileName()), false);
                        } else {
                            Log.e("SearchFragment", "favorited");
                            clickholder.singlefavbtn.setImageResource(R.drawable.favicoenabled);
                            getItem(i).setFavorited(true);
                            InfoLoader.getInstance(getActivity()).setFavorited(getItem(i).getCategoryName(), String.valueOf(getItem(i).getFileName()), true);
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
                            MainActivity.getInstance().postImgToVKWall("Опубликовано через приложение Кулинарная Книга для Android \n https://play.google.com/store/apps/details?id=com.aristocrat.cooking \n" + getItem(i).getRecipeName() + "\n" + getItem(i).getRecipeText(), getItem(i).getCategoryName(), String.valueOf(getItem(i).getFileName()));
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
                    if (getItem(i).getFileName() == i + 1) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = getItem(i).getRecipeName() + "\n" + getItem(i).getRecipeText();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getItem(i).getRecipeName());
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Cooking"));
                    }
                }
            });

            if(getItem(i).getPicUrl()!=null){
                ImageLoader.getInstance().displayImage(getItem(i).getPicUrl(),holder.singlepic,getActivity());
            }else {
                ImageLoader.getInstance().displayImage("assets://"+getItem(i).getCategoryName()+"/"+getItem(i).getFileName()+".jpg",holder.singlepic,getActivity());
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
