package com.example.android.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hansson on 2017/8/14.
 */

public class NewsAdapter  extends ArrayAdapter<News>{

    public NewsAdapter( Context context, ArrayList<News> newsList) {
        super(context, 0, newsList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            //这里传入了要渲染的xml文件
            //如果没有,从头渲染一个
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_news,parent,false);
        }
        //找到当前位置的news class
        News currentNews = getItem(position);
        //找到对应TextView,传入数据
        TextView title = (TextView) convertView.findViewById(R.id.title_text_view);
        title.setText(currentNews.getWebTitle());

        TextView date =(TextView)convertView.findViewById(R.id.date_text_view);
        date.setText(currentNews.getDate());

        return convertView;
    }
}
