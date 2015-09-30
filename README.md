# ComputerSMS

Using this Android application in conjunction with the [ComputerSMSServer Java Application](https://github.com/JacobMDavidson/ComputerSMSServer) will allow you to send and receive text messages through your Android phone using your computer. The Android phone and Computer must be connected to the same local network.

## Usage

1. Within the ComputerSMSServer application:
  * Select *"Start"* and
  * Wait for the message *"Ready to connect"* to be displayed. This will take several seconds.
2. Wait for the toggle button marked *"Enable"* to be enabled and select it.
3. The message *"Service started"* should now be displayed on both this application and the Java ComputerSMSServer desktop application.
4. To send a message from the Java ComputerSMSServer application:
  * Enter the 10 digit recipient's number in the *"Phone Number:"* text box.
  * Enter the message in the *"Message:"* text box.
  * Select the *"Send"* button.

## Known Bugs

* If the Android app is disabled while the ComputerSMSServer is running, both applications will need to be closed and restarted before the Android app can once again connect to the ComputerSMSServer.
* If the ComputerSMSServer is closed while the Android app is still enabled, the Android app will need to be disabled and restarted before it can once again connect to the ComputerSMSServer.
