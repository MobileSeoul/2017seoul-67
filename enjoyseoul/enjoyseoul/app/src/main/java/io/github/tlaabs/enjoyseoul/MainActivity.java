package io.github.tlaabs.enjoyseoul;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //LinearLayout btn
    LinearLayout monthlyEventsBtn;
    LinearLayout searchEventBtn;
    LinearLayout zzimEventBtn;
    LinearLayout popularEventBtn;
    LinearLayout newEventBtn;
    LinearLayout hurryEventBtn;

//    View
    TextView appTitleText;
    TextView monthlyEventText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView(){
        //LinearLayout btn
        monthlyEventsBtn = (LinearLayout)findViewById(R.id.monthly_events_btn);
        searchEventBtn = (LinearLayout)findViewById(R.id.search_event_btn);
        zzimEventBtn = (LinearLayout)findViewById(R.id.zzim_event_btn);
        popularEventBtn = (LinearLayout)findViewById(R.id.popular_event_btn);
        newEventBtn = (LinearLayout)findViewById(R.id.new_event_btn);
        hurryEventBtn = (LinearLayout)findViewById(R.id.hurry_event_btn);

        monthlyEventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FlexListActivity.class);
                intent.putExtra("option",FlexListActivity.OPTION_MONTH);
                startActivity(intent);
            }
        });
        searchEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
            }
        });

        zzimEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FlexListActivity.class);
                intent.putExtra("option",FlexListActivity.OPTION_FAVOR);
                startActivity(intent);
            }
        });

        popularEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PopularActivity.class);
                startActivity(intent);
            }
        });

        newEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FlexListActivity.class);
                intent.putExtra("option",FlexListActivity.OPTION_NEW);
                startActivity(intent);
            }
        });

        hurryEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FlexListActivity.class);
                intent.putExtra("option",FlexListActivity.OPTION_HURRY);
                startActivity(intent);
            }
        });


        //end
        appTitleText = (TextView)findViewById(R.id.app_title_txt);
        monthlyEventText = (TextView)findViewById(R.id.monthly_events_txt);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/dancing_bold.ttf");
        appTitleText.setTypeface(typeface);

        monthlyEventText.setText("이달의 서울시\n문화 행사\n"+getMonth()+"월");
    }

    private String getMonth(){
        Calendar c = Calendar.getInstance();
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        return month;
    }


}
