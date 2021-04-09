package org.androidtown.dormitory.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidtown.dormitory.R;

public class ImageZoomActivity extends AppCompatActivity {

    private PhotoViewAttacher mAttacher;
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;
    private TextView mTitle;

    private LinearLayout mImageZoomLayout;

    private boolean appBarShow = true;


    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        mToolbar = (Toolbar) findViewById(R.id.zoom_image_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("사진");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back_white);




        mImageView = (ImageView) findViewById(R.id.image_view);

        mImageZoomLayout = (LinearLayout) findViewById(R.id.image_zoom_layout);

//        mImageZoomLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ImageZoomActivity.this, "실행됨",Toast.LENGTH_SHORT).show();
//
//            }
//        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("로딩중");
        mProgressDialog.setMessage("잠시만 기다려주세요.");
        mProgressDialog.show();

        final String image_uri = getIntent().getStringExtra("image_uri");
        Picasso.get().load(image_uri).into(mImageView, new Callback(){
            @Override
            public void onSuccess() {
                mProgressDialog.dismiss();
                mAttacher = new PhotoViewAttacher(mImageView);

                mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

                    @Override
                    public void onPhotoTap(ImageView view, float v, float v1) {
                        if(appBarShow){
                            getSupportActionBar().hide();
                            appBarShow = false;
                        }else{
                            getSupportActionBar().show();
                            appBarShow = true;
                        }
                    }
//
//                    @Override
//                    public void onOutsidePhotoTap(ImageView view) {
//                        if(appBarShow){
//                            getSupportActionBar().hide();
//                            appBarShow = false;
//                        }else{
//                            getSupportActionBar().show();
//                            appBarShow = true;
//                        }
//                    }
                });
            }

            @Override
            public void onError(Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(ImageZoomActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    @Override
    public void onBackPressed() {
        finish();
//        overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
    }
}
