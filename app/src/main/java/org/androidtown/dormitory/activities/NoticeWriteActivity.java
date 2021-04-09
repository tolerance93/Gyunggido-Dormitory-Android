package org.androidtown.dormitory.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NoticeWriteActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mNoticeWriteTitleEdit;
    private EditText mNoticeWriteContentEdit;

    private ImageView mNoticeWriteImageView;
    private Button mNoticeWriteImageBtn;
    private Button mNoticeWriteOpenBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mNoticeWriteDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mNoticeStaffDatabase;
    private User user;

    private Uri imageUri = null;

    private ProgressDialog mNoticeUploadDialog;

    private StorageReference mStorageReference;

    private static final int GALLERY_REQUEST = 1;

    private boolean checkImage = false;
    private boolean isOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }


        mStorageReference = FirebaseStorage.getInstance().getReference();

        mNoticeWriteImageBtn = (Button) findViewById(R.id.notice_write_image_btn);
        mNoticeWriteImageView = (ImageView) findViewById(R.id.notice_write_image_view);

        mNoticeWriteTitleEdit = (EditText) findViewById(R.id.notice_write_title);
        mNoticeWriteContentEdit = (EditText) findViewById(R.id.notice_write_contents);

        mNoticeWriteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkImage == false){
                    //mNoticeWriteImageBtn.setBackgroundResource(R.drawable.image_upload);
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }else{
                    mNoticeWriteImageView.setImageURI(null);
                    mNoticeWriteImageBtn.setBackgroundResource(R.drawable.google_photo);
                    checkImage = false;
                }

            }
        });

        mToolbar = findViewById(R.id.notice_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_cancel);
        mTitle.setText("글쓰기");
        mTitle.setTextColor(Color.BLACK);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_write_bar, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            mNoticeWriteImageBtn.setBackgroundResource(R.drawable.google_delete);
            mNoticeWriteImageView.setImageURI(imageUri);
            checkImage = true;
        }
    }

    @Override
    public void onBackPressed(){

        final String title = mNoticeWriteTitleEdit.getText().toString();
        final String content = mNoticeWriteContentEdit.getText().toString();

        if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
            finish();
        } else{
            final AlertDialog alertDialog = new AlertDialog.Builder(NoticeWriteActivity.this)
                    .setTitle("뒤로가기")
                    .setMessage("작성을 취소하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                }
            });
            alertDialog.show();
        }
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                final String title = mNoticeWriteTitleEdit.getText().toString();
                final String content = mNoticeWriteContentEdit.getText().toString();

                if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
                    finish();
                }else{
                    final AlertDialog alertDialog = new AlertDialog.Builder(NoticeWriteActivity.this)
                            .setTitle("뒤로가기")
                            .setMessage("작성을 취소하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                        }
                    });
                    alertDialog.show();
                }
                return true;
            case R.id.action_notice_write_done:
                submitRequest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitRequest() {

        final String mTitle = mNoticeWriteTitleEdit.getText().toString();
        final String mContents = mNoticeWriteContentEdit.getText().toString();


        if(TextUtils.isEmpty(mTitle)){
            Toast.makeText(NoticeWriteActivity.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(mContents)) {
            Toast.makeText(NoticeWriteActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else{

            mNoticeUploadDialog = new ProgressDialog(this);
            mNoticeUploadDialog.setTitle("업로딩");
            mNoticeUploadDialog.setMessage("잠시만 기다려주세요.");
            mNoticeUploadDialog.show();

            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = current_user.getUid();

            mNoticeWriteDatabase = FirebaseDatabase.getInstance().getReference().child("Notice").push();
            String textKey = mNoticeWriteDatabase.getKey().toString();

            final String textId = textKey;

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            ValueEventListener noticeWriteListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    user = dataSnapshot.getValue(User.class);
                    final HashMap<String, Object> noticeMap = new HashMap<String, Object>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
                    final String currentDateandTime = sdf.format(new Date());
                    if(checkImage == true){
                        final StorageReference filepath = mStorageReference.child("Notice_Images").child(textId);
                        UploadTask  uploadTask = filepath.putFile(imageUri);


                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    uploadData(noticeMap, mTitle, mContents,Integer.valueOf(mNoticeWriteImageView.getMeasuredHeight()),Integer.valueOf(mNoticeWriteImageView.getMeasuredWidth()),
                                             uid, downloadUri.toString(), currentDateandTime.toString(),Double.valueOf((System.currentTimeMillis())));

                                } else {
                                    mNoticeUploadDialog.dismiss();
                                    Toast.makeText(NoticeWriteActivity.this,"Error, try again",Toast.LENGTH_SHORT).show();
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }else{
                        uploadData(noticeMap, mTitle, mContents,Integer.valueOf(mNoticeWriteImageView.getMeasuredHeight()),Integer.valueOf(mNoticeWriteImageView.getMeasuredWidth()),
                                 uid, "", currentDateandTime.toString(),Double.valueOf((System.currentTimeMillis())));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(NoticeWriteActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    // ...
                }
            };
            mDatabase.addListenerForSingleValueEvent(noticeWriteListener);
        }
    }

    public void uploadData(HashMap noticeMap, String title, String contents, int height, int width, String uid, String url, String time, Double orderTime){

        noticeMap.put("title",title);
        noticeMap.put("contents",contents);
        noticeMap.put("imageHeight",height);
        noticeMap.put("imageWidth",width);
        noticeMap.put("uid",uid);
        noticeMap.put("imageUrl",url);
        noticeMap.put("time",time);
        noticeMap.put("orderTime", orderTime);

        mNoticeWriteDatabase.setValue(noticeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mNoticeUploadDialog.dismiss();
                    InputMethodManager inputManager = (InputMethodManager)
                            getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mNoticeWriteContentEdit.getApplicationWindowToken(), 0);
                    finish();
                }else{
                    mNoticeUploadDialog.dismiss();
                    Toast.makeText(NoticeWriteActivity.this,"Error, try again",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
