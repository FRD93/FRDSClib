
/* * * * * * * * * * * * *
 * 64CH MULTIPLEXER SETUP*
 * * * * * * * * * * * * */


// Input signal pin number
#define SIGN_PIN 2
// Multiplexer encoded channel number bits ( 1 -> LSD, 6 -> MSD )
#define BIT1_PIN 3
#define BIT2_PIN 4
#define BIT3_PIN 5
#define BIT4_PIN 6
#define BIT5_PIN 7
#define BIT6_PIN 8
// Multiplexer switch time (us, min. 29 for stability)
#define MUX_TIME 50
// Multiplexer debounce factor
#define MUX_DEBOUNCE 3

// Joystick debounce time (us)
#define JOY_TIME 50

// SERIAL PORT BAUD RATE
#define BAUD_RATE 9600

// Data structure to store and recall decoding table
typedef struct {
   char decoded [6];
} _decode;
// Decode table
_decode decoded;
const _decode PROGMEM decoder[64] = {
  {"000000"},
  {"000001"},
  {"000010"},
  {"000011"},
  {"000100"},
  {"000101"},
  {"000110"},
  {"000111"},
  {"001000"},
  {"001001"},
  {"001010"},
  {"001011"},
  {"001100"},
  {"001101"},
  {"001110"},
  {"001111"},
  {"010000"},
  {"010001"},
  {"010010"},
  {"010011"},
  {"010100"},
  {"010101"},
  {"010110"},
  {"010111"},
  {"011000"},
  {"011001"},
  {"011010"},
  {"011011"},
  {"011100"},
  {"011101"},
  {"011110"},
  {"011111"},

  {"100000"},
  {"100001"},
  {"100010"},
  {"100011"},
  {"100100"},
  {"100101"},
  {"100110"},
  {"100111"},
  {"101000"},
  {"101001"},
  {"101010"},
  {"101011"},
  {"101100"},
  {"101101"},
  {"101110"},
  {"101111"},
  {"110000"},
  {"110001"},
  {"110010"},
  {"110011"},
  {"110100"},
  {"110101"},
  {"110110"},
  {"110111"},
  {"111000"},
  {"111001"},
  {"111010"},
  {"111011"},
  {"111100"},
  {"111101"},
  {"111110"},
  {"111111"}
};

// Data structure for reindexing
typedef struct {
   char reindexed [2];
} _reindex;
// Reindex table
_reindex reindexed;
const _reindex PROGMEM reindex[64] = {
  {"08"}, // 0
  {"07"},
  {"06"},
  {"05"},
  {"01"}, // 4
  {"02"},
  {"03"},
  {"04"},
  {"09"}, // 8
  {"10"},
  {"11"},
  {"12"},
  {"13"}, // 12
  {"14"},
  {"15"},
  {"16"},
  {"20"}, // 16
  {"19"},
  {"28"},
  {"27"},
  {"18"}, // 20
  {"17"},
  {"26"},
  {"25"},
  {"29"}, // 24
  {"30"},
  {"21"},
  {"22"},
  {"31"}, // 28
  {"32"},
  {"23"},
  {"24"},
  {"61"}, // 32
  {"62"},
  {"63"},
  {"64"},
  {"53"}, // 36
  {"54"},
  {"55"},
  {"56"},
  {"45"}, // 40
  {"46"},
  {"47"},
  {"48"},
  {"37"}, // 44
  {"38"},
  {"39"},
  {"40"},
  {"57"}, // 48
  {"58"},
  {"59"},
  {"60"},
  {"49"}, // 52
  {"50"},
  {"51"},
  {"52"},
  {"41"}, // 56
  {"42"},
  {"43"},
  {"44"},
  {"33"}, // 60
  {"34"},
  {"35"},
  {"36"} // 63
};

int value;
int counter;
int old_values[64];



/* * * * * * * * * * * *
 * ROTARY ENCODER SETUP*
 * * * * * * * * * * * */

int encoder0PinA = 53;
int encoder0PinB = 51;
int encoder0Pos = 0;
int encoder0PinALast = LOW;

int encoder1PinA = 47;
int encoder1PinB = 45;
int encoder1Pos = 0;
int encoder1PinALast = LOW;

int encoder2PinA = 41;
int encoder2PinB = 39;
int encoder2Pos = 0;
int encoder2PinALast = LOW;

int encoder3PinA = 35;
int encoder3PinB = 33;
int encoder3Pos = 0;
int encoder3PinALast = LOW;

int encoder4PinA = 52;
int encoder4PinB = 50;
int encoder4Pos = 0;
int encoder4PinALast = LOW;

int encoder5PinA = 46;
int encoder5PinB = 44;
int encoder5Pos = 0;
int encoder5PinALast = LOW;

int encoder6PinA = 40;
int encoder6PinB = 38;
int encoder6Pos = 0;
int encoder6PinALast = LOW;

int encoder7PinA = 34;
int encoder7PinB = 32;
int encoder7Pos = 0;
int encoder7PinALast = LOW;

int n = LOW;








/* * * * * * * * * *
 * JOYSTICKS SETUP *
 * * * * * * * * * */
int anal_joyX[5] = {A1, A3, A5, A8, A10};
int anal_joyY[5] = {A2, A4, A6, A9, A11};
int anal_joyX_old_vals [5] = {512, 512, 512, 512, 512};
int anal_joyX_vals [5]     = {512, 512, 512, 512, 512};
int anal_joyY_old_vals [5] = {512, 512, 512, 512, 512};
int anal_joyY_vals [5]     = {512, 512, 512, 512, 512};

#define MUX1_A 11
#define MUX1_B 10
#define MUX1_C 9
#define MUX1_COM A0

const String index_table[8][3] = {
  {"0", "0", "0"},
  {"0", "0", "1"},
  {"0", "1", "0"},
  {"0", "1", "1"},
  {"1", "0", "0"},
  {"1", "0", "1"},
  {"1", "1", "0"},
  {"1", "1", "1"}
};

const String index_names[8] = {
  "J6Y",
  "J6X",
  "J7Y",
  "J7X",
  "J8Y",
  "J8X",
  "J9Y",
  "J9X",
};

int mux1_val[8], mux1_val_old[8], mux1_der1[8], mux1_der1_old[8], mux1_der2[8];





void setup() {

  // Initialize stuff
  for(int i=0; i<64; i++) {
    old_values[i] = 0;
  }
  counter = 0;
  value = 0;
  
  
  // Declare pin modes

  // 64CH MULTIPLEXER
  pinMode(SIGN_PIN, INPUT);
  pinMode(BIT1_PIN, OUTPUT);
  pinMode(BIT2_PIN, OUTPUT);
  pinMode(BIT3_PIN, OUTPUT);
  pinMode(BIT4_PIN, OUTPUT);
  pinMode(BIT5_PIN, OUTPUT);
  pinMode(BIT6_PIN, OUTPUT);

  // ROTARY ENCODERS
  pinMode (encoder0PinA,INPUT_PULLUP);
   pinMode (encoder0PinB,INPUT_PULLUP);
   pinMode (encoder1PinA,INPUT_PULLUP);
   pinMode (encoder1PinB,INPUT_PULLUP);
   pinMode (encoder2PinA,INPUT_PULLUP);
   pinMode (encoder2PinB,INPUT_PULLUP);
   pinMode (encoder3PinA,INPUT_PULLUP);
   pinMode (encoder3PinB,INPUT_PULLUP);
  pinMode (encoder4PinA,INPUT_PULLUP);
   pinMode (encoder4PinB,INPUT_PULLUP);
   pinMode (encoder5PinA,INPUT_PULLUP);
   pinMode (encoder5PinB,INPUT_PULLUP);
   pinMode (encoder6PinA,INPUT_PULLUP);
   pinMode (encoder6PinB,INPUT_PULLUP);
   pinMode (encoder7PinA,INPUT_PULLUP);
   pinMode (encoder7PinB,INPUT_PULLUP);



  // JOYSTICKS
  // Analog pins
  for(int i=0; i<5; i++) {
    pinMode(anal_joyX[i], INPUT);
    pinMode(anal_joyY[i], INPUT);
  }
  
  // MUX pins
  pinMode(MUX1_A, OUTPUT);
  pinMode(MUX1_B, OUTPUT);
  pinMode(MUX1_C, OUTPUT);
  pinMode(MUX1_COM, INPUT);
  // init all values to avoid bad numbers when turning on the device
  for(int i=0; i<=8; i++){
    mux1_val[i] = analogRead(MUX1_COM);
    mux1_val_old[i] = mux1_val[i];
    mux1_val[i] = analogRead(MUX1_COM);

    mux1_der1[i] = mux1_val[i] - mux1_val_old[i];
    mux1_der1_old[i] = mux1_der1[i];
    mux1_der1[i] = mux1_val[i] - mux1_val_old[i];
  }

  // Start serial port (SC max is 230400)
  Serial.begin(BAUD_RATE); 
}




void loop() {
  // Get decoded pin values at counter
  memcpy_P (&decoded, &decoder[counter], sizeof decoded);
  // Get reindexed counter
  memcpy_P (&reindexed, &reindex[counter], sizeof reindexed);
  
  // Write decoded index to output pins
  digitalWrite(BIT4_PIN, decoded.decoded[0] == '1');
  digitalWrite(BIT5_PIN, decoded.decoded[1] == '1');
  digitalWrite(BIT6_PIN, decoded.decoded[2] == '1');
  digitalWrite(BIT3_PIN, decoded.decoded[3] == '1');
  digitalWrite(BIT2_PIN, decoded.decoded[4] == '1');
  digitalWrite(BIT1_PIN, decoded.decoded[5] == '1');

  // Wait 29us to stabilize multiplexer (minimum for stability is 29us)
  delayMicroseconds(MUX_TIME);

  // Read current value
  value = digitalRead(2);

  // If corrispondent value changed, do...
  if( old_values[counter] == 0 && value == 1 ) {
    Serial.print("B");
    Serial.print(reindexed.reindexed[0]);
    Serial.print(reindexed.reindexed[1]);
    Serial.print(" ");
    Serial.println(1);
  } else if( old_values[counter] == 1 && value == 0 ) {
    Serial.print("B");
    Serial.print(reindexed.reindexed[0]);
    Serial.print(reindexed.reindexed[1]);
    Serial.print(" ");
    Serial.println(0);
  }

  // Store current value as old button value
  old_values[counter] = value;
  
  // Update counter
  counter += 1;
  if(counter >= 64) counter = 0;


  /*
   * ROTARY ENCODERS
   */
 // 0
 n = digitalRead(encoder0PinA);
 if ((encoder0PinALast == LOW) && (n == HIGH)) {
   if (digitalRead(encoder0PinB) == LOW) {
     encoder0Pos++;
   } else {
     encoder0Pos--;
   }
   Serial.print ("Encoder 0: ");
   Serial.println (encoder0Pos);
 } 
 encoder0PinALast = n;

 // 1
 n = digitalRead(encoder1PinA);
 if ((encoder1PinALast == LOW) && (n == HIGH)) {
   if (digitalRead(encoder1PinB) == LOW) {
     encoder1Pos++;
   } else {
     encoder1Pos--;
   }
   Serial.print ("Encoder 1: ");
   Serial.println (encoder1Pos);
 } 
 encoder1PinALast = n;

 // 2
 n = digitalRead(encoder2PinA);
 if ((encoder2PinALast == LOW) && (n == HIGH)) {
   if (digitalRead(encoder2PinB) == LOW) {
     encoder2Pos++;
   } else {
     encoder2Pos--;
   }
   Serial.print ("Encoder 2: ");
   Serial.println (encoder2Pos);
 } 
 encoder2PinALast = n;

 // 3
 n = digitalRead(encoder3PinA);
 if ((encoder3PinALast == LOW) && (n == HIGH)) {
   if (digitalRead(encoder3PinB) == LOW) {
     encoder3Pos++;
   } else {
     encoder3Pos--;
   }
   Serial.print ("Encoder 3: ");
   Serial.println (encoder3Pos);
 } 
 encoder3PinALast = n;

 // 4
   n = digitalRead(encoder4PinA);
   if ((encoder4PinALast == LOW) && (n == HIGH)) {
     if (digitalRead(encoder4PinB) == LOW) {
       encoder4Pos++;
     } else {
       encoder4Pos--;
     }
     Serial.print ("Encoder 4: ");
     Serial.println (encoder4Pos);
   } 
   encoder4PinALast = n;

   // 5
   n = digitalRead(encoder5PinA);
   if ((encoder5PinALast == LOW) && (n == HIGH)) {
     if (digitalRead(encoder5PinB) == LOW) {
       encoder5Pos++;
     } else {
       encoder5Pos--;
     }
     Serial.print ("Encoder 5: ");
     Serial.println (encoder5Pos);
   } 
   encoder5PinALast = n;

   // 6
   n = digitalRead(encoder6PinA);
   if ((encoder6PinALast == LOW) && (n == HIGH)) {
     if (digitalRead(encoder6PinB) == LOW) {
       encoder6Pos++;
     } else {
       encoder6Pos--;
     }
     Serial.print ("Encoder 6: ");
     Serial.println (encoder6Pos);
   } 
   encoder6PinALast = n;

   // 7
   n = digitalRead(encoder7PinA);
   if ((encoder7PinALast == LOW) && (n == HIGH)) {
     if (digitalRead(encoder7PinB) == LOW) {
       encoder7Pos++;
     } else {
       encoder7Pos--;
     }
     Serial.print ("Encoder 7: ");
     Serial.println (encoder7Pos);
   } 
   encoder7PinALast = n;







   /*
    * JOYSTICKS
    */
   for(int i=0; i<5; i++){
    anal_joyX_old_vals[i] = anal_joyX_vals[i];
    anal_joyX_vals[i] = analogRead(anal_joyX_vals[i]);
    if(abs(anal_joyX_old_vals[i] - anal_joyX_vals[i]) > 100) {
      Serial.print("Joy");
      Serial.print(i+1);
      Serial.print("X ");
      if( (anal_joyX_old_vals[i] - anal_joyX_vals[i]) > 0)  Serial.println("-1");
      else Serial.println("1");
      // Serial.println(anal_joyX_vals[i]);
    }
    
    // small delay
    delayMicroseconds(JOY_TIME);
    
    anal_joyY_old_vals[i] = anal_joyY_vals[i];
    anal_joyY_vals[i] = analogRead(anal_joyY_vals[i]);
    if(abs(anal_joyY_old_vals[i] - anal_joyY_vals[i]) > 100) {
      Serial.print("Joy");
      Serial.print(i+1);
      Serial.print("Y ");
      if( (anal_joyY_old_vals[i] - anal_joyY_vals[i]) > 0)  Serial.println("-1");
      else Serial.println("1");
      // Serial.println(anal_joyY_vals[i]);
    }

    // small delay
    delayMicroseconds(JOY_TIME);
   }

   
   
   for(int i=0; i<8; i++){
    // set reading pin
    if(index_table[i][0] == "1") digitalWrite(MUX1_A, HIGH);
    else digitalWrite(MUX1_A, LOW);
    if(index_table[i][1] == "1") digitalWrite(MUX1_B, HIGH);
    else digitalWrite(MUX1_B, LOW);
    if(index_table[i][2] == "1") digitalWrite(MUX1_C, HIGH);
    else digitalWrite(MUX1_C, LOW);  
    // small delay
    delayMicroseconds(MUX_TIME);   
    // Read value
    mux1_val_old[i] = mux1_val[i];
    mux1_val[i] = analogRead(MUX1_COM);
    // Send to serial port
    if(abs(mux1_val_old[i] - mux1_val[i]) > MUX_DEBOUNCE) {
     Serial.println(index_names[i]);
     Serial.println(mux1_val[i]);
    }
  }
}
