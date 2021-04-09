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

import org.androidtown.dormitory.display.PromotionMainDisplay;
import org.androidtown.dormitory.R;
import org.androidtown.dormitory.model.User;
import org.androidtown.dormitory.activities.PromotionReadActivity;
import org.androidtown.dormitory.activities.PromotionWriteActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class PromotionFragment extends Fragment {

    private Toolbar mToolbar;
    private TextView mTitle;
    private BoardFragment boardFragment;

    private RecyclerView mPromotionMainList;
    private LinearLayoutManager mLayoutManager;

    private DatabaseReference mPromotionDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabase;

    private ProgressDialog mPromotionMainProgress;


    private User user;
    private User writeUser;
    private String mClass="";
    private String mNickname="";
    private boolean onDataChangedCalled = false;



    public String mStatus;



    public PromotionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_promotion, container, false);
        setHasOptionsMenu(true);

        boardFragment = new BoardFragment();

        mToolbar = rootView.findViewById(R.id.promotion_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.google_back_white);
        mTitle.setText("홍보 게시판");

        mPromotionMainProgress = new ProgressDialog(getActivity());
        mPromotionMainProgress.setTitle("로딩중");
        mPromotionMainProgress.setMessage("잠시만 기다려주세요.");
        mPromotionMainProgress.show();

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
                mPromotionDatabase = FirebaseDatabase.getInstance().getReference().child("Promotion");
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

        mPromotionMainList = (RecyclerView) rootView.findViewById(R.id.promotion_list);
        mPromotionMainList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPromotionMainList.setLayoutManager(mLayoutManager);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mPromotionMainProgress.isShowing()){
            mPromotionMainProgress = new ProgressDialog(getActivity());
            mPromotionMainProgress.setTitle("로딩중");
            mPromotionMainProgress.setMessage("잠시만 기다려주세요.");
            mPromotionMainProgress.show();
        }

        if(onDataChangedCalled){
            Query promotionDatabaseQuery;
            promotionDatabaseQuery = mPromotionDatabase.orderByChild("orderTime");

            FirebaseRecyclerAdapter<PromotionMainDisplay,PromotionViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PromotionMainDisplay, PromotionViewHolder>(
                    PromotionMainDisplay.class,
                    R.layout.promotion_single_layout,
                    PromotionViewHolder.class,
                    promotionDatabaseQuery
            ) {
                @Override
                protected void populateViewHolder(final PromotionViewHolder viewHolder, PromotionMainDisplay model, int position) {

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
                            Intent promotionIntent = new Intent(getActivity(),PromotionReadActivity.class);
                            promotionIntent.putExtra("user_id", uid);
                            promotionIntent.putExtra("text_id", text_id);
                            startActivity(promotionIntent);
                            getActivity().overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        }
                    });

                }

                @Override
                public void onDataChanged() {
                    if (mPromotionMainProgress != null && mPromotionMainProgress.isShowing()) {
                        mPromotionMainProgress.dismiss();
                    }
                }
            };
            mPromotionMainList.setAdapter(firebaseRecyclerAdapter);

        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.promotion_bar, menu);
        final String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                user.setUid(uid);
                if ( user.getmClass().equals("미확인")){
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
                Intent i = new Intent(getActivity(), PromotionWriteActivity.class);
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

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView promotionTitleView;
        private TextView promotionNicknameView;
        private TextView promotionDateView;
        private LinearLayout mPromotionLayout;



        public PromotionViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setInvisible(){

            mView.setVisibility(View.GONE);
            mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        public void setTitle(String title) {

            promotionTitleView = (TextView) mView.findViewById(R.id.promotion_single_title);
            promotionTitleView.setText(title);

        }

        public void setNickname(String nickname) {

            promotionNicknameView = (TextView) mView.findViewById(R.id.promotion_single_nickname);
            promotionNicknameView.setText(nickname);

        }
        public void setDate(String date) {

            promotionDateView = (TextView) mView.findViewById  (R.id.promotion_single_date);
            promotionDateView.setText(date);

        }

    }

}
