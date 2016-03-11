package com.foobnix.dou.events.search.dou;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import com.foobnix.dou.events.search.net.DouServices;

/**
 * Created by ivan-dev on 02.03.16.
 */
public class DouUtils {


    public static List<DouEvent> parseEvents(Document document) {
        List<DouEvent> events = new ArrayList<>();

        Elements sections = document.select("div[class=\"event\"]");
        for (Element item : sections) {

            Elements date = item.select("span[class=\"date\"]");
            Elements address = item.select("div[class=\"when-and-where\"]");
            Elements title = item.select("div[class=\"title\"] > a");
            Elements info = item.select("div[class=\"b-typo\"]");
            Elements image = item.select("img[class=\"logo\"]");

            DouEvent event = new DouEvent();
            event.setTitle(title.text());
            event.setDate(date.text());
            event.setDescription(info.text());
            event.setImgUrl(image.attr("src"));
            event.setWebUrl(title.attr("href"));
            event.setAddress(address.text().replaceAll(event.getDate(), ""));


            events.add(event);

        }
        return events;
    }


    public static List<DouCity> parseCities(Document document) {
        List<DouCity> list = new ArrayList<>();
        Elements sections = document.select("select[name=\"city\"]>option");
        for (Element item : sections) {
            String name = item.text();
            if (name.equals("по всем городам")) {
                name = DouServices.ALL_CITYIES;
            }

            list.add(new DouCity(name, item.attr("value")));
        }
        return list;
    }


    public static List<DouTag> parseTags(Document document) {
        List<DouTag> list = new ArrayList<>();
        Elements sections = document.select("select[name=\"tag\"]>option");
        for (Element item : sections) {
            String name = item.text();
            if (name.equals("по всем темам")) {
                name = DouServices.ALL_TAGS;
            }
            list.add(new DouTag(name, item.attr("value")));
        }
        return list;
    }


}
