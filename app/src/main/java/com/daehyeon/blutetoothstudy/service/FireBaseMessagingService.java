package com.daehyeon.blutetoothstudy.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.daehyeon.blutetoothstudy.R;
import com.daehyeon.blutetoothstudy.activity.FcmActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/*
    FCM(Firebase Cloud Messaging) 사용 절차
    1. Firebase에 앱 추가하기 / 참고 : https://firebase.google.com/docs/android/setup
    2. Gradle에 종속성 추가하기
    3. 인터넷 퍼미션 추가
    4. 매니페스트 서비스 추가
    <service
        android:name="{FirebaseMessagingService를 상속받은 클래스 이름}"
        android:exported="false">
        <intent-filter>
             <action android:name="com.google.firebase.MESSAGING_EVENT" />
        </intent-filter>
    </service>
 */
public class FireBaseMessagingService extends FirebaseMessagingService {
    public static final String channelId = "alarm_channel_id";

    // 클라우드 서버에 등록되었을 때 호출되는 메서드
    // @param token -> 매개변수로 전달된 token은 앱을 구분하기 위한 고유 키
    @Override
    public void onNewToken(@NonNull String token) {
        // super.onNewToken(token);
        // 서버간 토근 연동 작업 실행
        Log.d("FCM Log", "Refreshed token: "+token);
    }

    // 클라우드 서버ㅔ서 메시지를 전송하면 자동 호출
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // super.onMessageReceived(message);
        // 서버에서 수신된 메시지 처리
        if(message.getNotification() != null){ // 포어그라운드 처리
            sendNotification(message.getNotification().getTitle(),
                    message.getNotification().getBody());
        }else if(message.getData().size() > 0){
            sendNotification(message.getData().get("title"),
                    message.getData().get("body"));
        }
    }

    // 수신된 데이터 처리하기
    private void sendNotification(String messageTitle, String messageBody){
        Log.d("FCM Log", "수신받은 메시지 : "+messageBody);

        // 알람 클릭 시 실행될 액티비티 지정(PendingIntent)
        Intent intent = new Intent(getApplicationContext(), FcmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE );

        // 알림 생성
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE); // 사운드, 진동 설정;

        // 알림 팝업 띄우기
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
