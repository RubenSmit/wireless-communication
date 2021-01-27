#include <mbed.h>
#include <stdio.h>
#include <nRF24L01P.h>
#include "platform/mbed_thread.h"

nRF24L01P nrf24(D11, D12, D13, D8, D9, D7);    // mosi, miso, sck, csn, ce, irq

static int frequencies[] = { NRF24L01P_MIN_RF_FREQUENCY, NRF24L01P_MAX_RF_FREQUENCY };
static int outputPowers[] = { NRF24L01P_TX_PWR_ZERO_DB, NRF24L01P_TX_PWR_MINUS_6_DB, NRF24L01P_TX_PWR_MINUS_12_DB, NRF24L01P_TX_PWR_MINUS_18_DB };
static int dataRates[] = { NRF24L01P_DATARATE_250_KBPS, NRF24L01P_DATARATE_1_MBPS, NRF24L01P_DATARATE_2_MBPS };
static int artDelays[] = { 250, 1000, 4000 };
static int artRetrys[] = { 1, 2, 8, 15 };

Ticker heartbeat;

void transmitter() {
  thread_sleep_for(5000);

  nrf24.setTransmitMode();
  nrf24.enable();
  nrf24.enableAutoAcknowledge();

  printf( "TRANSMITTER\r\n" );
  printf( "Frequency;OutputPower;Datarate;ArtDelay;ArtRetry;PacketsSent;Run\r\n");

  char run = 0;

  for (int op = 0; op < (sizeof(outputPowers)/sizeof(*outputPowers)); op++) {
    int outputPower = outputPowers[op];

    for (int dr = 0; dr < (sizeof(dataRates)/sizeof(*dataRates)); dr++) {
      int dataRate = dataRates[dr];

      for (int ad = 0; ad < (sizeof(artDelays)/sizeof(*artDelays)); ad++) {
        int artDelay = artDelays[ad];

        for (int ar = 0; ar < (sizeof(artRetrys)/sizeof(*artRetrys)); ar++) {
          int artRetry = artRetrys[ar];

          nrf24.setRfOutputPower(outputPower);
          nrf24.enableAutoRetransmit(artDelay, artRetry);

          printf( "%d;%d;%d;%d;%d;%d;%d\r\n", frequencies[FREQUENCY], outputPower, dataRates[DATARATE], artDelay, artRetry, SAMPLES, (int) run);

          for (int i = 0; i < SAMPLES; i++) {
            nrf24.write(NRF24L01P_PIPE_P0, &run, sizeof(run));
          }

          run++;

          thread_sleep_for(100);
        }
      }
    }
  }

  printf( "DONE!\r\n" );
}

void reciever() {
  thread_sleep_for(5000);

  nrf24.setReceiveMode();
  nrf24.enable();
  nrf24.enableAutoAcknowledge();

  printf( "RECIEVER\r\n" );
  printf( "Frequency    : %d MHz\r\n",  nrf24.getRfFrequency() );

  char recieveData[DEFAULT_NRF24L01P_TRANSFER_SIZE];
  int recieveCount = 0;
  char currentRun = 0x00;
  int recieved = 0;

  while(1) {
    if ( nrf24.readable() ) {
      printf( "readable!\r\n" );
      recieveCount = nrf24.read(NRF24L01P_PIPE_P0, recieveData, sizeof(recieveData));

      if (recieveCount == 1 && currentRun == recieveData[0]) {
        recieved++;
      } else {
        printf( "Run: %d Recieved: %d\r\n", (int) currentRun, recieved);
        recieved = 0;
        currentRun = recieveData[0];
      }

      recieveCount = 0;
    }
  }
}

int main() {
  printf("starting...\r\n");

  nrf24.powerUp();
  nrf24.setRfFrequency(frequencies[FREQUENCY]);
  nrf24.setAirDataRate(dataRates[DATARATE]);

  #ifdef TRANSMITTER
  transmitter();
  #endif

  #ifdef RECIEVER
  reciever();
  #endif

  while (1) {
    thread_sleep_for(1000);
  }
}