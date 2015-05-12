#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>

byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xDD, 0xAF
};


IPAddress remote;

unsigned int tcpPort = 8008;

unsigned int udpPort = 28780;

EthernetClient client;

EthernetUDP udp;

char packetBuffer[UDP_TX_PACKET_MAX_SIZE];

char recieveBuffer[40];

//Define the pins for the fluids
int liquid1 = 9;
int liquid2 = 8;
int liquid3 = 7;
int liquid4 = 6;
int glassPlacedPin = 5;

volatile int pulses = 0;
volatile int chosen_liquid;

volatile boolean glassUsed = false;
volatile boolean glassPlaced = false;

typedef enum
{
  POURING_DRINK,
  READY,
  DRINK_DONE,
  NOT_CONNECTED
} state_t;

state_t current_state = NOT_CONNECTED;
state_t next_state = NOT_CONNECTED;

void setup() {
  Ethernet.begin(mac);

  udp.begin(udpPort);

  //give the Ethernet shield a second to initialize:
  delay(1000);

  //initialize the output pins
  pinMode(liquid1, OUTPUT);
  pinMode(liquid2, OUTPUT);
  pinMode(liquid3, OUTPUT);
  pinMode(liquid4, OUTPUT);
  pinMode(glassPlacedPin, INPUT);
}

boolean discoverServer() {
  int packetSize = udp.parsePacket();
  if (packetSize) {
    remote = udp.remoteIP();
    udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    while (!client.connected()) {
      client.connect(remote, tcpPort);
    }
    return true;
  }
  return false;
}

boolean recieve() {
  if (client.available()) {
    //Read the content into a char array
    int count = 0;
    while (client.available() > 0) {
      recieveBuffer[count] = (char)client.read();
      count++;
    }
    //Choose what liquid to pour based on the first char
    chooseLiquid( (char)recieveBuffer[0]);
    return true;
  }
  return false;
}

void chooseLiquid(char liquid) {
  switch (liquid) {
    case 'A':
      chosen_liquid = liquid1;
      break;
    case 'B':
      chosen_liquid = liquid2;
      break;
    case 'C':
      chosen_liquid = liquid3;
      break;
    case 'D':
      chosen_liquid = liquid4;
      break;
    default:
      chosen_liquid = -1;
      break;
  }
}

void pourDrink(int pin, int amount) {
  int realAmount = ((amount * 35) - 35) / 10 ;
  attachInterrupt(0, addPulse, RISING);
  pulses = 0;
  while ( pulses < realAmount) {
    digitalWrite(pin, HIGH);
  }
  digitalWrite(pin, LOW);
  detachInterrupt(0);
}

void addPulse() {
  pulses++;
}

void loop() {
  while (true) {
    if (digitalRead(glassPlacedPin) == HIGH) {
      glassPlaced = true;
    } else {
      glassPlaced = true;
    }
    switch (current_state) {
      case NOT_CONNECTED:
        if (discoverServer()) {
          next_state = READY;
        }
        break;
      case READY:
        if (recieve()) {
          if ( (char)recieveBuffer[0] == 'Q') {
            client.print("OK\n");
          } else if (chosen_liquid > 0) {
            next_state = POURING_DRINK;
          } else if ( (char)recieveBuffer[0] == 'K') {
            next_state = DRINK_DONE;
          } else {
            client.print("BADFORMAT\n");
          }
        } else if (!client.connected()) {
          next_state = NOT_CONNECTED;
        }
        break;
      case POURING_DRINK:
        if (glassPlaced) {
          int amount = 10 * ((int)recieveBuffer[1] - 48)
                       + ((int)recieveBuffer[2] - 48);
          pourDrink(chosen_liquid, amount);
          delay(100);
          //Report back to server when done
          client.print("ACK\n");
          next_state = READY;
        }
        break;
      case DRINK_DONE:
        digitalWrite(liquid3, HIGH);
        if (recieve()) {
          if ( (char)recieveBuffer[0] == 'Q') {
            client.print("OK\n");
          }
        }
        if (!glassPlaced) {
          client.print("ACK\n");
          next_state = READY;
        }
        break;
    }
    current_state = next_state;
  }
}
