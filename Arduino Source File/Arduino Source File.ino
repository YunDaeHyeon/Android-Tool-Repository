#include <SoftwareSerial.h>
const byte rxPin = 2;
const byte txPin = 3;
int LED = 8;
String getData;
SoftwareSerial hc06(txPin, rxPin);

void setup(){
  //시리얼 시작
  Serial.begin(9600);
  //블루투스 시리얼 포트 시작
  hc06.begin(9600);
  // LED핀 설정
  pinMode(LED, OUTPUT);
}

void loop(){
  //hc06에서 모니터로 데이터 쓰기
  if (hc06.available()){
    getData = hc06.readString();
    Serial.print(getData);
    if(getData == "true"){
      digitalWrite(LED, HIGH);
    }
    if(getData == "false"){
      digitalWrite(LED, LOW);
    }
  }
  
  //시리얼 모니터네서 hc06으로 데이터 쓰기
  if (Serial.available()){
    hc06.write(Serial.read());
  }
}

/*
  HC-06의 AT 펌웨어 확인 필수.
  2.0, 3.0 버전의 비트레이트, 시리얼 모니터 지정이 다르다.
  3.0 기준 비트레이트 변경 -> AT+UART=9600,0,0

  이때, AT 명령어를 사용할 때만 Both NL & CR을 사용하자.
  통신 데이터 확인할 때는 No line ending으로 하자.
  시리얼이나 블루투스를 읽어올때 에러난다.
*/