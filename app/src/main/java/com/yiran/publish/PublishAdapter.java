package com.yiran.publish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PublishAdapter extends BaseAdapter {
    private Context context;
    private List<DataBean> dataList;

    public PublishAdapter(Context context, List<DataBean> list){
        this.context = context;
        this.dataList = list;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.public_item, null);
            holder.tv_title = convertView.findViewById(R.id.tv_title);
            holder.tv_time = convertView.findViewById(R.id.tv_time);
            holder.tv_content = convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_title.setText(dataList.get(position).getTitle());
        holder.tv_content.setText(dataList.get(position).getContent());
        holder.tv_time.setText(dataList.get(position).getTime());
        return convertView;
    }


    class ViewHolder{
        TextView tv_title, tv_content, tv_time;
    }

}
