package com.av.controller;

import com.av.base.BaseController;
import com.av.decoder.DefaultDecoder;
import com.av.utils.CookieUtils;
import com.av.utils.OkHttp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/avt")
public class AvtController extends BaseController {

    @Autowired
    OkHttp okHttp;

    @Autowired
    DefaultDecoder decoder;

    public static String BASE_URL = "http://www.avtbv.com/";

    @ResponseBody
    @GetMapping(value = "/api/update")
    public Object UpdateUrl(String url, String password) {
        //懒就直接写了
        if (password.equals("avmob")) {
            BASE_URL = url;
            return success(200, "update success ,new url is " + BASE_URL);
        } else {
            return fail(400);
        }

    }

    @ResponseBody
    @GetMapping(value = "/api/cate")
    public Object CateList() {

        String html = okHttp.GET(BASE_URL);
        if (StringUtils.isEmpty(html)) {
            return fail(400);
        }
        return success(200, decoder.parseCates(html));
    }

    @ResponseBody
    @GetMapping(value = "/api/index")
    public Object IndexList() {
        String html = okHttp.GET(BASE_URL);
        if (StringUtils.isEmpty(html)) {
            return fail(400);
        }
        return success(200, decoder.parseIndex(html));
    }

    @ResponseBody
    @GetMapping(value = "/api/list")
    public Object ListData(@RequestParam HashMap params, int page) {

        String cate = (String) params.get("cate");
        if (StringUtils.isEmpty(cate))
            cate = "recent";
        String url = BASE_URL + cate + (page > 1 ? "/recent/" + page : "");
        url = url.replace("recent/recent", "recent");

        String html = okHttp.GET(url);
        if (StringUtils.isEmpty(html)) {
            return fail(400);
        }
        return success(200, decoder.parseList(html));
    }

    @ResponseBody
    @GetMapping(value = "/api/video")
    public Object VideoData(String href) {

        if (StringUtils.isEmpty(href)) {
            return fail(401, "href不能为空");
        }
        String url = BASE_URL + URLEncoder.encode(href);
        String html = okHttp.GET(url);
        if (StringUtils.isEmpty(html)) {
            return fail(400);
        }

        return success(200, decoder.parseVideo(html));
    }

    @ResponseBody
    @GetMapping(value = "/api/search")
    public Object SearchList(@RequestParam HashMap params, int page) {

        String keyword = (String) params.get("keyword");
        //int page = (int) params.get("page");

        if (StringUtils.isEmpty(keyword)) {
            return fail(400, "关键词不能为空");
        }
        if (page <= 0)
            page = 1;
        String url = BASE_URL + "/search/video/?s=" + URLEncoder.encode(keyword) + "&page=" + page;
        String html = okHttp.GET(url);
        if (StringUtils.isEmpty(html)) {
            return fail(400);
        }
        return success(200, decoder.parseList(html));
    }


    // Web接口
    @GetMapping(value = "/list")
    public String WebList(@RequestParam HashMap params, int page, ModelMap modelMap) {
        String cate = (String) params.get("cate");
        String keyword = (String) params.get("keyword");
        String cateName = (String) params.get("cateName");

        if (StringUtils.isEmpty(keyword)) {
            //正常列表
            if (StringUtils.isEmpty(cate))
                cate = "recent";
            cate = cate.replace("/", "");
            String url = BASE_URL + cate + (page > 1 ? "/recent/" + page : "");
            url = url.replace("recent/recent", "recent");
            String html = okHttp.GET(url);
            List<Map> data = decoder.parseList(html);
            modelMap.addAttribute("data", data);
            modelMap.addAttribute("cate", cate);
            modelMap.addAttribute("cateName",
                    StringUtils.isEmpty(cateName) ? "" : cateName + " -");

        } else {

            String url = BASE_URL + "/search/video/?s=" + URLEncoder.encode(keyword) + "&page=" + page;
            String html = okHttp.GET(url);
            List<Map> data = decoder.parseList(html);
            modelMap.addAttribute("data", data);
            modelMap.addAttribute("cateName", keyword + " -");
            modelMap.addAttribute("keyword", keyword);
        }

        modelMap.addAttribute("page", page);

        return "/avt/list";
    }

    @GetMapping(value = "/cate")
    public String WebCate(ModelMap modelMap) {
        String html = okHttp.GET(BASE_URL);
        List<Map> data = decoder.parseCates(html);
        modelMap.addAttribute("data", data);
        return "/avt/cate";
    }

    @GetMapping(value = "/player")
    public String WebPlayer(HttpServletRequest httpServletRequest, @RequestParam String href, @RequestParam HashMap params, ModelMap modelMap) {

        String url = BASE_URL + URLEncoder.encode(href);
        String title = (String) params.get("title");
        String html = okHttp.GET(url);

        String client = CookieUtils.getCookie(httpServletRequest, "client");

        if (client != null && client.equalsIgnoreCase("cn.lmcw.koa")) {
            modelMap.addAttribute("isApp", true);
        } else {
            modelMap.addAttribute("isApp", false);
        }
        Map data = decoder.parseVideo(html);
        modelMap.addAttribute("data", data);
        modelMap.addAttribute("title", title == null ? "" : title);
        return "/avt/player";
    }

}
