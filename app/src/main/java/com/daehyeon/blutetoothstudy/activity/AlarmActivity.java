package com.daehyeon.blutetoothstudy.activity;

import com.daehyeon.blutetoothstudy.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/*
    알람 액티비티, 해당 액티비티는 Android 지원 라이브러리의 NotificationCompat API 사용하여 푸시 알림을 구현한다.
    build.gradle(Module)에 의존성 추가 : implementation "com.android.support:support-compat:28.0.0"
    단, API 14 이하만 의존성 추가.
    레퍼런스 : https://developer.android.com/training/notify-user/build-notification?hl=ko
    레이아웃 : activity_main.xml

    Notification(노티피케이션, 알림)은 아래와 같은 로직으로 진행된다.
    1. 알림 콘텐츠 설정 및 알람 탭 작업 설정 (Notification.Builder, PendingIntent 사용)
    2. 채널 만들기, 중요도 설정
    3. 알림 표시

    단, 노티피케이션은 API 26(오레오) 이상부터 채널이 필요하다.
    이를 위해 OS가 오레오 이상인지, 이하인지 구별하여 알람을 구현하자.

    노티피케이션으로 구현된 알람이 출력될 때 카카오톡 알람이 오는 것 처럼 팝업으로 띄우려고 했는데
    코드 구현 방법을 몰라, 우선 디바이스 내 권한을 임의로 켰다.
    1. 설정 -> 애플리케이션 -> 제작한 어플리케이션 이름 -> 애플리케이션 설정(알림) -> 제작한 채널 이름 -> 팝업으로 표시 ON
*/

public class AlarmActivity extends AppCompatActivity {
    private final String TAG = "AlarmActivity";
    // 뒤로가기 버튼 설정을 위한 시간
    private long backKeyPressedTime = 0;

    Button basicAlarmButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        basicAlarmButton = (Button) findViewById(R.id.basicAlarmButton);
        backButton = (Button) findViewById(R.id.backButton);

        // 노티피케이션의 채널은 앱 실행될 때(액티비티 실행될 때) 바로 생성되는 것이 좋다.
        // 첫 번째 인자 (채널 아이디), 두 번째 인자 (채널 이름), 세 번쨰 인자 (채널 우선순위)
        createNotificationChannel("alarm_channel_id", "기본 채널", NotificationManager.IMPORTANCE_HIGH);

        // 알람 클릭 시 해당 액티비티가 실행될 수 있도록 플래그 설정
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // 기본 알람 버튼 클릭 리스너
        basicAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 알람 실행
                createNotification(
                        "alarm_channel_id",
                        1,
                        "기본 알람 제목",
                        "기본 알람 내용",
                        intent
                );
            }
        });

        // 뒤로 가기 버튼 클릭 리스너
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // 액티비티 종료
            }
        });
    }

    // 노티피케이션(알림) 채널 만들기
    private void createNotificationChannel(String channelId, String channelName, int importance){
        // API 26(오레오) 이상만 채널 만들기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 알림 서비스 구현
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(
                    new NotificationChannel(
                            channelId, // 채널 ID (중요)
                            channelName, // 채널 이름
                            importance // 채널 중요도는 기본으로
                    )
            );
        }
    }

    // 노티피케이션 만들기
    private void createNotification(String channelId, int id, String title, String content, Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 우선순위 설정
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 작은 아이콘 설정
                .setContentTitle(title) // 알람 타이틀 지정
                .setContentText(content) // 알람 내용 지정
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("알람 내용을 더 크게 설정")) // 알람 내용 확장 (필수 X)
                .setContentIntent(pendingIntent) // 알람 클릭시 지정한 인탠트 실행
                .setAutoCancel(true) // AutoCancel이 true이면 클릭 시 알람이 삭제된다.
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE); // 사운드, 진동 설정
        // 노티피케이션을 시스템에 등록
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build()); // 알람 빌드
    }

    // 노티피케이션 파괴
    private void destroyNotification(int id){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
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