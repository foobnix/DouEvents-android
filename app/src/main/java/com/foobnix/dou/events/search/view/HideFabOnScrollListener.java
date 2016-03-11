package com.foobnix.dou.events.search.view;

import android.support.design.widget.FloatingActionButton;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by yarolegovich on 11.03.2016.
 */
public class HideFabOnScrollListener implements AbsListView.OnScrollListener {

    public static void bindTo(FloatingActionButton fab, ListView listView) {
        HideFabOnScrollListener listener = new HideFabOnScrollListener(fab);
        listView.setOnScrollListener(listener);
    }

    private int mPreviousVisible;
    private FloatingActionButton fab;

    public HideFabOnScrollListener(FloatingActionButton fab) {
        this.fab = fab;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem > mPreviousVisible) {
            if (fab != null) {
                fab.hide();
            }
        } else if (firstVisibleItem < mPreviousVisible) {
            if (fab != null) {
                fab.show();
            }
        }
        mPreviousVisible = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }
}