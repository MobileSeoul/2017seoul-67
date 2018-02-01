package io.github.tlaabs.enjoyseoul.util;

import java.util.ArrayList;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class PackageToSQL {
    String title = null;
    ArrayList<String> genreList;//무용,국악,영화
    String startDate = null;//2017-7-30
    String endDate = null; //2017-7-30
    ArrayList<String> regionList; //마포구, 서대문구,...
    String fee = null; //요금무관,유료,무료

    public PackageToSQL(SearchConditionPackage pack){
        title = pack.getTitle();
        genreList = pack.getGenreList();
        startDate = pack.getStartDate();
        endDate = pack.getEndDate();
        regionList = pack.getRegionList();
        fee = pack.getFee();
    }

    private String getTitleSubSQL(String title){
        String subQuery = "";
        if(title == null || title.equals("")){//전체
            subQuery = new String("(TITLE LIKE '%%')");
        }
        else{
            subQuery = new String("(TITLE LIKE '%" + title +  "%')");
        }

        return subQuery;
    }

    private String getGenreSubSQL(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if(list.size() == 1 && (list.get(0)).equals("전체"))
            return "";
        for(int i = 0 ; i < list.size(); i++) {
            sb.append("CODENAME = \"" + list.get(i)+ "\"");
            if(i < list.size()-1)
                sb.append(" OR ");
        }
        sb.append(")");
        return sb.toString();
    }

    private String getPeriodSubSQL(String start, String end) {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        if(start == null && end == null)
            return sb.toString();
        sb.append("(END_DATE >= date(\"" + start + "\") and date(\"" + end + "\") >= STRT_DATE)");
        return sb.toString();
    }

    private String getRegionSubSQL(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if(list.size() == 1 && (list.get(0)).equals("전체"))
            return "";
        for(int i = 0 ; i < list.size(); i++) {
            sb.append("GCODE = \"" + list.get(i)+ "\"");
            if(i < list.size()-1)
                sb.append(" OR ");
        }
        sb.append(")");
        return sb.toString();
    }

    private String getFeeSubSQL(String fee) {
        String subQuery = "";
        if(fee.equals("유료"))
            subQuery = "(IS_FREE = 0)";
        else if(fee.equals("무료"))
            subQuery = "(IS_FREE = 1)";
        return subQuery;

    }

    public String getSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM EVENTS WHERE ");
        String titleSubSQL = getTitleSubSQL(title);

        if(!titleSubSQL.equals("")){
            sb.append(titleSubSQL);
        }
        String genreSubSQL = getGenreSubSQL(genreList);
        if(!genreSubSQL.equals("")){
            sb.append(" and ");
            sb.append(genreSubSQL);
        }
        String periodSubSQL = getPeriodSubSQL(startDate,endDate);
        if(!periodSubSQL.equals("")){
            sb.append(" and ");
            sb.append(periodSubSQL);
        }
        String regionSubSQL = getRegionSubSQL(regionList);
        if(!regionSubSQL.equals("")){
            sb.append(" and ");
            sb.append(regionSubSQL);
        }
        String feeSubSQL = getFeeSubSQL(fee);
        if(!feeSubSQL.equals("")){
            sb.append(" and ");
            sb.append(feeSubSQL);
        }
        sb.append(" ORDER BY CULTCODE DESC;");
        return sb.toString();
    }
}
