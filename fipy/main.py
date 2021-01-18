from network import Bluetooth
from machine import Pin
from machine import PWM
import pycom
import time

currentAngle = 0

def conn_cb (bt_o):
    events = bt_o.events()
    if  events & Bluetooth.CLIENT_CONNECTED:
        print("Client connected")
        pycom.heartbeat(False)
        pycom.rgbled(0x007f00)
        time.sleep(3)
        pycom.heartbeat(True)
    elif events & Bluetooth.CLIENT_DISCONNECTED:
        print("Client disconnected")
        pycom.heartbeat(False)
        pycom.rgbled(0x7f0000)
        time.sleep(3)
        pycom.heartbeat(True)

def char1_cb_handler(chr, data):
    events, value = data
    if  events & Bluetooth.CHAR_WRITE_EVENT:
        currentAngle = int.from_bytes(value, "big")
        setServoPwn(currentAngle)
        chr1.value(currentAngle)
        print("Set new angle: ", currentAngle)

def setServoPwn(angle):
    servo.duty_cycle(((angle / 180) * 0.05) + 0.05)

bluetooth = Bluetooth()
bluetooth.set_advertisement(name='Fypi')
bluetooth.callback(trigger=Bluetooth.CLIENT_CONNECTED | Bluetooth.CLIENT_DISCONNECTED, handler=conn_cb)
bluetooth.advertise(True)

print("Started BLE")

srv1 = bluetooth.service(uuid=0x1843 , isprimary=True)
chr1 = srv1.characteristic(uuid=0x2763 , value=currentAngle)
char1_cb = chr1.callback(trigger=Bluetooth.CHAR_WRITE_EVENT, handler=char1_cb_handler)

pwm = PWM(0, frequency=50)
servo = pwm.channel(0, pin='P23', duty_cycle=0.0)
setServoPwn(currentAngle)