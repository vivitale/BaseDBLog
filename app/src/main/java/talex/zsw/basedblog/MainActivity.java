package talex.zsw.basedblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import talex.zsw.baselog.LogFilterActivity;
import talex.zsw.baselog.LogUtil;
import talex.zsw.baselog.database.LogLevel;
import talex.zsw.baselog.database.LogMessage;

public class MainActivity extends AppCompatActivity
{

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LogUtil.deleteAll();

		for(int i = 0; i < 20; i++)
		{
			LogUtil.add(new LogMessage(LogLevel.V, "日志"+i));
		}

		Intent intent = new Intent(MainActivity.this, LogFilterActivity.class);
		intent.putExtra("pageSize",10);
		startActivity(intent);
	}
}
