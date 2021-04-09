package org.androidtown.dormitory.fragments;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.dormitory.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusFragment extends Fragment {

    private Toolbar mToolbar;
    private TextView mTitle;

    private TextView gFirstTime;
    private TextView sFirstTime;
    private TextView gSecondTime;
    private TextView sSecondTime;
    private TextView gFirstTime2;
    private TextView sFirstTime2;
    private TextView gSecondTime2;
    private TextView sSecondTime2;
    private TextView gFirstTime3;
    private TextView sFirstTime3;
    private TextView gSecondTime3;
    private TextView sSecondTime3;

    private ProgressDialog mBusDialog;


    String dobong01_1[];
    String dobong01_2[];
    String dobong02_1[];
    String dobong02_2[];
    String dobong03_1[];
    String dobong03_2[];


    public BusFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bus_bar, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_one:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);

        setHasOptionsMenu(true);

        mBusDialog = new ProgressDialog(getActivity());

        mBusDialog.setTitle("불러오는 중");
        mBusDialog.setMessage("잠시만 기다려주세요.");
        mBusDialog.show();

//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.abs_layout);

        mToolbar = rootView.findViewById(R.id.bus_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("버스");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }
        gFirstTime = (TextView) rootView.findViewById(R.id.gFirstTime);
        gSecondTime = (TextView) rootView.findViewById(R.id.gSecondTime);
        sFirstTime = (TextView) rootView.findViewById(R.id.sFirstTime);
        sSecondTime = (TextView) rootView.findViewById(R.id.sSecondTime);

        gFirstTime2 = (TextView) rootView.findViewById(R.id.gFirstTime2);
        gSecondTime2 = (TextView) rootView.findViewById(R.id.gSecondTime2);
        sFirstTime2 = (TextView) rootView.findViewById(R.id.sFirstTime2);
        sSecondTime2 = (TextView) rootView.findViewById(R.id.sSecondTime2);

        gFirstTime3 = (TextView) rootView.findViewById(R.id.gFirstTime3);
        gSecondTime3 = (TextView) rootView.findViewById(R.id.gSecondTime3);
        sFirstTime3 = (TextView) rootView.findViewById(R.id.sFirstTime3);
        sSecondTime3 = (TextView) rootView.findViewById(R.id.sSecondTime3);

        getXmlsite();

        return rootView;
    }

    private void getXmlsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder1_1 = new StringBuilder();
                final StringBuilder builder1_2 = new StringBuilder();

                final StringBuilder builder2_1 = new StringBuilder();
                final StringBuilder builder2_2 = new StringBuilder();

                final StringBuilder builder3_1 = new StringBuilder();
                final StringBuilder builder3_2 = new StringBuilder();

                final String apiKey = "1%2BBzaBAcSlgVGh4IdbZiXNcXgTGtiE6tzQ8lEh1ATXxz%2B6iWZGb2N4VVNdbaKUlnuQT8ltI3McIEClf0TQwayA%3D%3D";

                try {
                    Document doc = Jsoup.connect("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?ServiceKey="+ apiKey + "&busRouteId=109900001").get();
                    Document doc2 = Jsoup.connect("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?ServiceKey="+ apiKey + "&busRouteId=108900010").get();
                    Document doc3 = Jsoup.connect("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?ServiceKey="+ apiKey + "&busRouteId=109900007").get();

                    Elements links1_1 = doc.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg1");
                    Elements links1_2 = doc.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg2");

                    Elements links2_1 = doc2.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg1");
                    Elements links2_2 = doc2.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg2");

                    Elements links3_1 = doc3.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg1");
                    Elements links3_2 = doc3.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg2");

                    int arridx1_1 = 0;
                    int arridx1_2 = 0;

                    int arridx2_1 = 0;
                    int arridx2_2 = 0;

                    int arridx3_1 = 0;
                    int arridx3_2 = 0;

                    dobong01_1 = new String [1000];
                    dobong01_2 = new String [1000];

                    dobong02_1 = new String [1000];
                    dobong02_2 = new String [1000];

                    dobong03_1 = new String [1000];
                    dobong03_2 = new String [1000];




                    for (Element link : links1_1) {

                        builder1_1.append("\n\n").append(link.text());
                        dobong01_1[arridx1_1] = link.text().toString();
                        arridx1_1 = arridx1_1+1;

                    }
                    for (Element link2 : links1_2) {

                        builder1_2.append("\n\n").append(link2.text());
                        dobong01_2[arridx1_2] = link2.text().toString();
                        arridx1_2 = arridx1_2+1;

                    }



                    for (Element link : links2_1) {

                        builder2_1.append("\n\n").append(link.text());
                        dobong02_1[arridx2_1] = link.text().toString();
                        arridx2_1 = arridx2_1+1;

                    }
                    for (Element link2 : links2_2) {

                        builder2_2.append("\n\n").append(link2.text());
                        dobong02_2[arridx2_2] = link2.text().toString();
                        arridx2_2 = arridx2_2+1;

                    }



                    for (Element link : links3_1) {

                        builder3_1.append("\n\n").append(link.text());
                        dobong03_1[arridx3_1] = link.text().toString();
                        arridx3_1 = arridx3_1+1;

                    }
                    for (Element link2 : links3_2) {

                        builder3_2.append("\n\n").append(link2.text());
                        dobong03_2[arridx3_2] = link2.text().toString();
                        arridx3_2 = arridx3_2+1;

                    }
                } catch (IOException e) {
                    builder1_1.append("오류 : ").append(e.getMessage()).append("\n");
                    builder1_2.append("오류 : ").append(e.getMessage()).append("\n");
                    builder2_1.append("오류 : ").append(e.getMessage()).append("\n");
                    builder2_2.append("오류 : ").append(e.getMessage()).append("\n");
                    builder3_1.append("오류 : ").append(e.getMessage()).append("\n");
                    builder3_2.append("오류 : ").append(e.getMessage()).append("\n");
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            gFirstTime.setText(dobong01_1[7]);
                            gSecondTime.setText(dobong01_2[7]);
                            sFirstTime.setText(dobong01_1[20]);
                            sSecondTime.setText(dobong01_2[20]);

                            gFirstTime2.setText(dobong02_1[8]);
                            gSecondTime2.setText(dobong02_2[8]);
                            sFirstTime2.setText(dobong02_1[15]);
                            sSecondTime2.setText(dobong02_2[15]);

                            gFirstTime3.setText(dobong03_1[4]);
                            gSecondTime3.setText(dobong03_2[4]);
                            sFirstTime3.setText(dobong03_1[10]);
                            sSecondTime3.setText(dobong03_2[10]);

                            mBusDialog.dismiss();
                        }catch(Exception e){
                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                            mBusDialog.dismiss();

                        }
                    }
                });
            }
        }).start();

    }

}
