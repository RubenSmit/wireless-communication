# Packet Error Rate App

This chapter describes the research preformed for the packet error rate app assignment.

## Introduction

The goal of the Packet Error Rate app is to measure the packet error rate when sending a number of messages while varying the output power for sending, the frequency channels, data rates and Auto Retransmit settings of the NRF24L01+. Firstly the problem and research question is defined. Secondly the methodology used to answer the research question is described. Thirdly the results of the research is presented. Finally a conclusion and answer to the research question is given.

## Problem definition

The parameters for the output power, the frequency channels, data rates and Auto Retransmit settings influence the packet error rate of the app. To prevent package loss the optimal setting of the parameters should be determined. Therefore our research question is:

1. What combination of output power, the frequency channels, data rates and Auto Retransmit settings of the NRF24L01+ gives the lowest package loss.

## Methodology

Two Nucleos will be exquipped with a NRF24L01+, one using the IoT shield and one using a breakout board. Both Nucleos will be connected to a computer using USB cables. A example of this setup is shown in the image below.

![Nucleos-with-nrf24.jpg](img/Nucleos-with-nrf24.jpg)

One of the Nucleos will act as the transmitter, the other as the reciever. They will listen to serial commands recieved via the USB cable to synchonosly cycle through different combinations of settings. The transmitter will send a fixed number of packets. The reciever will measure the total amount of packets recieved. The transmitter will record the time it took to send all packets. A csv file will be generated containing the value of each setting, the amount of packets sent and recieved and the total transmit time.

## Results

Unfortunately we did not succeed in getting the nrf24l01+ running with a Nucleo. Please see [the implementation of the LED app chapter](https://rubensmit.github.io/wireless-communication/led-app/#implementation) for a more detailed description of the problems we faced and our attempts to overcome these.

Ultimately we where unable to collect any results. In the folder [/nrf24-packet-error-rate](https://github.com/RubenSmit/wireless-communication/tree/main/nrf24-packet-error-rate/PacketErrorRAte) a preliminary setup for the app can be found. This is a work-in-progress application and not near the final solution.

## Conclusion

Due to the lack of results a conclusion can not be given.
