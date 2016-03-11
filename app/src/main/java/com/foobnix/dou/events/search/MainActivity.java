package com.foobnix.dou.events.search;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final int FIRST_PAGE = 1;
    public static final String TAG = "TEST";
    SwipeRefreshLayout swipeContainer;
    MyAdapter listAdapter;
    int page = 1;
    private Button moreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final TextView vCities = (TextView) toolbar.findViewById(R.id.tCity);
        final TextView vTags = (TextView) toolbar.findViewById(R.id.tEvents);

        vCities.setText(Html.fromHtml("<u>...</u>"));
        vTags.setText(Html.fromHtml("<u>...</u>"));


        ImageView plus = (ImageView) toolbar.findViewById(R.id.addEvent);
        plus.setColorFilter(vTags.getCurrentTextColor());
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://dou.ua/calendar/add/"));
                startActivity(browserIntent);
            }
        });


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents(FIRST_PAGE);
            }
        });


        final ListView list = (ListView) findViewById(R.id.listView);


        listAdapter = new MyAdapter();
        list.setAdapter(listAdapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(listAdapter.getList().get(position).getWebUrl()));
                startActivity(browserIntent);
            }
        });

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
                vCities.setText(Html.fromHtml("<u>" + AppConfig.get.city + "</u>"));

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
                                vCities.setText(Html.fromHtml("<u>" + item.getTitle() + "</u>"));
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
                vTags.setText(Html.fromHtml("<u>" + AppConfig.get.tag + "</u>"));

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
                                vTags.setText(Html.fromHtml("<u>" + item.getTitle() + "</u>"));
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
                    listAdapter.getList().clear();
                }
                listAdapter.getList().addAll(body);
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

    class MyAdapter extends BaseAdapter {

        List<DouEvent> list = new ArrayList<>();

        public List<DouEvent> getList() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflate = convertView;
            if (inflate == null) {
                inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.row_event, parent, false);
            }
            TextView title = (TextView) inflate.findViewById(R.id.title);
            TextView dateTxt = (TextView) inflate.findViewById(R.id.dateText);
            TextView description = (TextView) inflate.findViewById(R.id.description);
            ImageView image = (ImageView) inflate.findViewById(R.id.imageView);

            final DouEvent event = list.get(position);

            final View menuItem = inflate.findViewById(R.id.imageMenu);
            menuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuItem);
                    popupMenu.getMenu().addSubMenu(R.string.map);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            try {
                                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", event.getAddress());
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                v.getContext().startActivity(intent);
                            } catch (Exception e) {
                                Log.e(TAG, "No activity", e);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });


            ImageLoader.getInstance().displayImage(event.getImgUrl(), image);


            dateTxt.setText(event.getDate() + "," + event.getAddress());
            title.setText(event.getTitle());
            description.setText(event.getDescription());

            return inflate;
        }
    }
}
