# avmob
让开车众享丝滑 ~ 

## 介绍

没什么介绍的了，网络请求`Okhttp`，Dom解析 `Jsoup`，缓存 `Ehcache`。

（偷懒就直接给网络请求添加的缓存~）

#### 前端使用 MDUI


### 1. 本地如何调试？

- utils -> Okhttp类可设置代理

```
    static final Proxy.Type PROXY_TYPE = Proxy.Type.SOCKS;
    static final String PROXY_ADDRESS = "192.168.100.5";
    static final int PROXY_PORT = 1080;
    static final boolean USE_PROXY = true;

```

### 2. 部分提示！

- WordsUtils 过滤关键词

- decoder 包下为解析页面具体实现

- 修改端口可以修改 application.properties

