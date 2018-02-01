package io.github.tlaabs.enjoyseoul;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import io.github.tlaabs.enjoyseoul.util.POSTParams;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class WriteReviewActivity extends AppCompatActivity {
    String id = "";
    String endDate = "";
    String username = "";
    String rating = "10";
    String content = "";
    //View

    EditText usernameView;
    RatingBar ratingBarView;
    TextView ratingTextView;
    EditText contentView;
    TextView contentLenView;

    Button cancelBtn;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        endDate = i.getStringExtra("end_date");
        setContentView(R.layout.activity_write_review);
        usernameView = (EditText)findViewById(R.id.username);
        ratingBarView = (RatingBar)findViewById(R.id.rating_bar);
        ratingTextView = (TextView)findViewById(R.id.rating_text);
        contentView = (EditText)findViewById(R.id.content);
        contentLenView = (TextView)findViewById(R.id.content_len);

        cancelBtn = (Button)findViewById(R.id.cancel_btn);
        submitBtn = (Button)findViewById(R.id.submit_btn);

        LayerDrawable stars = (LayerDrawable)ratingBarView.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorStar), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.colorDarkGray), PorterDuff.Mode.SRC_IN);

        ratingBarView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float _rating, boolean fromUser) {
                rating = (int)(_rating*2) + "";
                ratingTextView.setText(rating);
            }
        });

        contentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contentLenView.setText(s.length()+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        GradientDrawable gradient = (GradientDrawable)cancelBtn.getBackground();
        gradient.setColor(getResources().getColor(R.color.colorMainDark));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gradient = (GradientDrawable)submitBtn.getBackground();
        gradient.setColor(getResources().getColor(R.color.prettyPink));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFormValition()){
                    WriteReViewTask wr = new WriteReViewTask();
                    wr.execute();
                }
            }
        });

    }

    private boolean checkFormValition(){
        username = usernameView.getText().toString();
        if(username.equals("")){
            Toast.makeText(getApplicationContext(),"작성자명을 바르게 입력하세요",Toast.LENGTH_SHORT).show();
            return false;
        }
        content = contentView.getText().toString();
        if(content.length() < 5){
            Toast.makeText(getApplicationContext(),"내용을 최소 5자 이상 작성해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class WriteReViewTask extends AsyncTask<String,Integer,String>{

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
                postParams.addParam("method","write_review");
                postParams.addParam("cultcode",id);
                postParams.addParam("author",username);
                postParams.addParam("rating",rating);
                postParams.addParam("content",content);
                postParams.addParam("end_date",endDate);


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

                while((line = in.readLine()) != null){
                    buff.append(line);
                }
                data = buff.toString().trim();
                return data;

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
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
                JSONObject jsonObj = new JSONObject(s);
                int resultCode = jsonObj.getInt("success");
                if(resultCode == 1){
                    Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }
}
