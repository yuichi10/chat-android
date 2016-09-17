package dev.yuichi.com.chat.FirebaseModel;

/**
 * Created by yuichi on 9/17/16.
 */
public class Room {
    private Boolean group;
    private String name;
    public Room() {

    }
    public Room(Boolean group, String name) {
        this.group = group;
        this.name = name;
    }
    public Boolean getGroup() {
        return this.group;
    }
    public void setGroup(Boolean group) {
        this.group = group;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
