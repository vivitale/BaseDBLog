package talex.zsw.baselog.database;

/**
 * 作用: 日志等级
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2018/6/8 15:50 
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public enum LogLevel
{
	V("V"),
	I("I"),
	D("D"),
	W("W"),
	E("E"),
	A("A");

	private String level;

	private LogLevel(String lev)
	{
		level = lev;
	}

	public String getLevel()
	{
		try
		{
			return level;
		} catch (Exception ignored)
		{
			throw new Error("没有日志等级返回");
		}
	}
}
