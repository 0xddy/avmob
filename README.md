# AvMob
让开车众享丝滑 ~ 

![666](https://ws1.sinaimg.cn/large/005LHiOnly1fvip0oqu56j30e80aoglq.jpg)

## 介绍

没什么介绍的了，网络请求`Okhttp`，Dom解析 `Jsoup`，缓存 `Ehcache`。

- SpringBoot
- MDUI + DPlayer

（偷懒就直接给网络请求添加的缓存~）

#### 前端使用 MDUI ,兼容移动端效果不错


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

在模板文件里面不要乱格式化代码，会导致 FreeMaker 代码排版错误。

![666](https://ws1.sinaimg.cn/large/005LHiOnly1fvip85mjqoj31fy0p875f.jpg)


