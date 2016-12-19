package com.example.administrator.gamedemo.utils;

import java.util.List;

/**
 * Created by lixu on 2016/12/12.
 */

public class ToolUtil {

    public static boolean isListEmpty(List<?> datas) {
        return datas == null || datas.size() <= 0;
    }
}
