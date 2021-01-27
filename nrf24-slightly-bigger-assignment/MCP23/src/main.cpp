#include <mbed.h>
#include <SPI.h>
#include <nRF24L01P.h>

void turnOnLed(int number);
void readButtons();

nRF24L01P nrf24(D11, D12, D13, D8, D9, D7);    // mosi, miso, sck, csn, ce, irq

#define TRANSFER_SIZE 4
char txData[TRANSFER_SIZE], rxData[TRANSFER_SIZE];

SPI spi(D11, D12, D13);
DigitalOut MCP23S08(D10);
DigitalOut NRF24CE(D9);


InterruptIn MCPint(D6);

// int positions[] = { 0x3E, 0x3D, 0x3B, 0x37, 0x2F, 0x1F};
int positions[] = { 0x3E, 0x3D, 0x37, 0x1F, 0x2F, 0x3B};
int LEDNUMBER = 0;

#define MCP23S08_ADDRESS 0x46


#define MCP23S08_IODIR		0x00
#define MCP23S08_IPOL		0x01
#define MCP23S08_GPINTEN	0x02
#define MCP23S08_DEFVAL		0x03
#define MCP23S08_INTCON		0x04
#define MCP23S08_IOCON		0x05
#define MCP23S08_GPPU		0x06
#define MCP23S08_INTF		0x07
#define MCP23S08_INTCAP		0x08
#define MCP23S08_GPIO		0x09
#define MCP23S08_OLAT		0x0A

void writeToMCP23S08(int a, int r, int v){
    auto NRF24CEold = NRF24CE;
    MCP23S08 = 0;
    NRF24CE = 1;
    spi.write(a); // Device opcode
    spi.write(r); // Write to GPINTEN register
    spi.write(v); // Set pin 6-7 to high
    MCP23S08 = 1;
    NRF24CE = NRF24CEold;
}

int readFromMCP23S08(int a, int r){
  auto NRF24CEold = NRF24CE;
  MCP23S08 = 0;
  NRF24CE = 1;
  spi.write(a);
  spi.write(r);
  return spi.write(positions[LEDNUMBER]);
  MCP23S08 = 1;
  NRF24CE = NRF24CEold;
}


int main() {
  // auto NRF24CEold = NRF24CE;
  // MCP23S08 = 0;
  // NRF24CE = 1;
  // spi.write(MCP23S08_ADDRESS); // Device opcode
  // for (uint8_t i = 0; i < MCP23S08_OLAT; i++)
  // {
  //     spi.write(0x00);
  // }
  
  // MCP23S08 = 1;
  // NRF24CE = NRF24CEold;
  
  // //set IO direction so the inputs and outputs are set
  // writeToMCP23S08(MCP23S08_ADDRESS, MCP23S08_IODIR, 0xC0 );
  // writeToMCP23S08(MCP23S08_ADDRESS, MCP23S08_GPPU, 0xC0 );
  // turnOnLed(LEDNUMBER);
  // while(1) {
  //     // turnOnLed(LEDNUMBER);
  //     readButtons();
  //     ThisThread::sleep_for(1s);
  // }
  nrf24.powerUp();
  nrf24.disableAutoAcknowledge();
  nrf24.setRxAddress(1234, sizeof(1234));
  nrf24.setTxAddress(5678, sizeof(5678));
  // Display the (default) setup of the nRF24L01+ chip
    printf( "nRF24L01+ Frequency    : %d MHz\r\n",  nrf24.getRfFrequency() );
    printf( "nRF24L01+ Output power : %d dBm\r\n",  nrf24.getRfOutputPower() );
    printf( "nRF24L01+ Data Rate    : %d kbps\r\n", nrf24.getAirDataRate() );

  while(1){
    nrf24.write( NRF24L01P_PIPE_P0, txData, 1);
  }
}

void turnOnLed(int number){
  writeToMCP23S08(MCP23S08_ADDRESS, MCP23S08_OLAT, positions[number] );
}

void readButtons(){
  int value = readFromMCP23S08(MCP23S08_ADDRESS,MCP23S08_OLAT);
  int buttons = value & ~ LEDNUMBER;
  printf("%d, %d\n", value, buttons);
}