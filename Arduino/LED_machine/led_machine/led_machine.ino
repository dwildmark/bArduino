
#include <LiquidCrystal.h>
#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>

byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xDD, 0xAE
};

// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(9, 8, 5, 4, 3, 2);

IPAddress remote;

unsigned int tcpPort = 8006;

unsigned int udpPort = 28780;

EthernetClient client;

EthernetUDP udp;

char packetBuffer[UDP_TX_PACKET_MAX_SIZE];

char recieveBuffer[40];

void setup() {
  Ethernet.begin(mac);

  udp.begin(udpPort);

  delay(1000);
  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  // Print a message to the LCD.
  lcd.print("BARDUINO");
  lcd.display();
}

void discoverServer() {
  int packetSize = udp.parsePacket();
  if (packetSize) {
    remote = udp.remoteIP();
    udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    while (!client.connected()) {
      client.connect(remote, tcpPort);
    }
  }
}

void recieve() {
  if (client.available()) {
    //Read the content into a char array
    int count = 0;
    while (client.available() > 0) {
      recieveBuffer[count] = (char)client.read();
      count++;
    }
    String message = "";
    for(int i = 0; i < count - 1; i++) {
      message += (char)recieveBuffer[i];
    }
    lcd.clear();
    lcd.print(message);
  }
}

void loop() {
  if (client.available()) {
    recieve();
  }

  if (!client.connected()) {
    client.stop();
    discoverServer();
  }
}
