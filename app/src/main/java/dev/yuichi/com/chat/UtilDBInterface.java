package dev.yuichi.com.chat;

/**
 * Created by yuichi on 9/18/16.
 */
public interface UtilDBInterface {
    void afterGetUserIDFromEmail(String UserID, String caseVal);
    void afterDoesAlreadyHasFriend(Boolean doesFriend, String caseVal);
    void afterGetUserName(String userName, String caseVal);
}
