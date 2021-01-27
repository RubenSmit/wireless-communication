#include <mbed.h>
#include <nRF24L01P.h>

#define masterAddress "Master"
#define slaveAddress "Slave"

nRF24L01P nrf24(D11, D12, D13, D8, D9, D7);    // mosi, miso, sck, csn, ce, irq

#define TRANSFER_SIZE 4
char txData[TRANSFER_SIZE], rxData[TRANSFER_SIZE];

struct payloadStruct{
  uint8_t ledNumber;
}payload;

int main() {
  printf("Booting up....\n");
  
  nrf24.powerUp();
  nrf24.disableAutoAcknowledge();
  nrf24.setTxAddress(1234, sizeof(1234));
  nrf24.setRxAddress(5678, sizeof(5678));
  // Display the (default) setup of the nRF24L01+ chip
    printf( "nRF24L01+ Frequency    : %d MHz\r\n",  nrf24.getRfFrequency() );
    printf( "nRF24L01+ Output power : %d dBm\r\n",  nrf24.getRfOutputPower() );
    printf( "nRF24L01+ Data Rate    : %d kbps\r\n", nrf24.getAirDataRate() );


  // put your setup code here, to run once:

  while(1) {
    
    if(nrf24.readable()){
      int test = nrf24.read(NRF24L01P_PIPE_P0, rxData, sizeof(rxData));
      int i;
      for (i = 0; i < TRANSFER_SIZE; i++)
      {
          if (i > 0) printf(":");
          printf("%02X", rxData[i]);
      }
      printf("\n"); 
  }}
}