package io.github.tlaabs.enjoyseoul.util;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class ReviewItem {
    String rating;
    String content;
    String author;
    String time;

    public ReviewItem(){}
    public ReviewItem(String _rating, String _content, String _author, String _time){
        rating = _rating;
        content = _content;
        author = _author;
        time = _time;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }
}
