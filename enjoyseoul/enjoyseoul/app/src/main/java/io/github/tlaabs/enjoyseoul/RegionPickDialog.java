package io.github.tlaabs.enjoyseoul;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.util.ArrayList;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class RegionPickDialog extends AppCompatActivity {

    private static final int ITEM_PER_ROW = 3;

//    view
    LinearLayout rowsItemBox;
    ToggleButton allSelectBtn;
    Button cancelBtn;
    Button submitBtn;


    int regionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_region_pick);
        initView();
        createListItems();

//        preSelect init
        restorePreSelect();
    }

    private void initView(){
        cancelBtn = (Button)findViewById(R.id.cancel_btn);
        submitBtn = (Button)findViewById(R.id.submit_btn);
        rowsItemBox = (LinearLayout)findViewById(R.id.rows_item_box);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list;
                list = getActiveItemList(regionCount);
                Intent intent = new Intent();
                intent.putExtra("region_list",list);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    private void createListItems(){
        SQLiteDatabase db = openOrCreateDatabase(getString(R.string.database_name), MODE_PRIVATE, null);
        String sql = "SELECT GCODE FROM EVENTS WHERE GCODE != \"\" GROUP BY GCODE;";
        Cursor cursor = db.rawQuery(sql,null);

        regionCount = cursor.getCount()+1; //including allSelect
        int rowboxCount = getRowBoxCount(regionCount);

        if(cursor != null && regionCount != 0){
            cursor.moveToFirst();
            //Create Interface
            for(int i = 0; i <= rowboxCount; i++){
                LinearLayout itemRow = createItemRow();
                rowsItemBox.addView(itemRow);
                for(int k = 1; k <= 3; k++){
                    final ToggleButton toggleBtn;
                    //out of range
                    if(i*ITEM_PER_ROW + k > regionCount){
                        toggleBtn = createEmptyItem();
                        itemRow.addView(toggleBtn);
                    }
                    //allSelectBtn
                    else if(i ==0 && k == 1){
                        allSelectBtn = createItem("전체");
                        allSelectBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToggleButton view = (ToggleButton)v;
                                if(!(view.isChecked())){
                                    setItemOn(view);
                                }
                                else{
                                    //다른 모든 버튼 끄기
                                    setAllItemOff(regionCount);
                                    setItemOn(view);
                                }
                            }
                        });
                        itemRow.addView(allSelectBtn);
                    }
                    else {
                        toggleBtn = createItem(cursor.getString(0)); //GCODE
                        toggleBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToggleButton view = (ToggleButton)v;
                                if(!(view.isChecked())){
                                    setItemOff(view);
                                }
                                else{
                                    setItemOn(view);
                                    setItemOff(allSelectBtn);
                                }
                            }
                        });
                        itemRow.addView(toggleBtn);
                        cursor.moveToNext();
                    }
                }
            }
        }
        else
        {
            //fail
        }
        db.close();
    }

    private void restorePreSelect(){
        Intent intent = getIntent();
        ArrayList<String> pre = (ArrayList<String>)intent.getSerializableExtra("preSelect");
        for(int i = 0 ; i < pre.size(); i++){
            for(int j = 0 ; j <= regionCount/ITEM_PER_ROW; j++){
                for(int k = 0; k < ITEM_PER_ROW; k++){
                    if(j*ITEM_PER_ROW + (k+1) > regionCount)break;
                    else
                    {
                        LinearLayout itemRow = (LinearLayout)rowsItemBox.getChildAt(j);
                        ToggleButton tb = (ToggleButton)itemRow.getChildAt(k);
                        if(tb.getText().toString().equals(pre.get(i))){
                            setItemOn(tb);
                        }
                    }
                }
            }
        }
    }

    private LinearLayout createItemRow(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(3 * dm.density);

        LinearLayout itemRow = new LinearLayout(this);

        LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lparam.setMargins(0,0,0,size);
        lparam.gravity = Gravity.CENTER;
        itemRow.setLayoutParams(lparam);
        itemRow.setOrientation(LinearLayout.HORIZONTAL);

        return itemRow;
    }

    private ToggleButton createItem(String title){

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(3 * dm.density);

        ToggleButton item = new ToggleButton(this);

        LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lparam.setMargins(0,0,size,0);
        lparam.weight = 1.0f;

        item.setLayoutParams(lparam);
        item.setTextColor(getResources().getColor(R.color.colorMainDark));
        item.setTextOn(title);
        item.setTextOff(title);
        item.setText(title);

        setItemOff(item);


        return item;
    }
    private ToggleButton createEmptyItem(){
        ToggleButton tmp = createItem("");
        tmp.setBackgroundDrawable(null);
        return tmp;
    }

    void setAllItemOff(int regionCount){
        for(int i = 0 ; i <= regionCount/ITEM_PER_ROW; i++){
            for(int k = 0; k < ITEM_PER_ROW; k++){
                if(i*ITEM_PER_ROW + (k+1) > regionCount)break;
                else
                {
                    LinearLayout itemRow = (LinearLayout)rowsItemBox.getChildAt(i);
                    ToggleButton tb = (ToggleButton)itemRow.getChildAt(k);
                    setItemOff(tb);
                }
            }
        }
    }

    private int getRowBoxCount(int regionCount)
    {
        int quot = regionCount / ITEM_PER_ROW; //몫
        if(ITEM_PER_ROW * quot != regionCount)
            quot++;
        return quot;
    }

    private void setItemOn(ToggleButton item)
    {
        item.setChecked(true);
        item.setBackgroundDrawable(getResources().getDrawable(R.drawable.pink_stroke_rectangle));
    }

    private void setItemOff(ToggleButton item)
    {
        item.setChecked(false);
        item.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_stroke_rectangle));
    }

    private ArrayList<String> getActiveItemList(int regionCount)
    {
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0 ; i <= regionCount/ITEM_PER_ROW; i++){
            for(int k = 0; k < ITEM_PER_ROW; k++){
                if(i*ITEM_PER_ROW + (k+1) > regionCount)break;
                else
                {
                    LinearLayout itemRow = (LinearLayout)rowsItemBox.getChildAt(i);
                    ToggleButton tb = (ToggleButton)itemRow.getChildAt(k);
                    if(tb.isChecked()){
                        list.add(new String(tb.getText().toString()));
                    }
                }
            }
        }
        return list;
    }
}
