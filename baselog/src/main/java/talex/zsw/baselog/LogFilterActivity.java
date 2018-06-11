package talex.zsw.baselog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import talex.zsw.baselog.database.LogMessage;
import talex.zsw.baselog.slidedatetimepicker.SlideDateTimeListener;
import talex.zsw.baselog.slidedatetimepicker.SlideDateTimePicker;
import talex.zsw.baselog.util.CalendarUtil;
import talex.zsw.baselog.util.PermissionsTool;

/**
 * 作用: 日志筛选
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2017/5/25 11:05 
 * 修改人：赵盛纬
 * 修改时间：2018.06.07
 * 修改备注：加入分页
 */
public class LogFilterActivity extends AppCompatActivity
	implements View.OnClickListener, AdapterView.OnItemClickListener
{
	private TextView mTvStart, mTvEnd, mTvSave, mTvDeleteAll;
	private Spinner mSpinner;
	private ListView mListView;
	private LinearLayout mLLBottom;
	private LinearLayout mLLPage;
	private TextView mTvPrePage, mTvNextPage, mTvPageNow;
	private ImageView mIvBack;

	private Date startDate, endDate;
	private String[] levels = {"ALL", "V", "I", "D", "W", "E", "A"};
	private LogAdapter adapter;
	private int pageSize = 0;
	private int pageNow = 1;
	private boolean isFirst = true;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		pageSize = getIntent().getIntExtra("pageSize", 0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_logfilter);

		if(getSupportActionBar() != null)
		{
			getSupportActionBar().hide();
		}
		if(getActionBar() != null)
		{
			getActionBar().hide();
		}

		mIvBack = (ImageView) findViewById(R.id.mIvBack);
		mTvStart = (TextView) findViewById(R.id.mTvStart);
		mTvEnd = (TextView) findViewById(R.id.mTvEnd);
		mTvSave = (TextView) findViewById(R.id.mTvSave);
		mTvDeleteAll = (TextView) findViewById(R.id.mTvDeleteAll);
		mSpinner = (Spinner) findViewById(R.id.mSpinner);
		mListView = (ListView) findViewById(R.id.mListView);
		mLLBottom = (LinearLayout) findViewById(R.id.mLLBottom);
		mLLPage = (LinearLayout) findViewById(R.id.mLLPage);
		mTvPrePage = (TextView) findViewById(R.id.mTvPrePage);
		mTvNextPage = (TextView) findViewById(R.id.mTvNextPage);
		mTvPageNow = (TextView) findViewById(R.id.mTvPageNow);

		mIvBack.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				finish();
			}
		});
		mTvPrePage.setOnClickListener(prePageListener);
		mTvNextPage.setOnClickListener(nextPageListener);
		if(pageSize == 0)
		{
			mLLPage.setVisibility(View.GONE);
		}

		//		setSupportActionBar(mToolbar);

		Date rightNow = new Date();

		String strStart = CalendarUtil.getDateString(rightNow, CalendarUtil.STR_FOMATER_DATA)+" 00:00";

		Log.e("TEST", strStart);
		startDate = CalendarUtil.getDate(strStart, CalendarUtil.STR_FOMATER_DATA_TIME2);
		endDate = rightNow;

		mTvStart.setOnClickListener(this);
		mTvEnd.setOnClickListener(this);
		mTvSave.setOnClickListener(this);
		mTvDeleteAll.setOnClickListener(this);

		adapter = new LogAdapter(LogFilterActivity.this);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				pageNow = 1;
				if(!isFirst)
				{
					isFirst = false;
					refresh();
				}
			}

			@Override public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		pageNow = 1;

		if(PermissionsTool.checkPermission(LogFilterActivity.this, PermissionsTool.WRITE_EXTERNAL_STORAGE))
		{
			if(dialog != null && !dialog.isShowing() && adapter.getCount() == 0)
			{
				refresh();
			}
		}
		else
		{
			new PermissionsTool.Builder(LogFilterActivity.this)
				.addPermission(PermissionsTool.READ_EXTERNAL_STORAGE)
				.addPermission(PermissionsTool.WRITE_EXTERNAL_STORAGE)
				.initPermission();
		}
	}

	@Override protected void onResume()
	{
		super.onResume();
		if(PermissionsTool.checkPermission(LogFilterActivity.this, PermissionsTool.WRITE_EXTERNAL_STORAGE))
		{
			if(dialog != null && !dialog.isShowing() && adapter.getCount() == 0)
			{
				refresh();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == PermissionsTool.REQUEST_CODE)
		{
			refresh();
		}
	}

	// ------------------------ 分页 - start ----------------------
	private View.OnClickListener prePageListener = new View.OnClickListener()
	{
		@Override public void onClick(View v)
		{
			if(isFastClick())
			{
				return;
			}
			if(pageNow == 1)
			{
				mTvPrePage.setEnabled(false);
			}
			else
			{
				pageNow--;
				mTvPrePage.setEnabled(true);
				mTvNextPage.setEnabled(true);
				if(pageNow == 1)
				{
					mTvPrePage.setEnabled(false);
				}
				refresh();
			}
		}
	};

	private View.OnClickListener nextPageListener = new View.OnClickListener()
	{
		@Override public void onClick(View v)
		{
			if(isFastClick())
			{
				return;
			}
			pageNow++;
			mTvPrePage.setEnabled(true);
			mTvNextPage.setEnabled(true);
			if(pageNow == 1)
			{
				mTvPrePage.setEnabled(false);
			}
			refresh();
		}
	};

	private static long mLastClickTime;
	public static final int MIN_CLICK_DELAY_TIME = 500;

	private boolean isFastClick()
	{
		// 当前时间
		long currentTime = System.currentTimeMillis();
		// 两次点击的时间差
		long time = currentTime-mLastClickTime;
		if(0 < time && time < MIN_CLICK_DELAY_TIME)
		{
			return true;
		}
		mLastClickTime = currentTime;
		return false;
	}
	// ------------------------ 分页 - end ----------------------

	private void refresh()
	{
		if(PermissionsTool.checkPermission(LogFilterActivity.this, PermissionsTool.WRITE_EXTERNAL_STORAGE))
		{
			mTvStart.setText(CalendarUtil.getDateString(startDate, CalendarUtil.STR_FOMATER_DATA_TIME2));
			mTvEnd.setText(CalendarUtil.getDateString(endDate, CalendarUtil.STR_FOMATER_DATA_TIME2));
			mTvPageNow.setText("第"+pageNow+"页");

			List<LogMessage> list = new ArrayList<>();

			if(mSpinner.getSelectedItemPosition() == 0)
			{
				if(pageSize == 0)
				{
					list = LogUtil.queryDate(startDate.getTime(), endDate.getTime());
				}
				else
				{
					long offset = pageNow*pageSize;
					long limit = pageSize;
					list = LogUtil.queryDate(startDate.getTime(), endDate.getTime(), offset, limit);
					if(pageNow == 1)
					{
						mTvPrePage.setEnabled(false);
						mTvNextPage.setEnabled(true);
					}
					else
					{
						mTvPrePage.setEnabled(true);
						mTvNextPage.setEnabled(true);
					}
				}

				if(list.size() > 0)
				{
					mLLBottom.setVisibility(View.VISIBLE);
				}
				else
				{
					mLLBottom.setVisibility(View.GONE);
				}
				if(list.size() < pageSize)
				{
					mTvNextPage.setEnabled(false);
				}
				if(pageNow <= 1 && (list == null || list.size() == 0))
				{
					mLLPage.setVisibility(View.GONE);
				}
				else
				{
					mLLPage.setVisibility(View.VISIBLE);
				}
				adapter.setContentArray(list, true);
			}
			else
			{
				String level = getSelected(mSpinner.getSelectedItemPosition());
				if(pageSize == 0)
				{
					list = LogUtil.queryDateAndLevel(startDate.getTime(), endDate.getTime(), level);
				}
				else
				{
					long offset = pageNow*pageSize;
					long limit = pageSize;
					list = LogUtil.queryDateAndLevel(startDate.getTime(), endDate.getTime(), level, offset, limit);
					if(pageNow == 1)
					{
						mTvPrePage.setEnabled(false);
						mTvNextPage.setEnabled(true);
					}
					else
					{
						mTvPrePage.setEnabled(true);
						mTvNextPage.setEnabled(true);
					}
				}

				if(list.size() > 0)
				{
					mLLBottom.setVisibility(View.VISIBLE);
				}
				else
				{
					mLLBottom.setVisibility(View.GONE);
				}
				if(list.size() < pageSize)
				{
					mTvNextPage.setEnabled(false);
				}
				if(pageNow <= 1 && (list == null || list.size() == 0))
				{
					mLLPage.setVisibility(View.GONE);
				}
				else
				{
					mLLPage.setVisibility(View.VISIBLE);
				}
				adapter.setContentArray(list, true);
			}
		}
		else
		{
			Toast.makeText(this, "读写日志需要SD卡权限,请于设置中允许", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
			Uri uri = Uri.fromParts("package", this.getPackageName(), null);
			intent.setData(uri);
			startActivity(intent);
		}
	}

	private String getSelected(int position)
	{
		return levels[position];
	}

	// ================================ 显示日历 ===============================

	@Override public void onClick(View v)
	{
		if(v.getId() == R.id.mTvStart)
		{
			new SlideDateTimePicker.Builder(getSupportFragmentManager())
				.setListener(startListener)
				.setInitialDate(startDate)
				//.setMinDate(minDate)
				.setMaxDate(endDate)
				.setIs24HourTime(true)
				.setTheme(SlideDateTimePicker.HOLO_LIGHT)
				.build()
				.show();
		}
		else if(v.getId() == R.id.mTvEnd)
		{
			new SlideDateTimePicker.Builder(getSupportFragmentManager())
				.setListener(endListener)
				.setInitialDate(endDate)
				.setMinDate(startDate)
				.setMaxDate(new Date())
				.setIs24HourTime(true)
				.setTheme(SlideDateTimePicker.HOLO_LIGHT)
				.build()
				.show();
		}
		else if(v.getId() == R.id.mTvSave)
		{
			List<String> paths = new ArrayList<>();
			String inPath = getInnerSDCardPath();
			if(!CalendarUtil.isBlank(inPath))
			{
				paths.add(inPath);
			}
			List<String> extPaths = getExtSDCardPath();
			if(extPaths != null && extPaths.size() > 0)
			{
				paths.addAll(extPaths);
			}

			if(paths.size() == 0)
			{
				Toast.makeText(LogFilterActivity.this, "未检测到SD卡,不能执行该操作", Toast.LENGTH_LONG).show();
			}
			else
			{
				savePath = paths.get(0);
				startSaveThread();
			}
		}
		else if(v.getId() == R.id.mTvDeleteAll)
		{
			LogUtil.deleteAll();
			pageNow = 1;
			refresh();
		}
	}

	private SlideDateTimeListener startListener = new SlideDateTimeListener()
	{
		@Override public void onDateTimeSet(Date date)
		{
			if(CalendarUtil.isAB(date, endDate))
			{
				Toast.makeText(LogFilterActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
			}
			else
			{
				startDate = date;
				pageNow = 1;
				refresh();
			}
		}

		@Override public void onDateTimeCancel()
		{
		}
	};

	private SlideDateTimeListener endListener = new SlideDateTimeListener()
	{
		@Override public void onDateTimeSet(Date date)
		{
			if(CalendarUtil.isAB(startDate, date))
			{
				Toast.makeText(LogFilterActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
			}
			else
			{
				endDate = date;
				pageNow = 1;
				refresh();
			}
		}

		@Override public void onDateTimeCancel()
		{
		}
	};

	// ================================ 显示详情 ===============================
	private Dialog dialog;
	private View dView;
	private TextView dTvTime, dTvOperation, dTvOperator, dTvContent, dTvIP, dTvMac, dTvPackage, dTvVersion, dTvManu,
		dTvIMEI;

	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		LogMessage message = (LogMessage) adapter.getItem(position);
		showDialog(message);
	}

	private void showDialog(LogMessage message)
	{
		if(dialog == null)
		{
			dialog = new Dialog(LogFilterActivity.this, R.style.dialog);
			dialog.setContentView(R.layout.dialog_log);
			dialog.setCanceledOnTouchOutside(true);
			WindowManager windowManager = getWindow().getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();

			int width = display.getWidth();
			int height = display.getHeight();

			if(width > height)
			{
				lp.width = (int) (display.getWidth()*0.6f); // 设置宽度
				lp.height = (int) (display.getHeight()*0.8f); // 设置高度
			}
			else
			{
				lp.width = (int) (display.getWidth()*0.8f); // 设置宽度
				lp.height = (int) (display.getHeight()*0.6f); // 设置高度
			}
			dialog.getWindow().setAttributes(lp);

			dView = dialog.findViewById(R.id.dView);
			dTvTime = (TextView) dialog.findViewById(R.id.dTvTime);
			dTvOperation = (TextView) dialog.findViewById(R.id.dTvOperation);
			dTvOperator = (TextView) dialog.findViewById(R.id.dTvOperator);
			dTvContent = (TextView) dialog.findViewById(R.id.dTvContent);
			dTvIP = (TextView) dialog.findViewById(R.id.dTvIP);
			dTvMac = (TextView) dialog.findViewById(R.id.dTvMac);
			dTvPackage = (TextView) dialog.findViewById(R.id.dTvPackage);
			dTvVersion = (TextView) dialog.findViewById(R.id.dTvVersion);
			dTvManu = (TextView) dialog.findViewById(R.id.dTvManu);
			dTvIMEI = (TextView) dialog.findViewById(R.id.dTvIMEI);

			dTvContent.setOnLongClickListener(new View.OnLongClickListener()
			{
				@Override public boolean onLongClick(View v)
				{
					copyText(LogFilterActivity.this, dTvContent.getText());
					Toast.makeText(LogFilterActivity.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
					return false;
				}
			});
		}

		// V I D W E A
		if(message.getLevel().equals("V"))
		{
			dView.setBackgroundColor(Color.parseColor("#BBBBBB"));
		}
		else if(message.getLevel().equals("I"))
		{
			dView.setBackgroundColor(Color.parseColor("#66BB13"));
		}
		else if(message.getLevel().equals("D"))
		{
			dView.setBackgroundColor(Color.parseColor("#2068BB"));
		}
		else if(message.getLevel().equals("W"))
		{
			dView.setBackgroundColor(Color.parseColor("#BBB000"));
		}
		else if(message.getLevel().equals("E"))
		{
			dView.setBackgroundColor(Color.parseColor("#FF6B68"));
		}
		else if(message.getLevel().equals("A"))
		{
			dView.setBackgroundColor(Color.parseColor("#FF0000"));
		}
		else
		{
			dView.setBackgroundColor(Color.parseColor("#000000"));
		}

		dTvTime.setText("时间："+CalendarUtil.getDateStr(message.getTime(), CalendarUtil.STR_FOMATER_DATA_TIME));
		dTvOperation.setText("操作："+message.getOperation());
		dTvOperator.setText("操作人："+message.getOperator());
		dTvIP.setText("IP："+message.getIp());
		dTvMac.setText("MAC："+message.getMac());
		dTvPackage.setText("包名："+message.getPackageName());
		dTvVersion.setText("版本号："+message.getVersion());
		dTvManu.setText("设备："+message.getManuFacturer());
		dTvIMEI.setText("IMEI："+message.getImei());
		dTvContent.setText(message.getContent());

		dialog.show();
	}


	/**
	 * 获取内置SD卡路径
	 *
	 * @return
	 */
	public String getInnerSDCardPath()
	{
		return Environment.getExternalStorageDirectory()+"/log/";
	}

	/**
	 * 获取外置SD卡路径
	 *
	 * @return 应该就一条记录或空
	 */
	public List<String> getExtSDCardPath()
	{
		List<String> lResult = new ArrayList<>();
		try
		{
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while((line = br.readLine()) != null)
			{
				if(line.contains("extSdCard"))
				{
					String[] arr = line.split(" ");
					String path = arr[1];
					File file = new File(path);
					if(file.isDirectory())
					{
						lResult.add(path+"/log/");
					}
				}
			}
			isr.close();
		}
		catch(Exception ignored)
		{
		}
		return lResult;
	}

	// ==============================打印==================================

	private SaveThread thread;
	private String savePath;

	private Handler handler = new Handler()
	{
		@Override public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what == 99)
			{
				Toast.makeText(LogFilterActivity.this, "保存完成\n"+savePath, Toast.LENGTH_LONG).show();
			}
		}
	};

	private class SaveThread extends Thread
	{
		@Override public void run()
		{
			try
			{
				Log.d("TEST", "开始输出");
				LogUtil.print(savePath, adapter.getList());
				Log.d("TEST", "输出完成");
				handler.sendEmptyMessage(99);
			}
			catch(Exception ignore)
			{
			}
			super.run();
		}
	}

	public void startSaveThread()
	{
		if(thread == null)
		{
			thread = new SaveThread();
			thread.start();
		}
	}

	public void stopSaveThread() throws IOException
	{
		if(thread != null)
		{
			thread.interrupt();
			thread = null;
		}
	}

	// --------------------------- 剪切板 ---------------------

	/**
	 * 复制文本到剪贴板
	 *
	 * @param text 文本
	 */
	public static void copyText(Context context, CharSequence text)
	{
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(ClipData.newPlainText("text", text));
	}

	/**
	 * 获取剪贴板的文本
	 *
	 * @return 剪贴板的文本
	 */
	public static CharSequence getText(Context context)
	{
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = clipboard.getPrimaryClip();
		if(clip != null && clip.getItemCount() > 0)
		{
			return clip.getItemAt(0).coerceToText(context);
		}
		return null;
	}
}
