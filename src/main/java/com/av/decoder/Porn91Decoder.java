package com.av.decoder;

import com.av.utils.WordsUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Porn91Decoder {

    /**
     * 解析列表
     *
     * @param html
     * @return
     */
    public ArrayList<Map> parseList(String html) {
        ArrayList<Map> list = new ArrayList<>();
        if (html == null || html.equals(""))
            return list;

        html = WordsUtils.wordsFilter(html);
        Document document = Jsoup.parse(html);

        Elements elements = document.select("div[class='listchannel']");

        Map video;
        String url, image, title, time, viewkey;
        Pattern pattern = Pattern.compile("viewkey=(.*?)&");
        Pattern pattern2 = Pattern.compile("</span>(\\d+:\\d+)");
        Matcher matcher;
        for (Element element : elements) {
            video = new HashMap();
            time = "";
            viewkey = "";
            image = element.select("div[class^='imagecha'] a img").attr("src");
            //System.out.println(image.equals("") ? element.html() : "");
            url = element.select("a").attr("href");
            title = element.select("a").attr("title");
            matcher = pattern.matcher(url);
            if (matcher.find()) {
                viewkey = matcher.group(1);
            }
            matcher = pattern2.matcher(element.html());
            if (matcher.find()) {
                time = matcher.group(1);
            }
            video.put("viewkey", viewkey);
            video.put("time", time);
            video.put("thumbnail", image);
            video.put("title", title);
            list.add(video);
        }

        return list;
    }

    public Map parseData(String html) {
        Map map = new HashMap();
        if (html == null || html.equals(""))
            return map;

        html = WordsUtils.wordsFilter(html);
        Document document = Jsoup.parse(html);
        Elements elements = document.select("source[type='video/mp4']");
        String src = elements.attr("src");

        System.out.println(src);
        String title = document.getElementById("viewvideo-title").text();

        map.put("video", src);
        map.put("title", title);

//        Pattern pattern = Pattern.compile("<source src=\"(.*?)\" type=\\'video/mp4\\'");
//        Matcher matcher = pattern.matcher(html);
//        if (matcher.find()) {
//            String video = matcher.group(1);
//            map.put("video", video);
//        }
//        pattern = Pattern.compile("<div id=\"viewvideo-title\">(.*?)</div>");
//        matcher = pattern.matcher(html);
//        if (matcher.find()) {
//            String title = matcher.group(1);
//            map.put("title", title);
//        }

        return map;
    }

}
