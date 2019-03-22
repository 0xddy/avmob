package com.av;

import com.av.controller.AvtController;
import com.av.controller.Porn91Controller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class
        , DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableConfigurationProperties
@EnableCaching
public class RunApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(RunApplication.class, args);
        loadConf();
    }


    public static void loadConf() {

        ApplicationHome home = new ApplicationHome(RunApplication.class);
        File jarFile = home.getSource();
        System.out.println(jarFile);
        File confJson = new File(jarFile.isFile() ? jarFile.getParent() + File.separator + "conf.json" : jarFile.getAbsolutePath() + File.separator + "conf.json");
        System.out.println(confJson);
        if (confJson.exists()) {
            Properties prop = new Properties();
            try {
                //读取属性文件a.properties
                InputStream in = new BufferedInputStream(new FileInputStream(confJson));
                prop.load(new InputStreamReader(in, "utf-8"));     ///加载属性列表
                Iterator<String> it = prop.stringPropertyNames().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if (!StringUtils.isEmpty(prop.getProperty(key)))
                        continue;
                    if (key.equalsIgnoreCase("avt")) {
                        AvtController.BASE_URL = prop.getProperty(key);
                        System.out.println("加载：" + key + ":" + prop.getProperty(key));
                    } else if (key.equalsIgnoreCase("91porn")) {
                        Porn91Controller.BASE = prop.getProperty(key);
                        System.out.println("加载：" + key + ":" + prop.getProperty(key));
                    }

                }
                in.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


}
