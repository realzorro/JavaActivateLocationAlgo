package common.config;

public class ZConfig {
	// --------Rssi-------
	public static int RSSI_MIN_VALUE = -128;
	
	public static int RSSI_QUEUE_LENGTH = 10;
	public static int RSSI_QUEUE_EXPIRETIME = 2 * 60 * 60 * 1000; 
	
	public static int RSSI_METER_ONE = -55;
	public static int RSSI_DAMPING_FACTOR = 20;
	
	// --------Building------
	public static float BUILDING_HEIGHT = 2.0f;
	
	// -------Filter--------
	public static int DEVICE_LOCATE_FREQUENCY = 1000; // 定位控制器发包频率
	public static float RSSI_VALID_FACTOR = 0.8f; // 梯度在一维线性下的简单计算系数

	public static int RSSI_VALID_TIME = 5000; // 5000毫秒
	public static int RSSI_VALID_PERCENT = 40; // 收包率百分比下限

	public static int RSSI_DISTANCE_SORT_TIME = 2000;
	
	//--------Algo-------
	public static int ALGO_MIN_DEVICE = 3;    //这个值不要修改
	public static int ALGO_MAX_DEVICE = 5;
	
	public static int ALGO_SINGLE_CALCULATE_RSSI = -70; 

	public static float ALGO_AREA_BLOCK_SIZE = 0.5f;              
	public static int   ALGO_AREA_MAX = 200;                     
	
	// -------Offset--------
	public static int OFFSET_MOVE_STEP = 4;
	public static int OFFSET_STOP_STEP = 2;
	
	public static float OFFSET_LONG_FACTOR = 0.5f;
	public static float OFFSET_SHORT_FACTOR = 0.6f;

	// -------Activate Location-------
	public static String SELF_IDENTIFY = "ACTIVATE_LOCATION_000000";
	
	
	//--------Trend--------
	public static boolean TREND_SWITCH = true ;
	
	public static double TREND_ACC_UP   = 1.08;
	public static double TREND_ACC_DOWN = 0.92;
	
	public static double TREND_ACC_STOP = 0.05 ;	
	public static int    STOP_ENTER_TIME = 2000;
	public static int    STILL_ENTER_TIME = 6000;
	
	public static float  TREND_STEP_LENGTH = 0.65f;
	
	
}
