package com.example.a87784.select.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.a87784.select.R;
import com.example.a87784.select.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

public class RecordsActivity extends AppCompatActivity {



    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        listView = (ListView)findViewById(R.id.list);

        initData(getIntent().getStringExtra("userObjectId"));
    }


    /**
     *
     * @param userObjectId
     */
    public void initData(String userObjectId){
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(this, userObjectId , new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                records = user.getSeatRecords();
                adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,records);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

}
