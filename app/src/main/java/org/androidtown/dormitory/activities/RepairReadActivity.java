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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.RepairComment;
import org.androidtown.dormitory.display.RepairMainDisplay;
import org.androidtown.dormitory.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RepairReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mRepairDatabase;
    private DatabaseReference mRepairWriterDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mRepairCommentDatabase;
    private DatabaseReference mUserDatabase;
    private RepairMainDisplay text;

    private TextView mRepairReadTitle;
    private TextView mRepairReadOpen;
    private TextView mRepairReadNickname;
    private TextView mRepairReadDate;
    private TextView mRepairReadContents;
    private EditText mRepairReadComments;
    private Button mRepairReadSubmitBtn;
    private Spinner mRepairReadSpinner;

    private User writeUser;

    private ImageView mRepairReadImage;

    private RecyclerView mRepairCommentList;

    private LinearLayout mReadTitleLayout;

    private ProgressDialog mRepairCommentProgress;
    private ProgressDialog mRepairDeleteProgress;
    private ProgressDialog mRepairLoadDialog;

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
        setContentView(R.layout.activity_repair_read);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        mRepairLoadDialog = new ProgressDialog(this);
        mRepairLoadDialog.setTitle("?????????");
        mRepairLoadDialog.setMessage("????????? ??????????????????.");
        mRepairLoadDialog.show();


        mToolbar = findViewById(R.id.repair_read_appBar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back);
        mTitle.setText("?????? ??????");
        mTitle.setTextColor(Color.BLACK);

        mRepairCommentList = (RecyclerView) findViewById(R.id.repair_write_comments_list);
        mRepairCommentList.setNestedScrollingEnabled(false);
        //mRepairCommentList.setHasFixedSize(true);
        mRepairCommentList.setLayoutManager(new LinearLayoutManager(this));

        mRepairReadComments = (EditText) findViewById(R.id.repair_read_comments);
        mRepairReadSubmitBtn = (Button) findViewById(R.id.repair_read_submit_btn);

        mReadTitleLayout = (LinearLayout) findViewById(R.id.repair_read_title_layout);

        mRepairReadImage = (ImageView) findViewById(R.id.repair_read_image_view);

        mRepairReadTitle = (TextView) findViewById(R.id.repair_read_title);
        mRepairReadContents = (TextView) findViewById(R.id.repair_read_contents);
        mRepairReadOpen = findViewById(R.id.repair_read_open);
        mRepairReadNickname = findViewById(R.id.repair_read_nickname);
        mRepairReadDate = findViewById(R.id.repair_read_date);
        mRepairReadSpinner = (Spinner) findViewById(R.id.repair_read_spinner);

        //????????? uid, ???id
        user_id = getIntent().getStringExtra("user_id");
        text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

        mRepairDatabase = FirebaseDatabase.getInstance().getReference().child("Repair").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);



        //??????????????? ??????
        ValueEventListener currentUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                nickname = user.getNickname();
                mClass = user.getmClass();

                if((mClass.equals("?????????") )){
                    mRepairReadSpinner.setEnabled(true);
                }else{
                    mRepairReadSpinner.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(currentUserListener);

        //?????? ????????????
        mRepairReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mRepairReadComments.getText().toString();
                commentsUpdate(comments, current_user_id, text_id);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mRepairReadComments.getApplicationWindowToken(), 0);
                onStart();
            }
        });


        //????????? ????????????
        mRepairWriterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {

                    //????????? ???????????? ??????
                    ValueEventListener writeUserListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            writer = dataSnapshot.getValue(User.class);
                            String writerNickname = writer.getNickname();
                            mRepairReadNickname.setText(writerNickname);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    mRepairWriterDatabase.addValueEventListener(writeUserListener);
                    //????????? ???????????? ???


                    text = dataSnapshot.getValue(RepairMainDisplay.class);
                    mRepairReadTitle.setText(text.getTitle());
                    mRepairReadOpen.setText(text.getOpen());
                    if (text.getOpen().equals("??????")){
                        mRepairReadOpen.setBackgroundColor(Color.parseColor("#1276d6"));
                    } else if (text.getOpen().equals("?????????")){
                        mRepairReadOpen.setBackgroundColor(Color.parseColor("#ff5733"));
                    }
                    mRepairReadDate.setText(text.getTime());
                    mRepairReadContents.setText(text.getContents());
                    imageUri = text.getImageUrl();

                    //image ????????????
                    if(!imageUri.equals("")){
                        Picasso.get().load(imageUri).into(mRepairReadImage, new Callback(){
                            @Override
                            public void onSuccess() {
                                mRepairLoadDialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                mRepairLoadDialog.dismiss();
                                Toast.makeText(RepairReadActivity.this,"????????? ????????? ??? ????????????.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mRepairLoadDialog.dismiss();
                    }

                    final String mStatus = text.getStatus().toString();
                    switch (mStatus) {
                        case "?????????":
                            mRepairReadSpinner.setBackgroundColor(Color.parseColor("#fafafa"));
                            break;
                        case "?????????":
                            mRepairReadSpinner.setBackgroundColor(Color.parseColor("#ffec8b"));
                            break;
                        case "??????":
                            mRepairReadSpinner.setBackgroundColor(Color.parseColor("#b4eeb4"));
                            break;
                    }


                    ArrayAdapter<CharSequence> repairAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.repair_array, R.layout.repair_spinner_item);
                    repairAdapter.setDropDownViewResource(R.layout.repair_spinner_item);
                    mRepairReadSpinner.setAdapter(repairAdapter);
                    if (!mStatus.equals(null)) {
                        int spinnerPosition = repairAdapter.getPosition(mStatus);
                        mRepairReadSpinner.setSelection(spinnerPosition);
                    }

                    // ...
                    mRepairReadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String mStatus_update = mStatus;
                            switch (position) {
                                case 0:
                                    mStatus_update = "?????????";

                                    break;
                                case 1:
                                    mStatus_update = "?????????";

                                    break;
                                case 2:
                                    mStatus_update = "??????";
                                    break;
                            }
                            updateStatus(mStatus_update);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(RepairReadActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        mRepairDatabase.addValueEventListener(userListener);

        //????????? ??????
        mRepairReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("")){
                    Intent intent = new Intent(RepairReadActivity.this, ImageZoomActivity.class);
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

        Query repairDatabaseQuery;
        repairDatabaseQuery = FirebaseDatabase.getInstance().getReference().child("RepairComments").child(text_id).orderByChild("orderTime");

        FirebaseRecyclerAdapter<RepairComment, RepairCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<RepairComment, RepairCommentViewHolder>(
                RepairComment.class,
                R.layout.repair_comments_layout,
                RepairCommentViewHolder.class,
                repairDatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(final RepairCommentViewHolder viewHolder, RepairComment model, int position) {

                System.out.println("wow   " + model.getUid());
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
        mRepairCommentList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repair_read_bar, menu);
        if (!current_user_id.equals(user_id)){
            menu.findItem(R.id.action_repair_read_delete).setVisible(false);
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
            case R.id.action_repair_read_delete:
                deleteRepair();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRepair() {
        final AlertDialog alertDialog = new AlertDialog.Builder(RepairReadActivity.this)
                .setTitle("??????")
                .setMessage("????????? ?????? ?????????????????????????")
                .setPositiveButton("???", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRepairDeleteProgress = new ProgressDialog(RepairReadActivity.this);
                        mRepairDeleteProgress.setTitle("?????????");
                        mRepairDeleteProgress.setMessage("????????? ????????? ?????????.");
                        mRepairDeleteProgress.setCanceledOnTouchOutside(false);
                        mRepairDeleteProgress.show();
                        mRepairDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mRepairDeleteProgress.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    mRepairDeleteProgress.dismiss();
                                    Toast.makeText(RepairReadActivity.this, "???????????? ???????????????. ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                })
                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
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
        mRepairCommentProgress = new ProgressDialog(this);
        mRepairCommentProgress.setTitle("?????????");
        mRepairCommentProgress.setMessage("????????? ??????????????????.");
        mRepairCommentProgress.show();

        final HashMap<String, Object> commentMap = new HashMap<String, Object>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("text",comments);
        commentMap.put("uid",myUid);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("orderTime", Double.valueOf((System.currentTimeMillis())));
        mRepairCommentDatabase = FirebaseDatabase.getInstance().getReference().child("RepairComments").child(textId).push();
        final String commentTextKey = mRepairCommentDatabase.getKey();

        mRepairCommentDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mRepairReadComments.setText("");
                    mRepairCommentProgress.dismiss();
                }else{
                    mRepairCommentProgress.dismiss();
                    Toast.makeText(RepairReadActivity.this,"????????? ???????????? ???????????????.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void updateStatus(final String status) {
        mRepairDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }else{
                    Toast.makeText(RepairReadActivity.this,"????????? ???????????? ???????????????.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class RepairCommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private TextView repairCommentContent;
        private TextView repairCommentDate;
        private TextView repairCommentNickname;

        public RepairCommentViewHolder(View itemView){
            super(itemView);
            mView = itemView;

        }
        public void setContent(String content){
            repairCommentContent = (TextView) mView.findViewById(R.id.repair_comments_content);
            repairCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            repairCommentNickname = (TextView) mView.findViewById(R.id.repair_comments_nickname);
            repairCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            repairCommentDate = (TextView) mView.findViewById(R.id.repair_comments_date);
            repairCommentDate.setText(date);
        }

    }
}
