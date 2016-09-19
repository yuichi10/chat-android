package dev.yuichi.com.chat;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class BindAppActivity extends FragmentActivity implements FragmentTabHost.OnTabChangeListener {
    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_app);

        setToolBarInfo();

        setTabInfo();

        mFirebaseAuth = FirebaseAuth.getInstance();

        //Firebase firebase = new Firebase("https://" + D.FirebaseURL +".firebaseio.com/");
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.d("onTabChanged", "tabId: " + tabId);
    }

    private void setToolBarInfo() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Chatアプリ");
        toolbar.setTitleTextColor(-1);
        toolbar.setNavigationIcon(R.drawable.common_google_signin_btn_icon_dark);
        //setSupportActionBar(toolbar);
    }

    private void setTabInfo() {
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);
        TabHost.TabSpec tabSpec1, tabSpec2;
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("ルーム");
        // TabHost に追加
        tabHost.addTab(tabSpec1, RoomListFragment.class, null);

        // TabSpec を生成する
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("tab2");
        // TabHost に追加
        tabHost.addTab(tabSpec2, FriendSearchFragment.class, null);
        // リスナー登録
        tabHost.setOnTabChangedListener(this);
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
