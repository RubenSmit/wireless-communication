#include <mbed.h>
#include <nRF24L01P.h>

#define TRANSMITTER 1
#define RECIEVER 1

nRF24L01P nrf24(D11, D13, D12, D8, D9, D7);    // mosi, miso, sck, csn, ce, irq

int main() {
  nrf24.powerUp();

  #ifdef TRANSMITTER

  nrf24.setTransmitMode();
  nrf24.enable();

  printf( "TRANSMITTER" );
  printf( "Frequency    : %d MHz\r\n",  nrf24.getRfFrequency() );
  printf( "Output power : %d dBm\r\n",  nrf24.getRfOutputPower() );
  printf( "Data Rate    : %d kbps\r\n", nrf24.getAirDataRate() );

  while(1) {

  }

  #endif

  #ifdef RECIEVER

  nrf24.setReceiveMode();
  nrf24.enable();

  printf( "RECIEVER" );
  printf( "Frequency    : %d MHz\r\n",  nrf24.getRfFrequency() );
  printf( "Output power : %d dBm\r\n",  nrf24.getRfOutputPower() );
  printf( "Data Rate    : %d kbps\r\n", nrf24.getAirDataRate() );

  while(1) {

  }

  #endif
}