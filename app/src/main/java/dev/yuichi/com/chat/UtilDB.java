package dev.yuichi.com.chat;


import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dev.yuichi.com.chat.FirebaseModel.Room;

/**
 * Created by yuichi on 9/18/16.
 */
public class UtilDB {
    DatabaseReference mDatabase;
    private static UtilDB Instance = new UtilDB();
    private String mUserID;
    private Boolean mIsUser;
    private String mUserName;
    private Boolean mIsFinish;

    private UtilDB(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        System.out.println("インスタンスを作成しました。");
    }
    public static UtilDB getInstance(){
        return Instance;
    }
    public String getOwnUserID(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

    //database から値を参照
    //interfaceの変数を呼ぶようにする
    public synchronized void getUserIDFromEmail(final String email, final String caseVal, final UtilDBInterface utilDBInterface) {
        mUserID = "";
        mIsFinish = false;
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userEmail = (String)dataSnapshot.child("email").getValue();
                    System.out.println("UtilDB: userEmail: " + userEmail);
                    if (email != null && userEmail != null) {
                        if (userEmail.equals(email)) {
                            mUserID = dataSnapshot.getKey();
                        }
                    }
                }
                mIsFinish = true;
                utilDBInterface.afterGetUserIDFromEmail(mUserID, caseVal);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("キャンセル");
                mIsFinish = true;
                utilDBInterface.afterGetUserIDFromEmail("", caseVal);
            }
        });
    }
    public synchronized void doesAlreadyHasFriend(final String friendID, final String caseVal, final UtilDBInterface utilDBInterface){
        mIsUser = false;
        mDatabase.child(D.Users).child(getOwnUserID()).child(D.Friends).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> roomFriend = (HashMap)dataSnapshot.getValue();
                if (roomFriend != null) {
                    for (Map.Entry<String, String> entry : roomFriend.entrySet()) {
                        if (entry.getValue().equals(friendID)) {
                            mIsUser = true;
                        }
                    }
                }
                if (!mIsUser) {
                    //まだ友達じゃなかったとき added friendの方も探す
                    mDatabase.child(D.Users).child(getOwnUserID()).child(D.AddedFriends).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, String> roomFriend = (HashMap)dataSnapshot.getValue();
                            if (roomFriend != null) {
                                for (Map.Entry<String, String> entry : roomFriend.entrySet()) {
                                    if (entry.getValue().equals(friendID)) {
                                        mIsUser = true;
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                utilDBInterface.afterDoesAlreadyHasFriend(mIsUser, caseVal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                utilDBInterface.afterDoesAlreadyHasFriend(false, caseVal);
            }
        });
    }
    public synchronized void getUserName(String userID, final String caseVal, final UtilDBInterface utilDBInterface) {
        mUserName = "";
        mDatabase.child(D.Users).child(userID).child(D.Name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.getValue().toString();
                utilDBInterface.afterGetUserName(mUserName, caseVal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //新しいルームをセット
    public synchronized String setRoom(){
        Room room = new Room(false, "");
        DatabaseReference postRef = mDatabase.child(D.Rooms).push();
        postRef.setValue(room);
        return postRef.getKey();
    }
    //新しいフレンドを追加
    public synchronized void setFriend(String friendID, String roomID) {
        //自身にフレンドを追加
        HashMap<String, String> friend2 = new HashMap<String, String>();
        //friend2.put(getOwnUserID(), roomID);
        friend2.put(roomID, getOwnUserID());
        //自身のaddにユーザーを追加
        mDatabase.child(D.Users).child(getOwnUserID()).child(D.Friends).child(roomID).setValue(friendID);

        //相手に追加したことを通知
        System.out.println("相手に通知");
        mDatabase.child(D.Users).child(friendID).child(D.AddedFriends).child(roomID).setValue(getOwnUserID());
    }
}
