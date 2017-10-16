package com.example.a87784.select.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a87784.select.R;
import com.example.a87784.select.bean.Room;
import com.example.a87784.select.bean.User;
import com.example.a87784.select.fragment.RoomFragment;

import org.w3c.dom.Text;

import java.util.HashMap;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

import static com.example.a87784.select.config.Constans.APPLICATION_ID;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public  User testUser;
    private static final String TAG = "MainActivity";


    private Spinner floorSpinner,roomSpinner;
    private ArrayAdapter spinnerAdapter;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private int selectedFloor,selectedRoom;

    private ImageButton search;

    private HashMap<String,Room> roomHashMap;

    private RoomFragment from = null;      //正在跳转的视图

    private TextView noSearch,loading;
    private FrameLayout roomViewContainer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initView();
        initListener();

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        initData();

   //     Bmob.initialize(this,APPLICATION_ID);   //初始化Bmob SDK

  //      test();
    }

    public void test(){
        testUser = new User("20162201","123456");
        testUser.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "done: -------------------注册成功");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, "done: -------------------注册失败  " + s);
            }
        });



    }

    public void initView(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        floorSpinner = (Spinner)findViewById(R.id.floor);
        roomSpinner = (Spinner)findViewById(R.id.room);
        search = (ImageButton)findViewById(R.id.search);

        noSearch = (TextView)findViewById(R.id.noSearch);
        loading = (TextView)findViewById(R.id.loading);
        roomViewContainer = (FrameLayout)findViewById(R.id.roomViewContainer);

    }

    public void initListener(){
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setDrawerListener(toggle);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"search " + selectedFloor + "楼" + selectedRoom + "书库的座位表",Toast.LENGTH_SHORT).show();
                noSearch.setVisibility(View.GONE);
                roomViewContainer.setVisibility(View.VISIBLE);
                switchFragment();
            }
        });


    }

    public void initData(){

        setFloorSpinner();
        setRoomSpinner();

        selectedFloor = selectedRoom = 0;
        roomHashMap = new HashMap<>();
        Room room;

        //
        for(int i=0;i<5;i++)
            for(int j=0;j<4;j++){
                String key = String.valueOf(i) + String.valueOf(j);
                room = new Room(i,j,new RoomFragment());
                roomHashMap.put(key,room);
            }


    }

    public void setFloorSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.floors,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(spinnerAdapter);
        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFloor = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void setRoomSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.rooms,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(spinnerAdapter);
        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRoom = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void switchFragment(){
        RoomFragment to = matchFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(from == null){
            fragmentTransaction.add(R.id.roomViewContainer,to).commit();
        } else if(to.isAdded()){
            fragmentTransaction.hide(from).show(to).commit();
        }else {
            fragmentTransaction.hide(from).add(R.id.roomViewContainer,to).commit();
        }
        from = to;
    }

    public RoomFragment matchFragment() {
        String key = String.valueOf(selectedFloor) + String.valueOf(selectedRoom);
        return roomHashMap.get(key).getRoomView();
    }





    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
