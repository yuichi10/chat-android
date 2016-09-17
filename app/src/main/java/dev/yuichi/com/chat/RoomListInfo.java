package dev.yuichi.com.chat;

/**
 * Created by yuichi on 9/17/16.
 */
public class RoomListInfo {
    String roomID;
    Boolean group;
    String name;
    //このidを何に使うかわ変わってない、、、
    long id;
    public RoomListInfo(String roomID, Boolean group, String name) {
        this.roomID = roomID;
        this.group = group;
        this.name = name;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getRoomID() {
        return roomID;
    }
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
    public Boolean getGroup() {
        return group;
    }
    public void setGroup(Boolean group){
        this.group = group;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
