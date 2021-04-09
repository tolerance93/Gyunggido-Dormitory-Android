package org.androidtown.dormitory.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidtown.dormitory.model.NoticeComment;
import org.androidtown.dormitory.display.NoticeMainDisplay;
import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NoticeReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mNoticeDatabase;
    private DatabaseReference mNoticeWriterDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mNoticeCommentDatabase;
    private DatabaseReference mUserDatabase;
    private NoticeMainDisplay text;

    private TextView mNoticeReadTitle;
    private TextView mNoticeReadOpen;
    private TextView mNoticeReadNickname;
    private TextView mNoticeReadDate;
    private TextView mNoticeReadContents;
    private EditText mNoticeReadComments;
    private Button mNoticeReadSubmitBtn;
    private Spinner mNoticeReadSpinner;

    private User writeUser;

    private ImageView mNoticeReadImage;

    private RecyclerView mNoticeCommentList;

    private LinearLayout mReadTitleLayout;

    private ProgressDialog mNoticeCommentProgress;
    private ProgressDialog mNoticeDeleteProgress;
    private ProgressDialog mNoticeLoadDialog;

    private String imageUri = "";


    private String nickname;
    private String mClass;
    private String text_id;
    private String current_user_id;
    private String user_id;

    private User user,writer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_read);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        mNoticeLoadDialog = new ProgressDialog(this);
        mNoticeLoadDialog.setTitle("로딩중");
        mNoticeLoadDialog.setMessage("잠시만 기다려주세요.");
        mNoticeLoadDialog.show();


        mToolbar = findViewById(R.id.notice_read_appBar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back);
        mTitle.setText("공지사항");
        mTitle.setTextColor(Color.BLACK);

        mNoticeCommentList = (RecyclerView) findViewById(R.id.notice_write_comments_list);
        mNoticeCommentList.setNestedScrollingEnabled(false);
        //mNoticeCommentList.setHasFixedSize(true);
        mNoticeCommentList.setLayoutManager(new LinearLayoutManager(this));

        mNoticeReadComments = (EditText) findViewById(R.id.notice_read_comments);
        mNoticeReadSubmitBtn = (Button) findViewById(R.id.notice_read_submit_btn);


        mNoticeReadImage = (ImageView) findViewById(R.id.notice_read_image_view);

        mNoticeReadTitle = (TextView) findViewById(R.id.notice_read_title);
        mNoticeReadContents = (TextView) findViewById(R.id.notice_read_contents);
        mNoticeReadNickname = findViewById(R.id.notice_read_nickname);
        mNoticeReadDate = findViewById(R.id.notice_read_date);

        //작성자 uid, 글id
        user_id = getIntent().getStringExtra("user_id");
        text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

        mNoticeDatabase = FirebaseDatabase.getInstance().getReference().child("Notice").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        //댓글 입력하기
        mNoticeReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mNoticeReadComments.getText().toString();
                commentsUpdate(comments, current_user_id, text_id);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mNoticeReadComments.getApplicationWindowToken(), 0);
                onStart();
            }
        });


        //텍스트 입력하기
        mNoticeWriterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {

                    //닉네임 입력하기 시작
                    ValueEventListener writeUserListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            writer = dataSnapshot.getValue(User.class);
                            String writerNickname = writer.getNickname();
                            mNoticeReadNickname.setText(writerNickname);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    mNoticeWriterDatabase.addValueEventListener(writeUserListener);
                    //닉네임 입력하기 끝


                    text = dataSnapshot.getValue(NoticeMainDisplay.class);
                    mNoticeReadTitle.setText(text.getTitle());
                    mNoticeReadDate.setText(text.getTime());
                    mNoticeReadContents.setText(text.getContents());
                    imageUri = text.getImageUrl();

                    //image 불러오기
                    if(!imageUri.equals("")){
                        Picasso.get().load(imageUri).into(mNoticeReadImage, new Callback(){
                            @Override
                            public void onSuccess() {
                                mNoticeLoadDialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                mNoticeLoadDialog.dismiss();
                                Toast.makeText(NoticeReadActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mNoticeLoadDialog.dismiss();
                    }


                    // ...
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(NoticeReadActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        mNoticeDatabase.addValueEventListener(userListener);

        //이미지 확대
        mNoticeReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("")){
                    Intent intent = new Intent(NoticeReadActivity.this, ImageZoomActivity.class);
                    intent.putExtra("image_uri",imageUri);
                    startActivity(intent);
                    overridePendingTransition(R.anim.below_in, R.anim.below_out);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query noticeDatabaseQuery;
        noticeDatabaseQuery = FirebaseDatabase.getInstance().getReference().child("NoticeComments").child(text_id).orderByChild("orderTime");

        FirebaseRecyclerAdapter<NoticeComment, NoticeCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<NoticeComment, NoticeCommentViewHolder>(
                NoticeComment.class,
                R.layout.notice_comments_layout,
                NoticeCommentViewHolder.class,
                noticeDatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(final NoticeCommentViewHolder viewHolder, NoticeComment model, int position) {

                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUid());
                ValueEventListener userListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        writeUser = dataSnapshot.getValue(User.class);
                        viewHolder.setNickname(writeUser.getNickname());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(getApplicationContext(), databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    }
                };
                mUserDatabase.addValueEventListener(userListener);
                viewHolder.setDate(model.getTime());
                viewHolder.setContent(model.getText());

            }
        };
        mNoticeCommentList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_read_bar, menu);
        if (!current_user_id.equals(user_id)){
            menu.findItem(R.id.action_notice_read_delete).setVisible(false);
        }

        return true;
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_notice_read_delete:
                deleteNotice();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNotice() {
        final AlertDialog alertDialog = new AlertDialog.Builder(NoticeReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNoticeDeleteProgress = new ProgressDialog(NoticeReadActivity.this);
                        mNoticeDeleteProgress.setTitle("삭제중");
                        mNoticeDeleteProgress.setMessage("잠시만 기다려 주세요.");
                        mNoticeDeleteProgress.setCanceledOnTouchOutside(false);
                        mNoticeDeleteProgress.show();
                        mNoticeDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mNoticeDeleteProgress.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    mNoticeDeleteProgress.dismiss();
                                    Toast.makeText(NoticeReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
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


    private void commentsUpdate(String comments, String myUid, String textId) {
        mNoticeCommentProgress = new ProgressDialog(this);
        mNoticeCommentProgress.setTitle("로딩중");
        mNoticeCommentProgress.setMessage("잠시만 기다려주세요.");
        mNoticeCommentProgress.show();

        final HashMap<String, Object> commentMap = new HashMap<String, Object>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("text",comments);
        commentMap.put("uid",myUid);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("orderTime", Double.valueOf((System.currentTimeMillis())));
        mNoticeCommentDatabase = FirebaseDatabase.getInstance().getReference().child("NoticeComments").child(textId).push();
        final String commentTextKey = mNoticeCommentDatabase.getKey();

        mNoticeCommentDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mNoticeReadComments.setText("");
                    mNoticeCommentProgress.dismiss();
                }else{
                    mNoticeCommentProgress.dismiss();
                    Toast.makeText(NoticeReadActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void updateStatus(final String status) {
        mNoticeDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }else{
                    Toast.makeText(NoticeReadActivity.this,"상태가 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class NoticeCommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private TextView noticeCommentContent;
        private TextView noticeCommentDate;
        private TextView noticeCommentNickname;

        public NoticeCommentViewHolder(View itemView){
            super(itemView);
            mView = itemView;

        }
        public void setContent(String content){
            noticeCommentContent = (TextView) mView.findViewById(R.id.notice_comments_content);
            noticeCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            noticeCommentNickname = (TextView) mView.findViewById(R.id.notice_comments_nickname);
            noticeCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            noticeCommentDate = (TextView) mView.findViewById(R.id.notice_comments_date);
            noticeCommentDate.setText(date);
        }

    }
}
