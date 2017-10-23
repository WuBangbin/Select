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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a87784.select.R;
import com.example.a87784.select.bean.RoomType;
import com.example.a87784.select.ui.CustomSeatView;
import com.example.a87784.select.ui.SeatView;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 87784 on 2017/10/13.
 */

public class RoomFragment extends Fragment {


    private static final String TAG = "RoomFragment";

    private static final int DETERMINE_SEAT = 0;
    private static final int CANCEL_SEAT = 1;

    private View view;

    private CustomSeatView customSeatView;
    private TextView showDetail;
    private Button determine;
    private Button cancel;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DETERMINE_SEAT:
                    showDetail.setText("正在使用座位...");
                    break;
                case CANCEL_SEAT:
                    showDetail.setText("您还未选择座位 (๑>\u0602<๑）");
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.room_fragment,container,false);

        customSeatView = (CustomSeatView)view.findViewById(R.id.customSeatView);
        showDetail = (TextView)view.findViewById(R.id.showDetail);
        determine = (Button)view.findViewById(R.id.determine);
        cancel = (Button)view.findViewById(R.id.cancel);

        customSeatView.setOnClickSeatCallBack(new CustomSeatView.ClickSeatCallBack() {
            @Override
            public void onClickSeat(int raw, int col, String s) {
                showDetail.setText(s);
            }
        });

        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDetermineDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }


    public void popDetermineDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("是否确定选座？");
        dialog.setTitle("提示");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Message message = handler.obtainMessage();
                message.what = DETERMINE_SEAT;
                handler.sendMessage(message);
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
                Message message = handler.obtainMessage();
                message.what = CANCEL_SEAT;
                handler.sendMessage(message);
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

}
