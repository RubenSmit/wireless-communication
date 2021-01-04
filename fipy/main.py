from network import Bluetooth
from machine import Pin
from machine import PWM

currentAngle = 0

def conn_cb (bt_o):
    events = bt_o.events()
    if  events & Bluetooth.CLIENT_CONNECTED:
        print("Client connected")
    elif events & Bluetooth.CLIENT_DISCONNECTED:
        print("Client disconnected")

def char1_cb_handler(chr, data):

    # The data is a tuple containing the triggering event and the value if the event is a WRITE event.
    # We recommend fetching the event and value from the input parameter, and not via characteristic.event() and characteristic.value()
    events, value = data
    if  events & Bluetooth.CHAR_WRITE_EVENT:
        currentAngle = int.from_bytes(value, "big")
        setServoPwn(currentAngle)

def setServoPwn(angle):
    servo.duty_cycle(((angle / 180) * 0.05) + 0.05)

bluetooth = Bluetooth()
bluetooth.set_advertisement(name='LoPy')
bluetooth.callback(trigger=Bluetooth.CLIENT_CONNECTED | Bluetooth.CLIENT_DISCONNECTED, handler=conn_cb)
bluetooth.advertise(True)

srv1 = bluetooth.service(uuid=0x1843 , isprimary=True)
chr1 = srv1.characteristic(uuid=0x2763 , value=0)
char1_cb = chr1.callback(trigger=Bluetooth.CHAR_WRITE_EVENT, handler=char1_cb_handler)

pwm = PWM(0, frequency=50)
servo = pwm.channel(0, pin='P23', duty_cycle=0.0)
setServoPwn(currentAngle)