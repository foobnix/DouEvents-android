package com.foobnix.dou.events.search.net;

import java.util.List;

import com.foobnix.dou.events.search.dou.DouCity;
import com.foobnix.dou.events.search.dou.DouEvent;
import com.foobnix.dou.events.search.dou.DouTag;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ivan-dev on 10.03.16.
 */
public interface DouServiceAPI {

    @GET("/calendar/page-{page}/")
    Call<List<DouEvent>> getAllEvents(@Path("page") int page);

    @GET("/calendar/city/{city}/{page}/")
    Call<List<DouEvent>> getAllEventsByCity(@Path("city") String city, @Path("page") int page);

    @GET("/calendar/tags/{tag}/{page}/")
    Call<List<DouEvent>> getAllEventsByTag(@Path("tag") String tag, @Path("page") int page);

    @GET("/calendar/tags/{tag}/{city}/{page}/")
    Call<List<DouEvent>> getAllEventsByTagAndCity(@Path("tag") String tag, @Path("city") String city, @Path("page") int page);

    @GET("/calendar/")
    Call<List<DouCity>> getCities();


    @GET("/calendar/")
    Call<List<DouTag>> getTags();

}
