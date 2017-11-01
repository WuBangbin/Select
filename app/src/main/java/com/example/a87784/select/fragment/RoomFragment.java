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
import com.example.a87784.select.bean.User;
import com.example.a87784.select.config.Constans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 87784 on 2017/10/13.
 */


public class RoomFragment extends Fragment {


    private static final String TAG = "RoomFragment";


    private static final int DETERMINE_SEAT = 0;            //确定选座
    private static final int CANCLE_SEAT = 1;               //取消选座


    private View view;

    private GridView gridView;
    private TextView showDetail;
    private Button determine;
    private Button cancel;

    //room对应的Bmob表id
    private String roomObjectId;
    //room的楼层和编号
    private int roomNumber;
    private int floorNumber;
    //座位图表(80)
    private int[] seatImgLists;
    //座位编号信息
    private String[] seatTipLists;

    private ArrayList<Map<String,Object>> seatItemList;
    private SimpleAdapter seatItemAdapter;

    private Integer lastClickItem;      //上一个点击的座位编号
    private Integer nowClickItem;       //正点击的座位编号
    private Integer selectedSeat;           //已选择的座位

    //用户id
    private String userObjectId;






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获得要显示的书库信息
        seatImgLists = (int[]) getArguments().get("seatImgLists");
        seatTipLists = (String[])getArguments().get("seatTipLists");
        roomObjectId = (String)getArguments().get("roomObjectId");
        floorNumber = (int)getArguments().get("floorNumber");
        roomNumber = (int)getArguments().get("roomNumber");
        userObjectId = (String)getArguments().get("userObjectId");

        //设置上次选择的座位
        setSelectedSeat();
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
        seatItemList = new ArrayList<>();
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
                if(checkIsSelectSeat(nowClickItem)){
                    //更新视图
                    seatImgLists[nowClickItem] = R.drawable.seat_selected;
                    seatItemAdapter = new SimpleAdapter(getContext(),getSeatDateList(seatImgLists,seatTipLists),R.layout.seat_item , new String[]{"image","text"},new int[]{R.id.seatView,R.id.seatData});
                    gridView.setAdapter(seatItemAdapter);
                    //更新信息
                    updateRoom(roomObjectId,floorNumber,roomNumber);
                    updateUser(floorNumber,roomNumber,nowClickItem);
                    //显示提示
                    showDetail.setText("您正在使用座位：" + getSeatLocation(floorNumber,roomNumber,nowClickItem));
                    selectedSeat = nowClickItem;
                    nowClickItem = null;
                    lastClickItem = null;
                }

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
                dialogInterface.dismiss();
                //取消选座，更新数据和视图
                cancelRefreshView(selectedSeat);
                updateRoom(roomObjectId,floorNumber,roomNumber);
                showDetail.setText("您还未选择座位 (๑>\u0602<๑）");
                selectedSeat = null;
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
            if(selectedSeat != null){
                Toast.makeText(getContext(),"你已选座，请先取消座位" , Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(),"选座：" + clickItem , Toast.LENGTH_SHORT).show();
                nowClickItem = clickItem ;
                refreshView(nowClickItem,clickSeatImg);
            }
        }else if(clickSeatImg == R.drawable.seat_selected){
            Toast.makeText(getContext(),"抱歉，此座已有人,请重新选择" , Toast.LENGTH_SHORT).show();
        }else if(clickSeatImg == R.drawable.seat_selcting){
            Toast.makeText(getContext(),"取消选座" , Toast.LENGTH_SHORT).show();
            refreshView(nowClickItem,clickSeatImg);
        }
    }

    /**
     * 点击更新视图
     * @param nowClickItem
     * @param clickSeatImg
     */
    public void refreshView(int nowClickItem,int clickSeatImg){
        //若点击到无人的座位，则更换为有人状态
        if(clickSeatImg == R.drawable.seat_noselected){
            seatImgLists[nowClickItem] = R.drawable.seat_selcting;
            //如果之前已经点击过座位，则取消上一个，选中现在的座位
            if(lastClickItem!=null) {
                seatImgLists[lastClickItem] = R.drawable.seat_noselected;
            }
            this.lastClickItem =  nowClickItem;
            seatItemAdapter = new SimpleAdapter(getContext(),getSeatDateList(seatImgLists,seatTipLists),R.layout.seat_item , new String[]{"image","text"},new int[]{R.id.seatView,R.id.seatData});
            gridView.setAdapter(seatItemAdapter);
        }else if(clickSeatImg == R.drawable.seat_selcting){         //若点击到正在选择的座位，则更换为无人状态（取消)
            seatImgLists[nowClickItem] = R.drawable.seat_noselected;
            seatItemAdapter = new SimpleAdapter(getContext(),getSeatDateList(seatImgLists,seatTipLists),R.layout.seat_item , new String[]{"image","text"},new int[]{R.id.seatView,R.id.seatData});
            gridView.setAdapter(seatItemAdapter);
            this.lastClickItem = null;
            this.nowClickItem = null;
        }

    }


    /**
     * 取消选座，更新视图
     * @param selectedSeat
     */
    public void cancelRefreshView(int selectedSeat){
        seatImgLists[selectedSeat] = R.drawable.seat_noselected;
        seatItemAdapter = new SimpleAdapter(getContext(),getSeatDateList(seatImgLists,seatTipLists),R.layout.seat_item , new String[]{"image","text"},new int[]{R.id.seatView,R.id.seatData});
        gridView.setAdapter(seatItemAdapter);
    }



    /**
     *
     */
    public void setSelectedSeat(){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username",getArguments().get("studentId"));
        query.findObjects(getContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                selectedSeat = list.get(0).getSelectSeatItem();
                Log.d(TAG, "onSuccess: -------------------------selectedSeat = " + selectedSeat);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    /**
     * 将座位图片表转换为座位类型表
     * @param seatImgLists
     * @return
     */
    public Integer[] getSeatTypeLists(int[] seatImgLists){
        Integer[] seatTypeLists = new Integer[seatImgLists.length];
        for(int i=0;i<seatImgLists.length;i++){
            seatTypeLists[i] = getSeatType(seatImgLists[i]);
        }
        return seatTypeLists;
    }


    /**
     * 由座位图获得座位类型
     * @param seatImg
     * @return
     */
    public int getSeatType(int seatImg){
        if(seatImg == R.drawable.seat_noselected){
            return 0;
        }else if(seatImg == R.drawable.seat_selected){
            return 1;
        }else {
            return 2;
        }
    }


    /**
     * 更新用户选座记录
     * @param floorNumber
     * @param roomNumber
     * @param nowClickSeat
     */
    public void updateUser(int floorNumber,int roomNumber,int nowClickSeat){
        User user = new User((String)getArguments().get("studentId"),(String)getArguments().get("password"));
  /*      User user = new User();
        user.addSeatRecords(getSeatLocation(floorNumber,roomNumber,nowClickSeat));
        user.setSelectSeatLocation(getSeatLocation(floorNumber,roomNumber,nowClickSeat));
        user.setSelectSeatItem(nowClickSeat);
        BmobUser bmobUser = BmobUser.getCurrentUser(getContext());
        user.update(getContext(), bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ---------------------更新用户成功");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });*/
        user.addSeatRecords(getSeatLocation(floorNumber,roomNumber,nowClickSeat));
        user.setSelectSeatLocation(getSeatLocation(floorNumber,roomNumber,nowClickSeat));
        user.setSelectSeatItem(nowClickSeat);
        user.update(getContext(), userObjectId, new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ---------------------更新用户成功");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }



    /**
     * 更新书库信息
     * @param roomObjectId
     * @param floorNumber
     * @param roomNumber
     */
    public void updateRoom(String roomObjectId,int floorNumber,int roomNumber){
        Room room = new Room(floorNumber,roomNumber);
        room.setRoomTypeLists(getSeatTypeLists(seatImgLists));
        room.update(getContext(), roomObjectId, new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: --------------------------更新数据成功");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }




    /**
     * 是否选择了座位
     * @param nowClickSeat
     * @return
     */
    public boolean checkIsSelectSeat(Integer nowClickSeat){
        if(nowClickSeat == null){
            return false;
        }else {
            return true;
        }
    }


    /**
     * 座位所在
     * @param floorNumber
     * @param roomNumber
     * @param seatItem
     * @return
     */
    public String getSeatLocation(int floorNumber,int roomNumber,int seatItem){
        return floorNumber + "层第" + roomNumber + "书库" + (seatItem/8+1) + "排" + (seatItem%8+1) + "列";
    }
}
