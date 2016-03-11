package com.foobnix.dou.events.search.dou;

/**
 * Created by ivan-dev on 11.03.16.
 */
public class DouCity {
    private String name;
    private String url;

    public DouCity() {
    }

    public DouCity(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
