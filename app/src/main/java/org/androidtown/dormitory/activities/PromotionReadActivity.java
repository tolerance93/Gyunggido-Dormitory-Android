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

import org.androidtown.dormitory.model.PromotionComment;
import org.androidtown.dormitory.display.PromotionMainDisplay;
import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PromotionReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mPromotionDatabase;
    private DatabaseReference mPromotionWriterDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mPromotionCommentDatabase;
    private DatabaseReference mUserDatabase;
    private PromotionMainDisplay text;

    private TextView mPromotionReadTitle;
    private TextView mPromotionReadOpen;
    private TextView mPromotionReadNickname;
    private TextView mPromotionReadDate;
    private TextView mPromotionReadContents;
    private EditText mPromotionReadComments;
    private Button mPromotionReadSubmitBtn;
    private Spinner mPromotionReadSpinner;

    private User writeUser;

    private ImageView mPromotionReadImage;

    private RecyclerView mPromotionCommentList;

    private LinearLayout mReadTitleLayout;

    private ProgressDialog mPromotionCommentProgress;
    private ProgressDialog mPromotionDeleteProgress;
    private ProgressDialog mPromotionLoadDialog;

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
        setContentView(R.layout.activity_promotion_read);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        mPromotionLoadDialog = new ProgressDialog(this);
        mPromotionLoadDialog.setTitle("로딩중");
        mPromotionLoadDialog.setMessage("잠시만 기다려주세요.");
        mPromotionLoadDialog.show();


        mToolbar = findViewById(R.id.promotion_read_appBar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back);
        mTitle.setText("홍보 게시판");
        mTitle.setTextColor(Color.BLACK);

        mPromotionCommentList = (RecyclerView) findViewById(R.id.promotion_write_comments_list);
        mPromotionCommentList.setNestedScrollingEnabled(false);
        //mPromotionCommentList.setHasFixedSize(true);
        mPromotionCommentList.setLayoutManager(new LinearLayoutManager(this));

        mPromotionReadComments = (EditText) findViewById(R.id.promotion_read_comments);
        mPromotionReadSubmitBtn = (Button) findViewById(R.id.promotion_read_submit_btn);


        mPromotionReadImage = (ImageView) findViewById(R.id.promotion_read_image_view);

        mPromotionReadTitle = (TextView) findViewById(R.id.promotion_read_title);
        mPromotionReadContents = (TextView) findViewById(R.id.promotion_read_contents);
        mPromotionReadNickname = findViewById(R.id.promotion_read_nickname);
        mPromotionReadDate = findViewById(R.id.promotion_read_date);

        //작성자 uid, 글id
        user_id = getIntent().getStringExtra("user_id");
        text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

        mPromotionDatabase = FirebaseDatabase.getInstance().getReference().child("Promotion").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        //댓글 입력하기
        mPromotionReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mPromotionReadComments.getText().toString();
                commentsUpdate(comments, current_user_id, text_id);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mPromotionReadComments.getApplicationWindowToken(), 0);
                onStart();
            }
        });


        //텍스트 입력하기
        mPromotionWriterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
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
                            mPromotionReadNickname.setText(writerNickname);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    mPromotionWriterDatabase.addValueEventListener(writeUserListener);
                    //닉네임 입력하기 끝


                    text = dataSnapshot.getValue(PromotionMainDisplay.class);
                    mPromotionReadTitle.setText(text.getTitle());
                    mPromotionReadDate.setText(text.getTime());
                    mPromotionReadContents.setText(text.getContents());
                    imageUri = text.getImageUrl();

                    //image 불러오기
                    if(!imageUri.equals("")){
                        Picasso.get().load(imageUri).into(mPromotionReadImage, new Callback(){
                            @Override
                            public void onSuccess() {
                                mPromotionLoadDialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                mPromotionLoadDialog.dismiss();
                                Toast.makeText(PromotionReadActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mPromotionLoadDialog.dismiss();
                    }


                    // ...
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(PromotionReadActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        mPromotionDatabase.addValueEventListener(userListener);

        //이미지 확대
        mPromotionReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("")){
                    Intent intent = new Intent(PromotionReadActivity.this, ImageZoomActivity.class);
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

        Query promotionDatabaseQuery;
        promotionDatabaseQuery = FirebaseDatabase.getInstance().getReference().child("PromotionComments").child(text_id).orderByChild("orderTime");

        FirebaseRecyclerAdapter<PromotionComment, PromotionCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<PromotionComment, PromotionCommentViewHolder>(
                PromotionComment.class,
                R.layout.promotion_comments_layout,
                PromotionCommentViewHolder.class,
                promotionDatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(final PromotionCommentViewHolder viewHolder, PromotionComment model, int position) {

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
        mPromotionCommentList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.promotion_read_bar, menu);
        if (!current_user_id.equals(user_id)){
            menu.findItem(R.id.action_promotion_read_delete).setVisible(false);
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
            case R.id.action_promotion_read_delete:
                deletePromotion();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePromotion() {
        final AlertDialog alertDialog = new AlertDialog.Builder(PromotionReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPromotionDeleteProgress = new ProgressDialog(PromotionReadActivity.this);
                        mPromotionDeleteProgress.setTitle("삭제중");
                        mPromotionDeleteProgress.setMessage("잠시만 기다려 주세요.");
                        mPromotionDeleteProgress.setCanceledOnTouchOutside(false);
                        mPromotionDeleteProgress.show();
                        mPromotionDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mPromotionDeleteProgress.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    mPromotionDeleteProgress.dismiss();
                                    Toast.makeText(PromotionReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();

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
        mPromotionCommentProgress = new ProgressDialog(this);
        mPromotionCommentProgress.setTitle("로딩중");
        mPromotionCommentProgress.setMessage("잠시만 기다려주세요.");
        mPromotionCommentProgress.show();

        final HashMap<String, Object> commentMap = new HashMap<String, Object>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("text",comments);
        commentMap.put("uid",myUid);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("orderTime", Double.valueOf((System.currentTimeMillis())));
        mPromotionCommentDatabase = FirebaseDatabase.getInstance().getReference().child("PromotionComments").child(textId).push();
        final String commentTextKey = mPromotionCommentDatabase.getKey();

        mPromotionCommentDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mPromotionReadComments.setText("");
                    mPromotionCommentProgress.dismiss();
                }else{
                    mPromotionCommentProgress.dismiss();
                    Toast.makeText(PromotionReadActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void updateStatus(final String status) {
        mPromotionDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }else{
                    Toast.makeText(PromotionReadActivity.this,"상태가 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class PromotionCommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private TextView promotionCommentContent;
        private TextView promotionCommentDate;
        private TextView promotionCommentNickname;

        public PromotionCommentViewHolder(View itemView){
            super(itemView);
            mView = itemView;

        }
        public void setContent(String content){
            promotionCommentContent = (TextView) mView.findViewById(R.id.promotion_comments_content);
            promotionCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            promotionCommentNickname = (TextView) mView.findViewById(R.id.promotion_comments_nickname);
            promotionCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            promotionCommentDate = (TextView) mView.findViewById(R.id.promotion_comments_date);
            promotionCommentDate.setText(date);
        }

    }
}
