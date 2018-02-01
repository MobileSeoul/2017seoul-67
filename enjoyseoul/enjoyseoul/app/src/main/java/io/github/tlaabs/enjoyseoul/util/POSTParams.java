package io.github.tlaabs.enjoyseoul.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class POSTParams {
    StringBuilder sb;
    public POSTParams(){
        sb = new StringBuilder();
    }

    public void addParam(String key, String value){
        if(sb.length() != 0) sb.append("&");
        try {
            String sub = key+"=" + URLEncoder.encode(value, "UTF-8");
            sb.append(sub);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
