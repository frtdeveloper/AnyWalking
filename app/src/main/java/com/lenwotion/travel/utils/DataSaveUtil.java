package com.lenwotion.travel.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 一些数据保存
 * Created by John on 2017/11/28.
 */

public class DataSaveUtil {

    /**
     * 导航 SDK 通过回调将播报的文字内容
     */
    private static List<String> navigationPlanningList;

    public static List<String> getNavigationPlanningList() {
        if (navigationPlanningList == null) {
            navigationPlanningList = new ArrayList<>();
        }
        return navigationPlanningList;
    }

    public static void saveNavigationPlanningList(String text) {
        if (navigationPlanningList == null) {
            navigationPlanningList = new ArrayList<>();
        }
        navigationPlanningList.add(text);
    }

    public static void clearNavigationPlanningList() {
        if (navigationPlanningList == null) {
            navigationPlanningList = new ArrayList<>();
            return;
        }
        navigationPlanningList.clear();
    }

}
