/*
  This is the main program for the Barduino machine.
  It discovers a Barduino-server on the network by listening to UDP-broadcasts,
  and connects to it. When connected it receives messages from
  the server and processes the message.
  The program controls 4 flowmeters and valves, and one LED.
  It uses these to pour drinks based on the information it receives
  from the server.

  Contributers: Dennis Wildmark, Jonathan Bocker

  @author Dennis Wildmark
*/

#include <SPI.h>
//#include <Ethernet.h>
#include <EthernetUdp.h>

IPAddress remote;

//define the tcp port to use
#define PORT_TCP (8008)

//define the udp port to use
#define PORT_UDP (28780)



class MockupClient {
  public:
  MockupClient();
  void print(String trosa);
  void stop();
  boolean connected();
  int available();
  int read();
  void connect(IPAddress remote, int port);
};

MockupClient::MockupClient()
{
  Serial.begin(115200);
  while (!Serial);
  Serial.println("Varfor???");
}

void MockupClient::connect(IPAddress remote, int port)
{
}

void MockupClient::print(String trosa)
{
  Serial.println(trosa);
}

void MockupClient::stop()
{
}

boolean MockupClient::connected()
{
  return true;
}

int MockupClient::read()
{
  return Serial.read();
}

int MockupClient::available()
{
  return Serial.available();
}


MockupClient client;
//EthernetClient client;

EthernetUDP udp;

//receivebuffer for the tcp connection
#define BUFFERLENGTH (3)

//Define the pins for the fluids
#define PIN_LIQUID1 (9)
#define PIN_LIQUID2 (8)
#define PIN_LIQUID3 (7)
#define PIN_LIQUID4 (6)
#define PIN_GLASSPLACED (5)
#define PIN_GLASSINDICATED (4)
#define PIN_INTERRUPT (3)
#define CONST_PULSESPERCL (35)

volatile int pulses = 0;

//define the states used in the state machine
typedef enum
{
  POURING_DRINK,
  READY,
  DRINK_DONE,
  NOT_CONNECTED
} state_t;

void setup() {

  byte mac[] = {
    0xDE, 0xAD, 0xBE, 0xEF, 0xDD, 0xAF
  };
  //initialize the ethernet shield with DHCP
  //Ethernet.begin(mac);  remember to uncomment!

  //give the Ethernet shield a second to initialize:
  delay(1000);

  //initialize the udp connection
  udp.begin(PORT_UDP);

  //initialize the output pins
  pinMode(PIN_LIQUID1, OUTPUT);
  pinMode(PIN_LIQUID2, OUTPUT);
  pinMode(PIN_LIQUID3, OUTPUT);
  pinMode(PIN_LIQUID4, OUTPUT);
  pinMode(PIN_GLASSPLACED, INPUT);
  pinMode(PIN_GLASSINDICATED, OUTPUT);
}

/*
  This function is called when the arduino is not connected
  to a server. It checks if there is a datagram in the buffer,
  and if so, connect to the IP which sent it.
*/
boolean discoverServer() {
  //create a packetbuffer at maximum size of a udp packet
  char packetBuffer[UDP_TX_PACKET_MAX_SIZE];
  int packetSize = udp.parsePacket();
  if (packetSize) {
    remote = udp.remoteIP();
    udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    while (!client.connected()) {
      client.connect(remote, PORT_TCP);
    }
    return true;
  }
  return false;
}

/*
  This function checks if there is any information available,
  and if so, places it into the receiveBuffer.
*/
boolean receive(char receiveBuffer[], uint8_t bufferLength) {
  if (client.available()) {
    //Read the content into a char array
    uint8_t count = 0;
    while (client.available() > 0) {
      char character = (char)client.read();
      if (count < bufferLength) {
        receiveBuffer[count] = character;
      }
      count++;
    }
    return true;
  }
  return false;
}

/*
  Assignt a pin number to the variable 'chosenLiquid'
  based on the input char.
*/
int8_t chooseLiquid(char liquid) {
  switch (liquid) {
    case 'A':
      return PIN_LIQUID1;
      break;
    case 'B':
      return PIN_LIQUID2;
      break;
    case 'C':
      return PIN_LIQUID3;
      break;
    case 'D':
      return PIN_LIQUID4;
      break;
    default:
      return -1;
      break;
  }
}

/*
  This function pours the drink. It attaches an interrupt to pin 3
  and compares the number of pulses registered to the desired amount.
  If the glass is removed during the pouring, the pouring pauses.
  The 'glassIndicatedPin' is connected to a LED that indicates if the
  glass is placed.
*/
void pourDrink(int8_t pin, uint16_t amount) {
  uint16_t realAmount = ((amount * CONST_PULSESPERCL) - CONST_PULSESPERCL) / 10 ;
  attachInterrupt(PIN_INTERRUPT, addPulse, RISING);
  pulses = 0;
  while ( pulses < realAmount) {
    if (glassPlaced()) {
      digitalWrite(pin, HIGH);
      digitalWrite(PIN_GLASSINDICATED, HIGH);
    } else {
      digitalWrite(pin, LOW);
      digitalWrite(PIN_GLASSINDICATED, LOW);
    }

  }
  digitalWrite(pin, LOW);
  detachInterrupt(0);
}

/*
  Function that is called by the interrupt vector.
  Simply adds a pulse to the counter.
*/
void addPulse() {
  pulses++;
}

/*
  This function determines if there is a glass placed
  or not and returns true or false depending on that fact.
*/
boolean glassPlaced() {
  if (digitalRead(PIN_GLASSPLACED) == HIGH) {
    return true;
  } else {
    return false;
  }
}

/*
  The main loop is basically a state machine that takes
  action depending on what state it currently is in.
*/
void loop() {
  int8_t chosen_liquid;
  static char receiveBuffer[BUFFERLENGTH];
  static state_t current_state = NOT_CONNECTED;
  static state_t next_state = NOT_CONNECTED;
  digitalWrite(PIN_GLASSINDICATED, glassPlaced());
  
  switch (current_state) {
    case NOT_CONNECTED:
      if (discoverServer()) {
        next_state = READY;
      }
      break;
    case READY:
      if (receive(receiveBuffer, BUFFERLENGTH)) {
        //Choose what liquid to pour based on the first char
        chosen_liquid = chooseLiquid(receiveBuffer[0]);

        if (receiveBuffer[0] == 'Q') {
          client.print("OK\n");
        } else if (chosen_liquid > 0) {
          next_state = POURING_DRINK;
        } else if (receiveBuffer[0] == 'K') {
          next_state = DRINK_DONE;
        } else {
          client.print("BADFORMAT\n");
        }
      } else if (!client.connected()) {
        client.stop();
        next_state = NOT_CONNECTED;
      }
      break;
    case POURING_DRINK:
      if (glassPlaced()) {
        uint16_t amount = 10 * ((uint8_t)receiveBuffer[1] - 48)
                     + ((uint8_t)receiveBuffer[2] - 48);
        pourDrink(chosen_liquid, amount);
        delay(100);
        //Report back to server when done
        client.print("ACK\n");
        next_state = READY;
      }
      break;
    case DRINK_DONE:
      if (!glassPlaced()) {
        client.print("ACK\n");
        next_state = READY;
      }
      break;
  }
  current_state = next_state;
}
