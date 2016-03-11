package com.foobnix.dou.events.search.net;

import java.util.List;

import com.foobnix.dou.events.search.dou.DouConverterFactory;
import com.foobnix.dou.events.search.dou.DouEvent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by ivan-dev on 11.03.16.
 */
public final class DouServices {

    public static final String ALL_CITYIES = "все города";
    public static final String ALL_TAGS = "все темы";
    public static final int PAGE_SIZE = 20;

    private static final DouServices INSTANCE = new DouServices();
    private static Retrofit retrofit;

    private DouServices() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //httpClient.addInterceptor(logging);
        //unkommnet this for logging


        retrofit = new Retrofit.Builder()

                .baseUrl("http://dou.ua")
                .addConverterFactory(new DouConverterFactory())
                .client(httpClient.build())
                .build();


    }

    public static final DouServiceAPI get() {
        return INSTANCE.retrofit.create(DouServiceAPI.class);
    }

    public static Call<List<DouEvent>> getAllEvents(String tag, String city, int page) {
        if (city.equals("онлайн")) {
            city = "online";
        }

        if (tag.equals(ALL_TAGS) && city.equals(ALL_CITYIES)) {
            return get().getAllEvents(page);
        }
        if (tag.equals(ALL_TAGS)) {
            return get().getAllEventsByCity(city, page);
        }
        if (city.equals(ALL_CITYIES)) {
            return get().getAllEventsByTag(tag, page);
        }

        return get().getAllEventsByTagAndCity(tag, city, page);


    }


}
