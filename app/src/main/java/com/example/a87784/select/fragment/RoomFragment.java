package com.example.a87784.select.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.a87784.select.R;
import com.example.a87784.select.bean.RoomType;
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

    private static final int GET_SEATTYPELISTS_OK = 0;          //成功获得座位类型表

    private static final int NO_PEOPLE = 0;         //座位无人
    private static final int HAVE_PEOPLE = 1;       //座位有人
    private static final int SELECTED = 2;          //座位被选中

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



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_SEATTYPELISTS_OK:
                    seatTypeLists = (int[]) msg.obj;
                    seatImgLists = getSeatImgLists(seatTypeLists);

                    seatItemAdapter = new SimpleAdapter(getContext(),getSeatDateList(),R.layout.seat_item , new String[]{"image","text"},new int[]{R.id.seatView,R.id.seatData});
                    gridView.setAdapter(seatItemAdapter);
                    break;

            }
        }
    };




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获得要显示的视图id
        roomId = matchRoomTypeId((String)getArguments().get("roomId"));


        seatItemList = new ArrayList<>();

        //设置座位坐标
        seatTipLists = getSeatTipLists();

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



        getRoomSeatTypeLists(roomId);



        return view;
    }


    public ArrayList<Map<String,Object>> getSeatDateList(){
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
     *
     * @param room 书库的楼层和编号结合的字符串
     * @return
     */
    public String matchRoomTypeId(String room){
        switch (room){
            case "11":return Constans.ONE_ONE_ROOM_OBJECTID;
            case "12":return Constans.ONE_TWO_ROOM_OBJECTID;
            case "13":return Constans.ONE_THREE_ROOM_OBJECTID;
            case "14":return Constans.ONE_FOUR_ROOM_OBJECTID;

            case "21":return Constans.TWO_ONE_ROOM_OBJECTID;
            case "22":return Constans.TWO_TWO_ROOM_OBJECTID;
            case "23":return Constans.TWO_THREE_ROOM_OBJECTID;
            case "24":return Constans.TWO_FOUR_ROOM_OBJECTID;

            case "31":return Constans.THREE_ONE_ROOM_OBJECTID;
            case "32":return Constans.THREE_TWO_ROOM_OBJECTID;
            case "33":return Constans.THREE_THREE_ROOM_OBJECTID;
            case "34":return Constans.THREE_FOUR_ROOM_OBJECTID;

            case "41":return Constans.FOUR_ONE_ROOM_OBJECTID;
            case "42":return Constans.FOUR_TWO_ROOM_OBJECTID;
            case "43":return Constans.FOUR_THREE_ROOM_OBJECTID;
            case "44":return Constans.FOUR_FOUR_ROOM_OBJECTID;

            case "51":return Constans.FIVE_ONE_ROOM_OBJECTID;
            case "52":return Constans.FIVE_TWO_ROOM_OBJECTID;
            case "53":return Constans.FIVE_THREE_ROOM_OBJECTID;
            case "54":return Constans.FIVE_FOUR_ROOM_OBJECTID;
        }
        return null;
    }


    /**
     * 从bmob上获得座位type表
     * @param roomId
     */
    public void getRoomSeatTypeLists(String roomId){
        BmobQuery<RoomType> query = new BmobQuery<>();
        query.getObject(getContext(), roomId, new GetListener<RoomType>() {
            @Override
            public void onSuccess(RoomType roomType) {
                Message message = handler.obtainMessage();
                message.what = GET_SEATTYPELISTS_OK;
                message.obj = roomType.getRoomTypes();
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }


    /**
     * 获得座位图id
     * @param seatType
     * @return
     */
    public int getSeatResId(int seatType){
        switch (seatType){
            case NO_PEOPLE:
                return R.drawable.seat_noselected;
            case HAVE_PEOPLE:
                return R.drawable.seat_selected;
            case SELECTED:
                return R.drawable.seat_selcting;
        }
        return 0;
    }


    /**
     * 获得座位图list
     * @param seatTypeLists
     * @return
     */
    public int[]  getSeatImgLists(int[] seatTypeLists){
        int[] seatImgLists = new int[80];
        for(int i =0;i<80;i++){
            seatImgLists[i] = getSeatResId(seatTypeLists[i]);
        }
        return seatImgLists;
    }


    public String[] getSeatTipLists(){
        int i=0,raw,col;
        for(raw=0;raw<10;raw++){
            for(col=0;col<8;col++,i++){
                seatTipLists[i] = "(" + raw + "," + col + ")";
            }
        }
        return seatTipLists;
    }

}
