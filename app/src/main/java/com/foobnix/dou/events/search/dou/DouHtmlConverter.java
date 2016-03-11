package com.foobnix.dou.events.search.dou;


import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by ivan-dev on 11.03.16.
 */
public class DouHtmlConverter implements Converter<ResponseBody, Object> {
    Type type;

    public DouHtmlConverter(Type type) {
        this.type = type;
    }

    @Override
    public Object convert(ResponseBody value) throws IOException {


        if (type.toString().equals(new TypeToken<List<DouEvent>>() {
        }.getType().toString())) {
            return DouUtils.parseEvents(Jsoup.parse(value.string()));
        }
        if (type.toString().equals(new TypeToken<List<DouCity>>() {
        }.getType().toString())) {
            return DouUtils.parseCities(Jsoup.parse(value.string()));
        }
        if (type.toString().equals(new TypeToken<List<DouTag>>() {
        }.getType().toString())) {
            return DouUtils.parseTags(Jsoup.parse(value.string()));
        }
        throw new IllegalArgumentException("Not supported type: " + type);
    }
}
