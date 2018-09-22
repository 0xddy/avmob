package com.av.decoder;

import com.av.utils.WordsUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultDecoder {

    /**
     * 解析分类
     *
     * @param html
     * @return
     */
    public List<Map> parseCates(String html) {

        //nav nav-stacked navigation
        ArrayList<Map> list = new ArrayList<>();
        if (html == null || html.equals(""))
            return list;

        html = WordsUtils.wordsFilter(html);
        Map item;
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class='nav nav-stacked navigation'] li");
        String num = null, title, href, temp;
        String[] temps;
        for (Element element : elements) {
            temp = element.text();
            temps = temp.split(" ");
            if (temps.length == 2) {
                num = temps[1];
            }
            title = temps[0];
            href = element.selectFirst("a").attr("href");
            item = new HashMap();
            item.put("title", title);
            item.put("num", num);
            item.put("href", href);
            list.add(item);
        }
        return list;
    }

    /**
     * 解析首页推荐内容
     *
     * @param html
     * @return
     */
    public List<Map> parseIndex(String html) {
        //
        ArrayList<Map> list = new ArrayList<>();
        if (html == null || html.equals(""))
            return list;

        html = WordsUtils.wordsFilter(html);
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class='videos']");

        Elements videos, temp = null;
        String title, thumbnail, href, time;

        Map root, videoEntry = null;
        ArrayList<Map> v = null;
        for (Element node : elements) {
            root = new HashMap();
            v = new ArrayList<>();
            String nodeTitle = node.parent().parent().select("[class^='panel-title']").text();
            //获取video
            videos = node.select("li");
            for (Element vnode : videos) {
                videoEntry = new HashMap();
                temp = vnode.select("a[class='thumbnail']");
                thumbnail = temp.select("div[class='video-thumb'] img").attr("src");
                title = temp.attr("title");
                href = temp.attr("href");
                time = temp.select("span[class='video-overlay badge transparent']").text().trim();
                videoEntry.put("thumbnail", thumbnail);
                videoEntry.put("title", title);
                videoEntry.put("href", URLEncoder.encode(href));
                videoEntry.put("time", time);
                v.add(videoEntry);
            }
            root.put("data", v);
            root.put("title", nodeTitle);
            list.add(root);
        }
        return list;
    }

    /**
     * 解析列表视频
     *
     * @param html
     * @return
     */
    public List<Map> parseList(String html) {
        ArrayList<Map> list = new ArrayList<>();
        if (html == null || html.equals(""))
            return list;

        html = WordsUtils.wordsFilter(html);
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class='videos'] li");

        HashMap<String, String> videoEntry;
        Elements temp = null;

        String title, thumbnail, href, time;

        for (Element vnode : elements) {
            videoEntry = new HashMap();
            temp = vnode.select("a[class='thumbnail']");
            thumbnail = temp.select("div[class='video-thumb'] img").attr("src");
            title = temp.attr("title");
            href = temp.attr("href");
            time = temp.select("span[class='video-overlay badge transparent']").text().trim();
            videoEntry.put("thumbnail", thumbnail);
            videoEntry.put("title", title);
            videoEntry.put("href", URLEncoder.encode(href));
            videoEntry.put("time", time);
            list.add(videoEntry);
        }

        return list;
    }

    /**
     * 解析视频地址
     * @param html
     * @return
     */
    public Map parseVideo(String html) {

        Map map = new HashMap();
        if (html == null || html.equals(""))
            return map;
        Document document = Jsoup.parse(html);
        Element player = document.getElementById("player");
        String poster = player.attr("poster");
        map.put("poster", poster);
        Elements urlsNode = player.select("source");

        ArrayList<Map> videos = new ArrayList<>();
        Map video = null;
        String temp;
        for (Element node : urlsNode) {
            video = new HashMap();
            temp = node.attr("src");
            video.put("src", temp);
            temp = node.attr("label");
            video.put("label", temp);
            videos.add(video);
        }

        map.put("videos", videos);
        return map;
    }

}
