package com.foobnix.dou.events.search.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foobnix.dou.events.search.MainActivity;
import com.foobnix.dou.events.search.R;
import com.foobnix.dou.events.search.dou.DouEvent;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Locale;

/**
 * Created by yarolegovich on 11.03.2016.
 */
public class EventsAdapter extends ArrayAdapter<DouEvent> {

    public EventsAdapter(Context context, List<DouEvent> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventHolder eh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_event, parent, false);
            eh = new EventHolder(convertView);
            convertView.setTag(eh);
        } else eh = (EventHolder) convertView.getTag();

        final DouEvent event = getItem(position);

        eh.event = event;

        ImageLoader.getInstance().displayImage(event.getImgUrl(), eh.image);

        eh.date.setText(String.format("%s,%s", event.getDate(), event.getAddress()));
        eh.title.setText(event.getTitle().trim());
        eh.description.setText(event.getDescription());

        return convertView;
    }

    public static class EventHolder implements View.OnClickListener {

        private DouEvent event;

        private View itemView;

        private TextView title;
        private TextView date;
        private TextView description;
        private ImageView image;

        public EventHolder(View v) {
            itemView = v;

            title = (TextView) v.findViewById(R.id.title);
            date = (TextView) v.findViewById(R.id.dateText);
            description = (TextView) v.findViewById(R.id.description);
            image = (ImageView) v.findViewById(R.id.imageView);

            v.findViewById(R.id.explore).setOnClickListener(this);
            v.findViewById(R.id.map).setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_root:
                case R.id.explore:
                    openEvent(v.getContext());
                    break;
                case R.id.map:
                    openMap(v);
                    break;
            }
        }

        private void openEvent(Context context) {
            Uri eventUri = Uri.parse(event.getWebUrl());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, eventUri);
            context.startActivity(browserIntent);
        }

        private void openMap(View v) {
            try {
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", event.getAddress());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                itemView.getContext().startActivity(intent);
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "No activity", e);
            }
        }
    }
}
