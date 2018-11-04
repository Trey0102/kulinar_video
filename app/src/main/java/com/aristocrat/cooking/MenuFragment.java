package com.aristocrat.cooking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aristocrat.cooking.vkgroup.VKListFragment;

/**
 * Created by Erzhan on 24-Sep 14.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{
    private RelativeLayout hotbtn;
    private RelativeLayout snackbtn;
    private RelativeLayout breakbtn;
    private RelativeLayout desbtn;
    private RelativeLayout soupbtn;
    private RelativeLayout sldbtn;
    private RelativeLayout saucebtn;
    private RelativeLayout drinkbtn;
    private RelativeLayout forumbtn;
    private RelativeLayout favbtn;
    private TextView hottv;
    private TextView snacktv;
    private TextView breaktv;
    private TextView destv;
    private TextView souptv;
    private TextView sldtv;
    private TextView saucetv;
    private TextView drinktv;
    private TextView forumtv;
    private TextView favtv;
    private TextView favcount;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_menu, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        MainActivity.getInstance().showSearchButton();
        MainActivity.getInstance().changeTitle(0);
        hotbtn=(RelativeLayout) getActivity().findViewById(R.id.cathot);
        snackbtn=(RelativeLayout) getActivity().findViewById(R.id.catsnack);
        breakbtn=(RelativeLayout) getActivity().findViewById(R.id.catbreak);
        desbtn=(RelativeLayout) getActivity().findViewById(R.id.catdes);
        soupbtn=(RelativeLayout) getActivity().findViewById(R.id.catsoup);
        sldbtn=(RelativeLayout) getActivity().findViewById(R.id.catsld);
        saucebtn=(RelativeLayout) getActivity().findViewById(R.id.catsauce);
        drinkbtn=(RelativeLayout) getActivity().findViewById(R.id.catdrink);
        forumbtn=(RelativeLayout) getActivity().findViewById(R.id.catforum);
        favbtn=(RelativeLayout)getActivity().findViewById(R.id.catfav);
        hottv=(TextView)getActivity().findViewById(R.id.cathottxt);
        snacktv=(TextView)getActivity().findViewById(R.id.catsnacktxt);
        breaktv=(TextView)getActivity().findViewById(R.id.catbreaktxt);
        destv=(TextView)getActivity().findViewById(R.id.catdestxt);
        souptv=(TextView)getActivity().findViewById(R.id.catsouptxt);
        sldtv=(TextView)getActivity().findViewById(R.id.catsldtxt);
        saucetv=(TextView)getActivity().findViewById(R.id.catsaucetxt);
        drinktv=(TextView)getActivity().findViewById(R.id.catdrinktxt);
        forumtv=(TextView)getActivity().findViewById(R.id.catforumtxt);
        favtv=(TextView)getActivity().findViewById(R.id.catfavtxt);

        hottv.setTypeface(MainActivity.getInstance().typeface);
        snacktv.setTypeface(MainActivity.getInstance().typeface);
        breaktv.setTypeface(MainActivity.getInstance().typeface);
        destv.setTypeface(MainActivity.getInstance().typeface);
        souptv.setTypeface(MainActivity.getInstance().typeface);
        sldtv.setTypeface(MainActivity.getInstance().typeface);
        saucetv.setTypeface(MainActivity.getInstance().typeface);
        drinktv.setTypeface(MainActivity.getInstance().typeface);
        forumtv.setTypeface(MainActivity.getInstance().typeface);
        favtv.setTypeface(MainActivity.getInstance().typeface);
        hotbtn.setOnClickListener(this);
        snackbtn.setOnClickListener(this);
        breakbtn.setOnClickListener(this);
        desbtn.setOnClickListener(this);
        soupbtn.setOnClickListener(this);
        sldbtn.setOnClickListener(this);
        saucebtn.setOnClickListener(this);
        drinkbtn.setOnClickListener(this);
        forumbtn.setOnClickListener(this);
        //favbtn.setOnClickListener(this);
        //favcount.setText(String.valueOf(InfoLoader.getInstance(getActivity()).getFavoritesCount()));

        MainActivity.getInstance().updateFavButton();
        MainActivity.getInstance().showSearchButton();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cathot: openList(1); break;   // 1 Горячие
            case R.id.catsnack: openList(2); break; // 2 Закуски
            case R.id.catbreak: openList(3); break; // 3 завтраки
            case R.id.catdes: openList(4); break;   // 4 десерты
            case R.id.catsoup: openList(5); break;  // 5 супы
            case R.id.catsld: openList(6); break;   // 6 салаты
            case R.id.catsauce: openList(7); break; // 7 соусы
            case R.id.catdrink: openList(8); break; // 8 напитки
            case R.id.catfav: {
                openList(11);
            }; break;
            case R.id.catforum: openForum();break;
        }
    }



    private void openList(int categoryId ){
        MainActivity.getInstance().changeTitle(categoryId);
        ListFragment listFragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt("KEY_CATEGORY",categoryId);
        listFragment.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, listFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openForum(){
        MainActivity.getInstance().changeTitle(9);
        VKListFragment vkListFragment = new VKListFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, vkListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
