# NRF24L01+ documentation

## General	questions

1. **What is the maximum output power from the NRF24 module?**   
	- 0 dBm [2]
	1. **How does that compare to the output power of a 3G/4G mobile phone?**
		- Between 0 and 30 dBm [1]
2. **What is a frequency band?**
	* The operating frequencys that a device works in.
3. **Which frequency bands can be used by this radio? (check the datasheet)**
	* From 2.400GHz to 2.525GHz [2]
	1. **Which bands are used by WiFi and Bluetooth?**
		- Wifi: 2401 MHz to 2495 MHz [3]
		- Bluethooth: 2400 to 2483.5 MHz [4]
	2. **Where does the NRF24 overlap with these bands?**
		- From stat to  finnish only the 5G wifi is not overlapped
4. **What is the wavelength in meters for the used NRF24 frequency band?**
	- 300.000.000 / 2.400.000.000 = 0.125m
	- 300.000.000 / 2.483.500.000 = 0.120m
5. **Which data rates does the radio support?**
	- 250kbps, 1Mbps and 2Mbps [2]
6. **How many channels does the radio support?**
	- 126 channels so 8 bit [2]
	1. **What is the relationship between channels and frequencies?**
		- F0= 2400 + RF_CH MHz you add the channel to 2400 and you get the frequency [2]
7. **Which modulation technique does the radio use? Explain how this modulation technique
works in your own words.**	
	- GFSK: Gaussian frequency shift keying is a modulation method for digital communication. it uses a Gaussian filter before the signal is send. FSK is the input in this system this is modulation where the frycuentcy of a signal is changed depending if the bit is a 1 or 0
 
8. **Power consumption in the idle mode states (power-down, standby-I and standby-II). Explain
in your own words what the different states do.**
	- power-down: 900nA, this mode has the radio turned off so only the standby of the chip is used
	- standby-I: 26µA, this mode reduces power by running the radio in a half turned off way
	- standby-II: 320µA, the same mode as standby-i but with extra buffers so the startup time is lower

9. **Power consumption in the transmit state at the four different output levels.**
	- 0dBm output power: 11.3 mA
	- -6dBm output power: 9.0 mA
	- -12dBm output power: 7.5 mA
	- -18dBm output power: 7.0 mA
10. **Power consumption in the receive mode state for the three different air data rates.**
	- Supply current 2Mbps: 13.5 mA
	- Supply current 1Mbps: 13.1 mA
	- Supply current 250kbps: 12.6mA

11. **What is Enhanced ShockBurst and what are the most important functions? Describe this in
your own words.**
	- Enhanced ShockBurst (ESB) is a protocol supporting two way data transfer it takes care of importend features such as retransmission, buffering, packet acknowledgment.

12. **On what layer in the ISO/OSI model does this operate?**
	- layer 2
13. **Look at the format of the radio packet (section 7.3 on page 28 of the datasheet) and describe
the function of the following fields:**
	- Address: addres of the reciever this is for what radio the packet is ment
	- PID (Packet Identification): this part contains all the meta data of the packet like the legth of the payload, packet information (is it a retransmit or not)
	- No acknowledgement flag  should it send a ack back to the sender this so it knows you got the message
	- CRC: this is a checksum bytes these are set by means of a calculation 
14. **Enhanced ShockBurst is able to automatically validate a packet. Describe how this process
works in your own words. Give an example of a packet that is broken and describe how the
radio is able to detect this.**
	- before transmission the CRC bytes are calcculated by the radio this is done over the hole packet exept the crc bytes this is than recalculated at the receving end and checked, if the bytes dont match you can detect that the transmission was not succesfull. i

[1]: https://oem.bmj.com/content/61/9/769
[2]: https://www.nordicsemi.com/-/media/DocLib/Other/Product_Spec/nRF24L01PPSv10.pdf
[3]: https://en.wikipedia.org/wiki/List_of_WLAN_channels#2.4_GHz_(802.11b/g/n/ax)
[4]:https://www.bluetooth.com/learn-about-bluetooth/key-attributes/range/#:~:text=Bluetooth%C2%AE%20technology%20uses%20the,for%20low%2Dpower%20wireless%20connectivity.