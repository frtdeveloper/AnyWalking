package com.lenwotion.travel.global;

/**
 * 全局变量，用于存放通用常量
 */
public class GlobalConstants {

    // 外网服务器
    public static final String BASE_SERVICE_URL = "http://m.lenwotionapp.com/";
    // 本地测试服务器
    public static final String BASE_SERVICE_URL_LOCAL = "http://192.168.1.205:8080/";

    /* APP通用功能 */
    /**
     * APP数据库名
     */
    public static final String DATABASE_NAME = "AnyWalkingDb";
    /**
     * APP包名
     */
    public static final String PACKAGE_NAME = "com.lenwotion.travel";
//    /**
//     * 讯飞语音appId
//     */
//    public static final String IFLYTEK_APP_ID = "5a1cf9a2";
    /**
     * WIFI起始标记
     */
    public static final String WIFI_START_SIGN = "lwt";
    /**
     * 候车模块
     */
    //200 候车成功
    public static final int WAITING_BUS_SUCCESS = 200;
    //201 推送车载机超过三次
    public static final int WAITING_BUS_201 = 201;
    //202 选中线路无回调信息
    public static final int WAITING_BUS_202 = 202;
    //203 查不到该站台信息
    public static final int WAITING_BUS_203 = 203;
    //204 系统异常
    public static final int WAITING_BUS_204 = 204;
    //205 筛选之后无合适车辆
    public static final int WAITING_BUS_205 = 205;
//    //偏移所选站台
//    public static final int WAITING_BUS_DEVIATION_STATION = 206;

//    //提醒实时比较两个GPS
//    public static final int REMINDER_COMPARE_GPS = 0x7;
//    //提醒实时比较两个起点
//    public static final int REMINDER_COMPARE_START = 0x8;
    /**
     * 查询模块
     */
    //查询线路
    public static final int TYPE_SEARCH_LINE = 1;
    //查询站台
    public static final int TYPE_SEARCH_STATION = 2;

    /* APP网络功能接口 */
    // 用户模块
    /**
     * 登录
     */
    public static final String ACCOUNT_LOGIN_BY_PHONE_NUMBER = "app/account/login.app";
    /**
     * 账号注册
     */
    public static final String ACCOUNT_REGISTER = "app/account/handle.app";
    /**
     * 用户是否使用APP
     */
    public static final String ACCOUNT_USER_ISUSE_APP = "app/account/use.app";
    /**
     * 获取手机验证码
     */
    public static final String ACCOUNT_FETCH_PHONE_VERIFY_CODE = "request/getVerifyCode.msg";

    //我要坐车模块
    /**
     * 上传当前用户Token、GPS，获取站台列表数据
     */
    public static final String WAITING_GET_STATION_LIE = "app/waiting/station/handle.app";
    /**
     * 根据站台查询线路
     */
    public static final String WAITING_GET_STATION_LINE_LIE = "app/waiting/line/handle.app";
    /**
     * 确认候车
     */
    public static final String WAITING_GET_WAIT_AFFIRM_LINE = "app/waiting/affirm/handle.app";
    /**
     * 用户GPS上传判断
     */
    public static final String WAITING_REQUEST_GPS = "app/waiting/gps/handle.app";
    /**
     * 取消候车
     */
    public static final String WAITING_CANCEL = "app/waiting/affirm/cancel.app";
    /**
     * 上传WIFI连接信息
     */
    public static final String SEND_CONNECT_INFO = "app/account/upper/car.app";

    //搜索模块
    /**
     * 站台模糊匹配查询
     */
    public static final String SEARCH_STATION_MATCH = "app/query/station/matching.app";
    /**
     * 线路模糊匹配查询
     */
    public static final String SEARCH_LINE_MATCH = "app/query/line/matching.app";
    /**
     * 精准线路上下行查询
     */
    public static final String SEARCH_LINE_DIRECTION = "app/query/line/way.app";
//    /**
//     * 精品软件推荐
//     */
//    public static final String SEARCH_OBTAIN_SOFTWARE = "app/obtain/software.app";

    // APP常规功能
    /**
     * 获取wifi信息，工厂测试用
     */
    public static final String APP_GENERAL_FETCH_WIFI_INFO = "app/factory/test/wifi.app";
    /**
     * 获取最新版本信息
     */
    public static final String APP_GENERAL_FETCH_VERSION_INFO = "app/version/update/upload.app";
    /**
     * 提交意见建议信息
     */
    public static final String APP_GENERAL_SEND_ADVICE_INFO = "app/account/advice/upload.app";
    /**
     * 使用帮助URL
     */
    public static final String APP_GENERAL_USE_HELP_URL = BASE_SERVICE_URL + "admin/account/instructionsListV.do?tdsourcetag=s_pcqq_aiomsg";
//    /**
//     * 上传错误信息
//     */
//    public static final String APP_GENERAL_UPLOAD_CRASH_REPORTS = "app/transmitter/error/upload.app";

    /* APP全局数据 */
    /**
     * 地图缩放比例
     */
    public static final int MAP_ZOOM = 17;
    /**
     * LOG TAG
     */
    public static final String LOG_TAG = "LOG_TAG";

    /* SharedPreference字段标签 */
    /**
     * 用户token
     */
    public static final String SP_USER_TOKEN = "spUserToken";
    /**
     * 用户手机号
     */
    public static final String SP_USER_PHONE_NUMBER = "spUserPhoneNumber";
    /**
     * 版本更新下载ID
     */
    public static final String SP_DOWNLOADING_ID = "spDownloadingId";

    /* 广播intent标签 */
    /**
     * 地图定位数据更新
     */
    public static final String INTENT_MAP_LOCATION_CHANGED = "com.lenwotion.travel.INTENT_MAP_LOCATION_CHANGED";
//    /**
//     * 接收开始计算经纬度距离
//     */
    //public static final String SEND_COMPUTATIONS_DISTANCE = "com.lenwotion.travel.SEND_COMPUTATIONS_DISTANCE";

    /* NETTY通讯命令类型 */
    /**
     * 车载机播放路线名广播
     */
    public static final int ORDER_LINE_BROADCAST = 1001;
    /**
     * 发送用户候车信息到车载机
     */
    public static final int ORDER_SEND_USER_INFO = 1002;

}
