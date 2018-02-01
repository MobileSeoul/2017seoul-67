package io.github.tlaabs.enjoyseoul;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import io.github.tlaabs.enjoyseoul.util.SearchConditionPackage;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class SearchActivity extends AppCompatActivity {

    static final int PICK_GENRE_REQUEST = 1;
    static final int PICK_REGION_REQUEST = 2;

    //    title condition
    ImageView backBtn;
    EditText titleET;
    //    genre condition
    Button genrePickBtn;
    FlexboxLayout genreListBox;
    HashMap<String, Integer> genreColorMap;

    //    period condition
    TextView startDateBtn;
    TextView endDateBtn;

    //    region condition
    Button regionPickBtn;
    FlexboxLayout regionListBox;

    //    fee condition
    Button feeAllBtn;
    Button feeNFreeBtn;
    Button feeFreeBtn;

    //    all condition data
    String title = null;
    ArrayList<String> genreList;//무용,국악,영화
    String startDate = null;//2017-7-30
    String endDate = null; //2017-7-30
    ArrayList<String> regionList; //마포구, 서대문구,...
    String fee = null; //요금무관,유료,무료

    //reset, submit
    Button resetBtn;
    Button submitBtn;

    boolean isExistData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();

        initDefaultSelect();
        repaint();

        existPreDataCheck(getIntent());
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.back_btn);
        titleET = (EditText) findViewById(R.id.search_input);
        genreListBox = (FlexboxLayout) findViewById(R.id.genre_list_box);
        genrePickBtn = (Button) findViewById(R.id.genre_pick_btn);
        startDateBtn = (TextView) findViewById(R.id.start_date);
        endDateBtn = (TextView) findViewById(R.id.end_date);
        regionListBox = (FlexboxLayout) findViewById(R.id.region_list_box);
        regionPickBtn = (Button) findViewById(R.id.region_pick_btn);
        feeAllBtn = (Button) findViewById(R.id.fee_all_btn);
        feeNFreeBtn = (Button) findViewById(R.id.fee_nfree_btn);
        feeFreeBtn = (Button) findViewById(R.id.fee_free_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);
        submitBtn = (Button) findViewById(R.id.submit_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        genrePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleET.getText().toString();
                Intent intent = new Intent(getApplicationContext(), GenrePickDialog.class);
                intent.putExtra("preSelect", genreList);
                startActivityForResult(intent, PICK_GENRE_REQUEST);
            }
        });
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year, month, yoil;
                if (startDate == null) {
                    GregorianCalendar today = new GregorianCalendar();

                    year = today.get(today.YEAR);

                    month = today.get(today.MONTH);

                    yoil = today.get(today.DAY_OF_MONTH);
                } else {

                    String[] date = startDate.split("-");
                    year = Integer.parseInt(date[0]);
                    month = Integer.parseInt(date[1]) - 1;
                    yoil = Integer.parseInt(date[2]);

                }

                DatePickerDialog dialog = new DatePickerDialog(SearchActivity.this, listener, year, month, yoil);
                dialog.show();
            }

            private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String fixMonth, fixDayOfMonth;
                    if (month < 9) fixMonth = "0" + (month + 1);
                    else fixMonth = "" + (month + 1);
                    if (dayOfMonth < 10) fixDayOfMonth = "0" + dayOfMonth;
                    else fixDayOfMonth = "" + dayOfMonth;
                    startDate = year + "-" + fixMonth + "-" + fixDayOfMonth;
                    repaint();
                }
            };
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year, month, yoil;
                if (endDate == null) {
                    GregorianCalendar today = new GregorianCalendar();

                    year = today.get(today.YEAR);
                    month = today.get(today.MONTH);
                    yoil = today.get(today.DAY_OF_MONTH);
                } else {

                    String[] date = endDate.split("-");
                    year = Integer.parseInt(date[0]);
                    month = Integer.parseInt(date[1]) - 1;
                    yoil = Integer.parseInt(date[2]);

                }

                DatePickerDialog dialog = new DatePickerDialog(SearchActivity.this, listener, year, month, yoil);
                dialog.show();
            }

            private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String fixMonth, fixDayOfMonth;
                    if (month < 9) fixMonth = "0" + (month + 1);
                    else fixMonth = "" + (month + 1);
                    if (dayOfMonth < 10) fixDayOfMonth = "0" + dayOfMonth;
                    else fixDayOfMonth = "" + dayOfMonth;
                    endDate = year + "-" + fixMonth + "-" + fixDayOfMonth;
                    repaint();
                }
            };
        });

        regionPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleET.getText().toString();
                Intent intent = new Intent(getApplicationContext(), RegionPickDialog.class);
                intent.putExtra("preSelect", regionList);
                startActivityForResult(intent, PICK_REGION_REQUEST);
            }
        });

        feeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button bt = (Button) v;
                fee = new String("요금무관");
                feeItemOffWithout(bt);
            }

        });

        feeNFreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button bt = (Button) v;
                fee = new String("유료");
                feeItemOffWithout(bt);
            }
        });
        feeFreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button bt = (Button) v;
                fee = new String("무료");
                feeItemOffWithout(bt);
            }
        });

        GradientDrawable gradient = (GradientDrawable) resetBtn.getBackground();
        gradient.setColor(getResources().getColor(R.color.colorMainDark));
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAllCondition();
            }
        });

        gradient = (GradientDrawable) submitBtn.getBackground();
        gradient.setColor(getResources().getColor(R.color.prettyPink));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleET.getText().toString();
                //기간 타당성 검사
                if (!isPeriodValidation()) {
                    Toast.makeText(getApplicationContext(), "기간을 바르게 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //검색결과하면
                //인텐트 정보 전달
                SearchConditionPackage pack =
                        new SearchConditionPackage(title, genreList, startDate, endDate, regionList, fee);
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("preCondition", pack);
                intent.putExtras(bundle);
                if (isExistData == false)
                    startActivity(intent);
                else
                    setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    void existPreDataCheck(Intent i) {
        SearchConditionPackage pack = (SearchConditionPackage) i.getSerializableExtra("reSearch");
        if (pack != null) {
            title = pack.getTitle();
            genreList = pack.getGenreList();
            startDate = pack.getStartDate();
            endDate = pack.getEndDate();
            regionList = pack.getRegionList();
            fee = pack.getFee();
            isExistData = true;
            repaint();
        }
    }

    boolean isPeriodValidation() {
        if (startDate == null && endDate == null) {
            return true;
        }
        //한가지만 선택되어있을 때
        else if (startDate == null)
            return false;
        else if (endDate == null)
            return false;
        //둘다 설정되어있지만
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date before = dateFormat.parse(startDate);
            Date after = dateFormat.parse(endDate);
            if (before.after(after)) return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void resetAllCondition() {
        initDefaultSelect();
        repaint();
    }

    private void feeItemOffWithout(Button item) {

        feeAllBtn.setBackground(getResources().getDrawable(R.drawable.gray_rectangle));
        feeNFreeBtn.setBackground(getResources().getDrawable(R.drawable.gray_rectangle));
        feeFreeBtn.setBackground(getResources().getDrawable(R.drawable.gray_rectangle));

        item.setBackground(getResources().getDrawable(R.drawable.pink_stroke_rectangle));

    }

    //최초 기본 설정
    private void initDefaultSelect() {
        InitGenreColorMap();
        title = null;
        genreList = new ArrayList<String>();
        genreList.add("전체");
        startDate = null;
        endDate = null;
        regionList = new ArrayList<String>();
        regionList.add("전체");
        fee = "요금무관";
        feeItemOffWithout(feeAllBtn);
    }

    //화면 다시 그리기
    private void repaint() {
        repaintTitle();
        repaintGenreList();
        repaintPeriod();
        repaintRegionList();
    }

    private void repaintTitle() {
        titleET.setText(title);
    }

    private void repaintGenreList() {
        clearGenreListBox();
        int len = genreList.size();
        for (int i = 0; i < len; i++) {
            String genre = genreList.get(i);
            genreListBox.addView(createGenreListItem(genre));
        }
    }

    private void repaintPeriod() {
        startDateBtn.setText(startDate);
        endDateBtn.setText(endDate);
    }

    private void repaintRegionList() {
        clearRegionListBox();
        int len = regionList.size();

        for (int i = 0; i < len; i++) {
            String region = regionList.get(i);
            regionListBox.addView(createRegionListItem(region));
        }
    }

    private void clearGenreListBox() {
        genreListBox.removeAllViews();
    }

    private void clearRegionListBox() {
        regionListBox.removeAllViews();
    }

    private TextView createGenreListItem(String genre) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(4 * dm.density);

        TextView item = new TextView(this);

        FlexboxLayout.LayoutParams lparam = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lparam.setMargins(0, 0, size, size);

        item.setLayoutParams(lparam);


        item.setBackground(getResources().getDrawable(R.drawable.no_stroke_rectangle));

        item.setPadding(size, size, size, size);
        item.setTextColor(Color.parseColor("#ffffff"));
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(genre);

        String[] colors = getResources().getStringArray(R.array.genre_colors);

        int gen = genreColorMap.get(genre);

        GradientDrawable gradient = (GradientDrawable) item.getBackground();
        gradient.setColor(Color.parseColor(colors[gen]));

        return item;
    }

    private TextView createRegionListItem(String genre) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(4 * dm.density);

        TextView item = new TextView(this);

        FlexboxLayout.LayoutParams lparam = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lparam.setMargins(0, 0, size, size);

        item.setLayoutParams(lparam);

        item.setBackground(getResources().getDrawable(R.drawable.no_stroke_rectangle));

        item.setPadding(size, size, size, size);
        item.setTextColor(Color.parseColor("#ffffff"));
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(genre);

        GradientDrawable gradient = (GradientDrawable) item.getBackground();
        gradient.setColor(getResources().getColor(R.color.colorMainDark));

        return item;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_GENRE_REQUEST:

                    genreList = (ArrayList<String>) data.getSerializableExtra("genre_list");

                    repaint();
                    break;
                case PICK_REGION_REQUEST:
                    regionList = (ArrayList<String>) data.getSerializableExtra("region_list");

                    repaint();
                    break;

            }
        }
    }
}
