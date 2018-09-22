package com.av.controller;


import com.av.utils.OkHttp;
import com.av.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Controller
public class WebController {

    @Autowired
    OkHttp okHttp;

    @GetMapping(value = "/")
    public String IndexRender(ModelMap modelMap) {


        Properties props = System.getProperties();
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1048576;
        long freeMemory = runtime.freeMemory() / 1048576;
        long maxMemory = (Runtime.getRuntime().maxMemory()) / 1048576;
        long availableProcessors = runtime.availableProcessors();

        modelMap.put("totalMemory", totalMemory);
        modelMap.put("freeMemory", freeMemory);
        modelMap.put("maxMemory", maxMemory);
        modelMap.put("availableProcessors", availableProcessors);

        String javaVersion = props.getProperty("java.version");
        String javaPath = props.getProperty("java.home");
        String javaVendor = props.getProperty("java.vendor");
        String osName = props.getProperty("os.name");
        String osArch = props.getProperty("os.arch");
        String osVersion = props.getProperty("os.arch");

        modelMap.put("javaVersion", javaVersion);
        modelMap.put("javaPath", javaPath);
        modelMap.put("javaVendor", javaVendor);
        modelMap.put("osName", osName);
        modelMap.put("osArch", osArch);
        modelMap.put("osVersion", osVersion);


        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date startDate = new Date(startTime);
        String pid = new org.springframework.boot.system.ApplicationPid().toString();
        modelMap.put("pid", pid == null ? "未获取到" : pid);
        modelMap.put("starTime", Utils.getDatePoor(new Date(System.currentTimeMillis()), startDate));
        modelMap.put("projectDir", new org.springframework.boot.system.ApplicationHome().toString());

        return "/home/index";
    }

    @GetMapping(value = "/pip")
    public void PipStream(@RequestHeader Map headers,
                          HttpServletResponse response) {

        String url = "http://185.38.13.130//mp43/282011.mp4?st=8dXuoLBwtqL5rio9mhQeHA&e=1537249792";
        okHttp.stream(url, headers, response);
    }

}
