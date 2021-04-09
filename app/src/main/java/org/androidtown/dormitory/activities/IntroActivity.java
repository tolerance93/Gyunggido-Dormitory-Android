package org.androidtown.dormitory.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import org.androidtown.dormitory.R;

public class IntroActivity extends Activity {

    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        h = new Handler();
        h.postDelayed(mrun, 500);
    }
    Runnable mrun= new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        h.removeCallbacks(mrun);
    }
}
