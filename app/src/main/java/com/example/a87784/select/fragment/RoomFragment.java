package com.example.a87784.select.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a87784.select.R;
import com.example.a87784.select.bean.Room;
import com.example.a87784.select.config.Constans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by 87784 on 2017/10/13.
 */


public class RoomFragment extends Fragment {


    private static final String TAG = "RoomFragment";


    private View view;

    private GridView gridView;
    private TextView showDetail;
    private Button determine;
    private Button cancel;

    //room的楼层和编号
    private String roomId;
    //该书库的座位类型表(80)
    private int[] seatTypeLists;
    //座位图表
    private int[] seatImgLists;
    //座位编号信息
    private String[] seatTipLists;

    private ArrayList<Map<String,Object>> seatItemList;
    private SimpleAdapter seatItemAdapter;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获得要显示的书库信息
        seatImgLists = (int[]) getArguments().get("seatImgLists");
        seatTipLists = (String[])getArguments().get("seatTipLists");


    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.room_fragment,container,false);

        gridView = (GridView)view.findViewById(R.id.gridSeatView);
        showDetail = (TextView)view.findViewById(R.id.showDetail);
        determine = (Button)view.findViewById(R.id.determine);
        cancel = (Button)view.findViewById(R.id.cancel);

        //按钮监听
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDetermineDialog();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popCancelDialog();
            }
        });



        seatItemList = new ArrayList<>();
        seatItemAdapter = new SimpleAdapter(getContext(),getSeatDateList(seatImgLists,seatTipLists),R.layout.seat_item , new String[]{"image","text"},new int[]{R.id.seatView,R.id.seatData});
        gridView.setAdapter(seatItemAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckSeatState(i);
            }
        });

        return view;
    }


    public ArrayList<Map<String,Object>> getSeatDateList(int[] seatImgLists,String[] seatTipLists){
        Map<String,Object> map ;
        for(int i=0;i<80;i++){
            map = new HashMap<>();
            map.put("image",seatImgLists[i]);
            map.put("text",seatTipLists[i]);
            seatItemList.add(map);
        }
        return seatItemList;
    }


    public void popDetermineDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("是否确定选座？");
        dialog.setTitle("提示");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                showDetail.setText("正在使用座位...");
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.create().show();
    }




    public void popCancelDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("是否取消选座？");
        dialog.setTitle("提示");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //取消的逻辑  座位图改变
                //balabala

                dialogInterface.dismiss();
                showDetail.setText("您还未选择座位 (๑>\u0602<๑）");
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.create().show();
    }

    /**
     * 检查座位状态
     * @param clickItem
     */
    public void CheckSeatState(int clickItem){
        Log.d(TAG, "CheckSeatState: -------------------clickItem " + clickItem);
        int clickSeatImg = seatImgLists[clickItem];
        if(clickSeatImg == R.drawable.seat_noselected){
            Toast.makeText(getContext(),"选座：" + clickItem , Toast.LENGTH_SHORT).show();
        }else if(clickSeatImg == R.drawable.seat_selected){
            Toast.makeText(getContext(),"抱歉，此座已有人" , Toast.LENGTH_SHORT).show();
        }else if(clickSeatImg == R.drawable.seat_selcting){
            Toast.makeText(getContext(),"取消选座" , Toast.LENGTH_SHORT).show();
        }
    }







}
