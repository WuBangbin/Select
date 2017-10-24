package com.example.a87784.select.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a87784.select.R;
import com.example.a87784.select.bean.Room;
import com.example.a87784.select.bean.User;
import com.example.a87784.select.fragment.RoomFragment;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.example.a87784.select.config.Constans.APPLICATION_ID;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public  User user;
    private static final String TAG = "MainActivity";

    private static final int QUERY_USER_FINISHED = 1;


    private Spinner floorSpinner,roomSpinner;
    private ArrayAdapter spinnerAdapter;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private TextView nameView;
    private TextView majorView;
    private String name;
    private String major;
    private String studentId;
    private String password;
    private String identity;
    private String cookie;

    private int selectedFloor,selectedRoom;

    private ImageButton search;

    private HashMap<String,Room> roomHashMap;

    private RoomFragment from = null;      //正在跳转的视图

    private TextView noSearch,loading;
    private FrameLayout roomViewContainer;

    //用户是否已注册
    private boolean isRegistered;
    //书库编号
    private String key;
    //传递书库编号的bundle
    private Bundle bundle;




    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case QUERY_USER_FINISHED:
                    if(!isRegistered){
                        register();
                    }
                    break;
            }
        }
    };




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


        if(initInfo()){
            setInfo();
            //查询用户是否已注册
            queryIsRegistered(studentId);
        }

    }

    /**
     * 注册信息
     */
    public void register(){
        user = new User(studentId,password);
        user.setMajor(major);
        user.setIdentity(identity);
        user.setName(name);
        user.setCookie(cookie);
        user.signUp(this, new SaveListener() {
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


    /**
     *查询用户是否已注册
     */
    public boolean queryIsRegistered(String studentId){
        isRegistered = false;
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username",studentId);
        Log.d(TAG, "queryIsRegistered: --------------------------------1");
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if(list.size() > 0){
                    isRegistered = true;
                    Message message = handler.obtainMessage();
                    message.what = QUERY_USER_FINISHED;
                    Log.d(TAG, "onSuccess: ---------------------list" + list);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: ------------------" + s);

            }
        });
        return isRegistered;
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

        Bmob.initialize(this,APPLICATION_ID);   //初始化Bmob SDK

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

    /**
     * 切换书库视图 这里不能用hide show 必须重新加载视图
     */
    public void switchFragment(){
        RoomFragment to = matchFragment(selectedFloor,selectedRoom);

        bundle.putString("roomId",key);
        to.setArguments(bundle);

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


    /**
     * 匹配视图
     * @param selectedFloor
     * @param selectedRoom
     * @return
     */
    public RoomFragment matchFragment(int selectedFloor,int selectedRoom) {
        key = String.valueOf(selectedFloor) + String.valueOf(selectedRoom);
        return roomHashMap.get(key).getRoomView();
    }





    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            popIsFinishDialog();
        }
    }


    public void popIsFinishDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("是否确定退出？");
        dialog.setTitle("提示");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
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


    /**
     * 初始化数据
     * @return
     */
    public boolean initInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        if(!(sharedPreferences.getString("name","").isEmpty())){
            name = sharedPreferences.getString("name","");
            studentId = sharedPreferences.getString("studentId","");
            password = sharedPreferences.getString("password","");
            major = sharedPreferences.getString("major","");
            identity = sharedPreferences.getString("identity","");
            cookie = sharedPreferences.getString("cookie","");
            return true;
        }
        return false;
    }


    public void setInfo(){
        View headerView = navigationView.getHeaderView(0);
        nameView = (TextView)headerView.findViewById(R.id.name);
        majorView = (TextView)headerView.findViewById(R.id.major);

        nameView.setText(name);
        majorView.setText(major);
    }

}
