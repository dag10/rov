/* Definitions */
#define NUM_MOTORS 10
#define BUFFER_SIZE 20
#define DEBUG_MOTORS false
#define SEND_FEEDBACK true

/* Settings */

const int motorPins[NUM_MOTORS] = {
  3,  // 0 = Top front
  5,  // 1 = Top back
  6,  // 2 = Forward left
  9,  // 3 = Forward right
  10,  // 4 = Sideways
  11,  // 5 = Claw
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
void sendFeedback(char * message);

/* Setup */

void setup() {
  for (int i = 0; i < NUM_MOTORS; i++)
    pinMode(motorPins[i], OUTPUT);
    
  Serial.begin(serialBaud);
  sendFeedback("STARTED\n");
  
  if (debugLED > 0)
    pinMode(debugLED, OUTPUT);
  
  resetMotors();
  sendFeedback("INITIALIZED!\n");
}

/* Loop */

void loop() {
  if (Serial.available())
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
  
  static int counter = 0;
  if (counter++ > 100) {
    counter = 0;
    printSpeeds();
  }
  
  delay(10);
}

/* Read number from serial */
void scanSerial() {
  static char motorBuffer[BUFFER_SIZE];
  static char speedBuffer[BUFFER_SIZE];
  static int motorBufferIndex = 0;
  static int speedBufferIndex = 0;
  static int buffer = 0;
  
  char input = Serial.read();
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
      printSpeeds();
      if (SEND_FEEDBACK) {
        Serial.print(motor);
        Serial.print("=");
        Serial.println(motorSpeed);
      }
      //digitalWrite(debugLED, motor == 1);
      //if (motor == 4) digitalWrite(debugLED, motorSpeeds[4] > 127);
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

/* Sends feedback */
void sendFeedback(char * message) {
  if (!SEND_FEEDBACK) return;
  
  Serial.print(message);
}

/* Print motor speeds to Serial for debugging */
void printSpeeds() {
  if (!DEBUG_MOTORS) return;
  
  for (int i = 0; i < NUM_MOTORS; i++) {
    Serial.print("pin[");
    Serial.print(motorPins[i]);
    Serial.print("] = ");
    Serial.println(motorSpeeds[i]);
  }
  
  Serial.println("-------------------------------------");
}

/* Reset all motors to no speed (2.5V) */
void resetMotors() {
  for (int i = 0; i < NUM_MOTORS; i++) {
    motorSpeeds[i] = 127;
    analogWrite(motorPins[i], motorSpeeds[i]);
  }
}
