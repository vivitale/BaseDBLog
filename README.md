# BaseDBLog - 日志存储工具类
[![](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html)  [![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)  [![](https://jitpack.io/v/vivitale/BaseDBLog.svg)](https://jitpack.io/#vivitale/BaseDBLog) [![Twitter](https://img.shields.io/badge/Gradle-3.0.1-brightgreen.svg)](https://github.com/vivitale/BaseCore)

[TOC]
 
## 使用方法
> 第一步 在 build.gradle(Project:XXXX) 的 repositories 添加::	allprojects {		repositories {			...			maven { url "https://jitpack.io" }		}	}> 第二步 在 build.gradle(Module:app) 的 dependencies 添加:	dependencies {	        implementation 'com.github.vivitale:BaseDBLog:v1.0.0'	}> 第三步 使用方法,在Application中初始化:
 
    @Override public void onCreate()
	{
		super.onCreate();
		LogUtil.init(getApplicationContext());
	}

    public void exit()
	{
		LogUtil.exit();
	}

> 第四步 您可以自己写日志的Activity也可以直接使用已经写好的基础Activity
    
    在清单文件加入
    <activity android:name="talex.zsw.baselog.LogFilterActivity"/>
    
    调用方法
    Intent intent = new Intent(MainActivity.this, LogFilterActivity.class);
	intent.putExtra("pageSize",10);//不传pageSize 则不使用分页功能
	startActivity(intent);

> 第五步 添加日志

    LogUtil.add(new LogMessage(LogLevel.V, "日志"+i));

## LogMessage
```
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

```


## LogUtil
方法名 | 说明
--------- | -------------
init                        | 初始化
exit                        | 退出,清理
getLogMessageDao            | 获取Dao实例操作数据库
add                         | 添加一条日志
queryAll                    | 查询所有日志
queryDate                   | 查询一个时间段的日志
queryDateOffset             | 查询日志,带分页属性
queryDateLimit              | 获取指定时间后的多少条日志
queryDateAndLevel           | 查询某个日期下,某级别的日志
deleteDays                  | 删除几天内的日志
deleteAll                   | 删除全部日志
print                       | 打印日志到SD卡跟目录/log文件夹下


