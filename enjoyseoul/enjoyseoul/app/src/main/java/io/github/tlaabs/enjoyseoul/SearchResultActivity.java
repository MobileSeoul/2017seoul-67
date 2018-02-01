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
import java.util.HashMap;

import io.github.tlaabs.enjoyseoul.util.EventItem;
import io.github.tlaabs.enjoyseoul.util.ListAdapter;
import io.github.tlaabs.enjoyseoul.util.PackageToSQL;
import io.github.tlaabs.enjoyseoul.util.SearchConditionPackage;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class SearchResultActivity extends AppCompatActivity {

    static final int RE_SEARCH_REQUEST = 2;

    SQLiteDatabase db;
    HashMap<String,Integer> genreColorMap;
    ListAdapter mAdapter;
    RecyclerView mRecycleView;
    ImageView backBtn;
    TextView conditionAlert;
    TextView reSearchBtn;
    SearchConditionPackage pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        conditionAlert = (TextView)findViewById(R.id.condition_alert);
        mRecycleView = (RecyclerView)findViewById(R.id.listView);
        reSearchBtn = (TextView)findViewById(R.id.re_search_btn);
        backBtn = (ImageView)findViewById(R.id.back_btn);

        reSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reSearch",pack);
                intent.putExtras(bundle);
                startActivityForResult(intent,RE_SEARCH_REQUEST);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init(getIntent());


    }
    private void init(Intent i){
        Intent intent = i;
        pack = (SearchConditionPackage)intent.getSerializableExtra("preCondition");
        PackageToSQL pts = new PackageToSQL(pack);
        String sql = pts.getSQL();
        setConditionAlert(pack);

        InitGenreColorMap();

        mAdapter = new ListAdapter(this,genreColorMap);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLayoutManager);

        mRecycleView.setAdapter(mAdapter);
        addData(sql);
    }
    private void setConditionAlert(SearchConditionPackage pack){
        int size;

        StringBuilder sb = new StringBuilder();
        sb.append("");

        //genre
        ArrayList<String> genreList = pack.getGenreList();
        size = genreList.size();
        String item;
        if(size == 1){
            item = genreList.get(0);
            if(item.equals("전체"))
                sb.append("모든 장르");
            else
                sb.append(genreList.get(0));
        }else{
            sb.append(genreList.get(0) + "외 " + genreList.size());
        }

        //title
        String title = pack.getTitle();
        if(title == null || title.equals("")){
            //pass
        }else{
            sb.append(", ");
            sb.append(title);
        }
        //region
        ArrayList<String> regionList = pack.getRegionList();
        size = regionList.size();
        if(size == 1){
            item = genreList.get(0);
            sb.append(", ");
            if(item.equals("전체"))
                sb.append("모든 지역");
            else
                sb.append(regionList.get(0));
        }else{
            sb.append(", ");
            sb.append(regionList.get(0) + "외 " + regionList.size());
        }

        //fee
        String fee = pack.getFee();
        if(fee.equals("요금무관")){
            sb.append(", ");
            sb.append("요금무관");
        }else if(fee.equals("유료")){
            sb.append(", ");
            sb.append("유료");
        }else{
            sb.append(", ");
            sb.append("무료");
        }

        //period
        String startDate = pack.getStartDate();
        String endDate = pack.getEndDate();

        if(startDate != null && endDate != null){
            sb.append(", ");
            sb.append(startDate + "~" + endDate);
        }

        conditionAlert.setText(sb.toString());

    }
    public void addData(String sql){
        db = openOrCreateDatabase(getString(R.string.database_name),MODE_PRIVATE,null);
        Cursor cursor = db.rawQuery(sql,null);
        int count = cursor.getCount();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case RE_SEARCH_REQUEST:
                    init(data);
                    break;
            }
        }
    }
}

