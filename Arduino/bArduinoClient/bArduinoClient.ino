#include <SPI.h>
#include <Ethernet.h>

//Enter the MAC-address and the IP-address for the bArduino below.
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xDD, 0xAC
};
IPAddress ip(192, 168, 1, 74);

//Enter the server IP address below.
IPAddress server(192, 168, 1, 62);

EthernetClient client;

int liquid1 = 9;
int liquid2 = 8;
int liquid3 = 7;
int liquid4 = 6;
int state = LOW;

volatile int pulses = 0;
volatile int chosen_liquid;

void setup()
{
  Ethernet.begin(mac, ip);
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  //while (!Serial) {
  //  ; // wait for serial port to connect. Needed for Leonardo only
  //}
  //give the Ethernet shield a second to initialize:
  delay(1000);
  //Serial.println("connecting...");
  // if you get a connection, report back via serial:
  if (client.connect(server, 8008)) {
    //Serial.println("connected");
  }
  else {
    // if you didn't get a connection to the server:
    //Serial.println("connection failed");
    while (true);
  }

  pinMode(liquid1, OUTPUT);
  pinMode(liquid2, OUTPUT);
  pinMode(liquid3, OUTPUT);
  pinMode(liquid4, OUTPUT);
}

void loop()
{
  while (true) {
    if (client.available()) {
      char c[40];
      int count = 0;
      while (client.available() > 0) {
        c[count] = (char)client.read();
        count++;
      }
      chooseLiquid(c[0]);
      int amount;
      if(((int)c[1] - 48) > -1 && ((int)c[1] - 48) < 10) {
        amount = 10 * ((int)c[1] - 48) + ((int)c[2] - 48);
      } else {
        amount = ((int)c[2] - 48);
      }
      if ((char)c[0] == 'Q') {
        client.print("OK\n");
      } else if (chosen_liquid > 0 && amount > 0 && amount < 100) {
        //Serial.println("Drink and amount is selected");
        pourDrink(chosen_liquid, amount);
        client.println("ACK\n");
      } else {
        client.println("BADFORMAT\n");
      }
    }
    if (!client.connected()) {
      //Serial.println();
      //Serial.println("disconnecting.");
      client.stop();
      // do nothing:
      while (!client.connected());
        if(client.connect(server, 8008));
    }
  }
}
void addPulse() {
  pulses++;
  //Serial.println(pulses);
}

void pourDrink(int pin, int amount) {
  attachInterrupt(0, addPulse, RISING);
  pulses = 0;
  while ( pulses < amount) {
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

