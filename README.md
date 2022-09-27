# Android Studio(Java) <-> Arduino Bluetooth Communication
시작 : 22.09.25  
진행중..  

# 2022-09-28 오전 12시 33분 기준 프로젝트 구조
```console
안드로이드 스튜디오
├─main
│  │  AndroidManifest.xml
│  │
│  ├─java
│  │  └─com
│  │      └─daehyeon
│  │          └─blutetoothstudy
│  │              │  MainActivity.java
│  │              │  PairingActivity.java
│  │              │
│  │              └─thread
│  │                      ConnectedThread.java
│  │
│  └─res
│      ├─drawable ...
│      │
│      ├─drawable-v24 ...
│      │
│      ├─layout
│      │      activity_main.xml
│      │      activity_pairing.xml

아두이노
├─Arduino
│      Arduino Source File.ino
```

# Reference
[안드로이드 공식 문서](https://developer.android.com/reference/android/bluetooth/BluetoothDevice)