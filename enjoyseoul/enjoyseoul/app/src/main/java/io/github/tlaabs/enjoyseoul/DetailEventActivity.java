package io.github.tlaabs.enjoyseoul;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.tlaabs.enjoyseoul.fragment.DetailEventFragment;
import io.github.tlaabs.enjoyseoul.fragment.EventReviewFragment;
import io.github.tlaabs.enjoyseoul.util.MapItemInfo;
import io.github.tlaabs.enjoyseoul.util.POSTParams;
import io.github.tlaabs.enjoyseoul.util.ReviewItem;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class DetailEventActivity extends AppCompatActivity {
    Context context;
    private static final int REQUEST_WRITE_REVIEW = 1;
    String id;

    public String imgSrc;
    public String title;
    public String genreAlert, feeAlert;
    public String endDate;
    public String period;
    public String time;
    public String region;
    public String place;
    public String target;
    public String detailFee;
    public String inquiry;
    public String orgLink;

    SQLiteDatabase db;

    HashMap<String, Integer> genreColorMap;

    ImageView backBtn;

    //    Layout
    ImageView mainImgView;
    TextView titleView;
    RatingBar ratingBarView;
    TextView ratingsTextView;
    TextView genreAlertView;
    TextView feeAlertView;

    TextView showEventInfoBtn;
    TextView showEventReviewBtn;
    LinearLayout eventInfoChoice;
    LinearLayout eventReviewChoice;

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    ImageView favorImg;

    //bottom menu
    LinearLayout linkBtn;
    LinearLayout mapBtn;
    LinearLayout writeReViewBtn;
    LinearLayout favorAddBtn;
    LinearLayout shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        context = this;
        backBtn = (ImageView) findViewById(R.id.back_btn);
        mainImgView = (ImageView) findViewById(R.id.main_img);
        titleView = (TextView) findViewById(R.id.title);
        ratingBarView = (RatingBar) findViewById(R.id.rating_bar);
        ratingsTextView = (TextView) findViewById(R.id.rating_people);
        genreAlertView = (TextView) findViewById(R.id.genre_alert);
        feeAlertView = (TextView) findViewById(R.id.fee_alert);

        favorImg = (ImageView) findViewById(R.id.favor_img);

        fragmentManager = getSupportFragmentManager();

        LayerDrawable stars = (LayerDrawable) ratingBarView.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorStar), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);

        showEventInfoBtn = (TextView) findViewById(R.id.show_event_info);
        showEventInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventInfo();
            }
        });
        showEventReviewBtn = (TextView) findViewById(R.id.show_event_review);
        showEventReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventReview();
            }
        });

        //chocie divider
        eventInfoChoice = (LinearLayout) findViewById(R.id.event_info_choice);
        eventReviewChoice = (LinearLayout) findViewById(R.id.event_review_choice);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mapBtn = (LinearLayout) findViewById(R.id.map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });

        shareBtn = (LinearLayout) findViewById(R.id.share_event_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoShare();
            }
        });

        linkBtn = (LinearLayout) findViewById(R.id.link_btn);
        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orgLink != null){
                    if(orgLink.lastIndexOf("http://") == -1){
                        orgLink = "http://"+ orgLink;
                    }
                }
                Uri uri = Uri.parse(orgLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //bottom menu
        writeReViewBtn = (LinearLayout) findViewById(R.id.write_review_btn);
        writeReViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteReviewActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("end_date", endDate);
                startActivityForResult(intent, REQUEST_WRITE_REVIEW);
            }
        });

        favorAddBtn = (LinearLayout) findViewById(R.id.favor_add_btn);
        favorAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addfavor();
            }
        });

        initGenreColorMap();

        id = getEventID();
        initDeatilData();
        paint();


    }

    private void showMap() {
        ShowMapTask task = new ShowMapTask();
        task.execute(place);
    }

    private void kakaoShare() {
        String templateId = getResources().getString(R.string.kakao_template);

        Map<String, String> templateArgs = new HashMap<String, String>();
        templateArgs.put("${event_img}", urlPass(imgSrc));
        templateArgs.put("${title}", cleanText(title));
        templateArgs.put("${link}", "링크 : " + cleanText(orgLink));

        KakaoLinkService.getInstance().sendScrap(this, "https://developers.kakao.com", templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }

    private void addfavor() {
        SQLiteDatabase db = openOrCreateDatabase(getString(R.string.database_name), MODE_PRIVATE, null);
        String sql;
        if (!isfavorExist()) {
            sql = "INSERT INTO " + getString(R.string.table_favorite) + " values(" + id + ")";
            Toast.makeText(getApplicationContext(),"찜!", Toast.LENGTH_SHORT).show();

        } else {
            sql = "DELETE FROM " + getString(R.string.table_favorite) + " WHERE CULTCODE = " + id;
            Toast.makeText(getApplicationContext(),"찜 해제!", Toast.LENGTH_SHORT).show();
        }
        db.execSQL(sql);
        db.close();
        paint();
    }

    private boolean isfavorExist() {
        SQLiteDatabase db = openOrCreateDatabase(getString(R.string.database_name), MODE_PRIVATE, null);
        String sql = "SELECT * FROM " + getString(R.string.table_favorite) + " WHERE CULTCODE = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            db.close();
            return false;
        }
        db.close();
        return true;

    }

    private String getEventID() {
        Intent i = getIntent();
        return i.getStringExtra("eventID");
    }

    private void initDeatilData() {
        db = openOrCreateDatabase(getString(R.string.database_name), MODE_PRIVATE, null);
        String sql = "SELECT * FROM EVENTS WHERE CULTCODE = " + id + ";";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        imgSrc = cursor.getString(9);
        title = cursor.getString(3);
        genreAlert = cursor.getString(2);
        feeAlert = cursor.getString(14);
        endDate = cursor.getString(5);
        period = cursor.getString(4) + " ~ " + cursor.getString(5);
        time = cursor.getString(6);
        region = cursor.getString(15);
        place = cursor.getString(7);
        target = cursor.getString(10);
        detailFee = cursor.getString(11);
        inquiry = cursor.getString(12);
        orgLink = cursor.getString(8);
        db.close();
    }

    private String cleanText(String org) {
        //&quot;,&#39;
        String result = org;
        result = result.replaceAll("&quot;", "\"");
        result = result.replaceAll("&#39;", "\'");
        return result;
    }

    private void paint() {
        reviewRatingRenew();
        //mainImg
        Glide
                .with(this)
                .asBitmap()
                .load(urlPass(imgSrc))
                .into(mainImgView);
        titleView.setText(cleanText(title));

        String[] colors = getResources().getStringArray(R.array.genre_colors);

        //genre
        int gen = genreColorMap.get(genreAlert);

        GradientDrawable gradient = (GradientDrawable) genreAlertView.getBackground();
        gradient.setColor(Color.parseColor(colors[gen]));
        genreAlertView.setText(genreAlert);

        //fee
        if (feeAlert.equals("0")) {
            feeAlertView.setText("유료");
            int color = ContextCompat.getColor(this, R.color.feeNFree);
            gradient = (GradientDrawable) feeAlertView.getBackground();
            gradient.setColor(color);

        } else if (feeAlert.equals("1")) {
            feeAlertView.setText("무료");
            int color = ContextCompat.getColor(this, R.color.feeFree);
            gradient = (GradientDrawable) feeAlertView.getBackground();
            gradient.setColor(color);

        }

        if (isfavorExist()) {
            int color = ContextCompat.getColor(this, R.color.isfavor);
            favorImg.setColorFilter(color);

            Log.i("mmsgg", "sexi");
        } else {
            favorImg.setColorFilter(Color.parseColor("#ffffff"));
        }

        showEventInfo();
    }

    private void initGenreColorMap() {
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

    private String urlPass(String src) {
        String url = "";
        String[] mainImg = src.split("\\/");
        for (int i = 0; i < mainImg.length; i++) {
            if ((i == 0) || (i == 1) || (i == 2)) {
                url = url + mainImg[i].toLowerCase() + "/";
            } else if (i == mainImg.length - 1) {
                url = url + mainImg[i];
            } else {
                url = url + mainImg[i] + "/";
            }
        }
        return url;
    }

    private void showEventInfo() {
        eventInfoChoice.setVisibility(View.VISIBLE);
        eventReviewChoice.setVisibility(View.INVISIBLE);

        fragmentTransaction = fragmentManager.beginTransaction();
        DetailEventFragment fragment = new DetailEventFragment();
        Bundle bundle = new Bundle();
        bundle.putString("period", period);
        bundle.putString("time", time);
        bundle.putString("region", region);
        bundle.putString("place", place);
        bundle.putString("target", target);
        bundle.putString("detailFee", detailFee);
        bundle.putString("inquiry", inquiry);

        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    private void showEventReview() {
        eventInfoChoice.setVisibility(View.INVISIBLE);
        eventReviewChoice.setVisibility(View.VISIBLE);
        fragmentTransaction = fragmentManager.beginTransaction();
        EventReviewFragment fragment = new EventReviewFragment();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
        reviewDataRenew(fragment);


    }

    private void reviewDataRenew(EventReviewFragment fragment) {

        GetReviewsTask grs = new GetReviewsTask(fragment);
        grs.execute();

    }

    private void reviewRatingRenew() {
        GetRatingTask task = new GetRatingTask();
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_WRITE_REVIEW:
                    //화면 갱신
                    paint();
                    break;
            }
        }
    }

    private class GetRatingTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(getString(R.string.server));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setDefaultUseCaches(false);

                POSTParams postParams = new POSTParams();
                postParams.addParam("method", "get_rating");
                postParams.addParam("cultcode", id);

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
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("rating");
                JSONObject jo = jsonArray.getJSONObject(0);
                double ratingSum_org = jo.getDouble("sum");
                int n = jo.getInt("count");
                double ratingSum = ratingSum_org / n;
                ratingBarView.setRating((float) ratingSum / 2);
                ratingSum = Double.parseDouble(String.format("%.1f", ratingSum));


                ratingsTextView.setText(ratingSum + " (참여 " + n + ")");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetReviewsTask extends AsyncTask<String, Integer, String> {
        EventReviewFragment fragment;

        public GetReviewsTask(EventReviewFragment _frag) {
            fragment = _frag;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(getString(R.string.server));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setDefaultUseCaches(false);

                POSTParams postParams = new POSTParams();
                postParams.addParam("method", "read_review");
                postParams.addParam("cultcode", id);

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
                Toast.makeText(getApplicationContext(), "서버와 연결할 수 없어요 ㅠㅠ", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    ReviewItem item = new ReviewItem();
                    item.setAuthor(jo.getString("author"));
                    item.setRating(jo.getString("rating"));
                    item.setContent(jo.getString("content"));
                    item.setTime(jo.getString("reg_date"));
                    fragment.addData(item);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class ShowMapTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String appkey = "KakaoAK " + getString(R.string.kakao_rest_api_key);
                String addr = URLEncoder.encode(strings[0], "UTF-8");
                String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + addr
                        + "&x=126.994277&y=37.558877&radius=20000"; //json
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Authorization", appkey);
                int responseCode = con.getResponseCode();
                BufferedReader br;

                if (responseCode == 200) { // 정상 호출

                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));

                }

                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                return response.toString();

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            ArrayList<MapItemInfo> list = new ArrayList<MapItemInfo>();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray arr = jsonObject.getJSONArray("documents");
                int count = arr.length();
                if (count > 5 || count == 0) {
                    Toast.makeText(context, "위치 정보가 부정확합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < count; i++) {
                    JSONObject order = arr.getJSONObject(i);

                    String placeName = order.getString("place_name");
                    double x = Double.parseDouble(order.getString("x"));
                    double y = Double.parseDouble(order.getString("y"));
                    MapItemInfo item = new MapItemInfo();
                    item.setPlaceName(placeName);
                    item.setX(x);
                    item.setY(y);
                    list.add(item);
                }

                //37.427795, 126.759179 왼
                //37.698649, 127.185332 오른
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "위치를 표시할 수 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            //success
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            intent.putExtra("LatLngs", list);
            intent.putExtra("title", place);
            startActivity(intent);


        }
    }
}
