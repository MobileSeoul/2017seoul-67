package io.github.tlaabs.enjoyseoul.util;

import java.io.Serializable;
import java.util.ArrayList;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/
@SuppressWarnings("serial")
public class SearchConditionPackage implements Serializable{
    private String title = null;
    private ArrayList<String> genreList;//무용,국악,영화
    private String startDate = null;//2017-7-30
    private String endDate = null; //2017-7-30
    private ArrayList<String> regionList; //마포구, 서대문구,...
    private String fee = null; //요금무관,유료,무료

    public SearchConditionPackage(String _title, ArrayList<String> _genreList, String _startDate,
                           String _endDate, ArrayList<String> _regionList, String _fee){
        title = _title;
        genreList = _genreList;
        startDate = _startDate;
        endDate = _endDate;
        regionList =  _regionList;
        fee = _fee;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getGenreList() {
        return genreList;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public ArrayList<String> getRegionList() {
        return regionList;
    }

    public String getFee() {
        return fee;
    }
}
