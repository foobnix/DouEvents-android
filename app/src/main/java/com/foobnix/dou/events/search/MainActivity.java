package com.foobnix.dou.events.search;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.foobnix.dou.events.search.dou.DouCity;
import com.foobnix.dou.events.search.dou.DouEvent;
import com.foobnix.dou.events.search.dou.DouTag;
import com.foobnix.dou.events.search.net.DouServices;
import com.foobnix.dou.events.search.net.ResponseCallback;
import com.foobnix.dou.events.search.view.EventsAdapter;
import com.foobnix.dou.events.search.view.HideFabOnScrollListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int FIRST_PAGE = 1;
    public static final String TAG = "TEST";

    private SwipeRefreshLayout swipeContainer;
    private EventsAdapter listAdapter;

    private int page = 1;
    private Button moreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView vCities = (TextView) findViewById(R.id.tCity);
        final TextView vTags = (TextView) findViewById(R.id.tEvents);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.materialPrimaryBlue,
                R.color.materialPrimaryGreen,
                R.color.materialPrimaryRed,
                R.color.materialPrimaryAmber);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents(FIRST_PAGE);
            }
        });

        final ListView list = (ListView) findViewById(R.id.listView);
        ViewCompat.setNestedScrollingEnabled(list, true);
        listAdapter = new EventsAdapter(this, new ArrayList<DouEvent>());
        list.setAdapter(listAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        HideFabOnScrollListener.bindTo(fab, list);
        fab.setOnClickListener(this);

        moreButton = new Button(this);
        moreButton.setBackgroundColor(Color.TRANSPARENT);
        list.addFooterView(moreButton);

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEvents(++page);
            }
        });

        loadEvents(FIRST_PAGE);

        DouServices.get().getCities().enqueue(new ResponseCallback<List<DouCity>>() {
            @Override
            public void onResponse(Call<List<DouCity>> call, Response<List<DouCity>> response) {
                final List<DouCity> douCities = response.body();
                vCities.setText(Html.fromHtml(AppConfig.get.city));

                vCities.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(MainActivity.this, vCities);

                        for (DouCity item : douCities) {
                            popupMenu.getMenu().addSubMenu(item.getName());
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AppConfig.get.city = item.getTitle().toString();
                                vCities.setText(item.getTitle());
                                loadEvents(FIRST_PAGE);
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        });

        DouServices.get().getTags().enqueue(new ResponseCallback<List<DouTag>>() {
            @Override
            public void onResponse(Call<List<DouTag>> call, Response<List<DouTag>> response) {
                final List<DouTag> douCities = response.body();
                vTags.setText(Html.fromHtml(AppConfig.get.tag));
                vTags.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(MainActivity.this, vTags);

                        for (DouTag item : douCities) {
                            popupMenu.getMenu().addSubMenu(item.getName());
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AppConfig.get.tag = item.getTitle().toString();
                                vTags.setText(item.getTitle());
                                loadEvents(FIRST_PAGE);
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                addDouEvent();
                break;
        }
    }

    private void addDouEvent() {
        Uri addEventPage = Uri.parse("http://dou.ua/calendar/add/");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, addEventPage);
        startActivity(browserIntent);
    }

    public void loadEvents(final int page) {
        moreButton.setText(R.string.loading);
        swipeContainer.setRefreshing(true);
        DouServices.getAllEvents(AppConfig.get.tag, AppConfig.get.city, page).enqueue(new Callback<List<DouEvent>>() {
            @Override
            public void onResponse(Call<List<DouEvent>> call, Response<List<DouEvent>> response) {
                List<DouEvent> body = response.body();

                if (body == null || body.isEmpty() || body.size() < DouServices.PAGE_SIZE) {
                    moreButton.setVisibility(View.GONE);
                } else {
                    moreButton.setVisibility(View.VISIBLE);
                    moreButton.setText(R.string.more);
                }

                if (page == 1) {
                    listAdapter.clear();
                }
                listAdapter.addAll(body);
                listAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<DouEvent>> call, Throwable t) {
                Log.e(TAG, "Response erorr", t);
                swipeContainer.setRefreshing(false);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        AppConfig.save(this);
    }


}
