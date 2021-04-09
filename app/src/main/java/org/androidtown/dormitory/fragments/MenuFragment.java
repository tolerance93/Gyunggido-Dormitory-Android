package org.androidtown.dormitory.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.androidtown.dormitory.R;
import org.androidtown.dormitory.activities.LoginActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private Toolbar mToolbar;
    private TextView mTitle;

    private TextView mon1;
    private TextView mon2;
    private TextView mon3;
    private TextView mon4;
    private TextView mon5;
    private TextView tue1;
    private TextView tue2;
    private TextView tue3;
    private TextView tue4;
    private TextView tue5;
    private TextView wed1;
    private TextView wed2;
    private TextView wed3;
    private TextView wed4;
    private TextView wed5;
    private TextView thu1;
    private TextView thu2;
    private TextView thu3;
    private TextView thu4;
    private TextView thu5;
    private TextView fri1;
    private TextView fri2;
    private TextView fri3;
    private TextView fri4;
    private TextView fri5;
    private TextView sat1;
    private TextView sat2;
    private TextView sat3;
    private TextView sat4;
    private TextView sat5;
    private TextView sun1;
    private TextView sun2;
    private TextView sun3;
    private TextView sun4;
    private TextView sun5;

    private ProgressDialog mMenuDialog;

    private TableRow linMon;
    private TableRow linTue;
    private TableRow linWed;
    private TableRow linThr;
    private TableRow linFri;
    private TableRow linSat;
    private TableRow linSun;


    private ProgressDialog mLogoutDialog;
    String menu[];


    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        setHasOptionsMenu(true);

        mMenuDialog = new ProgressDialog(getActivity());

        mMenuDialog.setTitle("불러오는 중");
        mMenuDialog.setMessage("잠시만 기다려주세요.");
        mMenuDialog.show();

        mToolbar = rootView.findViewById(R.id.menu_toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("식단표");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        linMon = (TableRow)rootView.findViewById(R.id.linMon);
        linTue = (TableRow)rootView.findViewById(R.id.linTue);
        linWed = (TableRow)rootView.findViewById(R.id.linWed);
        linThr = (TableRow)rootView.findViewById(R.id.linThr);
        linFri = (TableRow)rootView.findViewById(R.id.linFri);
        linSat = (TableRow)rootView.findViewById(R.id.linSat);
        linSun = (TableRow)rootView.findViewById(R.id.linSun);

        mon1 = (TextView)rootView.findViewById(R.id.mon1);
        mon2 = (TextView)rootView.findViewById(R.id.mon2);
        mon3 = (TextView)rootView.findViewById(R.id.mon3);
        mon4 = (TextView)rootView.findViewById(R.id.mon4);
        mon5 = (TextView)rootView.findViewById(R.id.mon5);
        tue1 = (TextView)rootView.findViewById(R.id.tue1);
        tue2 = (TextView)rootView.findViewById(R.id.tue2);
        tue3 = (TextView)rootView.findViewById(R.id.tue3);
        tue4 = (TextView)rootView.findViewById(R.id.tue4);
        tue5 = (TextView)rootView.findViewById(R.id.tue5);
        wed1 = (TextView)rootView.findViewById(R.id.wed1);
        wed2 = (TextView)rootView.findViewById(R.id.wed2);
        wed3 = (TextView)rootView.findViewById(R.id.wed3);
        wed4 = (TextView)rootView.findViewById(R.id.wed4);
        wed5 = (TextView)rootView.findViewById(R.id.wed5);
        thu1 = (TextView)rootView.findViewById(R.id.thu1);
        thu2 = (TextView)rootView.findViewById(R.id.thu2);
        thu3 = (TextView)rootView.findViewById(R.id.thu3);
        thu4 = (TextView)rootView.findViewById(R.id.thu4);
        thu5 = (TextView)rootView.findViewById(R.id.thu5);
        fri1 = (TextView)rootView.findViewById(R.id.fri1);
        fri2 = (TextView)rootView.findViewById(R.id.fri2);
        fri3 = (TextView)rootView.findViewById(R.id.fri3);
        fri4 = (TextView)rootView.findViewById(R.id.fri4);
        fri5 = (TextView)rootView.findViewById(R.id.fri5);
        sat1 = (TextView)rootView.findViewById(R.id.sat1);
        sat2 = (TextView)rootView.findViewById(R.id.sat2);
        sat3 = (TextView)rootView.findViewById(R.id.sat3);
        sat4 = (TextView)rootView.findViewById(R.id.sat4);
        sat5 = (TextView)rootView.findViewById(R.id.sat5);
        sun1 = (TextView)rootView.findViewById(R.id.sun1);
        sun2 = (TextView)rootView.findViewById(R.id.sun2);
        sun3 = (TextView)rootView.findViewById(R.id.sun3);
        sun4 = (TextView)rootView.findViewById(R.id.sun4);
        sun5 = (TextView)rootView.findViewById(R.id.sun5);

        setDayColor();
        setBackColor();

        getWebsite();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bar, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    private void logOut() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        sendToStart();
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
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ffffff"));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ffffff"));
            }
        });
        alertDialog.show();
    }

    private void sendToStart() {
        Intent startIntent = new Intent(getActivity(),LoginActivity.class);
        startActivity(startIntent);
        getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        getActivity().finish();
    }

    private void setDayColor(){
        sun1.setBackgroundColor(Color.parseColor("#6dd5ec"));
        mon1.setBackgroundColor(Color.parseColor("#83cbe9"));
        tue1.setBackgroundColor(Color.parseColor("#a2c7ea"));
        wed1.setBackgroundColor(Color.parseColor("#b5c4f8"));
        thu1.setBackgroundColor(Color.parseColor("#cdc1f2"));
        fri1.setBackgroundColor(Color.parseColor("#ddc0e5"));
        sat1.setBackgroundColor(Color.parseColor("#e9bcdc"));
    }

    private void getWebsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("http://ggjh.co.kr/0206/cafeteria/menu/").get();
                    Elements links = doc.select("td");
                    int arridx = 0;
                    menu = new String [100];
                    for (Element link : links) {
                        builder.append("\n\n").append(link.text());
                        menu[arridx] = link.text().toString();
                        arridx = arridx+1;
                    }
                } catch (IOException e) {
                    System.out.println("오류:" + e.toString());
                    builder.append("오류 : ").append(e.getMessage()).append("\n");
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            sun1.setText( getMenu(menu[0]));
                            sun2.setText( monthAndDateToDate(getMenu(menu[1])));
                            sun3.setText( getMenu(menu[2]));
                            sun4.setText( getMenu(menu[3]));
                            sun5.setText( getMenu(menu[4]));
                            mon1.setText( getMenu(menu[5]));
                            mon2.setText( monthAndDateToDate(getMenu(menu[6])));
                            mon3.setText( getMenu(menu[7]));
                            mon4.setText( getMenu(menu[8]));
                            mon5.setText( getMenu(menu[9]));
                            tue1.setText( getMenu(menu[10]));
                            tue2.setText( monthAndDateToDate(getMenu(menu[11])));
                            tue3.setText( getMenu(menu[12]));
                            tue4.setText( getMenu(menu[13]));
                            tue5.setText( getMenu(menu[14]));
                            wed1.setText( getMenu(menu[15]));
                            wed2.setText( monthAndDateToDate(getMenu(menu[16])));
                            wed3.setText( getMenu(menu[17]));
                            wed4.setText( getMenu(menu[18]));
                            wed5.setText( getMenu(menu[19]));
                            thu1.setText( getMenu(menu[20]));
                            thu2.setText( monthAndDateToDate(getMenu(menu[21])));
                            thu3.setText( getMenu(menu[22]));
                            thu4.setText( getMenu(menu[23]));
                            thu5.setText( getMenu(menu[24]));
                            fri1.setText( getMenu(menu[25]));
                            fri2.setText( monthAndDateToDate(getMenu(menu[26])));
                            fri3.setText( getMenu(menu[27]));
                            fri4.setText( getMenu(menu[28]));
                            fri5.setText( getMenu(menu[29]));
                            sat1.setText( getMenu(menu[30]));
                            sat2.setText( monthAndDateToDate(getMenu(menu[31])));
                            sat3.setText( getMenu(menu[32]));
                            sat4.setText( getMenu(menu[33]));
                            sat5.setText( getMenu(menu[34]));


                            mMenuDialog.dismiss();
                        } catch(Exception e){
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            mMenuDialog.dismiss();
                        }

                    }
                });
            }
        }).start();
    }

    private String monthAndDateToDate(String mAndD){
        String[] splited = mAndD.split("\\s+");
        String res = new String("");
        if (splited.length > 0) {
            res = splited[1].substring(0, splited[1].length() - 1);
        } else {
            res = splited[0].substring(0, splited[0].length() - 1);
        }
        return res;
    }

    private String getMenu(String menu){
        try{
            String[] splitmeu = menu.split("\\s");
            String printmenu = "";
            for (String me : splitmeu) {
                printmenu = printmenu + me + "\n";
            }
            return printmenu;
        } catch(Exception e){
            return "불러올 정보가 없습니다.";

        }
    }

    private void setBackColor() {

        Calendar cal = Calendar.getInstance();
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        System.out.println("nWeek" + nWeek);

        try{
            switch(nWeek){
                case 1:
                    sun1.setBackgroundColor(Color.parseColor("#6dd5ec"));
                    sun2.setBackgroundColor(Color.parseColor("#6dd5ec"));
                    sun3.setBackgroundColor(Color.parseColor("#6dd5ec"));
                    sun4.setBackgroundColor(Color.parseColor("#6dd5ec"));
                    sun5.setBackgroundColor(Color.parseColor("#6dd5ec"));
                    break;
                case 2:
                    mon1.setBackgroundColor(Color.parseColor("#83cbe9"));
                    mon2.setBackgroundColor(Color.parseColor("#83cbe9"));
                    mon3.setBackgroundColor(Color.parseColor("#83cbe9"));
                    mon4.setBackgroundColor(Color.parseColor("#83cbe9"));
                    mon5.setBackgroundColor(Color.parseColor("#83cbe9"));
                    break;
                case 3:
                    tue1.setBackgroundColor(Color.parseColor("#a2c7ea"));
                    tue2.setBackgroundColor(Color.parseColor("#a2c7ea"));
                    tue3.setBackgroundColor(Color.parseColor("#a2c7ea"));
                    tue4.setBackgroundColor(Color.parseColor("#a2c7ea"));
                    tue5.setBackgroundColor(Color.parseColor("#a2c7ea"));
                    break;
                case 4:
                    wed1.setBackgroundColor(Color.parseColor("#b5c4f8"));
                    wed2.setBackgroundColor(Color.parseColor("#b5c4f8"));
                    wed3.setBackgroundColor(Color.parseColor("#b5c4f8"));
                    wed4.setBackgroundColor(Color.parseColor("#b5c4f8"));
                    wed5.setBackgroundColor(Color.parseColor("#b5c4f8"));
                    break;
                case 5:
                    thu1.setBackgroundColor(Color.parseColor("#cdc1f2"));
                    thu2.setBackgroundColor(Color.parseColor("#cdc1f2"));
                    thu3.setBackgroundColor(Color.parseColor("#cdc1f2"));
                    thu4.setBackgroundColor(Color.parseColor("#cdc1f2"));
                    thu5.setBackgroundColor(Color.parseColor("#cdc1f2"));
                    break;
                case 6:
                    fri1.setBackgroundColor(Color.parseColor("#ddc0e5"));
                    fri2.setBackgroundColor(Color.parseColor("#ddc0e5"));
                    fri3.setBackgroundColor(Color.parseColor("#ddc0e5"));
                    fri4.setBackgroundColor(Color.parseColor("#ddc0e5"));
                    fri5.setBackgroundColor(Color.parseColor("#ddc0e5"));
                    break;
                case 7:
                    sat1.setBackgroundColor(Color.parseColor("#e9bcdc"));
                    sat2.setBackgroundColor(Color.parseColor("#e9bcdc"));
                    sat3.setBackgroundColor(Color.parseColor("#e9bcdc"));
                    sat4.setBackgroundColor(Color.parseColor("#e9bcdc"));
                    sat5.setBackgroundColor(Color.parseColor("#e9bcdc"));
                    break;
                default:
                    break;
            }
        } catch(Exception e){

        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
