# import the necesery libs
from network import Bluetooth
from machine import Pin
from machine import PWM
import pycom
import time

# make a global variable to store the current servo angle 

currentAngle = 0 

# Callback function for the bleutooth connection.
def conn_cb (bt_o):
    events = bt_o.events() # Get the events that triggerd the callback
    if  events & Bluetooth.CLIENT_CONNECTED: # if the event was the connection event 
        print("Client connected")
        pycom.heartbeat(False) # disable the heartbeat
        pycom.rgbled(0x007f00) # show a green led
        time.sleep(3) # sleep for 3 sec
        pycom.heartbeat(True) # enable the hartbeat
    elif events & Bluetooth.CLIENT_DISCONNECTED: # if the event was the disconection event
        print("Client disconnected")
        pycom.heartbeat(False) # disable the heartbeat
        pycom.rgbled(0x7f0000) # show a red led
        time.sleep(3) # sleep for 3 sec
        pycom.heartbeat(True) #  enable the heartbeat

# callback handeler for the data event
def char1_cb_handler(chr, data): 
    events, value = data # store the events and data
    if  events & Bluetooth.CHAR_WRITE_EVENT: # if the event was a write event
        currentAngle = int.from_bytes(value, "big") # get the value from the payload
        setServoPwn(currentAngle) # set the servo to the right position
        chr1.value(currentAngle) # update the value that is displayed over Bluetooth
        print("Set new angle: ", currentAngle)

# Function to caculate the pwm value for the angle given.
def setServoPwn(angle):
    servo.duty_cycle(((angle / 180) * 0.05) + 0.05) # calculate the right value for PWM based on the angle given

bluetooth = Bluetooth() # Get a bluetooth instance
bluetooth.set_advertisement(name='FyPi') # Set the name
bluetooth.callback(trigger=Bluetooth.CLIENT_CONNECTED | Bluetooth.CLIENT_DISCONNECTED, handler=conn_cb) # set up the callbacks for connect and disconnect events
bluetooth.advertise(True) # advertise the device

print("Started BLE")

srv1 = bluetooth.service(uuid=0x1843 , isprimary=True) # set up the service to display the current angle of the servo
chr1 = srv1.characteristic(uuid=0x2763 , value=currentAngle) # set up the characteristic to get the server angle 
char1_cb = chr1.callback(trigger=Bluetooth.CHAR_WRITE_EVENT, handler=char1_cb_handler) # set up the callback when writen to characteristic

pwm = PWM(0, frequency=50) # make a pwm provider
servo = pwm.channel(0, pin='P23', duty_cycle=0.0) # Setup the pwm for the servo
setServoPwn(currentAngle) # Set the servo the the initial angle