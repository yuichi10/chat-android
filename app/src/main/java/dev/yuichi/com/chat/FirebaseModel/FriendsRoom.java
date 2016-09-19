package dev.yuichi.com.chat.FirebaseModel;

/**
 * Created by yuichi on 9/19/16.
 */
public class FriendsRoom {
    String friend_id;
    private FriendsRoom() {}
    public FriendsRoom(String friend_id) {
        this.friend_id = friend_id;
    }
    public String getFriend_id() {
        return friend_id;
    }
    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }
}
