package com.aristocrat.cooking;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Nursultan on 01.10.2015.
 */
public class TokenLayout extends LinearLayout {

    public TokenLayout(Context context) {
        super(context);
    }

    public TokenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        TextView v = (TextView)findViewById(R.id.name);
//        if (selected) {
            v.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.close_x, 0);
//        } else {
//            v.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
//        }
    }
}