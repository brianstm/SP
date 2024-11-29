import sys, os

sys.path.append(os.path.join(os.path.dirname(__file__), '..', 'hal'))

from threading import Thread
import time
import RPi.GPIO as GPIO
from flask import Flask, render_template, url_for
import hal_led as led
import hal_input_switch as switch
import hal_dc_motor as motor
import hal_servo as servo
import hal_usonic as usonic
import hal_moisture_sensor as moisture_sensor
import hal_temp_humidity_sensor as temp_humid_sensor
import hal_rfid_reader as rfid_reader
import hal_adc as adc
import hal_lcd as LCD
import hal_keypad as keypad
import hal_buzzer as buzzer
import functions

app = Flask(__name__)
global password
password = []
password_correct1 = [1, 2, 3, 4]
password_correct2 = [4, 3, 2, 1]
password_correct3 = [8, 0, 0, 8]
display_ast = ['*', '**', '***', '****']
global count
count = []
global status
status = 0
global lockStatus
lockStatus = ""
value = 0
LDR_string = "Off"


def key_pressed(key):
    global cbk_func
    global lcd
    global count

    lcd.lcd_display_string("Enter password", 1)
    password.append(key)
    print(password)
    i = len(password)
    lcd.lcd_display_string(display_ast[i - 1], 2)

    if i == 4 and functions.pass_correct(password, len(count)):
        print("Password Correct")
        lcd.lcd_clear()
        lcd.lcd_display_string("Correct", 1)
        time.sleep(1)
        lcd.lcd_clear()
        lcd.lcd_display_string("Door Unlocking", 1)
        time.sleep(1)
        door_status(1)
        lcd.lcd_clear()
        lcd.lcd_display_string("Unlocked", 1)

    elif i == 4 and functions.pass_incorrect(password, len(count)):
        print("Password Incorrect")
        lcd.lcd_clear()
        lcd.lcd_display_string("Incorrect", 1)
        time.sleep(1)
        lcd.lcd_clear()
        lcd.lcd_display_string("Enter password", 1)
        password.clear()
        count.append(1)
        door_status(0)

    elif functions.max_attempt(len(count)):
        print("System lock - Max tries reached")
        lcd.lcd_clear()
        lcd.lcd_display_string("System lock", 1)
        lcd.lcd_display_string("Max tries", 2)
        password.clear()
        door_status(0)


def door_status(status1):
    global status
    if status1 == 1:
        status = 1
        led.set_output(20, 0)

    elif status1 == 0:
        status = 0
        led.set_output(20, 1)


def IDS():
    while status != 1:
        pot_val = adc.get_adc_value(1)  # 0 - 1023
        if functions.check_door(pot_val):
            buzzer.short_beep(1)


def RFID():
    global status
    global count
    global password
    reader = rfid_reader.init()
    while True:
        switch1 = switch.read_slide_switch()
        id = reader.read_id_no_block()
        id = str(id)
        if switch1 == 1:
            print("RESET SYSTEM")
            count.clear()
            password.clear()
            lcd.lcd_clear()
            status = 0

            lcd.lcd_clear()
            lcd.lcd_display_string("Door Locking", 1)
            time.sleep(1)
            door_status(0)
            lcd.lcd_clear()

            lcd.lcd_display_string("Enter password", 1)
            threadIDS = Thread(target=IDS)
            threadIDS.start()
        if functions.check_RFID(id):
            lcd.lcd_clear()
            lcd.lcd_display_string("RFID read:", 1)
            lcd.lcd_display_string(id, 2)
            print("RFID card ID = " + id)
            time.sleep(1)
            lcd.lcd_clear()
            lcd.lcd_display_string("Door Unlocking", 1)
            time.sleep(1)
            door_status(1)
            lcd.lcd_clear()
            lcd.lcd_display_string("Unlocked", 1)


@app.route("/")
def index():
    templateData = {
        'title': 'ET0735 - DevOps Mini Project',
    }
    return render_template('raspberry_pi.html', **templateData)


@app.route("/<deviceName>/<action>")
def action(deviceName, action):
    global value
    global status
    global LDR_string

    if deviceName == 'ledRed':
        if action == "on":
            led.set_output(1, 1)
        elif action == "off":
            led.set_output(1, 0)

    elif deviceName == 'motor':
        if action == "on":
            motor.set_motor_speed(100)
        elif action == "off":
            motor.set_motor_speed(0)

    elif deviceName == 'sensor':
        if action == "refresh":
            moistlevel = moisture_sensor.read_sensor()
            value_list = temp_humid_sensor.read_temp_humidity()
            doorstat = check_door_status()

    elif deviceName == 'door':
        if action == "lock":
            deviceName = 'sensor'
            action = "refresh"
            status = 0
            count.clear()
            password.clear()
            lcd.lcd_clear()
            lcd.lcd_display_string("Door Locking", 1)
            time.sleep(1)
            door_status(0)
            lcd.lcd_clear()
            lcd.lcd_display_string("Enter password", 1)
            threadIDS = Thread(target=IDS)
            threadIDS.start()
        elif action == "unlock":
            status = 1
            count.clear()
            lcd.lcd_display_string("Door Unlocking", 1)
            time.sleep(1)
            door_status(1)
            lcd.lcd_clear()
            lcd.lcd_display_string("Unlocked", 1)
            password.clear()

    LDR = adc.get_adc_value(0)
    if (LDR > 800):
        LDR_string = "On"
    elif (LDR < 500):
        LDR_string = "Off"

    # Read Sensors Status
    buttonSts = switch.read_slide_switch()
    moistlevel = moisture_sensor.read_sensor()
    value_list = temp_humid_sensor.read_temp_humidity()
    while value_list[0] == -100 and value_list[1] == -100:
        value_list = temp_humid_sensor.read_temp_humidity()
        if value_list[0] != -100 and value_list[1] != -100:
            value = value_list
            print(value[0])

    templateData = {
        'title': 'House Security',
        'button': buttonSts,
        'rain_status': moistlevel,
        'temp_status': str(value[0]),
        'hum_status': str(value[1]),
        'door_status': check_door_status(),
        'ldr_status': LDR_string
    }
    return render_template('raspberry_pi.html', **templateData)


def check_door_status():
    global status
    if status == 1:
        return "Unlocked"
    elif status == 0:
        return "Locked"


def getkey():
    keypad.get_key()


def web():
    app.run(host='0.0.0.0', port=80, debug=False)


def run():
    door_status(0)
    threadRFID = Thread(target=RFID)
    threadRFID.start()
    threadIDS = Thread(target=IDS)
    threadIDS.start()

    lcd.lcd_clear()
    lcd.lcd_display_string("Enter password", 1)
    getkey()


if __name__ == "__main__":
    global lcd
    lcd = LCD.lcd()

    led.init()
    adc.init()
    buzzer.init()
    switch.init()
    motor.init()
    servo.init()
    moisture_sensor.init()
    temp_humid_sensor.init()
    keypad.init(key_pressed)

    threadWeb = Thread(target=web)
    threadWeb.start()

    door_status(0)
    run()

    # Run Python Flask Web Server
    # app.run(host='0.0.0.0', port=80, debug=True)
