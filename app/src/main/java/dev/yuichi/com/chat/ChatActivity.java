package dev.yuichi.com.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String roomID = intent.getStringExtra(D.RoomID);
        TextView textview = (TextView)findViewById(R.id.room_chat_text);
        textview.setText(roomID);
    }
}
