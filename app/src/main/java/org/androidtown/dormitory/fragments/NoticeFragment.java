package org.androidtown.dormitory.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidtown.dormitory.display.NoticeMainDisplay;
import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.User;
import org.androidtown.dormitory.activities.NoticeReadActivity;
import org.androidtown.dormitory.activities.NoticeWriteActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {

    private Toolbar mToolbar;
    private TextView mTitle;
    private BoardFragment boardFragment;

    private RecyclerView mNoticeMainList;
    private LinearLayoutManager mLayoutManager;

    private DatabaseReference mNoticeDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabase;

    private ProgressDialog mNoticeMainProgress;


    private User user;
    private User writeUser;
    private String mClass="";
    private String mNickname="";
    private boolean onDataChangedCalled = false;



    public String mStatus;



    public NoticeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notice, container, false);
        setHasOptionsMenu(true);

        boardFragment = new BoardFragment();

        mToolbar = rootView.findViewById(R.id.notice_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back_white);
        mTitle.setText("공지사항");

        mNoticeMainProgress = new ProgressDialog(getActivity());
        mNoticeMainProgress.setTitle("로딩중");
        mNoticeMainProgress.setMessage("잠시만 기다려주세요.");
        mNoticeMainProgress.show();

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                user.setUid(uid);

                onDataChangedCalled = true;
                mNoticeDatabase = FirebaseDatabase.getInstance().getReference().child("Notice");
                onStart();
                //Toast.makeText(getApplicationContext(), String.valueOf(onDataChangedCalled),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getActivity().getApplicationContext(), databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(userListener);

        mNoticeMainList = (RecyclerView) rootView.findViewById(R.id.notice_list);
        mNoticeMainList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mNoticeMainList.setLayoutManager(mLayoutManager);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mNoticeMainProgress.isShowing()){
            mNoticeMainProgress = new ProgressDialog(getActivity());
            mNoticeMainProgress.setTitle("로딩중");
            mNoticeMainProgress.setMessage("잠시만 기다려주세요.");
            mNoticeMainProgress.show();
        }

        if(onDataChangedCalled){
            Query noticeDatabaseQuery;
            noticeDatabaseQuery = mNoticeDatabase.orderByChild("orderTime");

            FirebaseRecyclerAdapter<NoticeMainDisplay,NoticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoticeMainDisplay, NoticeViewHolder>(
                    NoticeMainDisplay.class,
                    R.layout.notice_single_layout,
                    NoticeViewHolder.class,
                    noticeDatabaseQuery
            ) {
                @Override
                protected void populateViewHolder(final NoticeViewHolder viewHolder, NoticeMainDisplay model, int position) {

                    //fetch user start
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
                            Toast.makeText(getActivity().getApplicationContext(), databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    };
                    mUserDatabase.addValueEventListener(userListener);
                    //fetch user end
                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setDate(model.getTime());
                    viewHolder.setNickname("");

                    final String uid = model.getUid();
                    final String text_id = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent noticeIntent = new Intent(getActivity(),NoticeReadActivity.class);
                            noticeIntent.putExtra("user_id", uid);
                            noticeIntent.putExtra("text_id", text_id);
                            startActivity(noticeIntent);
                            getActivity().overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        }
                    });

                }

                @Override
                public void onDataChanged() {
                    if (mNoticeMainProgress != null && mNoticeMainProgress.isShowing()) {
                        mNoticeMainProgress.dismiss();
                    }
                }
            };
            mNoticeMainList.setAdapter(firebaseRecyclerAdapter);

        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notice_bar, menu);
        final String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                user.setUid(uid);
                if (!(user.getmClass().equals("자율회") || user.getmClass().equals("관리자"))){
                    menu.findItem(R.id.write).setVisible(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getActivity().getApplicationContext(), databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(userListener);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setFragment(boardFragment);

                return true;
            case R.id.write:
                Intent i = new Intent(getActivity(), NoticeWriteActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.below_in, R.anim.below_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    setFragment(boardFragment);

                    return true;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.right_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView noticeTitleView;
        private TextView noticeNicknameView;
        private TextView noticeDateView;
        private LinearLayout mNoticeLayout;



        public NoticeViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setInvisible(){

            mView.setVisibility(View.GONE);
            mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        public void setTitle(String title) {

            noticeTitleView = (TextView) mView.findViewById(R.id.notice_single_title);
            noticeTitleView.setText(title);

        }

        public void setNickname(String nickname) {

            noticeNicknameView = (TextView) mView.findViewById(R.id.notice_single_nickname);
            noticeNicknameView.setText(nickname);

        }
        public void setDate(String date) {

            noticeDateView = (TextView) mView.findViewById  (R.id.notice_single_date);
            noticeDateView.setText(date);

        }

    }

}
