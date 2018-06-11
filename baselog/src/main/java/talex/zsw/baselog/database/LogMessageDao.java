package talex.zsw.baselog.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import talex.zsw.baselog.LogMessageHelper;

/**
 * 作用: 日志文件的Dao文件
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2017/5/24 15:35 
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class LogMessageDao
{
	private Context context;
	private static Dao<LogMessage, Integer> dao;
	private LogMessageHelper helper;

	public LogMessageDao(Context context)
	{
		this.context = context;
		try
		{
			helper = LogMessageHelper.getHelper(context);
			dao = helper.getDao(LogMessage.class);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public Dao<LogMessage, Integer> getDao()
	{
		return dao;
	}

	/**
	 * 插入数据
	 *
	 * @param data 数据
	 */
	public static void add(LogMessage data)
	{
		try
		{
			dao.createIfNotExists(data);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 更新数据
	 *
	 * @param data 数据
	 */
	public static void update(LogMessage data)
	{
		try
		{
			dao.createOrUpdate(data);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 按照指定的id删除一项
	 *
	 * @param id 主键id
	 * @return 删除成功返回true ，失败返回false
	 */
	public static int delete(int id)
	{
		try
		{
			// 删除指定的信息，类似delete User where 'id' = id ;
			DeleteBuilder<LogMessage, Integer> deleteBuilder = dao.deleteBuilder();
			deleteBuilder.where().eq("id", id);
			return deleteBuilder.delete();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 按照字段查询数据
	 *
	 * @param name  要查询的字段名
	 * @param value 要查询的字段值
	 * @return 数据
	 */
	public static LogMessage search(String name, Object value)
	{
		try
		{
			// 查询的query 返回值是一个列表
			// 类似 select * from User where 'username' = username;
			List<LogMessage> datas = dao.queryBuilder().where().eq(name, value).query();
			if(datas.size() > 0)
			{
				return datas.get(0);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 按照id查询数据
	 *
	 * @param id 主键id
	 * @return 数据
	 */
	public static LogMessage search(int id)
	{
		try
		{
			// 查询的query 返回值是一个列表
			// 类似 select * from User where 'username' = username;
			List<LogMessage> datas = dao.queryBuilder().where().eq("id", id).query();
			if(datas.size() > 0)
			{
				return datas.get(0);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 按照id查询所有匹配数据
	 *
	 * @param id 主键id
	 * @return 数据
	 */
	public static List<LogMessage> searchAll(int id)
	{
		try
		{
			// 查询的query 返回值是一个列表
			// 类似 select * from User where 'username' = username;
			List<LogMessage> datas = dao.queryBuilder().where().eq("id", id).query();
			return datas;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/**
	 * 按照字段查询数据
	 *
	 * @param name  要查询的字段名
	 * @param value 要查询的字段值
	 * @return 数据
	 */
	public static List<LogMessage> searchAll(String name, Object value)
	{
		try
		{
			// 查询的query 返回值是一个列表
			// 类似 select * from User where 'username' = username;
			List<LogMessage> datas = dao.queryBuilder().where().eq(name, value).query();
			return datas;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/**
	 * 删除全部
	 */
	public static void deleteAll()
	{
		try
		{
			dao.delete(queryAll());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 查询所有的
	 */
	public static List<LogMessage> queryAll()
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao.queryForAll();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * 查询所有的 倒序
	 */
	public List<LogMessage> queryAllOrder()
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao.queryBuilder().orderBy("id", false).query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * 查询指定位置指定数量
	 *
	 * @param offset 表示查询的起始位置
	 * @param limit  表示总共获取的对象数量
	 */
	public static List<LogMessage> query(long offset, long limit)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao.queryBuilder().offset(offset).limit(limit).query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	public static List<LogMessage> queryDate(long starttime, long end)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao.queryBuilder().orderBy("id", false).where().between("time", starttime, end).query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * @param starttime 开始时间
	 * @param end       结束时间
	 * @param offset    查询的起始位置
	 * @param limit     总共获取的对象数量
	 */
	public static List<LogMessage> queryDate(long starttime, long end, long offset, long limit)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao
				.queryBuilder()
				.orderBy("id", false)
				.offset(offset)
				.limit(limit)
				.where()
				.between("time", starttime, end)
				.query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * 查询指定时间开始 指定条数
	 *
	 * @param starttime
	 * @param offset
	 * @param limit
	 */
	public static List<LogMessage> queryDateOffset(long starttime, long offset, long limit)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao.queryBuilder().offset(offset).limit(limit).where().gt("time", starttime).query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	public static List<LogMessage> queryDateOffseColumns(long starttime, List<String> columns, long offset, long limit)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao
				.queryBuilder()
				.selectColumns(columns)
				.offset(offset)
				.limit(limit)
				.where()
				.gt("time", starttime)
				.query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * @param start  开始时间
	 * @param end    结束时间
	 * @param level  日志等级
	 * @param offset 查询的起始位置
	 * @param limit  总共获取的对象数量
	 */
	public static List<LogMessage> queryDateAndLevel(long start, long end, String level, long offset, long limit)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao
				.queryBuilder()
				.orderBy("id", false)
				.offset(offset)
				.limit(limit)
				.where()
				.between("time", start, end)
				.and()
				.eq("level", level)
				.query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	public static List<LogMessage> queryDateAndLevel(long start, long end, String level)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao
				.queryBuilder()
				.orderBy("id", false)
				.where()
				.between("time", start, end)
				.and()
				.eq("level", level)
				.query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}

	public static void deleteDays(int days)
	{
		Calendar calendar = Calendar.getInstance();
		if(days != 0)
		{
			calendar.add(Calendar.DATE, days);// 把日期往后增加一天.整数往后推,负数往前移动
		}
		try
		{

			//            List<LogMessage> msgs = queryLt(calendar.getTimeInMillis());
			LogMessage user = dao
				.queryBuilder()
				.orderBy("id", false)
				.where()
				.lt("time", calendar.getTimeInMillis())
				.queryForFirst();
			if(null != user)
			{
				DeleteBuilder deleteBuilder = dao.deleteBuilder();
				deleteBuilder.where().lt("id", user.getId());
				deleteBuilder.delete();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static List<LogMessage> queryLt(long time)
	{
		List<LogMessage> users = new ArrayList<>();
		try
		{
			users = dao.queryBuilder().orderBy("id", false).where().lt("time", time).query();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return users;
	}
}