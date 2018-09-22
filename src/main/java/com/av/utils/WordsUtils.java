package com.av.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WordsUtils {

    static ArrayList<String> words;

    static {
        words = new ArrayList<String>();
        words.add("AV淘宝");
        words.add("解决视频卡顿加翻墙");
        words.add("（(.*?)）");
    }

    public static String wordsFilter(String str) {

        for (String w : words) {
            str = str.replaceAll(w, "");
        }
        return str;
    }

    public static void main(String[] args) {

    }
}
