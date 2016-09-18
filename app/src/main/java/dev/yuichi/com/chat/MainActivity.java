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

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.yuichi.com.chat.FirebaseModel.SignUp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this);
        Firebase.setAndroidContext(this);
        Firebase firebase = new Firebase(D.FirebaseURL);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //FirebaseUser user = firebaseAuth.getCurrentUser();
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
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
        EditText emailEdit = (EditText)findViewById(R.id.emailEditText);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEditText);
        SpannableStringBuilder email = (SpannableStringBuilder)emailEdit.getText();
        SpannableStringBuilder password = (SpannableStringBuilder)passwordEdit.getText();
        if (v != null) {
            switch (v.getId()) {
                case R.id.signInButton:
                    if (email.toString().equals("") == true || password.toString().equals("") == true){
                        Toast.makeText(this, "空欄があります", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Sign In
                    mAuth.signInWithEmailAndPassword(D.testEmail, D.testPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("auth", "signInWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w("auth", "signInWithEmail:failed", task.getException());
                                Toast.makeText(MainActivity.this, "authに失敗しました",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w("auth", "signInWithEmail:success", task.getException());
                                Toast.makeText(MainActivity.this, "authに成功しました",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClassName("dev.yuichi.com.chat", "dev.yuichi.com.chat.BindAppActivity");
                                startActivity(intent);
                            }
                        }
                    });
                    break;
                case R.id.signUpButton:
                    Intent intent = new Intent();
                    intent.setClassName("dev.yuichi.com.chat", "dev.yuichi.com.chat.SignUpActivity");
                    startActivity(intent);
                    //Sign Up
                    /*
                    */
                    break;
            }
        }
    }

    public void signIn(final String email, String password) {
        //Sign In
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("auth", "signInWithEmail:onComplete:" + task.isSuccessful());
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w("auth", "signInWithEmail:failed", task.getException());
                    Toast.makeText(MainActivity.this, "authに失敗しました",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.w("auth", "signInWithEmail:success", task.getException());
                    Toast.makeText(MainActivity.this, "authに成功しました",
                            Toast.LENGTH_SHORT).show();

                    SignUp signup = new SignUp("name", email);
                    //Firebase firebase = new Firebase(D.FirebaseURL);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = database.getReference();
                    dbRef.child("users").child(mUser.getUid()).setValue(signup);
                    //firebase.child("users/" + user.getUid()).setValue(signup);
                    //firebase.push().setValue(signup.getSignUpDataForm());
                }
            }
        });
    }
}
