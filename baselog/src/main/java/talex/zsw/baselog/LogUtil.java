package talex.zsw.baselog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import talex.zsw.baselog.database.LogMessage;
import talex.zsw.baselog.database.LogMessageDao;

/**
 * 作用: 日志系统
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2017/5/24 14:48 
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class LogUtil
{
	private static LogMessageHelper helper;
	private static LogMessageDao dao;
	private static PackageInfo packageInfo;
	private static WifiInfo wifiInfo;
	private static Context mContext;

	/**
	 * 初始化
	 */
	public static void init(Context context)
	{
		helper = LogMessageHelper.getHelper(context);
		mContext = context;
		dao = new LogMessageDao(context);
		try
		{
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		}
		catch(PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}

		@SuppressLint("WifiManagerPotentialLeak") WifiManager wifi
			= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiInfo = wifi.getConnectionInfo();
	}

	/**
	 * 退出
	 */
	public static void exit()
	{
		dao = null;
		packageInfo = null;
		wifiInfo = null;
		helper.close();
	}

	/**
	 * 返回LogMessageDao,可以自定义写方法
	 */
	public static LogMessageDao getLogMessageDao()
	{
		return dao;
	}

	/**
	 * 添加一条日志
	 */
	public static void add(LogMessage data)
	{
		if(dao != null)
		{
			dao.add(data);
		}
	}

	/**
	 * 查询所有日志
	 */
	public static List<LogMessage> queryAll()
	{
		if(dao != null)
		{
			return dao.queryAll();
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * 查询一个时间段的日志
	 *
	 * @param startDateTime 开始时间
	 * @param endDateTime   结束时间
	 */
	public static List<LogMessage> queryDate(long startDateTime, long endDateTime)
	{
		if(dao != null)
		{
			return dao.queryDate(startDateTime, endDateTime);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * 查询一个时间段的日志,带分页属性
	 */
	public static List<LogMessage> queryDate(long startDateTime, long endDateTime, long offset, long limit)
	{
		if(dao != null)
		{
			return dao.queryDate(startDateTime, endDateTime, offset, limit);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * 查询日志,带分页属性
	 */
	public static List<LogMessage> queryDateOffset(long starttime, long offset, long limit)
	{
		if(dao != null)
		{
			return dao.queryDateOffset(starttime, offset, limit);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	public static List<LogMessage> queryDateOffseColumns(long starttime, List<String> columns, long offset, long limit)
	{
		if(dao != null)
		{
			return dao.queryDateOffseColumns(starttime, columns, offset, limit);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * 获取指定时间后的多少条日志
	 */
	public static List<LogMessage> queryDateLimit(long startDateTime, int count)
	{
		if(dao != null)
		{
			return dao.query(startDateTime, count);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * 查询某个日期下,某级别的日志
	 *
	 * @param startDateTime 开始时间
	 * @param endDateTime   结束时间
	 * @param level         日志级别
	 */
	public static List<LogMessage> queryDateAndLevel(long startDateTime, long endDateTime, String level)
	{
		if(dao != null)
		{
			return dao.queryDateAndLevel(startDateTime, endDateTime, level);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * 查询某个日期下,某级别的日志,带分页属性
	 */
	public static List<LogMessage> queryDateAndLevel(long startDateTime, long endDateTime, String level, long offset, long limit)
	{
		if(dao != null)
		{
			return dao.queryDateAndLevel(startDateTime, endDateTime, level, offset, limit);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * 删除几天内的日志
	 *
	 * @param days 天数
	 */
	public static void deleteDays(int days)
	{
		if(dao != null)
		{
			dao.deleteDays(days);
		}
	}

	/**
	 * 删除全部日志
	 */
	public static void deleteAll()
	{
		if(dao != null)
		{
			dao.deleteAll();
		}
	}

	/**
	 * =================打印相关====================
	 * <p/>
	 * 需要在AndroidManifest中加入以下权限
	 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
	 */
	protected static final String PATH_ROOT = Environment.getExternalStorageDirectory()+"/log/";

	public static boolean print(String path, List<LogMessage> list)
	{
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
		dateFormat.applyPattern("yyyy-MM-dd");
		path += dateFormat.format(date)+".log";
		File file = new File(path);
		if(!file.exists())
		{
			createDipPath(path);
		}
		BufferedWriter out = null;
		try
		{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			for(LogMessage message : list)
			{
				out.write(message.getJson()+","+"\r\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(out != null)
				{
					out.close();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 根据文件路径 递归创建文件
	 *
	 * @param file
	 */
	public static void createDipPath(String file)
	{
		String parentFile = file.substring(0, file.lastIndexOf("/"));
		File file1 = new File(file);
		File parent = new File(parentFile);
		if(!file1.exists())
		{
			parent.mkdirs();
			try
			{
				file1.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// ------------------------------- 硬件相关 ----------------------------
	public static String getPackageName()
	{
		if(packageInfo != null)
		{
			return packageInfo.packageName;
		}
		else
		{
			return "";
		}
	}

	public static String getVersion()
	{
		if(packageInfo != null)
		{
			return packageInfo.versionName;
		}
		else
		{
			return "";
		}
	}

	/**
	 * gps获取ip
	 */
	public static String getLocalIpAddress()
	{
		try
		{
			for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
			{
				NetworkInterface intf = en.nextElement();
				for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if(!inetAddress.isLoopbackAddress())
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * wifi获取ip
	 */
	@SuppressLint("MissingPermission") public static String getIpAddressWifi(Context context)
	{
		try
		{
			//获取wifi服务
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			//判断wifi是否开启
			if(!wifiManager.isWifiEnabled())
			{
				wifiManager.setWifiEnabled(true);
			}
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			String ip = intToIp(ipAddress);
			return ip;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 格式化ip地址（192.168.11.1）
	 */
	private static String intToIp(int i)
	{

		return (i & 0xFF)+"."+((i >> 8) & 0xFF)+"."+((i >> 16) & 0xFF)+"."+(i >> 24 & 0xFF);
	}

	/**
	 * 3G/4g网络IP
	 */
	public static String getIpAddressOperator()
	{
		try
		{
			for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
			{
				NetworkInterface intf = en.nextElement();
				for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
					{
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取本机的ip地址（3中方法都包括）
	 * <p>
	 * <!--***************************ip地址*******************************-->
	 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
	 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
	 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
	 * <uses-permission android:name="android.permission.WAKE_LOCK"></uses-permission>
	 * <uses-permission android:name="android.permission.INTERNET" />
	 * <!--***************************ip地址*******************************-->
	 */
	public static String getIP()
	{
		String ip = null;
		try
		{
			ip = getIpAddressWifi(mContext);
			if(ip == null)
			{
				ip = getIpAddressOperator();
				if(ip == null)
				{
					ip = getLocalIpAddress();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		Log.d("", "ip == "+ip);
		return ip;
	}

	@SuppressLint("HardwareIds") public static String getMac()
	{
		String mac = getMacAddress();
		if(mac == null || mac.length() == 0)
		{
			if(wifiInfo == null)
			{
				return "";
			}
			else
			{
				return wifiInfo.getMacAddress();
			}
		}
		else
		{
			return mac;
		}
	}

	public static String getMacAddress()
	{
		String macAddress = null;
		LineNumberReader lnr = null;
		InputStreamReader isr = null;
		try
		{
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
			isr = new InputStreamReader(pp.getInputStream());
			lnr = new LineNumberReader(isr);
			macAddress = lnr.readLine();
			if(macAddress != null && macAddress.length() > 0)
			{
				macAddress = lnr.readLine().replace(":", "");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeIO(lnr, isr);
		}
		return macAddress == null ? "" : macAddress;
	}

	/**
	 * 关闭IO
	 *
	 * @param closeables closeable
	 */
	public static void closeIO(Closeable... closeables)
	{
		if(closeables == null)
		{
			return;
		}
		try
		{
			for(Closeable closeable : closeables)
			{
				if(closeable != null)
				{
					closeable.close();
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * 获取手机唯一标识序列号
	 */
	public static String getUniqueSerialNumber()
	{
		String phoneName = Build.MODEL;// Galaxy nexus 品牌类型
		String manuFacturer = Build.MANUFACTURER;//samsung 品牌
		Log.d("详细序列号", manuFacturer+"-"+phoneName+"-"+getSerialNumber());
		return manuFacturer+"-"+phoneName+"-"+getSerialNumber();
	}

	/**
	 * 序列号
	 */
	public static String getSerialNumber()
	{
		String serial = null;
		try
		{
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return serial;
	}

	/**
	 * 获取设备的IMEI
	 * <p>需添加权限 {@code <uses-permission android:name="android.permission.READ_PHONE_STATE"/>}</p>
	 */
	@SuppressLint("MissingPermission") public static String getDeviceIdIMEI()
	{
		String id;
		//android.telephony.TelephonyManager
		TelephonyManager mTelephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) !=
			PackageManager.PERMISSION_GRANTED)
		{
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return Settings.Secure.getString(mContext
				                                 .getApplicationContext()
				                                 .getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		if(mTelephony.getDeviceId() != null)
		{
			id = mTelephony.getDeviceId();
		}
		else
		{
			//android.provider.Settings;
			id = Settings.Secure.getString(mContext
				                               .getApplicationContext()
				                               .getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		return id;
	}
}
