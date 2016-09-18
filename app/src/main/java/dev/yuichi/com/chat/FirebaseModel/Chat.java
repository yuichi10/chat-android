package dev.yuichi.com.chat.FirebaseModel;

/**
 * Created by yuichi on 9/18/16.
 */
public class Chat {
    private String text;
    private String userid;
    private long timestamp;
    private String photoUrl;

    private Chat() {
    }

    public Chat(String text, String userid, String photoUrl) {
        this.text = text;
        this.userid = userid;
        this.timestamp = System.currentTimeMillis();
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }
    public void setText(String message){this.text = message;}

    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid){this.userid = userid;}

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {this.timestamp = timestamp;}

    public String getPhotoUrl(){return photoUrl;}
    public void setPhotoUrl(String photoUrl){this.photoUrl = photoUrl;}
}
