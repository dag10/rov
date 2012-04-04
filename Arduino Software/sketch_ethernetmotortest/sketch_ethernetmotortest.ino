/* Definitions */
#define NUM_MOTORS 10
#define BUFFER_SIZE 20

/* Settings */

const int motorPins[NUM_MOTORS] = {
  2,  // 0 = Top front
  3,  // 1 = Top back
  4,  // 2 = Forward left
  5,  // 3 = Forward right
  6,  // 4 = Sideways
  7,
  8,
  9,
  10,
  11
};
const int serialBaud = 9600;
const int debugLED = 13;

/* Variables */

int motorSpeeds[NUM_MOTORS];
int trueMotorSpeed = 128;

/* Functions */
int readSerialNumber();
void printSpeeds();
void resetMotors();

/* Setup */

void setup() {
  for (int i = 0; i < NUM_MOTORS; i++)
    pinMode(motorPins[i], OUTPUT);
    
  Serial3.begin(serialBaud);
  
  if (debugLED > 0)
    pinMode(debugLED, OUTPUT);
  
  resetMotors();
}

/* Loop */

void loop() {
  if (Serial3.available())
    scanSerial();
    
  /*
  if (Serial.available()) {
    int motorSpeed = readSerialNumber();
    if (motorSpeed > 100) motorSpeed = 100;
    if (motorSpeed < -100) motorSpeed = -100;
    
    motor = (int)((((float)(motorSpeed) + 100.f) * 255.f) / 200.f);
    
    Serial.print("Motor Speed: ");
    Serial.print(motorSpeed);
    Serial.print("\tTrue Motor Speed: ");
    Serial.println(trueMotorSpeed);
  }
  */
  
  delay(10);
}

/* Read number from serial */
void scanSerial() {
  static char motorBuffer[BUFFER_SIZE];
  static char speedBuffer[BUFFER_SIZE];
  static int motorBufferIndex = 0;
  static int speedBufferIndex = 0;
  static int buffer = 0;
  
  char input = Serial3.read();
  if (input == ',') {
    motorBuffer[motorBufferIndex] = 0; // Null terminator
    buffer = 1;
    motorBufferIndex = speedBufferIndex = 0;
  } else if (input == '\n') {
    speedBuffer[speedBufferIndex] = 0; // Null terminator
    buffer = 0;
    motorBufferIndex = speedBufferIndex = 0;
    int motor = atoi(motorBuffer);
    int motorSpeed = (int)((((float)(atoi(speedBuffer)) + 100.f) * 255.f) / 200.f);
    if (motor >= 0 && motor < NUM_MOTORS) {
      motorSpeeds[motor] = motorSpeed;
      analogWrite(motorPins[motor], motorSpeeds[motor]);
      //printSpeeds();
      //digitalWrite(debugLED, motor == 4);
      if (motor == 4) digitalWrite(debugLED, motorSpeeds[4] > 127);
    }
  } else { // Number to be read into a buffer
    if (buffer == 0) {
      if (motorBufferIndex < BUFFER_SIZE - 1)
        motorBuffer[motorBufferIndex++] = input;
    } else if (buffer == 1) {
      if (speedBufferIndex < BUFFER_SIZE - 1)
        speedBuffer[speedBufferIndex++] = input;
    }
  }
}

/* Print motor speeds to Serial for debugging */
void printSpeeds() {
  for (int i = 0; i < NUM_MOTORS; i++) {
    Serial3.print("pin[");
    Serial3.print(motorPins[i]);
    Serial3.print("] = ");
    Serial3.println(motorSpeeds[i]);
  }
  
  Serial3.println("-------------------------------------");
}

/* Reset all motors to no speed (2.5V) */
void resetMotors() {
  for (int i = 0; i < NUM_MOTORS; i++) {
    motorSpeeds[i] = 127;
    analogWrite(motorPins[i], motorSpeeds[i]);
  }
}
