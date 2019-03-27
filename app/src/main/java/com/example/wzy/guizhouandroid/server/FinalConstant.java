package com.example.wzy.guizhouandroid.server;

public class FinalConstant {
	// 用户账户
	public static final String USERNAME = "admin";
	public static final String USERNAME0 = "admin";
	public static final String PASSWORD = "admin123";

	// 系统超时时间
	public static final long WAIT_TIME = 10000;

	// HTTP通信相关
	public static final String ENCODING = "GB2312";
	public static final String QUERY_DATA = "queryData";
	public static final String QUERY_STATE = "queryState";
	public static final String QUERY_PARAMS = "queryParams";
	public static final String CONTROL = "control";
	public static final String SET_PARAMS = "setParams";

	public static final String SENSOR_DATA_HEAD = "SENSOR";
	public static final String CONTROL_STATE_HEAD = "EQUIPMENT";
	public static final String PARAMS_DATA_HEAD = "PARAMS";
	public static final String CONTROL_SUCCESS = "success";

	public static final int[] EQUIPMENT = {0, 1, 2, 3, 4};
	public static final int ON = 1;
	public static final int OFF = 0;

	public static final int QUERY_BACK_DATA = 1;
	public static final String BACK_INFO = "backMessageFromServer";
	public static final int CONTROL_BACK_DATA = 2;
	public static final String CONTROL_INFO = "controlbackMessageFromServer";

	public static final int TIME_OUT = 3;

	public static final String PC_BACK_INFO = "backMessageFromServer";
	public static final int PC_QUERY_BACK_DATA = 1;

	/**批次下拉菜单请求信息*/
	public static final int GT_QUERY_BACK_DATA = 1;

	/**批次下拉菜单返回信息*/
	public static final String GT_BACK_INFO = "backMessageFromServer";




	/**  myself-->设置-->系统更新版本号查询请求服务器命令*/
	public static final String UPDATE_REQUEST_SERVER ="UPDATE_SHOW";
	/**  myself-->设置-->系统更新版本号查询请求返回服务器命令*/
	public static final String UPDATE_REBACK_SERVER ="UPDATE_REBACK";


	/**硬盘录像机设备参数查询请求服务器命令*/
	public static final String FIND_NVR_SET_REQUEST_SERVER ="NVR_SET_SHOW";
	/**硬盘录像机设备参数查询请求服务器命令*/
	public static final String FIND_NVR_SET_REBACK_SERVER ="NVR_SET_REBACK";
	/**传感器参数查询请求服务器命令*/
    public static final String FIND_DATA_REQUEST_SERVER = "DATA_SHOW";
	/**传感器参数查询请求服务器返回命令*/
	public static final String FIND_DATA_REBACK_SERVER = "DATA_SHOW_REBACK";

	/**批次号参数查询请求服务器命令*/
	public static final String FIND_BATCH_REQUEST_SERVER = "BATCH_SHOW";
	/**批次号参数查询请求服务器返回命令*/
	public static final String FIND_BATCH_REBACK_SERVER = "BATCH_SHOW_REBACK";



	/**母猪记录耳标参数查询请求服务器命令*/
	public static final String FIND_TAG_REQUEST_SERVER = "TAG_SHOW";
	/**母猪记录耳标参数查询请求服务器返回命令*/
	public static final String FIND_TAG_REBACK_SERVER = "TAG_SHOW_REBACK";

	/**母猪记录参数提交请求服务器命令*/
	public static final String FIND_REPLACE_SUBMIT_SERVER = "REPLACE_SUBMIT";
	/**母猪记录参数提交请求服务器返回命令*/
	public static final String FIND_REPLACE_SUBMIT_SERVER_REBACK = "REPLACE_SUBMIT_REBACK";

	/**生长记录参数提交请求服务器命令*/
	public static final String FIND_GROW_SUBMIT_SERVER = "GROW_SUBMIT";
	/**生长记录参数提交请求服务器返回命令*/
	public static final String FIND_GROW_SUBMIT_SERVER_REBACK = "GROW_SUBMIT_REBACK";
}

