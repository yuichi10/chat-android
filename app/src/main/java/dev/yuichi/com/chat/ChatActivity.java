package dev.yuichi.com.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import dev.yuichi.com.chat.FirebaseModel.Chat;

public class ChatActivity extends AppCompatActivity {
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        //チャットで表示するビューを持ってるクラス
        public TextView otherChatTextView;
        public TextView otherChatUserTextView;

        public TextView myChatTextView;
        public TextView myChatUserTextView;

        public ChatViewHolder(View v) {
            super(v);
            otherChatTextView = (TextView) itemView.findViewById(R.id.otherChatTextView);
            otherChatUserTextView = (TextView) itemView.findViewById(R.id.otherChatUserTextView);
            myChatTextView = (TextView) itemView.findViewById(R.id.myChatTextView);
            myChatUserTextView = (TextView) itemView.findViewById(R.id.myChatUserTextView);
        }
    }

    private String mRoomID;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferences;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Chat, ChatViewHolder> mFirebaseAdapter;
    private UtilDB mUtilDB = UtilDB.getInstance();
    private EditText mTextEditText;
    private Button mSendButton;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFirebaseAuth = FirebaseAuth.getInstance();
        //ルームIDを取得
        Intent intent = getIntent();
        mRoomID = intent.getStringExtra(D.RoomID);
        //データ保存
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.child(D.Message).child(mRoomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat != null) {
                    System.out.println(chat.getText());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //リサイクルビューのアダプター
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.chat_list_item,
                ChatViewHolder.class,
                mFirebaseDatabaseReference.child(D.Message).child(mRoomID)
        ) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, int position) {
                //新しいメッセージが来たら自動で処理してくれる
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                System.out.println("populateViewHolder: " + model.getText() + " : " + model.getUserid());
                if (mUtilDB.getOwnUserID().equals(model.getUserid())) {
                    viewHolder.myChatTextView.setText(model.getText());
                    viewHolder.myChatUserTextView.setText(model.getUserid());
                    viewHolder.otherChatTextView.setText("");
                    viewHolder.otherChatUserTextView.setText("");
                } else {
                    viewHolder.otherChatTextView.setText(model.getText());
                    viewHolder.otherChatUserTextView.setText(model.getUserid());
                    viewHolder.myChatTextView.setText("");
                    viewHolder.myChatUserTextView.setText("");
                }
            }
        };
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            //アダプターの変化を察知
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mSendButton = (Button)findViewById(R.id.chatSendButton);
        mTextEditText = (EditText)findViewById(R.id.chatEditText);
        //editの文字制限 firebaseからいつでも変えられるように
        mTextEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, D.DEFAULT_MSG_LENGTH_LIMIT))});
        mTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpannableStringBuilder text = (SpannableStringBuilder)mTextEditText.getText();
                if (!text.toString().equals("")){
                    Chat chat = new Chat(
                        text.toString(), mUtilDB.getOwnUserID(), ""
                    );
                    mFirebaseDatabaseReference.child(D.Message).child(mRoomID).push().setValue(chat);
                    mTextEditText.setText("");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
