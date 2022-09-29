package com.daehyeon.blutetoothstudy.thread;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{

    private final BluetoothSocket socket; // BluetoothSocket 객체 선언
    private final InputStream inputStream; // InputStream 객체 선언
    private final OutputStream outputStream; // OutputStream 객체 선언

    BluetoothAdapter bluetoothAdapter;

    // 생성자
    public ConnectedThread(BluetoothSocket socket){
        this.socket = socket;
        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try{
            tempInputStream = socket.getInputStream();
            tempOutputStream = socket.getOutputStream();
        }catch (IOException e){
            e.printStackTrace();
        }
        inputStream = tempInputStream;
        outputStream = tempOutputStream;
    }

    // 쓰레드 시작
    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        // 블루투스 검색 취소 (블루투스 소켓 연결 전에 항상 해야하는 작업, 그렇지 않으면 성능 저하 발생)
        bluetoothAdapter.cancelDiscovery();
        byte[] buffer = new byte[1024];
        int bytes;
        while(true){
            try{
                bytes = inputStream.available();
                if(bytes != 0){
                    buffer = new byte[1024];
                    SystemClock.sleep(100);
                    bytes = inputStream.available();
                    bytes = inputStream.read(buffer, 0, bytes);
                }
            }catch (IOException e){
                e.printStackTrace();
                break;
            }
        }
        super.run();
    }

    // 입력 스트림에 데이터 삽입
    public void write(String input){
        byte[] bytes = input.getBytes();
        try{
            outputStream.write(bytes); // 입력 스트림으로 들어온 데이터를 출력 스크림으로 소켓 전송
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // 클라이언트 소켓을 닫고, 쓰레드 완료 처리
    public void cancel(){
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
