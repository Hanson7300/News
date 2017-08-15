package com.example.android.news;

/**
 * Created by Hansson on 2017/8/14.
 */

public class News {
    //新闻标题
    private String mWebTitle;
    //发布时间
    private String mDate;
    //新闻url
    private String mWebUrl;

    public News(String webTitle,String date,String webUrl){
        mWebTitle =webTitle;
        mWebUrl = webUrl;
        mDate = date;
    }

    public String getWebTitle() {
        return mWebTitle;
    }
    public String getDate() {
        return mDate;
    }
    public String getWebUrl() {
        return mWebUrl;
    }
}
