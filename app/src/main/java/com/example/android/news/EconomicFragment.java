package com.example.android.news;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class EconomicFragment extends Fragment {

    private NewsAdapter mAdapter;

    protected final String LOG_TAG = MainActivity.class.getSimpleName();

    //网址和API_KEY
    public final String BASIC_URL = "https://content.guardianapis.com/search?q=";
    public final String API_KEY = "&api-key=8e2ded07-be97-489b-975f-c29e206a8545";

    private URL formatUrl(String topic){
        URL url = null;
        try {
            url = new URL(BASIC_URL+topic+API_KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }return url;
    }

    URL economic =formatUrl("economic");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        mAdapter = new NewsAdapter(getContext(), new ArrayList<News>());
        listView.setAdapter(mAdapter);

        //数据未显示时加载圆圈进度
        ProgressBar progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        listView.setEmptyView(progressBar);

        //点击列表项打开网址
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = mAdapter.getItem(position);
                Uri webUri = null;
                if (currentNews != null) {
                    webUri = Uri.parse(currentNews.getWebUrl());
                }
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                startActivity(webIntent);
            }
        });
        NewsAsyncTask task = new NewsAsyncTask();

        task.execute(economic);
        return rootView;
    }

    private class NewsAsyncTask extends AsyncTask<URL, Void, ArrayList<News>> {
        @Override
        protected void onPostExecute(ArrayList<News> data) {
            mAdapter.clear();
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }

        @Override
        protected ArrayList<News> doInBackground(URL... urls) {
            //以这个url为准,execute(url)传入到这里

            String jsonResponse = "";
            //从String url 取得String jsonResponse(一整个Json)
            try {
                jsonResponse = makeHttpRequest(urls[0]);
            } catch (IOException e) {
                Log.e(LOG_TAG,"Error with creating url",e);
            }
            //从Json 中提取数据,返回ArrayList
            return extractFeatureFromJson(jsonResponse);
        }

        //主要负责通讯,通讯成功调用readFromJson
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(15000);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem with retrieving the news Json result");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;

        }

        //数据流Json转化为String
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<News> extractFeatureFromJson(String newsJson) {
            ArrayList<News> finalArraylist = new ArrayList<>();
            try {
                JSONObject baseJsonresponse = new JSONObject(newsJson);

                JSONObject response = baseJsonresponse.optJSONObject("response");
                JSONArray results = response.optJSONArray("results");
                if(results.length()>0){
                    for (int index = 0; index < results.length(); index++) {
                        JSONObject singleNews = results.optJSONObject(index);

                        //提取标题,网页Url,日期
                        String title_with_res = singleNews.optString("webTitle");

                        //去掉标题后面的|Letters
                        String title;
                        if(title_with_res.contains("|")){
                            String[] parts = title_with_res.split("[|]");
                            String part1 =parts[0];
                            title=part1;
                        }else{
                            title = title_with_res;
                        }
                        //去掉确切的时间,保留日期
                        String publicationDate;
                        String publicationDate_with_time = singleNews.optString("webPublicationDate");
                        if (publicationDate_with_time.contains("T")){
                            String publicationDate_with_year = publicationDate_with_time.split("T")[0];
                            if(publicationDate_with_year.length()>=10){
                                publicationDate = publicationDate_with_year.substring(5,publicationDate_with_year.length());
                            }else{
                                publicationDate = publicationDate_with_year;
                            }
                        }else{
                            publicationDate =publicationDate_with_time;
                        }

                        String webUrl = singleNews.optString("webUrl");
                        finalArraylist.add(new News(title, publicationDate, webUrl));
                    }}
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the news Json results", e);
            }
            return finalArraylist;
        }
    }

}
