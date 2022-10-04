# Android Studio(Java) 다양한 기능 모음
시작 : 22.09.25  
진행중..  

# 작성한 기능
1. BluetoothAdapter, Thread를 사용한 블루투스 페어링, 검색, 연결
2. AnimatedSvgView를 사용한 스플래쉬 액티비티 구현 (시작, 로딩화면)
3. Notification를 이용한 알람 구현
4. Notification, AlarmManager, Receiver를 이용한 특정 시간 지정 시 알람 울리기

# 2022-09-29 오후 20시 00분 기준 프로젝트 구조
```console
안드로이드 스튜디오
├─main
│  │  AndroidManifest.xml
│  │
│  ├─java
│  │  └─com
│  │      └─daehyeon
│  │          └─blutetoothstudy
│  │              ├─activity
│  │              │      AlarmActivity.java
│  │              │      BluetoothActivity.java
│  │              │      MainActivity.java
│  │              │      PairingActivity.java
│  │              │      SplashActivity.java
│  │              │
│  │              ├─fragment
│  │              │      TimePickerFragment.java
│  │              │
│  │              ├─helper
│  │              │      NotificationHelper.java
│  │              │
│  │              ├─receiver
│  │              │      AlertReceiver.java
│  │              │
│  │              └─thread
│  │                      ConnectedThread.java
│  │
│  └─res
│      ├─drawable
│      │      ic_launcher_background.xml
│      │
│      ├─drawable-v24
│      │      ic_launcher_foreground.xml
│      │
│      ├─layout
│      │      activity_alarm.xml
│      │      activity_bluetooth.xml
│      │      activity_main.xml
│      │      activity_pairing.xml
│      │      activity_splash.xml

아두이노
├─Arduino
│      Arduino Source File.ino
```

# Reference
[안드로이드 공식 문서](https://developer.android.com/reference/android/bluetooth/BluetoothDevice)