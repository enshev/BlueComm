## Bluecomm

This is an application to control arduino via android with bluetooth communication, data sent via android application is the serial data (ASCII data).
You can also use this application to read the bluetooth RSSI value.

there may be some bugs when pairing (depending smartphone you use).

### Features
- Send data to the arduino with TextBox
- Send the data with the ON / OFF (HIGH / LOW)
- Data send PWM 0-255 with seekbar
- Read the bluetooth RSSI value

### Device
on this project I use arduino mega 2560 and bluetooth hc-05 series (you can use any kind as long as it supports the bluetooth serial communication).
if you are using another type arduino device you have to adjust the output and input pins.

### Usage
This project uses the ADT Eclipse IDE, import the zip folder into your IDE.
Scratch for arduino you can compile [bluecomm.ino](https://github.com/enshev/BlueComm/blob/master/bluecomm.ino) with the Arduino IDE (adjust pin with arduino type you use).

### Command Format
Send from Apps = (*, X, Pin, Value)
-'*' = start command
-X = analogwrite (11) / digitalwrite (10) 11 and 10 is the value that is defined
-Pin = pin on arduino
-value = can use (3) for HIGH and (2) for LOW, or 0-255 if the PWM value

### Next Update
- Read data from arduino

### Link
-[Arduino Mega 2560](http://arduino.cc/en/Main/arduinoBoardMega2560)
-[Bluetooth HC-05(http://www.exp-tech.de/service/datasheet/HC-Serial-Bluetooth-Products.pdf)
-[Eclipse ADT](http://developer.android.com/sdk/index.html)
-[Arduino IDE](http://arduino.cc/en/main/software)