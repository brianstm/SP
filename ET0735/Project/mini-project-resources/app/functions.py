import sys, os

sys.path.append(os.path.join(os.path.dirname(__file__), '..', 'hal'))

from threading import Thread
import time
import RPi.GPIO as GPIO
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

password_correct1 = [1, 2, 3, 4]
password_correct2 = [4, 3, 2, 1]
password_correct3 = [8, 0, 0, 8]
RFID_id = "None"


def pass_correct(password, count):
    if password == password_correct1 or password == password_correct2 or password == password_correct3 and count < 3:
        return True


def pass_incorrect(password, count):
    if password != password_correct1 or password != password_correct2 or password != password_correct3 and count < 3:
        return True


def max_attempt(count):
    if count >= 3:
        return True


def check_RFID(id):
    if id != RFID_id:
        return True


def check_door(val):
    if val >= 340:
        return True