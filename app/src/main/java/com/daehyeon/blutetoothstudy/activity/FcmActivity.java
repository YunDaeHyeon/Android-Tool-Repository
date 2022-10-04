package com.daehyeon.blutetoothstudy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.daehyeon.blutetoothstudy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FcmActivity extends AppCompatActivity {
    private final String TAG = "FcmActivity";
    // 뒤로가기 버튼 설정을 위한 시간
    private long backKeyPressedTime = 0;

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);
        backButton = (Button) findViewById(R.id.backButton);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "토큰 생성 실패", task.getException());
                            return;
                        }
                        // 새로운 토큰 생성 성공 시
                        String token = task.getResult();
                        Log.d("MessageToken",token);
                    }
                });
        // 주제 구독
        // FirebaseMessaging.getInstance().subscribeToTopic("ALL");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // 액티비티 종료
            }
        });
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