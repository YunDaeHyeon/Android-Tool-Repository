package com.daehyeon.blutetoothstudy.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.daehyeon.blutetoothstudy.R;
import com.jaredrummler.android.widget.AnimatedSvgView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/*
    스플래쉬 액티비티. 어플리케이션 최초 시작 액티비티이며 SVG 애니메이션을 구현
    레이아웃 : activity_splash.xml

    사용한 라이브러리 : AnimatedSvgView (https://github.com/jaredrummler/AnimatedSvgView)
    SVG 제작 사이트 : http://emblemmatic.org/markmaker/#/
    build.gradle(Module) 의존성 추가 : implementation 'com.jaredrummler:animated-svg-view:1.0.6'
    app/res/values/string.xml에서 SVG 문자의 path를 가져와 string-array에 삽입.
 */

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AnimatedSvgView svgView = (AnimatedSvgView) findViewById(R.id.animated_svg_view);
        svgView.start(); // 애니메이션 시작
        // 로딩화면 시작
        LoadingStart();
    }

    private void LoadingStart(){
        // handle + er : 무언가를 다루는 사람.
        // Handler 객체는 다른 객체들이 보내는 메시지를 받고 이를 처리시키는 객체이다.
        Handler handler = new Handler();
        // postDelayed는 코드 실행 딜레이 처리 메서드.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000); // 지정한 ms가 끝나면 오버라이딩 메서드 run이 실행된다.
    }
}