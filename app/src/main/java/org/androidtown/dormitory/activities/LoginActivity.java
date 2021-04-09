package org.androidtown.dormitory.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.androidtown.dormitory.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private Button loginBtn;
    private Button signupBtn;
    private TextView mForgetTextView;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        mLoginProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        emailText = (EditText) findViewById(R.id.login_email);
        passwordText = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_login_btn);
        signupBtn = (Button) findViewById(R.id.login_signup_btn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();


                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mLoginProgress.setTitle("로그인");
                    mLoginProgress.setMessage("계정 정보를 확인 중 입니다.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email, password);

                } else {
                    Toast.makeText(LoginActivity.this, "로그인 할 수 없습니다. 이메일과 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });


    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    //String current_user_id = mAuth.getCurrentUser().getUid();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();

                }else{
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "로그인 할 수 없습니다. 이메일과 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
