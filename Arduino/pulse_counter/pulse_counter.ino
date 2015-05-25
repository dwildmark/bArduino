volatile int pulses = 0;

void addPulse(){
  pulses++;
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  while(!Serial);
  
  attachInterrupt(0, addPulse, FALLING);
}



void loop() {
  // put your main code here, to run repeatedly:
  Serial.println(pulses);
}
