package io.github.tlaabs.enjoyseoul;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.tlaabs.enjoyseoul.util.EventItem;
import io.github.tlaabs.enjoyseoul.util.POSTParams;
import io.github.tlaabs.enjoyseoul.util.TopTenListAdapter;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class PopularActivity extends AppCompatActivity {

    SQLiteDatabase db;
    HashMap<String,Integer> genreColorMap;
    TopTenListAdapter mAdapter;
    RecyclerView mRecycleView;

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        mRecycleView = (RecyclerView)findViewById(R.id.listView);
        backBtn = (ImageView)findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();
    }

    private void init(){
        InitGenreColorMap();
        mAdapter = new TopTenListAdapter(this,genreColorMap);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.setAdapter(mAdapter);

        GetReviewCountTask task = new GetReviewCountTask();
        task.execute(0);
    }

    public void addData(){
        db = openOrCreateDatabase(getString(R.string.database_name),MODE_PRIVATE,null);
        String sql = "SELECT CULTCODE FROM " + getString(R.string.table_favorite);
        Cursor cursor = db.rawQuery(sql,null);
        int count = cursor.getCount();
        ArrayList<String> cultcodeList = new ArrayList<String>();
        sql = "SELECT * FROM " + getString(R.string.table_events) + " WHERE ";
        if(cursor != null && count != 0){
            cursor.moveToFirst();
            for(int i = 0 ; i < count; i++){
                cultcodeList.add(cursor.getString(0));
                sql += "cultcode=" + cursor.getString(0);
                if(i < count -1)
                    sql += " OR ";
                cursor.moveToNext();
            }
        }else{
            return;
        }

        cursor = db.rawQuery(sql,null);
        count = cursor.getCount();

        if(cursor != null && count != 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                EventItem item = new EventItem();
                item.setId(cursor.getString(0));
                String title = cleanText(cursor.getString(3));
                item.setTitle(title);

                item.setGenre(cursor.getString(2));
                item.setFee(cursor.getString(14));
                item.setStartDate(cursor.getString(4));
                item.setEndDate(cursor.getString(5));
                item.setRegion(cursor.getString(15));
                item.setImgSrc(cursor.getString(9));

                mAdapter.add(item);
                cursor.moveToNext();
            }
        }
        db.close();
        mAdapter.notifyDataSetChanged();

    }


    private String cleanText(String org){
        //&quot;,&#39;
        String result = org;
        result = result.replaceAll("&quot;","\"");
        result = result.replaceAll("&#39;","\'");
        return result;
    }

    private void InitGenreColorMap(){
        genreColorMap = new HashMap<String,Integer>();
        SQLiteDatabase db = openOrCreateDatabase(getString(R.string.database_name),MODE_PRIVATE,null);
        String sql = "SELECT SUBJCODE, CODENAME FROM EVENTS GROUP BY SUBJCODE;";
        Cursor cursor = db.rawQuery(sql,null);
        genreColorMap.put("전체", 0);
        if(cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            for (int i = 1; i <= cursor.getCount(); i++) {
                genreColorMap.put(cursor.getString(1), i);

                cursor.moveToNext();
            }
        }
        db.close();
    }

    private class GetReviewCountTask extends AsyncTask<Integer,Integer,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                URL url = new URL(getString(R.string.server));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setDefaultUseCaches(false);

                POSTParams postParams = new POSTParams();
                postParams.addParam("method", "review_count");
                postParams.addParam("subjcode", params[0]+"");

                String param = postParams.toString();

                OutputStream os = conn.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();

                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line);
                }
                data = buff.toString().trim();
                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null) {
                return;
            }

            SQLiteDatabase db = openOrCreateDatabase(getString(R.string.database_name),MODE_PRIVATE,null);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("reviews");
                for(int i = 0 ; i < jsonArray.length(); i++){
                    String sql = "SELECT * FROM " + getString(R.string.table_events) + " WHERE CULTCODE= ";
                    JSONObject jo = jsonArray.getJSONObject(i);
                    String code = jo.getString("CULTCODE");
                    sql += code;

                    //db

                    Cursor cursor = db.rawQuery(sql,null);
                    int count = cursor.getCount();
                    if(cursor != null && count != 0){
                        cursor.moveToFirst();
                        EventItem item = new EventItem();
                        item.setId(cursor.getString(0));
                        item.setTitle(cleanText(cursor.getString(3)));
                        item.setGenre(cursor.getString(2));
                        item.setFee(cursor.getString(14));
                        item.setStartDate(cursor.getString(4));
                        item.setEndDate(cursor.getString(5));
                        item.setRegion(cursor.getString(15));
                        item.setImgSrc(cursor.getString(9));
                        mAdapter.add(item);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }

            mAdapter.notifyDataSetChanged();
        }
    }
}
