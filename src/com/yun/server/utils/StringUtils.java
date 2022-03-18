package com.yun.server.utils;

public class StringUtils {

    public static boolean isEmpty(String content){
        if(content == null || content.isEmpty()){
            return true;
        }
        return false;
    }

    public static String removePrefix(String content , String prefix){
        if(StringUtils.isEmpty(content) || StringUtils.isEmpty(prefix)){
            return null;
        }
        int index = content.indexOf(prefix);
        if(index == -1){
            return content;
        }
        content = content.substring(index + prefix.length());
        return content;
    }

    // /first/index.html  /    / ->  first
    public static String subString(String content, String prefix, String suffix){
        if(!StringUtils.isEmpty(content) && !StringUtils.isEmpty(prefix) && !StringUtils.isEmpty(suffix)){
            int beginIndex = content.indexOf(prefix);
            if(beginIndex != -1){
                content = content.substring(beginIndex + 1);
                int endIndex = content.indexOf(suffix);
                if(endIndex != -1){
                    String substring = content.substring(0, endIndex);
                    return substring;
                }
            }
        }
        return null;
    }

}
