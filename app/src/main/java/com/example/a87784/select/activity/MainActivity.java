package com.example.a87784.select.activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.a87784.select.config.Constans;
import com.example.a87784.select.fragment.RoomFragment;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

import static com.example.a87784.select.config.Constans.APPLICATION_ID;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public  User user;
    private static final String TAG = "MainActivity";

    private static final int QUERY_USER_FINISHED = 1;           //查询用户结束
    private static final int GET_ROOMINFO_OK = 0;          //成功获得指定书库信息

    public static final int NO_PEOPLE = 0;         //座位无人
    public static final int HAVE_PEOPLE = 1;       //座位有人
    public static final int SELECTED = 2;          //座位被选中


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
    private String userObjectId;

    //点击的楼层和书库编号
    private int selectedFloor,selectedRoom;

    private ImageButton search;

    private HashMap<String,Room> roomHashMap;


    private TextView noSearch,loading;
    private FrameLayout roomViewContainer;

    //用户是否已注册
    private boolean isRegistered;
    //书库编号
    private String key;
    private String roomId;
    //座位图表(80)
    private int[] seatImgLists;
    //座位编号信息
    private String[] seatTipLists;
    //书库fragment表
    private HashMap<String,RoomFragment> roomFragmentHashMap;



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case QUERY_USER_FINISHED:
                    if(!isRegistered){
                        register();
                    }
                    break;
                case GET_ROOMINFO_OK:
                    seatImgLists = getSeatImgLists((Object[]) msg.obj);
                    loading.setVisibility(View.GONE);
                    roomViewContainer.setVisibility(View.VISIBLE);
                    switchFragment(seatImgLists,seatTipLists);
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


                //正在请求数据，显示数据正在加载
                loading.setVisibility(View.VISIBLE);


                //请求点击书库的信息
                key = String.valueOf(selectedFloor) + String.valueOf(selectedRoom);
                roomId = matchRoomTypeId(key);
                getRoomSeatTypeLists(roomId);

            }
        });


    }

    public void initData(){

        Bmob.initialize(this,APPLICATION_ID);   //初始化Bmob SDK

        setFloorSpinner();
        setRoomSpinner();

        selectedFloor = selectedRoom = 0;
        roomHashMap = new HashMap<>();
        roomFragmentHashMap = new HashMap<>();

        seatTipLists = getSeatTipLists();


    }

    public void setFloorSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.floors,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(spinnerAdapter);
        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFloor = i;
                Log.d(TAG, "onItemSelected: ----------------floor" + i);
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
                Log.d(TAG, "onItemSelected: -------------------------room" + i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 切换书库视图 这里不能用hide show 必须重新replace视图
     */
    public void switchFragment(int[] seatImgLists,String[] seatTipLists){
        RoomFragment to = matchFragment(selectedFloor,selectedRoom);

        userObjectId = user.getObjectId();

        Bundle bundle = new Bundle();
        bundle.putSerializable("seatImgLists",seatImgLists);
        bundle.putSerializable("seatTipLists",seatTipLists);
        bundle.putString("roomObjectId",roomId);
        bundle.putInt("roomNumber",selectedRoom);
        bundle.putInt("floorNumber",selectedFloor);
        bundle.putString("userObjectId",userObjectId);
        to.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.roomViewContainer,to).commit();
    }


    /**
     * 匹配视图
     * @param selectedFloor
     * @param selectedRoom
     * @return
     */
    public RoomFragment matchFragment(int selectedFloor,int selectedRoom) {
        key = String.valueOf(selectedFloor) + String.valueOf(selectedRoom);
        if(roomHashMap.containsKey(key)){
            return roomFragmentHashMap.get(key);
        }else {
            Room room = new Room(selectedFloor,selectedRoom);
            roomFragmentHashMap.put(key,new RoomFragment());
            roomHashMap.put(key,room);
            return roomFragmentHashMap.get(key);
        }
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
        } else if (id == R.id.nav_record) {
            Intent i = new Intent(MainActivity.this,RecordsActivity.class);
            i.putExtra("userObjectId",userObjectId);
            startActivity(i);
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




    /**
     *
     * @param room 书库的楼层和编号结合的字符串
     * @return
     */
    public String matchRoomTypeId(String room){
        switch (room){
            case "00":return Constans.ONE_ONE_ROOM_OBJECTID;
            case "01":return Constans.ONE_TWO_ROOM_OBJECTID;
            case "02":return Constans.ONE_THREE_ROOM_OBJECTID;
            case "03":return Constans.ONE_FOUR_ROOM_OBJECTID;

            case "10":return Constans.TWO_ONE_ROOM_OBJECTID;
            case "11":return Constans.TWO_TWO_ROOM_OBJECTID;
            case "12":return Constans.TWO_THREE_ROOM_OBJECTID;
            case "13":return Constans.TWO_FOUR_ROOM_OBJECTID;

            case "20":return Constans.THREE_ONE_ROOM_OBJECTID;
            case "21":return Constans.THREE_TWO_ROOM_OBJECTID;
            case "22":return Constans.THREE_THREE_ROOM_OBJECTID;
            case "23":return Constans.THREE_FOUR_ROOM_OBJECTID;

            case "30":return Constans.FOUR_ONE_ROOM_OBJECTID;
            case "31":return Constans.FOUR_TWO_ROOM_OBJECTID;
            case "32":return Constans.FOUR_THREE_ROOM_OBJECTID;
            case "33":return Constans.FOUR_FOUR_ROOM_OBJECTID;

            case "40":return Constans.FIVE_ONE_ROOM_OBJECTID;
            case "41":return Constans.FIVE_TWO_ROOM_OBJECTID;
            case "42":return Constans.FIVE_THREE_ROOM_OBJECTID;
            case "43":return Constans.FIVE_FOUR_ROOM_OBJECTID;
        }
        return null;
    }

/*



    */
/**
     * 从bmob上获得座位type表
     * @param roomId
     */

    public void getRoomSeatTypeLists(String roomId){
        BmobQuery<Room> query = new BmobQuery<>();
        query.getObject(MainActivity.this, roomId, new GetListener<Room>() {
            @Override
            public void onSuccess(Room room) {
                Message message = handler.obtainMessage();
                message.what = GET_ROOMINFO_OK;
                message.obj = room.getRoomTypeLists();
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
    public int getSeatResId(Object seatType){
        if(seatType == null || (int)seatType == NO_PEOPLE){
            return R.drawable.seat_noselected;
        }else if((int)seatType == HAVE_PEOPLE) {
            return R.drawable.seat_selected;
        }else if((int)seatType == SELECTED) {
            return R.drawable.seat_selcting;
        }
        return 0;
    }


    /**
     * 获得座位图list
     * @param seatTypeLists
     * @return
     */
    public int[]  getSeatImgLists(Object[] seatTypeLists){
        int[] seatImgLists = new int[80];
        for(int i =0;i<80;i++){
            seatImgLists[i] = getSeatResId(seatTypeLists[i]);
        }
        return seatImgLists;
    }


    public String[] getSeatTipLists(){
        String[] seatTipLists = new String[80];
        int i=0,raw,col;
        for(raw=0;raw<10;raw++){
            for(col=0;col<8;col++){
                seatTipLists[i] = "(" + (raw+1) + "," + (col+1) + ")";
                i++;
            }
        }
        return seatTipLists;
    }



}
