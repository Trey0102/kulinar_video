package com.aristocrat.cooking;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by Nursultan on 01.10.2015.
 */
//public class AutoBoxView extends TokenCompleteTextView<String> {
public class AutoBoxView extends TokenCompleteTextView {

    public AutoBoxView(Context context) {
        super(context);
    }

    public AutoBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoBoxView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    protected View getViewForObject(Object o) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.ingredientbox, (ViewGroup) AutoBoxView.this.getParent(), false);
        TextView v=(TextView)view.findViewById(R.id.name);
        v.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_x, 0);
        v.setText((String)o);
        return view;
    }


    @Override
    protected String defaultObject(String s) {
        return s;
    }


}
