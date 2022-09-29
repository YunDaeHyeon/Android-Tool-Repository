package com.daehyeon.blutetoothstudy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daehyeon.blutetoothstudy.R;
import com.daehyeon.blutetoothstudy.thread.ConnectedThread;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PairingActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "PairingActivity";
    // 뒤로가기 버튼 설정을 위한 시간
    private long backKeyPressedTime = 0;
    // 테스트 블루투스 연결 변수
    boolean test = true;
    List<String> pairingList;
    List<String> bluetoothList;
    // 리스트뷰, 버튼, 텍스트뷰 선언
    ListView pairing_listview, bluetooth_listview;
    Button pairingSearchBtn, bluetoothSearchBtn, backButton, testBtn;
    TextView statusText;
    // 블루투스 어뎁터 선언
    BluetoothAdapter bluetoothAdapter;
    // ArrayList에 저장되어 있는 데이터를 ListView에 뿌려주기 위해 ArrayAdapter 선언
    ArrayAdapter<String> pairingAdapter;
    ArrayAdapter<String> _bluetoothAdapter;
    // 블루투스 장치의 Mac 주소를 저장하기 위한 ArrayList
    ArrayList<String> deviceAddressArrayList;
    // 블루투스 연결을 위한 소켓 설정
    BluetoothSocket bluetoothSocket = null;
    // 블루투스 통신을 위한 프로토콜 지정 (https://dsnight.tistory.com/13에 다양한 프로토콜 UUID 존재)
    // 해당 프로토콜은 SerialPortServiceClass_UUID 으로, 아두이노 시리얼 포트에 접근할 수 있는 프로토콜 지정
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 블루투스 연결을 시작하는 클라이언트 쓰레드 선언
    ConnectedThread connectedThread;

    @SuppressLint("MissingPermission") // 권한 재확인 X 어노테이션.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        pairing_listview = (ListView) findViewById(R.id.pairing_listview); // 페어링 되어있는 기기를 뿌려주는 리스트뷰
        bluetooth_listview = (ListView) findViewById(R.id.bluetooth_listview); // 주변 블루투스 기기들을 뿌려주는 리스트뷰
        pairingSearchBtn = (Button) findViewById(R.id.pairingSearchBtn); // 페어링 목록 검색 버튼
        bluetoothSearchBtn = (Button) findViewById(R.id.bluetoothSearchBtn); // 주변 블루투스 기기 검색 버튼
        backButton = (Button) findViewById(R.id.backButton); // 뒤로가기 버튼
        testBtn = (Button) findViewById(R.id.testBtn); // 안드로이드 -> 아두이노 테스트 버튼
        statusText = (TextView) findViewById(R.id.statusText); // 텍스트뷰

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
        backButton.setOnClickListener(this);
        pairingSearchBtn.setOnClickListener(this); // 페어링 검색 버튼 클릭
        bluetoothSearchBtn.setOnClickListener(this); // 블루투스 검색 버튼 클릭
        testBtn.setOnClickListener(this); // 안드로이드 -> 아두이노 데이터 전송 테스트
        pairing_listview.setOnItemClickListener(new bluetoothContectedClickListener()); // 페어링 리스트뷰 아이템 클릭
    }

    // 클릭 이벤트들을 정리해놓은 메서드
    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.backButton:
                Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(intent);
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
            case R.id.testBtn:
                // 만약, 쓰레드 객체가 생성되어 있다면, (소켓이 정상적으로 생성되었고, 프로토콜 연결이 되어있다면)
                if(connectedThread!=null){
                    if(test){
                        connectedThread.write("true");
                        test = false;
                    }else if(test == false){
                        connectedThread.write("false");
                        test = true;
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

    // 페어링 리스트뷰 아이템 클릭 리스너
    // 클릭한 아이템(기기)와 블루투스 연결 시도
    class bluetoothContectedClickListener implements AdapterView.OnItemClickListener{
        @SuppressLint("MissingPermission")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            // 클릭한 아이템(기기)의 이름 토스트 메시지 출력
            Toast.makeText(getApplicationContext(), pairingAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            statusText.setText("연결중..."); // 텍스트뷰 텍스트 변경

            final String deviceName = pairingAdapter.getItem(position); // 기기 이름 가져오기
            final String deviceMacAddress = deviceAddressArrayList.get(position); // 기기 Mac 주소 가져오기
            boolean flag = true; // 소켓 연결을 위한 flag 지정
            // 안드로이드와 페어링된 기기(HC-06)의 Mac 주소를 사용하여 원격 디바이스 객체 생성
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceMacAddress);
            // 소켓 생성, 연결
            try{
                // 소켓 생성 과정
                // 1. BluetoothSocket으로 생성한 원격 디바이스 객체를 createBluetoothSocket으로 넘긴다.
                // 2. 넘긴 객체에 존재하는 createInsecureRfcommSocketToServiceRecord 메서드를 리플렉션으로 검색한다.
                // 3. 검색한 메서드와 블루투스 프로토콜(UUID)를 매개변수로 createRfcommSocketToServiceRecord(UUID)를 호출시켜 소켓을 생성한다.
                bluetoothSocket = createBluetoothSocket(device);
                bluetoothSocket.connect(); // 생성된 소켓을 호출시켜 연결을 시작한다.
                /*
                    클라이언트는 connect()를 호출시켜 시스템에 세션 기술 프로토콜(Session Description Protocol, SDP)조회를 시작한다.
                    SDP 조회에 따라 일치하는 UUID를 포함한 기기를 찾게되어 해당 기기가 연결을 수락하는 경우,
                    연결되는 동안 RFCOMM 채널을 공유하고, connect() 객체가 반환된다.
                    만약, 연결에 실패하거나 시간(12초)이 초과되면 connect() 메서드는 IOException을 반환한다.
                 */
            }catch (IOException e){
                flag = false; // 소켓 연결이 실패되면 flag를 false로 변경
                statusText.setText("연결에 실패하였습니다.");
                e.printStackTrace();
            }
            if(flag){ // flag가 true라면, (소켓 연결에 성공하였다면)
                statusText.setText("연결된 기기 : "+deviceName);
                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start(); // 쓰레드 시작
            }
        }

        @SuppressLint("MissingPermission")
        private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException{
            try{
                // Reflection(리플렉션) 통해 createInsecureRfcommSocketToServiceRecord 메소드 접근
                // Reflection(리플렉션) : 구체적인 클래스 타입을 몰라도 메소드, 타입, 변수에 접근할 수 있는 API
                final Method method = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
                // 리플렉션으로 검색한 메서드를 invoke로 호출하여 실행. 그 뒤 BluetoothSocket으로 캐스팅하여 반환
                return (BluetoothSocket) method.invoke(device, BT_MODULE_UUID);
            }catch (Exception e){
                Log.e(TAG,"RFComm 연결을 진행할 수 없습니다.");
            }
            // createRfcommSocketToServiceRecord를 통해 UUID를 호출하여 소켓을 반환한다.
            return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        }
    }

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