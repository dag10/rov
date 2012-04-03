/* Settings */

const int motorPin = 7;
const int serialBaud = 9600;

/* Variables */

int motorSpeed = 0;
int trueMotorSpeed = 128;

/* Functions */
int readSerialNumber();

/* Setup */

void setup() {
  pinMode(motorPin, OUTPUT);
  Serial.begin(serialBaud);
  Serial.println("BEGIN");
}

/* Loop */

void loop() {
  analogWrite(motorPin, trueMotorSpeed);
  
  if (Serial.available()) {
    motorSpeed = readSerialNumber();
    if (motorSpeed > 100) motorSpeed = 100;
    if (motorSpeed < -100) motorSpeed = -100;
    
    trueMotorSpeed = (int)((((float)(motorSpeed) + 100.f) * 255.f) / 200.f);
    
    Serial.print("Motor Speed: ");
    Serial.print(motorSpeed);
    Serial.print("\tTrue Motor Speed: ");
    Serial.println(trueMotorSpeed);
  }
  
  delay(10);
}

/* Read number from serial */
int readSerialNumber() {
  char buffer[20];
  int i = 0;
  
  while (i < sizeof(buffer) && buffer[i-1] != '\n')
    buffer[i++] = Serial.read();
  
  buffer[i] = 0; // Null terminator
  
  return atoi(buffer);
}
