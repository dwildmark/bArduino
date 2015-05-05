/*
  This is the main program for the Barduino machine. 
  It discovers a Barduino-server on the network using UDP-broadcast, 
  and connects to it. When connected it recieves messages from
  the server and processes the message.
  The program controls 4 flowmeters and valves. 
  It uses these to pour drinks based on the information it recieves
  from the server.
  
  @author Dennis Wildmark
*/

#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>

//Enter the MAC-address for the bArduino below.
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xDD, 0xAC
};

IPAddress remote;

unsigned int tcpPort = 8008;

unsigned int udpPort = 28785;

EthernetClient client;

EthernetUDP udp;

char packetBuffer[UDP_TX_PACKET_MAX_SIZE];

//Define the pins for the fluids
int liquid1 = 9;
int liquid2 = 8;
int liquid3 = 7;
int liquid4 = 6;

volatile int pulses = 0;
volatile int chosen_liquid;

void setup()
{
  Ethernet.begin(mac);

  udp.begin(udpPort);

  //give the Ethernet shield a second to initialize:
  delay(1000);
  
  //look for and connect to a server
  discoverServer();

  //initialize the output pins
  pinMode(liquid1, OUTPUT);
  pinMode(liquid2, OUTPUT);
  pinMode(liquid3, OUTPUT);
  pinMode(liquid4, OUTPUT);
}

void loop()
{
  while (true) {
    //Check if the buffer contains anything
    if (client.available()) {
      //Read the content into a char array
      char c[40];
      int count = 0;
      while (client.available() > 0) {
        c[count] = (char)client.read();
        count++;
      }
      //Choose what liquid to pour based on the first char
      chooseLiquid(c[0]);

      //If the first char is 'Q', respond with 'OK'
      if ((char)c[0] == 'Q') {
        client.print("OK\n");
      } else if (chosen_liquid > 0) {
        
        //Converts the two numbers into a single integer
        int amount = 10 * ((int)c[1] - 48) + ((int)c[2] - 48);
        pourDrink(chosen_liquid, amount);
        delay(100);
        
        //Report back to server when done
        client.print("ACK\n");
      } else {
        client.print("BADFORMAT\n");
      }
    }
    
    //If Arduino disconnects from server
    //try to find a server again.
    if (!client.connected()) {
      client.stop();
      discoverServer();
    }
  }
}
void addPulse() {
  pulses++;
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

int determineAmount(int centiliters) {
  switch (centiliters) {
    case 1:
      return 2;
      break;
    case 2:
      return 4;
      break;
  }
}

void discoverServer() {
  boolean a = true;
  while (a) {
    //IPAddress broadcastIP(255, 255, 255, 255);

    //udp.beginPacket(broadcastIP, udpPort);
    //udp.write("DISCOVER_FUIFSERVER_REQUEST");
    //udp.endPacket();

    int packetSize = udp.parsePacket();
    if (packetSize) {
      remote = udp.remoteIP();
      udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
      while (!client.connected()) {
        client.connect(remote, tcpPort);
      }
      a = false;
    }
    //delay(1000);
  }
}


