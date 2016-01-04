package com.push.m.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.push.m.R;

public class FriendListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater; 
	private List<Map> mData;
	private int mResource;
    
    public FriendListAdapter(Context context, List<Map> data, int resource) {  
        // TODO Auto-generated constructor stub  
    	mContext = context;
        mInflater = LayoutInflater.from(context);  
        mData = data;
        mResource = resource;
    }  
      
    @Override  
    public int getCount() {  
        // TODO Auto-generated method stub  
        return mData.size();  
    }  
      
    @Override  
    public Object getItem(int position) {  
        // TODO Auto-generated method stub  
        return mData.get(position);  
    }  
      
    @Override  
    public long getItemId(int position) {  
        // TODO Auto-generated method stub  
        return 0;  
    }  
      
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        // TODO Auto-generated method stub 
        ViewHolder viewHolder = null;  
        if(convertView == null){  
            viewHolder = new ViewHolder();  
            convertView = mInflater.inflate(mResource, null);  
            viewHolder.tvFriendName = (TextView) convertView.findViewById(R.id.tvFriendName);   
            viewHolder.tvOnline = (TextView) convertView.findViewById(R.id.tvOnline);
            convertView.setTag(viewHolder);  
        }else{  
            viewHolder = (ViewHolder) convertView.getTag();  
        }  
        Map map = mData.get(position);
        String template = mContext.getResources().getString(R.string.online_status);
        String online = map.get("online").equals("1") ? String.format(template, "在线") : String.format(template, "离线");
        viewHolder.tvFriendName.setText((String)map.get("user_name"));
        viewHolder.tvOnline.setText(online);
        return convertView;  
    }  
      
    class ViewHolder{ 
    	TextView tvFriendName;
    	TextView tvOnline;
    }

}
