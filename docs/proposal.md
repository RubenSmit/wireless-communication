# Wireless Communication Proposal 
Willem Dekker - 436608
Ruben Smit - 423723

## Peripheral
Device: NRF52
Will provide a Human Interface Device (0x1812) service with a plane angle (0x2763) characteristic. The value of this characteristic will be set using a potentiometer.

## Central & Broadcaster
Device: Android phone
Will visualize the angle of the peripheral and enable setting the angle of the observer. It will provide a Generic Attribute (0x1801) service with a plane angle (0x2763) characteristic.

## Observer
Device: FiPy
Will observe the broadcaster and change the angle of a servo according to the broadcasted angle of the broadcaster.