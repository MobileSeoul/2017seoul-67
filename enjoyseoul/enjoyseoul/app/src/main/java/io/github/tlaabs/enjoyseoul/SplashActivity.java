package io.github.tlaabs.enjoyseoul;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    Context context;
    SQLiteDatabase db;

//    View
    ImageView logoImg;
    TextView titleText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;

        initView();
        initTask();
    }
    private void initView(){
        logoImg = (ImageView)findViewById(R.id.logo_img);
        titleText = (TextView)findViewById(R.id.title_txt);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        //Load logo img
        Glide
                .with(this)
                .load(R.drawable.logo_512)
                .into(logoImg);
        //Title font setting
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), getResources().getString(R.string.font_path));
        titleText.setTypeface(typeface);
    }

    private void initTask(){
        InitTaskStepOne taks = new InitTaskStepOne();
        taks.execute();
    }

    private class InitTaskStepOne extends AsyncTask<Integer,Integer,Integer>
    {
        @Override
        protected void onPreExecute() {
            db = openOrCreateDatabase(getString(R.string.database_name), MODE_PRIVATE, null);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + getString(R.string.table_events) + "("
                    + "CULTCODE INTEGER PRIMARY KEY, " //문화행사코드
                    + "SUBJCODE INTEGER, " //장르분류코드
                    + "CODENAME TEXT, " //장르명
                    + "TITLE TEXT, " //제목
                    + "STRT_DATE DATE, " //시작일자
                    + "END_DATE DATE, " //종료일자
                    + "TIME TEXT, " //시간
                    + "PLACE TEXT, " //장소
                    + "ORG_LINK TEXT, " //원문링크주소
                    + "MAIN_IMG TEXT, " //대표이미지
                    + "USE_TRGT TEXT, " //이용대상
                    + "USE_FEE TEXT, " //이용요금
                    + "INQUIRY TEXT, " //문의
                    + "ETC_DESC TEXT, " //기타내용
                    + "IS_FREE INTEGER, " //무료구분
                    + "GCODE TEXT);"); //자치구

            db.execSQL("DELETE FROM " + getString(R.string.table_events) + ";");


            //zzim
            db.execSQL("CREATE TABLE IF NOT EXISTS " + getString(R.string.table_favorite) + "("
            + "CULTCODE INTEGER PRIMARY KEY );");

            publishProgress(15);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            InitTaskStepTwo task = new InitTaskStepTwo();
            task.execute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
    private class InitTaskStepTwo extends AsyncTask<Integer,Integer,Integer>
    {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                URL url = new URL(getString(R.string.event_data_src));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);

                File cachePath = getCacheDir();
                File file = new File(cachePath,getString(R.string.event_data));
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = conn.getInputStream();


                //buffer
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while((bufferLength = inputStream.read(buffer)) > 0){
                    fileOutput.write(buffer,0,bufferLength);
                }
                fileOutput.close();

            }catch(MalformedURLException e){
                e.printStackTrace();
                return 0;
            }catch(IOException e){
                e.printStackTrace();
                return 0;
            }

            publishProgress(50);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
//            Log.i(TAG,"InitTaskStepTwo result : " + integer);
            if(integer == 1) {
                InitTaskStepThree task = new InitTaskStepThree();
                task.execute();
            }else{
                Toast.makeText(context,"인터넷 연결을 확인해주세요",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
    private class InitTaskStepThree extends AsyncTask<Integer,Integer,Integer>
    {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            ContentValues recordValues;
            try {
                //UTF-8
                File file = new File(getCacheDir(),getString(R.string.event_data));
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
                CSVReader reader = new CSVReader(isr);
                String[] nextLine = null;
                reader.readNext(); //skip first line
                db.beginTransaction();
                while((nextLine = reader.readNext()) != null){
                    try {
                        recordValues = new ContentValues();

                        recordValues.put("CULTCODE", Integer.parseInt(nextLine[0]));
                        recordValues.put("SUBJCODE", Integer.parseInt(nextLine[1]));
                        recordValues.put("CODENAME", nextLine[2]);
                        recordValues.put("TITLE", nextLine[3]);
                        recordValues.put("STRT_DATE", nextLine[4]);
                        recordValues.put("END_DATE", nextLine[5]);
                        recordValues.put("TIME", nextLine[6]);
                        recordValues.put("PLACE", nextLine[7]);
                        recordValues.put("ORG_LINK", nextLine[8]);
                        recordValues.put("MAIN_IMG", nextLine[9]);
                        recordValues.put("USE_TRGT", nextLine[11]);
                        recordValues.put("USE_FEE", nextLine[12]);
                        recordValues.put("INQUIRY", nextLine[14]);
                        recordValues.put("ETC_DESC", nextLine[16]);

                        //nullspace
                        if (!(nextLine[18].equals("")))
                            recordValues.put("IS_FREE", Integer.parseInt(nextLine[18]));
                        else
                            recordValues.put("IS_FREE", -1);
                        recordValues.put("GCODE", nextLine[19]);
                        db.insert(getString(R.string.table_events), null, recordValues);
                    }catch(Exception e){
//                        Log.i("pure",e.getMessage());
                    }
                }
                db.setTransactionSuccessful();

            }catch(Exception e){
//                e.printStackTrace();

            }finally {
                db.endTransaction();
                db.close();
            }
            publishProgress(100);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }

}
