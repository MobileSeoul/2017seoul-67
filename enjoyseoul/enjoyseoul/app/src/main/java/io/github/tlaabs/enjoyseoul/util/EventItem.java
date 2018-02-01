package io.github.tlaabs.enjoyseoul.util;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class EventItem {
    String id;
    String title;
    String rating;
    int rating_n;
    String genre;
    String fee;
    String startDate;
    String endDate;
    String region;
    String imgSrc;
    boolean isLoadRating = false;

    public EventItem(){

    }
    public EventItem(String id, String title,String rating, String genre, String fee,
                     String startDate, String endDate, String region, String imgSrc)
    {
        this.id = id;
        this.title = title;
        this.rating = rating; //null
        this.genre = genre;
        this.fee = fee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.region = region;
        this.imgSrc = imgSrc;
    }

    public int getRating_n() {
        return rating_n;
    }

    public void setRating_n(int rating_n) {
        this.rating_n = rating_n;
    }


    public void setLoadRating(boolean loadRating) {
        isLoadRating = loadRating;
    }

    public boolean isLoadRating() {
        return isLoadRating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return this.title;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getFee() {
        return this.fee;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public String getRegion() {
        return this.region;
    }

    public String getImgSrc() {
        String url = "";
        String[] mainImg = imgSrc.split("\\/");
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}
