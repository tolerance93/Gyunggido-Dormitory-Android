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
import org.androidtown.dormitory.display.RepairMainDisplay;
import org.androidtown.dormitory.model.User;
import org.androidtown.dormitory.activities.RepairReadActivity;
import org.androidtown.dormitory.activities.RepairWriteActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class RepairFragment extends Fragment {

    private Toolbar mToolbar;
    private TextView mTitle;

    private RecyclerView mRepairMainList;
    private LinearLayoutManager mLayoutManager;

    private DatabaseReference mRepairDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabase;

    private ProgressDialog mRepairMainProgress;


    private User user;
    private User writeUser;
    private String mClass="";
    private String mNickname="";
    private boolean onDataChangedCalled = false;
    private boolean isUserSet = false;



    public String mStatus;


    public RepairFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.repair_bar, menu);
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
                Intent i = new Intent(getActivity(), RepairWriteActivity.class);
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
        View rootView = inflater.inflate(R.layout.fragment_repair, container, false);

        setHasOptionsMenu(true);

        mRepairMainProgress = new ProgressDialog(getActivity());
        mRepairMainProgress.setTitle("로딩중");
        mRepairMainProgress.setMessage("잠시만 기다려주세요.");
        mRepairMainProgress.show();


        mToolbar = rootView.findViewById(R.id.repair_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("수리 요청");

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
                mRepairDatabase = FirebaseDatabase.getInstance().getReference().child("Repair");
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

        mRepairMainList = (RecyclerView) rootView.findViewById(R.id.repair_list);
        mRepairMainList.setHasFixedSize(true);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

// And set it to RecyclerView
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRepairMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRepairMainList.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mRepairMainProgress.isShowing()){
            mRepairMainProgress = new ProgressDialog(getActivity());
            mRepairMainProgress.setTitle("로딩중");
            mRepairMainProgress.setMessage("잠시만 기다려주세요.");
            mRepairMainProgress.show();
        }

        if(onDataChangedCalled){
            Query repairDatabaseQuery;
            repairDatabaseQuery = mRepairDatabase.orderByChild("orderTime");

            FirebaseRecyclerAdapter<RepairMainDisplay,RepairViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RepairMainDisplay, RepairViewHolder>(
                    RepairMainDisplay.class,
                    R.layout.repair_single_layout,
                    RepairViewHolder.class,
                    repairDatabaseQuery
            ) {
                @Override
                protected void populateViewHolder(final RepairViewHolder viewHolder, RepairMainDisplay model, int position) {

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
                    if (!user.getmClass().equals("관리자")){
                        if (model.getOpen().equals("비공개") && !model.getUid().equals(user.getUid())){
                            viewHolder.setInvisible();
                        }
                    }


                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setStatus(model.getStatus() + "\n" + model.getOpen());
                    viewHolder.setDate(model.getTime());
                    viewHolder.setNickname("");
                    viewHolder.setBackgroundColor(model.getStatus());

                    final String uid = model.getUid();
                    final String text_id = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent repairIntent = new Intent(getActivity(),RepairReadActivity.class);
                            repairIntent.putExtra("user_id", uid);
                            repairIntent.putExtra("text_id", text_id);
                            startActivity(repairIntent);
                            getActivity().overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        }
                    });

                }

                @Override
                public void onDataChanged() {
                    if (mRepairMainProgress != null && mRepairMainProgress.isShowing()) {
                        mRepairMainProgress.dismiss();
                    }
                }
            };
            mRepairMainList.setAdapter(firebaseRecyclerAdapter);

        }
    }

    public static class RepairViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView repairTitleView;
        private TextView repairStatusView;
        private TextView repairNicknameView;
        private TextView repairDateView;
        private LinearLayout mRepairLayout;
        //private Spinner mRepairMainSpinner;



        public RepairViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setInvisible(){

            mView.setVisibility(View.GONE);
            mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
        public void setTitle(String title) {

            repairTitleView = (TextView) mView.findViewById(R.id.repair_single_title);
            repairTitleView.setText(title);

        }

        public void setStatus(String status){
            repairStatusView = (TextView) mView.findViewById(R.id.repair_single_status);
            repairStatusView.setText(status);


        }
        public void setNickname(String nickname) {

            repairNicknameView = (TextView) mView.findViewById(R.id.repair_single_nickname);
            repairNicknameView.setText(nickname);

        }
        public void setDate(String date) {

            repairDateView = (TextView) mView.findViewById  (R.id.repair_single_date);
            repairDateView.setText(date);

        }

        public void setBackgroundColor(String backgroundColor) {

            mRepairLayout = (LinearLayout) mView.findViewById(R.id.repair_main_single_layout);

            switch(backgroundColor){
                case "미확인":
                    repairStatusView.setBackgroundColor(Color.parseColor("#fafafa"));
                    break;
                case "수리중":
                    repairStatusView.setBackgroundColor(Color.parseColor("#ffec8b"));
                    break;
                case "완료":
                    repairStatusView.setBackgroundColor(Color.parseColor("#b4eeb4"));
                    break;

            }
        }
    }

}
