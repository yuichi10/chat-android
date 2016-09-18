package dev.yuichi.com.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.yuichi.com.chat.FirebaseModel.SignUp;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUp = (Button)findViewById(R.id.execSignUpButton);
        signUp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //FirebaseUser user = firebaseAuth.getCurrentUser();
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    //IDの保存
                    Log.d("auth", "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d("auth", "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        EditText emailEdit = (EditText)findViewById(R.id.signUpEmailEditText);
        EditText passwordEdit = (EditText)findViewById(R.id.signUpPasswordEditText);
        EditText userNameEdit = (EditText)findViewById(R.id.signUpUserNameEditText);
        final SpannableStringBuilder email = (SpannableStringBuilder)emailEdit.getText();
        final SpannableStringBuilder password = (SpannableStringBuilder)passwordEdit.getText();
        final SpannableStringBuilder userName = (SpannableStringBuilder)userNameEdit.getText();
        if (email.toString().equals("") == true || password.toString().equals("") == true || userName.toString().equals("") == true) {
            Toast.makeText(this, "空欄があります", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v != null) {
            switch (v.getId()) {
                case R.id.execSignUpButton:
                    mAuth.createUserWithEmailAndPassword(email.toString(), password.toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("auth", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "ユーザー作成失敗: emailを変更してください",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "ユーザー作成成功",
                                        Toast.LENGTH_SHORT).show();
                                signUpLogin(email.toString(), password.toString(), userName.toString());
                            }
                        }
                    });
            }
        }
    }

    private void signUpLogin(final String email, String password, final String userName) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("auth", "createUserWithEmail:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "ユーザーログイン失敗",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClassName(D.packageRoot, D.packageRoot + ".MainActivity");
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "ユーザーログイン成功",
                            Toast.LENGTH_SHORT).show();
                    SignUp signup = new SignUp(userName, email);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = database.getReference();
                    dbRef.child("users").child(mUser.getUid()).setValue(signup);
                    Intent intent = new Intent();
                    intent.setClassName(D.packageRoot, D.packageRoot + ".BindAppActivity");
                    startActivity(intent);
                }
            }
        });
    }
}
