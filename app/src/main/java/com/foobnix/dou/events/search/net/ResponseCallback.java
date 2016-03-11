package com.foobnix.dou.events.search.net;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by ivan-dev on 11.03.16.
 */
public abstract class ResponseCallback<T> implements Callback<T> {

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e("TEST", "Response erorr", t);
    }
}
