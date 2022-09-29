package com.daehyeon.blutetoothstudy.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.daehyeon.blutetoothstudy.R;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "BluetoothActivity";
    // 뒤로가기 버튼 설정을 위한 시간
    private long backKeyPressedTime = 0;
    // 블루투스 객체 선언
    BluetoothAdapter bluetoothAdapter;
    Button bluetoothOnBtn, bluetoothOffBtn, pairingBtn, backButton;

    // 퍼미션 허용 (위치 권환을 허용시키기 위함.)
    String[] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        // 위치 권한 허용
        ActivityCompat.requestPermissions(BluetoothActivity.this, permission_list, 1);
        bluetoothOnBtn = (Button) findViewById(R.id.bluetoothOnBtn);
        bluetoothOffBtn = (Button) findViewById(R.id.bluetoothOffBtn);
        pairingBtn = (Button) findViewById(R.id.pairingBtn);
        backButton = (Button) findViewById(R.id.backButton);

        // 블루투스 getDefaultAdapter 메서드를 통해 모듈 호출
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 버튼 클릭 이벤트
        bluetoothOnBtn.setOnClickListener(this);
        bluetoothOffBtn.setOnClickListener(this);
        pairingBtn.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    Log.d(TAG, "블루투스가 활성화 되었습니다.");
                }
                if(result.getResultCode() == RESULT_CANCELED){
                    Log.d(TAG, "블루투스가 활성화되지 않았습니다.");
                }
            }
    );

    // 클릭 이벤트 정리
    @SuppressLint("MissingPermission") // 권한 재확인 X 어노테이션.
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bluetoothOnBtn:
                if(bluetoothAdapter == null){
                    Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
                }else{ // null이 아니라는 것은, 블루투스를 지원한다는 것!
                    if(!bluetoothAdapter.isEnabled()){
                        // 블루투스를 지원하지만, 비활성인 경우
                        // 블루투스를 활성화 시키기 위해 사용자 동의 요청
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        resultLauncher.launch(enableBtIntent);
                    }else{
                        // 블루투스를 지원하고, 활성화 상태인 경우.
                        // 페어링된 기기 목록을 보여주고, 연결할 장치 선택.
                    }
                }
                break;
            case R.id.bluetoothOffBtn:
                // 블루투스가 활성화 상태일 때
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable(); // 블루투스 off
                    Toast.makeText(getApplicationContext(), "블루투스를 비활성화 하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pairingBtn:
                // 페어링 액티비티에서 페어링된 기기 목록을 리스트뷰를 사용하여 화면에 출력.
                // 단, 이미 페어링된 기기만 출력되며 기기 검색은 X.
                // 블루투스가 켜있을 때
                if(bluetoothAdapter.isEnabled()){
                    Log.d(TAG, "블루투스 활성화 상태");
                    Intent pairingIntent = new Intent(BluetoothActivity.this, PairingActivity.class);
                    startActivity(pairingIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "블루투스가 꺼져있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.backButton:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // 액티비티 종료
                break;
        }
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

/*
    Android 어플리케이션은 Bluetooth API를 사용해서 다음과 같은 작업 수행 가능
    1. 다른 블루투스 스캔
    2. 페어링된 블루투스 기기에 대한 로컬 블루투스 어배터 쿼리
    3. RFCOMM 채널 설정
      : RFCOMM(Radio Frequency Communications)
      기존의 유선 RS232 시리얼 포트를 대체하기 위해 만들어진 프로토콜.
      : RS232 시리얼
      컴퓨터와 주변기기를 연결하는 비동기식 직렬방식 통신표준 중 하나.
      TCP 프로토콜과 유사하며 30개의 포트 번호를 가질 수 있음.
    4. 서비스 검색을 통해 다른 기기에 연결
    5. 기기 간 데이터 전송 및 수신
    6. 다중 연결 관리

    기본 사항 (개발 단계)
    * 블루투스 지원 기기가 서로 데이터를 전송하려면,
    페어링 프로세스를 통해 통신 채널을 형성해야함.

    [1] 블루투스 권한 설정
    app/manifests에 들어가 아래와 같은 블루투스 권한 설정
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  만약, 안드로이드 API가 28 이하, 혹은 Android 9 이하라면 ACCESS_FINE_LOCATION이 아닌,
  ACCESS_COARSE_LOCATION 권한 선언

   어플이 블루투스를 사용하여 통신하기 위해서는, 블루투스가 기기에서 지원되는지 확인해야한다.
   그렇다는 것은, 지원되는 경우 블루투스를 활성화하고, 지원되지 않을 경우 블루투스를 비활성화 해야한다.

    1. BluetoothAdapter를 호출하여 블루투스 어뎁터를 호출한다.
    2. 그 뒤 getDefaultAdapter 메서드를 호출시켜 기기가 블루투스를 지원하는지 확인한다.
    블루투스가 지원되면 BluetoothAdapter 객체를 반환하고, 지원하지 않으면 null을 반환한다.
    그렇다는 것은, 조건문으로 만약 null이라면 블루투스가 지원되지 않는다는 것을 명시한다.
 */