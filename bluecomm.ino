#define START_CMD_CHAR '*'
#define CMD_DIGITALWRITE 10
#define CMD_ANALOGWRITE 11
#define CMD_TEXT 12
#define CMD_READ 13
#define CMD_AUTO 14
#define CMD_MANUAL 15
#define PIN_HIGH 3
#define PIN_LOW 2

#define ldrPin 0
#define tempPin 1

String inText;

float tempC = 0;
int tempHot = 30;
int ldrValue = 0;
int Terang = 150;

void setup() {
  Serial.begin(9600);
  Serial.flush();
}

void loop()
{
  Serial.flush();
  int ard_command = 0;
  int pin_num = 0;
  int pin_value = 0;

  char get_char = ' ';  //read serial

  // wait for incoming data
  if (Serial.available() < 1) return; // if serial empty, return to loop().

  // parse incoming command start flag
  get_char = Serial.read();
  if (get_char != START_CMD_CHAR) return; // if no command start flag, return to loop().

  // parse incoming command type
  ard_command = Serial.parseInt(); // read the command

  // parse incoming pin# and value
  pin_num = Serial.parseInt(); // read the pin
  pin_value = Serial.parseInt();  // read the value

  if (ard_command == CMD_DIGITALWRITE){
    if (pin_value == PIN_LOW) pin_value = LOW;
    else if (pin_value == PIN_HIGH) pin_value = HIGH;
    else return; // error in pin value. return.
    set_digitalwrite(pin_num,  pin_value);  // Uncomment this function if you wish to use
    return;
  }
  if (ard_command == CMD_ANALOGWRITE) {
    analogWrite(pin_num, pin_value );
    return;
  }
  if (ard_command == CMD_AUTO){
    Serial.println("Auto Mode");
    automode();
    return;
  }
  if (ard_command == CMD_MANUAL){
    Serial.println("Manual Mode");
    return;
  }
}
void set_digitalwrite(int pin_num, int pin_value)
{
  switch (pin_num) {
  case 47:
    pinMode(47, OUTPUT);
    digitalWrite(47, pin_value);
    break;
  case 45:
    pinMode(45, OUTPUT);
    digitalWrite(45, pin_value);
    break;
  case 43:
    pinMode(43, OUTPUT);
    digitalWrite(43, pin_value);
    break;
  case 41:
    pinMode(41, OUTPUT);
    digitalWrite(41, pin_value);
    break;
  case 10:
    pinMode(10, OUTPUT);
    digitalWrite(10, pin_value);
    break;
  case 9:
    pinMode(9, OUTPUT);
    digitalWrite(9, pin_value);
    break;
  case 6:
    pinMode(6, OUTPUT);
    digitalWrite(6, pin_value);
    break;
  case 5:
    pinMode(5, OUTPUT);
    digitalWrite(5, pin_value);
    break;
  case 4:
    pinMode(4, OUTPUT);
    digitalWrite(4, pin_value);
    break;
  }
}
void automode()
{
  while (1)
  {
    Serial.flush();
    int ard_command = 0;

    char get_char = ' ';  //read serial

    // wait for incoming data
    if (Serial.available() < 1); // if serial empty, return to loop().

    // parse incoming command start flag
    get_char = Serial.read();
    if (get_char == START_CMD_CHAR) // if no command start flag, return to loop().

      ard_command = Serial.parseInt(); // read the command

    if (ard_command == CMD_MANUAL)
    {
      Serial.println("Manual Mode");
      break;
    }
    {
      ldrValue = analogRead(ldrPin);
      Serial.print(ldrValue);
      if (ldrValue <= Terang)
      {
        Serial.println(" Cahaya Terang");
        pinMode(6, OUTPUT);
        digitalWrite(6, LOW);
      }
      else
      {
        Serial.println(" Cahaya Gelap");
        pinMode(6, OUTPUT);
        digitalWrite(6, HIGH);
      }
      tempC = analogRead(tempPin);
      tempC = tempC = (4.97 * tempC * 100.0)/1024.0;
      if (tempC >= tempHot)
      {
        pinMode(5, OUTPUT);
        digitalWrite(5, HIGH);
      }
      else
      {
        pinMode(5, OUTPUT);
        digitalWrite(5, LOW);
      }
      Serial.print(" Suhu : ");
      Serial.print((int)tempC,DEC);
      Serial.println(" C");

      delay(1000);
    }
  }
}
