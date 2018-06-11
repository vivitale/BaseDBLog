package talex.zsw.baselog;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import talex.zsw.baselog.database.LogMessage;
import talex.zsw.baselog.util.CalendarUtil;

/**
 * 作用: 日志列表适配器
 * 作者: 赵小白 email:edisonzsw@icloud.com 
 * 日期: 2017/5/25 16:18 
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class LogAdapter extends BaseAdapter
{
	private List<LogMessage> list = new ArrayList<LogMessage>();
	private Context context;
	private String imageIp;

	public LogAdapter(Context context)
	{
		super();
		this.context = context;
	}

	public void setContentArray(List<LogMessage> newsList, boolean refresh)
	{
		if (refresh)
		{
			this.list.clear();
		}
		this.list = newsList;
		notifyDataSetChanged();
	}

	public List<LogMessage> getList()
	{
		return list;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (convertView == null)
		{
			convertView = inflate(R.layout.item_log);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		LogMessage resource = list.get(position);
		holder.showResource(resource);

		return convertView;
	}

	protected View inflate(int resource)
	{
		return LayoutInflater.from(context).inflate(resource, null);
	}

	class ViewHolder
	{
		public View iView;
		public TextView iTvTime, iTvOperation, iTvOperator, iTvContent;

		public ViewHolder(View convertView)
		{
			this.iView = convertView.findViewById(R.id.iView);
			this.iTvTime = (TextView) convertView.findViewById(R.id.iTvTime);
			this.iTvOperation = (TextView) convertView.findViewById(R.id.iTvOperation);
			this.iTvOperator = (TextView) convertView.findViewById(R.id.iTvOperator);
			this.iTvContent = (TextView) convertView.findViewById(R.id.iTvContent);
		}

		public void showResource(LogMessage resource)
		{
			iTvTime.setText(
				CalendarUtil.getDateStr(resource.getTime(), CalendarUtil.STR_FOMATER_DATA_TIME));
			iTvOperation.setText(resource.getOperation());
			iTvOperator.setText(resource.getOperator());
			iTvContent.setText(resource.getContent());
			// V I D W E A
			if (resource.getLevel().equals("V"))
			{
				iView.setBackgroundColor(Color.parseColor("#BBBBBB"));
			}
			else if (resource.getLevel().equals("I"))
			{
				iView.setBackgroundColor(Color.parseColor("#66BB13"));
			}
			else if (resource.getLevel().equals("D"))
			{
				iView.setBackgroundColor(Color.parseColor("#2068BB"));
			}
			else if (resource.getLevel().equals("W"))
			{
				iView.setBackgroundColor(Color.parseColor("#BBB000"));
			}
			else if (resource.getLevel().equals("E"))
			{
				iView.setBackgroundColor(Color.parseColor("#FF6B68"));
			}
			else if (resource.getLevel().equals("A"))
			{
				iView.setBackgroundColor(Color.parseColor("#FF0000"));
			}
			else
			{
				iView.setBackgroundColor(Color.parseColor("#000000"));
			}
		}
	}
}