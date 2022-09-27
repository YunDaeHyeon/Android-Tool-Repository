package com.daehyeon.blutetoothstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PairingActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "PairingActivity";
    // 뒤로가기 버튼 설정을 위한 시간
    private long backKeyPressedTime = 0;
    List<String> pairingList;
    List<String> bluetoothList;
    // 리스트뷰, 버튼 선언
    ListView pairing_listview, bluetooth_listview;
    Button pairingSearchBtn, bluetoothSearchBtn, cancelButton;
    // 블루투스 어뎁터 선언
    BluetoothAdapter bluetoothAdapter;
    // ArrayList에 저장되어 있는 데이터를 ListView에 뿌려주기 위해 ArrayAdapter 선언
    ArrayAdapter<String> pairingAdapter;
    ArrayAdapter<String> _bluetoothAdapter;
    // 블루투스 장치의 Mac 주소를 저장하기 위한 ArrayList
    ArrayList<String> deviceAddressArrayList;

    @SuppressLint("MissingPermission") // 권한 재확인 X 어노테이션.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        pairing_listview = (ListView) findViewById(R.id.pairing_listview); // 페어링 되어있는 기기를 뿌려주는 리스트뷰
        bluetooth_listview = (ListView) findViewById(R.id.bluetooth_listview); // 주변 블루투스 기기들을 뿌려주는 리스트뷰
        pairingSearchBtn = (Button) findViewById(R.id.pairingSearchBtn); // 페어링 목록 검색 버튼
        bluetoothSearchBtn = (Button) findViewById(R.id.bluetoothSearchBtn); // 주변 블루투스 기기 검색 버튼
        cancelButton = (Button) findViewById(R.id.cancelButton); // 뒤로가기 버튼

        // 블루투스 객체 호출
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // ArrayList 선언
        pairingList = new ArrayList<>();
        bluetoothList = new ArrayList<>();
        deviceAddressArrayList = new ArrayList<>();

        // 두 번째 매개변수는 안드로이드 자체적으로 정의된 레이아웃 사용.
        // 페어링 리스트뷰 어뎁터 연결
        pairingAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, pairingList);
        pairing_listview.setAdapter(pairingAdapter); // 어뎁터 지정

        // 블루투스 리스트뷰 어뎁터 연결
        _bluetoothAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, bluetoothList);
        bluetooth_listview.setAdapter(_bluetoothAdapter);

        // 버튼 클릭 이벤트 지정
        cancelButton.setOnClickListener(this); // 취소 버튼 클릭
        pairingSearchBtn.setOnClickListener(this); // 페어링 검색 버튼 클릭
        bluetoothSearchBtn.setOnClickListener(this); // 블루투스 검색 버튼 클릭

        // 페어링 리스트뷰 아이템 클릭
        pairing_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(), "연결기기: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 클릭 이벤트들을 정리해놓은 메서드
    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancelButton:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // 액티비티 종료
                break;
            case R.id.pairingSearchBtn: // 페어링 검색 버튼 클릭 시
                pairingAdapter.clear(); // 페어링 ArrayAdapter 초기화
                // 리스트뷰에 목록이 불러와져 있는 상태에서 버튼 클릭 시 ArrayList 초기화 후 다시 그리기
                if(deviceAddressArrayList != null && !deviceAddressArrayList.isEmpty())
                    deviceAddressArrayList.clear();
                // 기존 블루투스 어뎁터로부터 getBondedDevices()를 통해 페어링된 기기 목록 가져오기
                Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
                // 만약, 디바이스와 페어링된 기기가 하나 이상 존재할 시,
                if(bluetoothDevices.size() > 0){
                    // 각각의 페어링된 디바이스의 이름, Mac 주소를 가져와 저장한다.
                    for(BluetoothDevice device : bluetoothDevices){
                        String deviceName = device.getName(); // 디바이스 이름
                        String deviceMacAddress = device.getAddress(); // 디바이스 Mac 주소
                        // 디바이스 이름은 ArrayAdapter에, 맥 주소는 ArrayList에 저장
                        pairingAdapter.add(deviceName); // 페어링 리스트뷰와 연결되어있는 어뎁터이므로, 화면에는 기기 이름만 출력.
                        deviceAddressArrayList.add(deviceMacAddress);
                    }
                }
                break;
            case R.id.bluetoothSearchBtn:
                // 기기가 검색중인지 확인하여, 검색중이라면, 중지한다.
                // 안드로이드의 블루투스 검색작업은 엄청난 리소스를 사용하므로 꼭 cancelDiscovery()를 호출시켜야 한다.
                if(bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                }else{ // 검색중이 아니라면,
                    if(bluetoothAdapter.isEnabled()){ // 블루투스가 켜져있을 때
                        bluetoothAdapter.startDiscovery(); // 주변 블루투스 장치 검색 시작
                        // startDiscovery()는 비동기로 처리되며, 검색이 성공적으로 이루어졌는지 true or false로 반환.
                        _bluetoothAdapter.clear(); // ArrayAdapter 클리어
                        // Mac 주소를 담은 ArrayList에 데이터가 존재하면, 클리어 후 다시 저장
                        if(deviceAddressArrayList != null && !deviceAddressArrayList.isEmpty())
                            deviceAddressArrayList.clear();
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        registerReceiver(receiver, filter);
                    } else { // 블루투스가 꺼져있다면
                        Toast.makeText(getApplicationContext(), "블루투스가 꺼져있습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /*
    BroadcastReceiver는 4대 컴포넌트 중 하나로, 각종 앱에서 발생하는 이벤트를 캐치 후
    리시버로 처리할 수 있도록 도와준다. (Receiver : 수신자, 받는자. 디바이스를 수신자 역할로 지정.)
     */
    // BluetoothDevice.ACTION_FOUND에 대한 브로드캐스트 설정
    // ACTION_FOUND -> 블루투스 원격 장치가 발견되었을 때
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // ACTION_FOUND로 검색된 디바이스를 BluetoothDevice.EXTRA_DEVICE를 통해 장치의 객체를 가져온다.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMacAddress = device.getAddress();
                _bluetoothAdapter.add(deviceName);
                deviceAddressArrayList.add(deviceMacAddress);
                _bluetoothAdapter.notifyDataSetChanged();
            }
        }
    };

    // 액티비티가 소멸될 때 실행되는 메서드
    @Override
    protected void onDestroy(){
        super.onDestroy();
        // 브로드캐스트 리시버가 더 이상 필요하지 않을 경우, 등록 해제
        unregisterReceiver(receiver);
    }

    // 뒤로가기 버튼 설정을 위한 메소드 오버라이딩
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); 해당 super문을 주석 처리하면 뒤로가기 X
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        // 2초 이내에 뒤로가기 버튼 클릭 시
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
            finishAffinity(); // 루트 액티비티 종료
            System.runFinalization(); // 구동중인 쓰레드가 종료되면, 종료
            System.exit(0); // 현재 액티비티 강제 종료
        }
    }
}