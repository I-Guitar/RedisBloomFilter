package com.joe.utils;

/**
 * Create by joe on 2018/9/26
 */
public class UrlUtils {

    /**
     * 根据url抽取domain
     */
    public static String extraDomain(String url) {
        if (url == null || url.length() == 0) {
            return url;
        }
        if (!url.startsWith("http")) {
            return "";
        }
        url = url.substring(url.indexOf("//") + 2);
        int index = url.indexOf("/");
        if (index > 0) {
            url = url.substring(0, index);
        }
        int index1 = url.indexOf("?");
        if (index1 > 0) {
            url = url.substring(0, index1);
        }
        if (url.contains(":")) {
            url = url.substring(0, url.indexOf(":"));
        }
        return url;
    }


}
