package com.av.controller;

import com.av.base.BaseController;
import com.av.decoder.Porn91Decoder;
import com.av.utils.CookieUtils;
import com.av.utils.OkHttp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/91porn")
public class Porn91Controller extends BaseController {

    @Autowired
    OkHttp okHttp;

    @Autowired
    Porn91Decoder porn91Decoder;

    public static String BASE = "http://91porn.com/";
    static String BASE_URL = BASE + "v.php";
    static String BASE_VIDEO_URL = BASE + "view_video.php?viewkey=";

    final static ConcurrentHashMap<String, String> cateMap = new ConcurrentHashMap<>();

    final static LinkedList<Map> cateList = new LinkedList<>();

    static {
        cateMap.put("hot", "当前热门");
        cateMap.put("rp", "最近得分");
        cateMap.put("long", "十分钟以上");
        cateMap.put("md", "本月讨论");
        cateMap.put("tf", "本月收藏");
        cateMap.put("mf", "收藏最多");
        cateMap.put("rf", "最近加精");
        cateMap.put("top", "本月最热");

        Map cate;
        for (Map.Entry<String, String> entry : cateMap.entrySet()) {
            cate = new HashMap();
            cate.put("title", entry.getValue());
            cate.put("href", entry.getKey());
            cateList.add(cate);
        }
    }

    @RequestMapping("/decode")
    @ResponseBody
    public String decode() {

        return "解密失败";
    }

    /**
     * cate ：
     * hot 当前热门
     * rp 最近得分
     * long 十分钟以上
     * md 本月讨论
     * tf 本月收藏
     * mf 收藏最多
     * rf 最近加精
     * top 本月最热
     */
    @ResponseBody
    @GetMapping(value = "/api/list")
    public Object ListData(@RequestParam HashMap params, int page) {

        String cate = (String) params.get("cate");
        if (page < 1) {
            page = 1;
        }
        //主页url
        //http://91porn.com/v.php?next=watch&page=1
        //分类
        //http://91porn.com/v.php?category=hot&viewtype=basic&page=2
        String url = BASE_URL + "?next=watch&page=" + page;
        if (StringUtils.isEmpty(cate) || cateMap.containsKey(cate)) {
            url = BASE_URL + "?category=" + cate + "&viewtype=basic&page=" + page;
        }

        String html = okHttp.GET(url);
        if (StringUtils.isEmpty(html)) {
            return fail(400, "获取内容为空");
        }
        return success(200, porn91Decoder.parseList(html));
    }

    @ResponseBody
    @GetMapping(value = "/api/video")
    public Object VideoData(String viewkey) {
        if (StringUtils.isEmpty(viewkey)) {
            return fail(400, "viewkey不能为空");
        }
        String url = BASE_VIDEO_URL + viewkey;
        String html = okHttp.GET(url);
        if (StringUtils.isEmpty(html)) {
            return fail(400, "获取内容为空");
        }
        return success(200, porn91Decoder.parseData(html));
    }

    @ResponseBody
    @GetMapping(value = "/api/cate")
    public Object CateList() {
        return success(200, cateList);
    }

    // Web接口
    @GetMapping(value = "/list")
    public String WebList(@RequestParam HashMap params, int page, ModelMap modelMap) {
        String cate = (String) params.get("cate");
        String cateName = (String) params.get("cateName");

        //正常列表
        String url = BASE_URL + "?next=watch&page=" + page;
        if (StringUtils.isEmpty(cate) || cateMap.containsKey(cate)) {
            url = BASE_URL + "?category=" + cate + "&viewtype=basic&page=" + page;
        }

        String html = okHttp.GET(url);
        List<Map> data = porn91Decoder.parseList(html);
        modelMap.addAttribute("data", data);
        modelMap.addAttribute("cate", cate);
        modelMap.addAttribute("cateName",
                StringUtils.isEmpty(cateName) ? "" : cateName + " -");

        modelMap.addAttribute("page", page);

        return "/91porn/list";
    }

    @GetMapping(value = "/cate")
    public String WebCate(ModelMap modelMap) {

        modelMap.addAttribute("data", cateList);
        return "/91porn/cate";
    }

    @GetMapping(value = "/player")
    public String WebPlayer(HttpServletRequest httpServletRequest, String viewkey, @RequestParam HashMap params, ModelMap modelMap) {

        String title = (String) params.get("title");
        String url = BASE_VIDEO_URL + viewkey;
        String html = okHttp.GET(url);
        Map data = porn91Decoder.parseData(html);

        String client = CookieUtils.getCookie(httpServletRequest, "client");

        System.out.println(client);
        if (client != null && client.equalsIgnoreCase("cn.lmcw.koa")) {
            modelMap.addAttribute("isApp", true);
        } else {
            modelMap.addAttribute("isApp", false);
        }
        modelMap.addAttribute("data", data);
        modelMap.addAttribute("title", title == null ? "" : title);
        return "/91porn/player";
    }


}
