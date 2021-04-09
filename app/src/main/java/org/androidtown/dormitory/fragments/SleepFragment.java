package org.androidtown.dormitory.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import org.androidtown.dormitory.R;
import org.androidtown.dormitory.display.SleepMainDisplay;
import org.androidtown.dormitory.model.User;
import org.androidtown.dormitory.activities.SleepReadActivity;
import org.androidtown.dormitory.activities.SleepWriteActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SleepFragment extends Fragment {

    private Toolbar mToolbar;
    private TextView mTitle;

    private RecyclerView mSleepMainList;
    private LinearLayoutManager mLayoutManager;

    private DatabaseReference mSleepDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabase;

    private ProgressDialog mSleepMainProgress;


    private User user;
    private User writeUser;
    private String mClass="";
    private String mNickname="";
    private boolean onDataChangedCalled = false;
    private boolean isUserSet = false;



    public String mStatus;


    public SleepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sleep_bar, menu);
        final String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                user.setUid(uid);
                if (user.getmClass().equals("미확인")){
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
            case R.id.write:
                Intent i = new Intent(getActivity(), SleepWriteActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.below_in, R.anim.below_out);
//                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sleep, container, false);

        setHasOptionsMenu(true);

        mSleepMainProgress = new ProgressDialog(getActivity());
        mSleepMainProgress.setTitle("로딩중");
        mSleepMainProgress.setMessage("잠시만 기다려주세요.");
        mSleepMainProgress.show();


        mToolbar = rootView.findViewById(R.id.sleep_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("외박 신청");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                user.setUid(uid);
                isUserSet = true;

                onDataChangedCalled = true;
                mSleepDatabase = FirebaseDatabase.getInstance().getReference().child("Sleep");
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

        mSleepMainList = (RecyclerView) rootView.findViewById(R.id.sleep_list);
        mSleepMainList.setHasFixedSize(true);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

// And set it to RecyclerView
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mSleepMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSleepMainList.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mSleepMainProgress.isShowing()){
            mSleepMainProgress = new ProgressDialog(getActivity());
            mSleepMainProgress.setTitle("로딩중");
            mSleepMainProgress.setMessage("잠시만 기다려주세요.");
            mSleepMainProgress.show();
        }

        if(onDataChangedCalled){
            Query sleepDatabaseQuery;
            sleepDatabaseQuery = mSleepDatabase.orderByChild("orderTime");

            FirebaseRecyclerAdapter<SleepMainDisplay,SleepViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SleepMainDisplay, SleepViewHolder>(
                    SleepMainDisplay.class,
                    R.layout.sleep_single_layout,
                    SleepViewHolder.class,
                    sleepDatabaseQuery
            ) {
                @Override
                protected void populateViewHolder(final SleepViewHolder viewHolder, SleepMainDisplay model, int position) {

                    //fetch user start
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUid());
                    ValueEventListener userListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            writeUser = dataSnapshot.getValue(User.class);
//                            viewHolder.setNickname(writeUser.getNickname());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Toast.makeText(getActivity().getApplicationContext(), databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    };
                    mUserDatabase.addValueEventListener(userListener);
                    //fetch user end
                    if (!user.getmClass().equals("관리자")){
                        if (!model.getUid().equals(user.getUid())){
                            viewHolder.setInvisible();
                        }
                    }


                    viewHolder.setPeriod(model.getFrom() + " ~ " + model.getTo());
                    viewHolder.setStatus(model.getStatus());
                    viewHolder.setDate(model.getTime());
                    viewHolder.setLocation(model.getBuilding() + " " + model.getBuildingNumber() + " " + model.getName());
                    viewHolder.setBackgroundColor(model.getStatus());

                    final String uid = model.getUid();
                    final String text_id = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent sleepIntent = new Intent(getActivity(),SleepReadActivity.class);
                            sleepIntent.putExtra("user_id", uid);
                            sleepIntent.putExtra("text_id", text_id);
                            startActivity(sleepIntent);
                            getActivity().overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        }
                    });

                }

                @Override
                public void onDataChanged() {
                    if (mSleepMainProgress != null && mSleepMainProgress.isShowing()) {
                        mSleepMainProgress.dismiss();
                    }
                }
            };
            mSleepMainList.setAdapter(firebaseRecyclerAdapter);

        }
    }

    public static class SleepViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView sleepPeriodView;
        private TextView sleepStatusView;
        private TextView sleepLocationView;
        private TextView sleepDateView;
        private LinearLayout mSleepLayout;
        //private Spinner mSleepMainSpinner;



        public SleepViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setInvisible(){

            mView.setVisibility(View.GONE);
            mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
        public void setPeriod(String period) {

            sleepPeriodView = (TextView) mView.findViewById(R.id.sleep_single_period);
            sleepPeriodView.setText(period);

        }

        public void setStatus(String status){
            sleepStatusView = (TextView) mView.findViewById(R.id.sleep_single_status);
            sleepStatusView.setText(status);


        }
        public void setLocation(String location) {

            sleepLocationView = (TextView) mView.findViewById(R.id.sleep_single_location);
            sleepLocationView.setText(location);

        }
        public void setDate(String date) {

            sleepDateView = (TextView) mView.findViewById  (R.id.sleep_single_date);
            sleepDateView.setText(date);

        }

        public void setBackgroundColor(String backgroundColor) {

            mSleepLayout = (LinearLayout) mView.findViewById(R.id.sleep_main_single_layout);

            switch(backgroundColor){
                case "미확인":
                    sleepStatusView.setBackgroundColor(Color.parseColor("#fafafa"));
                    break;
                case "거절":
                    sleepStatusView.setBackgroundColor(Color.parseColor("#e96e67"));
                    break;
                case "확인":
                    sleepStatusView.setBackgroundColor(Color.parseColor("#b4eeb4"));
                    break;

            }
        }
    }

}
