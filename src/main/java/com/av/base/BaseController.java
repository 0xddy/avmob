package com.av.base;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseController {

    public String success() {

        return "{\"code\":200,\"msg\":\"请求成功\"}";
    }

    public String success(int code) {
        return "{\"code\":" + code + ",\"msg\":\"请求成功\"}";
    }

    public String success(int code, String msg) {
        return "{\"code\":" + code + ",\"msg\":\"" + msg + "\"}";
    }

    public String success(String msg) {
        return "{\"code\":200,\"msg\":\"" + msg + "\"}";
    }

    public String success(int code, Object data) {

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"code\":" + code + ",\"msg\":\"请求成功\",\"data\":" + json + "}";
    }

    public String success(int code, String msg, Object data) {

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"code\":" + code + ",\"msg\":\"" + msg + "\",\"data\":" + json + "}";
    }

    public String fail(int code) {
        return "{\"code\":" + code + ",\"msg\":\"请求失败\"}";
    }

    public String fail(int code, String msg) {
        return "{\"code\":" + code + ",\"msg\":\"" + msg + "\"}";
    }

    public String fail(String msg) {
        return "{\"code\":300,\"msg\":\"" + msg + "\"}";
    }
}
