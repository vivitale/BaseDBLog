package talex.zsw.baselog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import talex.zsw.baselog.database.LogMessage;

/**
 * 作用: 数据库帮助类
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2017/5/24 15:39 
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class LogMessageHelper extends OrmLiteSqliteOpenHelper
{
	private static final String TABLE_NAME = "log.db";
	private static final int VERSION = 8;

	private Map<String, Dao> daos = new HashMap<String, Dao>();

	public LogMessageHelper(Context context)
	{
		super(context, TABLE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
		ConnectionSource connectionSource)
	{
		try
		{
			TableUtils.createTable(connectionSource, LogMessage.class);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
		ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		try
		{
			TableUtils.dropTable(connectionSource, LogMessage.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static LogMessageHelper instance;

	/**
	 * 单例获取该Helper
	 */
	public static synchronized LogMessageHelper getHelper(Context context)
	{
		context = context.getApplicationContext();
		if (instance == null)
		{
			synchronized (LogMessageHelper.class)
			{
				if (instance == null)
				{
					instance = new LogMessageHelper(context);
				}
			}
		}
		return instance;
	}

	public synchronized Dao getDao(Class clazz) throws SQLException
	{
		Dao dao = null;
		String className = clazz.getSimpleName();

		if (daos.containsKey(className))
		{
			dao = daos.get(className);
		}
		if (dao == null)
		{
			dao = super.getDao(clazz);
			daos.put(className, dao);
		}

		return dao;
	}

	/**
	 * 释放资源
	 */
	@Override
	public void close()
	{
		super.close();

		for (String key : daos.keySet())
		{
			Dao dao = daos.get(key);
			dao = null;
		}
	}
}