# Throughput test app

This chapter describes the research preformed for the Throughput test app assignment.

## Introduction

For the NRF24L01+ a data rate can be configured. This is the amount of data that will be transmitted in a period of time. In practice this data rate is not reached. This research tries to determine the actual data rate of the NRF24L01+. Firstly the problem and research question is defined. Secondly the methodology used to answer the research question is described. Thirdly the results of the research is presented. Finally a conclusion and answer to the research question is given.

## Problem definition

The actual data rate of the NRF24L01+ does not precicely match the data rate set in the settings. It is unknow what the actual datarate is and therfore unknown how long it takes to transmit a large amount of data. Therefore the following research questions are determined:

1. How long does it take to transmit 10.000 32-bit numbers (320.000 bits in total) from a transmitter to a reciever for a set data rate.
2. What is the actual data rate when transmitting 320.000 bits and how much does it differ from the set data rate.
3. What causes the difference between the actual and set data rate.

During the tests all numbers must be recieved by the reciever.

## Methodology

Two Nucleos will be exquipped with a NRF24L01+, one using the IoT shield and one using a breakout board. Both Nucleos will be connected to a computer using USB cables. A example of this setup is shown in the image below.

![Nucleos-with-nrf24.jpg](img/Nucleos-with-nrf24.jpg)

One of the Nucleos will act as the transmitter, the other as the reciever. The transmitter will transmit the 10.000 32-bit numbers to the reciever. The reciever will acknowledge if the packet has been recieved using auto acknowledge. The transmitter will record the total time it takes to transmit all numbers as well as the total amount of retries. This will be sent to a serial monitor via the USB cable.

## Results

Due to problems with using the NRF24L01+ on a Nucleo we where unable to complete this assignment and get any results. Please see [the implementation of the LED app chapter](https://rubensmit.github.io/wireless-communication/led-app/#implementation) for a more detailed description of the problems we faced and our attempts to overcome these.

## Conclusion

Due to a lack of results we can not give a conclusion.

### How long does it take to transmit 10.000 32-bit numbers (320.000 bits in total) from a transmitter to a reciever for a set data rate

We where unable to answer this question.

### What is the actual data rate when transmitting 320.000 bits and how much does it differ from the set data rate

We where unable to answer this question.

### What causes the difference between the actual and set data rate

Although we do not have results one of the reasons that the set data rate is not reached is the auto acknowledge communication. This adds a overhead to the communication and limits the actual data rate.
