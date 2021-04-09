package org.androidtown.dormitory.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.androidtown.dormitory.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BoardFragment extends Fragment {

    private Button noticeButton, promotionButton, freeBoardButton;
    private NoticeFragment noticeFragment;
    private PromotionFragment promotionFragment;
    private FreeBoardFragment freeBoardFragment;

    private android.support.v7.widget.Toolbar mToolbar;
    private TextView mTitle;


    public BoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);

        mToolbar = rootView.findViewById(R.id.board_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("게시판");

        noticeFragment = new NoticeFragment();
        promotionFragment = new PromotionFragment();
        freeBoardFragment = new FreeBoardFragment();

        noticeButton = rootView.findViewById(R.id.board_notice_button);
        promotionButton = rootView.findViewById(R.id.board_promotion_button);
        freeBoardButton = rootView.findViewById(R.id.board_free_button);

        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(noticeFragment);
            }
        });

        promotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(promotionFragment);
            }
        });

        freeBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(freeBoardFragment);
            }
        });


        return rootView;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.left_in,R.anim.left_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

}
