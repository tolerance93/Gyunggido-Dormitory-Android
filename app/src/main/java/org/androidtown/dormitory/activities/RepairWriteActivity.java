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

public class RepairWriteActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mRepairWriteTitleEdit;
    private EditText mRepairWriteContentEdit;

    private ImageView mRepairWriteImageView;
    private Button mRepairWriteImageBtn;
    private Button mRepairWriteOpenBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mRepairWriteDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mRepairStaffDatabase;
    private User user;

    private Uri imageUri = null;

    private ProgressDialog mRepairUploadDialog;

    private StorageReference mStorageReference;

    private static final int GALLERY_REQUEST = 1;

    private boolean checkImage = false;
    private boolean isOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_write);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }


        mStorageReference = FirebaseStorage.getInstance().getReference();

        mRepairWriteImageBtn = (Button) findViewById(R.id.repair_write_image_btn);
        mRepairWriteImageView = (ImageView) findViewById(R.id.repair_write_image_view);

        mRepairWriteTitleEdit = (EditText) findViewById(R.id.repair_write_title);
        mRepairWriteContentEdit = (EditText) findViewById(R.id.repair_write_contents);

        mRepairWriteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkImage == false){
                    //mNoticeWriteImageBtn.setBackgroundResource(R.drawable.image_upload);
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }else{
                    mRepairWriteImageView.setImageURI(null);
                    mRepairWriteImageBtn.setBackgroundResource(R.drawable.google_photo);
                    checkImage = false;
                }

            }
        });

        mToolbar = findViewById(R.id.repair_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_cancel);
        mTitle.setText("글쓰기");
        mTitle.setTextColor(Color.BLACK);

        mRepairWriteOpenBtn = findViewById(R.id.repair_write_open_button);
        mRepairWriteOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (isOpen){
                    isOpen = false;
                    mRepairWriteOpenBtn.setBackgroundColor(Color.parseColor("#ff5733"));
                    mRepairWriteOpenBtn.setText("비공개");
                } else {
                    isOpen = true;
                    mRepairWriteOpenBtn.setBackgroundColor(Color.parseColor("#1276d6"));
                    mRepairWriteOpenBtn.setText("공개");
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repair_write_bar, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            mRepairWriteImageBtn.setBackgroundResource(R.drawable.google_delete);
            mRepairWriteImageView.setImageURI(imageUri);
            checkImage = true;
        }
    }

    @Override
    public void onBackPressed(){

        final String title = mRepairWriteTitleEdit.getText().toString();
        final String content = mRepairWriteContentEdit.getText().toString();

        if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
            finish();
        } else{
            final AlertDialog alertDialog = new AlertDialog.Builder(RepairWriteActivity.this)
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

                final String title = mRepairWriteTitleEdit.getText().toString();
                final String content = mRepairWriteContentEdit.getText().toString();

                if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
                    finish();
                }else{
                    final AlertDialog alertDialog = new AlertDialog.Builder(RepairWriteActivity.this)
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
            case R.id.action_repair_write_done:
                submitRequest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitRequest() {

        final String mTitle = mRepairWriteTitleEdit.getText().toString();
        final String mContents = mRepairWriteContentEdit.getText().toString();


        if(TextUtils.isEmpty(mTitle)){
            Toast.makeText(RepairWriteActivity.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(mContents)) {
            Toast.makeText(RepairWriteActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else{

            mRepairUploadDialog = new ProgressDialog(this);
            mRepairUploadDialog.setTitle("업로딩");
            mRepairUploadDialog.setMessage("잠시만 기다려주세요.");
            mRepairUploadDialog.show();

            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = current_user.getUid();

            mRepairWriteDatabase = FirebaseDatabase.getInstance().getReference().child("Repair").push();
            String textKey = mRepairWriteDatabase.getKey().toString();

            final String textId = textKey;
            final String mStatus = "미확인";

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            ValueEventListener repairWriteListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    user = dataSnapshot.getValue(User.class);
                    final HashMap<String, Object> repairMap = new HashMap<String, Object>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
                    final String currentDateandTime = sdf.format(new Date());
                    if(checkImage == true){
                        final StorageReference filepath = mStorageReference.child("Repair_Images").child(textId);
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
                                    uploadData(repairMap, mTitle, mContents,Integer.valueOf(mRepairWriteImageView.getMeasuredHeight()),Integer.valueOf(mRepairWriteImageView.getMeasuredWidth()),
                                            mStatus, uid, downloadUri.toString(), currentDateandTime.toString(),Double.valueOf((System.currentTimeMillis())));

                                } else {
                                    mRepairUploadDialog.dismiss();
                                    Toast.makeText(RepairWriteActivity.this,"Error, try again",Toast.LENGTH_SHORT).show();
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }else{
                        uploadData(repairMap, mTitle, mContents,Integer.valueOf(mRepairWriteImageView.getMeasuredHeight()),Integer.valueOf(mRepairWriteImageView.getMeasuredWidth()),
                                mStatus, uid, "", currentDateandTime.toString(),Double.valueOf((System.currentTimeMillis())));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(RepairWriteActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    // ...
                }
            };
            mDatabase.addListenerForSingleValueEvent(repairWriteListener);
        }
    }

    public void uploadData(HashMap repairMap, String title, String contents, int height, int width, String status, String uid, String url, String time, Double orderTime){
        String open = "공개";
        if(!isOpen){
            open = "비공개";
        }
        repairMap.put("title",title);
        repairMap.put("contents",contents);
        repairMap.put("imageHeight",height);
        repairMap.put("imageWidth",width);
        repairMap.put("status",status);
        repairMap.put("uid",uid);
        repairMap.put("open",open);
        repairMap.put("imageUrl",url);
        repairMap.put("time",time);
        repairMap.put("orderTime", orderTime);

        mRepairWriteDatabase.setValue(repairMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mRepairUploadDialog.dismiss();
                    InputMethodManager inputManager = (InputMethodManager)
                            getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mRepairWriteContentEdit.getApplicationWindowToken(), 0);
                    finish();
                }else{
                    mRepairUploadDialog.dismiss();
                    Toast.makeText(RepairWriteActivity.this,"Error, try again",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
