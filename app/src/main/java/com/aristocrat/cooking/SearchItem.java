package com.aristocrat.cooking;

/**
 * Created by Nursultan on 21.09.2015.
 */
public class SearchItem {

    public int categoryId;
    public int filename;

    @Override
    public boolean equals(Object o) {
        SearchItem item=(SearchItem)o;
        if(this.categoryId==item.categoryId && this.filename==item.filename){
            return true;
        }else {
            return false;
        }
        //return super.equals(o);
    }
}
