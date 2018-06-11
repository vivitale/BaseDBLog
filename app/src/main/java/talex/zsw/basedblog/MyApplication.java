package talex.zsw.basedblog;

import android.app.Application;

import talex.zsw.baselog.LogUtil;


/**
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2017/5/24 16:50 
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MyApplication extends Application
{
	@Override public void onCreate()
	{
		super.onCreate();
		LogUtil.init(getApplicationContext());
	}

	public void exit()
	{
		LogUtil.exit();
	}
}
