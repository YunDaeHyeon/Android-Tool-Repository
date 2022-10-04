package com.daehyeon.blutetoothstudy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.daehyeon.blutetoothstudy.R;

/*
    메인 액티비티. 다양한 기능이 작성되어있는 액티비티 혹은 프래그먼트로 이동할 수 있는 화면
    레이아웃 : activity_main.xml
*/

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "MainActivity";
    // 뒤로가기 버튼 설정을 위한 시간
    private long backKeyPressedTime = 0;

    Button bluetoothActivityButton, alarmActivityButton, splashActivityButton, FcmActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothActivityButton = (Button) findViewById(R.id.bluetoothActivityButton);
        alarmActivityButton = (Button) findViewById(R.id.alarmActivityButton);
        splashActivityButton = (Button) findViewById(R.id.splashActivityButton);
        FcmActivityButton = (Button) findViewById(R.id.FcmActivityButton);

        bluetoothActivityButton.setOnClickListener(this);
        alarmActivityButton.setOnClickListener(this);
        splashActivityButton.setOnClickListener(this);
        FcmActivityButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bluetoothActivityButton:
                Intent bluetoothActivityIntent = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(bluetoothActivityIntent);
                finish();
                break;
            case R.id.alarmActivityButton:
                Intent alarmActivityIntent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(alarmActivityIntent);
                finish();
                break;
            case R.id.splashActivityButton:
                Intent splashActivityIntent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(splashActivityIntent);
                finish();
                break;
            case R.id.FcmActivityButton:
                Intent FcmActivityIntent = new Intent(getApplicationContext(), FcmActivity.class);
                startActivity(FcmActivityIntent);
                finish();
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