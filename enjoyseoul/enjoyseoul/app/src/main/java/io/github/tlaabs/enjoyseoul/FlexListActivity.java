package io.github.tlaabs.enjoyseoul;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import io.github.tlaabs.enjoyseoul.util.EventItem;
import io.github.tlaabs.enjoyseoul.util.ListAdapter;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class FlexListActivity extends AppCompatActivity {
    private static final String TAG = "FlexListActivity";
    public static final int OPTION_FAVOR = 0;
    public static final int OPTION_NEW = 1;
    public static final int OPTION_HURRY = 2;
    public static final int OPTION_MONTH = 3;

    private int optionCode = -1;

    SQLiteDatabase db;
    HashMap<String, Integer> genreColorMap;
    ListAdapter mAdapter;
    RecyclerView mRecycleView;

    ImageView backBtn;
    TextView appTitleText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexlist);
        mRecycleView = (RecyclerView) findViewById(R.id.listView);
        appTitleText = (TextView) findViewById(R.id.app_title);
        backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        optionCode = getOptionCode();

        init();

    }

    private int getOptionCode() {
        int code;
        Intent i = getIntent();
        code = i.getIntExtra("option", -1);
        return code;
    }

    private void init() {
        //apptitle
        if (optionCode == OPTION_FAVOR) {
            appTitleText.setText("찜 목록");
        } else if (optionCode == OPTION_NEW) {
            appTitleText.setText("신규 행사");
        } else if (optionCode == OPTION_HURRY) {
            appTitleText.setText("마감 임박 행사");
        } else if (optionCode == OPTION_MONTH) {
            appTitleText.setText("이달의 행사");
        }

        InitGenreColorMap();
        mAdapter = new ListAdapter(this, genreColorMap);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.setAdapter(mAdapter);
        addData();
    }

    public void addData() {
        String sql = null;
        Cursor cursor = null;
        int count = 0;

        db = openOrCreateDatabase(getString(R.string.database_name), MODE_PRIVATE, null);
        if (optionCode == OPTION_FAVOR) {
            sql = "SELECT CULTCODE FROM " + getString(R.string.table_favorite);
            cursor = db.rawQuery(sql, null);
            count = cursor.getCount();
            ArrayList<String> cultcodeList = new ArrayList<String>();
            sql = "SELECT * FROM " + getString(R.string.table_events) + " WHERE ";
            if (cursor != null && count != 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    cultcodeList.add(cursor.getString(0));
                    sql += "cultcode=" + cursor.getString(0);
                    if (i < count - 1)
                        sql += " OR ";
                    cursor.moveToNext();
                }
            } else {
                return;
            }
        } else if (optionCode == OPTION_NEW) {
            sql = "select * from " + getString(R.string.table_events) +
                    " where date('now','-2 days') <= strt_date " +
                    " and strt_date <= date('now','localtime') order by strt_date desc";
        } else if (optionCode == OPTION_HURRY) {
            sql = "select * from " + getString(R.string.table_events) +
                    " where date('now','localtime') <= end_date " +
                    " and end_date <= date('now','+3 days') order by end_date asc";
        } else if (optionCode == OPTION_MONTH) {
            GregorianCalendar today = new GregorianCalendar();
            int year = today.get(today.YEAR);
            int m = today.get(today.MONTH) + 1;
            String month = null;
            if(m < 10) month = "0" + m;
            else month = "" + m;

            String format1 = "date(\'"+year+"-"+month+"-01\')";

            m += 1;
            if(m < 10) month = "0" + m;
            else month = "" + m;

            String format2 = "date(\'"+year+"-"+month+"-01\')";

            sql = "select * from " + getString(R.string.table_events) +
                    " where strt_date >= " + format1 +
                    " and end_date < " + format2 +
                    " order by CULTCODE asc;";
        }

        cursor = db.rawQuery(sql, null);
        count = cursor.getCount();

        if (cursor != null && count != 0) {
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


    private String cleanText(String org) {
        //&quot;,&#39;
        String result = org;
        result = result.replaceAll("&quot;", "\"");
        result = result.replaceAll("&#39;", "\'");
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(optionCode == OPTION_FAVOR)
            init();
    }

    private void InitGenreColorMap() {
        genreColorMap = new HashMap<String, Integer>();
        SQLiteDatabase db = openOrCreateDatabase(getString(R.string.database_name), MODE_PRIVATE, null);
        String sql = "SELECT SUBJCODE, CODENAME FROM EVENTS GROUP BY SUBJCODE;";
        Cursor cursor = db.rawQuery(sql, null);
        genreColorMap.put("전체", 0);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            for (int i = 1; i <= cursor.getCount(); i++) {
                genreColorMap.put(cursor.getString(1), i);

                cursor.moveToNext();
            }
        }
        db.close();
    }

}
