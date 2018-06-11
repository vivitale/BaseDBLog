package talex.zsw.baselog.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Calendar;

import talex.zsw.baselog.LogUtil;

/**
 * 作用: 单条日志
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2018/6/8 14:49 
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@DatabaseTable(tableName = "LogMessage")
public class LogMessage implements Serializable
{
	@DatabaseField(generatedId = true, columnName = "id") private int id;// 时间戳
	@DatabaseField(columnName = "time") private long time;// 时间戳
	@DatabaseField(columnName = "level") private String level;// V I D W E A
	@DatabaseField(columnName = "content") private String content;// 内容
	@DatabaseField(columnName = "operation") private String operation;// 操作
	@DatabaseField(columnName = "operator") private String operator;// 操作人
	@DatabaseField(columnName = "ip") private String ip;// IP地址
	@DatabaseField(columnName = "mac") private String mac;//设备物理地址
	@DatabaseField(columnName = "manuFacturer") private String manuFacturer;//获取手机唯一标识序列号
	@DatabaseField(columnName = "imei") private String imei;//获取设备的IMEI
	@DatabaseField(columnName = "packageName") private String packageName;//包名
	@DatabaseField(columnName = "version") private String version;//版本号

	public LogMessage()
	{
		this(LogLevel.D, "", "", "", 5);
	}

	/**
	 * @param logLevel 日志等级
	 * @param content  日志内容
	 */
	public LogMessage(LogLevel logLevel, String content)
	{
		this(logLevel, content, "", "", 5);
	}

	/**
	 * @param logLevel  日志等级
	 * @param content   日志内容
	 * @param operation 操作
	 * @param operator  操作员
	 */
	public LogMessage(LogLevel logLevel, String content, String operation, String operator)
	{
		this(logLevel, content, operation, operator, 5);
	}

	/**
	 * @param logLevel  日志等级
	 * @param content   日志内容
	 * @param operation 操作
	 * @param operator  操作员
	 */
	public LogMessage(LogLevel logLevel, String content, String operation, String operator, int level)
	{
		this.time = Calendar.getInstance().getTimeInMillis();
		this.level = logLevel.getLevel();
		this.content = generateTag(getCallerMethodName(level))+" - "+content;
		this.operation = operation;
		this.operator = operator;
		this.ip = LogUtil.getIP();
		this.mac = LogUtil.getMac();
		this.packageName = LogUtil.getPackageName();
		this.version = LogUtil.getVersion();
		this.manuFacturer = LogUtil.getUniqueSerialNumber();
		this.imei = LogUtil.getDeviceIdIMEI();
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public String getLevel()
	{
		return level;
	}

	public void setLevel(String level)
	{
		this.level = level;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getOperation()
	{
		return operation;
	}

	public void setOperation(String operation)
	{
		this.operation = operation;
	}

	public String getOperator()
	{
		return operator;
	}

	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getMac()
	{
		return mac;
	}

	public void setMac(String mac)
	{
		this.mac = mac;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getVersion()
	{
		return version;
	}

	public String getManuFacturer()
	{
		return manuFacturer;
	}

	public void setManuFacturer(String manuFacturer)
	{
		this.manuFacturer = manuFacturer;
	}

	public String getImei()
	{
		return imei;
	}

	public void setImei(String imei)
	{
		this.imei = imei;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	@Override public String toString()
	{
		return "LogMessage{"+"time="+time+", level='"+level+'\''+", content='"+content+'\''+", operation='"+operation+
			'\''+", operator='"+operator+'\''+", ip='"+ip+'\''+", mac='"+mac+'\''+", imei='"+imei+'\''+
			", manuFacturer='"+manuFacturer+'\''+", packageName='"+packageName+'\''+", version='"+version+'\''+'}';
	}

	public String getJson()
	{
		String json = "{\"id\": \""+id+"\","+"\"time\": \""+time+"\","+"\"level\": \""+level+"\","+"\"content\": \""+
			content+"\","+"\"operation\": \""+operation+"\","+"\"operator\": \""+operator+"\","+"\"ip\": \""+ip+"\","+
			"\"mac\": \""+mac+"\","+"\"imei\": \""+imei+"\","+"\"manuFacturer\": \""+manuFacturer+"\","+
			"\"packageName\": \""+packageName+"\","+"\"version\": \""+version+"\"}";
		return json;
	}

	// 跟踪到调用该日志的方法
	private static StackTraceElement getCallerMethodName(int level)
	{
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		return stacks[level];
	}

	// 规范TAG格式：类名[方法名， 调用行数]
	private static String generateTag(StackTraceElement caller)
	{
		String tag = "%s[%s, %d]";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".")+1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		return tag;
	}
}
