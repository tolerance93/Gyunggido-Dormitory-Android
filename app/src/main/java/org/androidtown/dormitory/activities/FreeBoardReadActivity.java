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

import org.androidtown.dormitory.model.FreeBoardComment;
import org.androidtown.dormitory.display.FreeBoardMainDisplay;
import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FreeBoardReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mFreeBoardDatabase;
    private DatabaseReference mFreeBoardWriterDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mFreeBoardCommentDatabase;
    private DatabaseReference mUserDatabase;
    private FreeBoardMainDisplay text;

    private TextView mFreeBoardReadTitle;
    private TextView mFreeBoardReadOpen;
    private TextView mFreeBoardReadNickname;
    private TextView mFreeBoardReadDate;
    private TextView mFreeBoardReadContents;
    private EditText mFreeBoardReadComments;
    private Button mFreeBoardReadSubmitBtn;
    private Spinner mFreeBoardReadSpinner;

    private User writeUser;

    private ImageView mFreeBoardReadImage;

    private RecyclerView mFreeBoardCommentList;

    private LinearLayout mReadTitleLayout;

    private ProgressDialog mFreeBoardCommentProgress;
    private ProgressDialog mFreeBoardDeleteProgress;
    private ProgressDialog mFreeBoardLoadDialog;

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
        setContentView(R.layout.activity_free_board_read);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        mFreeBoardLoadDialog = new ProgressDialog(this);
        mFreeBoardLoadDialog.setTitle("로딩중");
        mFreeBoardLoadDialog.setMessage("잠시만 기다려주세요.");
        mFreeBoardLoadDialog.show();


        mToolbar = findViewById(R.id.free_board_read_appBar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back);
        mTitle.setText("자유 게시판");
        mTitle.setTextColor(Color.BLACK);

        mFreeBoardCommentList = (RecyclerView) findViewById(R.id.free_board_write_comments_list);
        mFreeBoardCommentList.setNestedScrollingEnabled(false);
        //mFreeBoardCommentList.setHasFixedSize(true);
        mFreeBoardCommentList.setLayoutManager(new LinearLayoutManager(this));

        mFreeBoardReadComments = (EditText) findViewById(R.id.free_board_read_comments);
        mFreeBoardReadSubmitBtn = (Button) findViewById(R.id.free_board_read_submit_btn);


        mFreeBoardReadImage = (ImageView) findViewById(R.id.free_board_read_image_view);

        mFreeBoardReadTitle = (TextView) findViewById(R.id.free_board_read_title);
        mFreeBoardReadContents = (TextView) findViewById(R.id.free_board_read_contents);
        mFreeBoardReadNickname = findViewById(R.id.free_board_read_nickname);
        mFreeBoardReadDate = findViewById(R.id.free_board_read_date);

        //작성자 uid, 글id
        user_id = getIntent().getStringExtra("user_id");
        text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

        mFreeBoardDatabase = FirebaseDatabase.getInstance().getReference().child("FreeBoard").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        //댓글 입력하기
        mFreeBoardReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mFreeBoardReadComments.getText().toString();
                commentsUpdate(comments, current_user_id, text_id);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mFreeBoardReadComments.getApplicationWindowToken(), 0);
                onStart();
            }
        });


        //텍스트 입력하기
        mFreeBoardWriterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
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
                            mFreeBoardReadNickname.setText(writerNickname);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    mFreeBoardWriterDatabase.addValueEventListener(writeUserListener);
                    //닉네임 입력하기 끝


                    text = dataSnapshot.getValue(FreeBoardMainDisplay.class);
                    mFreeBoardReadTitle.setText(text.getTitle());
                    mFreeBoardReadDate.setText(text.getTime());
                    mFreeBoardReadContents.setText(text.getContents());
                    imageUri = text.getImageUrl();

                    //image 불러오기
                    if(!imageUri.equals("")){
                        Picasso.get().load(imageUri).into(mFreeBoardReadImage, new Callback(){
                            @Override
                            public void onSuccess() {
                                mFreeBoardLoadDialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                mFreeBoardLoadDialog.dismiss();
                                Toast.makeText(FreeBoardReadActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mFreeBoardLoadDialog.dismiss();
                    }


                    // ...
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(FreeBoardReadActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        mFreeBoardDatabase.addValueEventListener(userListener);

        //이미지 확대
        mFreeBoardReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("")){
                    Intent intent = new Intent(FreeBoardReadActivity.this, ImageZoomActivity.class);
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

        Query freeBoardDatabaseQuery;
        freeBoardDatabaseQuery = FirebaseDatabase.getInstance().getReference().child("FreeBoardComments").child(text_id).orderByChild("orderTime");

        FirebaseRecyclerAdapter<FreeBoardComment, FreeBoardCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<FreeBoardComment, FreeBoardCommentViewHolder>(
                FreeBoardComment.class,
                R.layout.free_board_comments_layout,
                FreeBoardCommentViewHolder.class,
                freeBoardDatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(final FreeBoardCommentViewHolder viewHolder, FreeBoardComment model, int position) {

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
        mFreeBoardCommentList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.free_board_read_bar, menu);
        if (!current_user_id.equals(user_id)){
            menu.findItem(R.id.action_free_board_read_delete).setVisible(false);
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
            case R.id.action_free_board_read_delete:
                deleteFreeBoard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteFreeBoard() {
        final AlertDialog alertDialog = new AlertDialog.Builder(FreeBoardReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFreeBoardDeleteProgress = new ProgressDialog(FreeBoardReadActivity.this);
                        mFreeBoardDeleteProgress.setTitle("삭제중");
                        mFreeBoardDeleteProgress.setMessage("잠시만 기다려 주세요.");
                        mFreeBoardDeleteProgress.setCanceledOnTouchOutside(false);
                        mFreeBoardDeleteProgress.show();
                        mFreeBoardDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFreeBoardDeleteProgress.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    mFreeBoardDeleteProgress.dismiss();
                                    Toast.makeText(FreeBoardReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();

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
        mFreeBoardCommentProgress = new ProgressDialog(this);
        mFreeBoardCommentProgress.setTitle("로딩중");
        mFreeBoardCommentProgress.setMessage("잠시만 기다려주세요.");
        mFreeBoardCommentProgress.show();

        final HashMap<String, Object> commentMap = new HashMap<String, Object>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("text",comments);
        commentMap.put("uid",myUid);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("orderTime", Double.valueOf((System.currentTimeMillis())));
        mFreeBoardCommentDatabase = FirebaseDatabase.getInstance().getReference().child("FreeBoardComments").child(textId).push();
        final String commentTextKey = mFreeBoardCommentDatabase.getKey();

        mFreeBoardCommentDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mFreeBoardReadComments.setText("");
                    mFreeBoardCommentProgress.dismiss();
                }else{
                    mFreeBoardCommentProgress.dismiss();
                    Toast.makeText(FreeBoardReadActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void updateStatus(final String status) {
        mFreeBoardDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }else{
                    Toast.makeText(FreeBoardReadActivity.this,"상태가 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class FreeBoardCommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private TextView freeBoardCommentContent;
        private TextView freeBoardCommentDate;
        private TextView freeBoardCommentNickname;

        public FreeBoardCommentViewHolder(View itemView){
            super(itemView);
            mView = itemView;

        }
        public void setContent(String content){
            freeBoardCommentContent = (TextView) mView.findViewById(R.id.free_board_comments_content);
            freeBoardCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            freeBoardCommentNickname = (TextView) mView.findViewById(R.id.free_board_comments_nickname);
            freeBoardCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            freeBoardCommentDate = (TextView) mView.findViewById(R.id.free_board_comments_date);
            freeBoardCommentDate.setText(date);
        }

    }
}
