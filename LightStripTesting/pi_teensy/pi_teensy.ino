// -------------------------------------------------------------------------------------------
// Basic Slave
// -------------------------------------------------------------------------------------------
//
// This creates a simple I2C Slave device which will print whatever text string is sent to it.
// It will retain the text string in memory and will send it back to a Master device if 
// requested.  It is intended to pair with a Master device running the basic_master sketch.
//
// This example code is in the public domain.
//
// -------------------------------------------------------------------------------------------

#include <i2c_t3.h>
#include <OctoWS2811.h>
#include <movingAvg.h>

// Function prototypes
void receiveEvent(size_t count);
void requestEvent(void);

// Memory
#define MEM_LEN 450
char databuf[MEM_LEN];
char colorbuf[MEM_LEN];
int colorMem[150];

volatile uint8_t received;

boolean on = true;


const int ledsPerStrip = 150;

DMAMEM int displayMemory[ledsPerStrip*6];
int drawingMemory[ledsPerStrip*6];

const int config = WS2811_GRB | WS2811_800kHz;

OctoWS2811 leds(ledsPerStrip, displayMemory, drawingMemory, config);

#define GREEN  0x002607


//
// Setup
//
void setup()
{

    leds.begin();
    leds.show();
    pinMode(LED_BUILTIN,OUTPUT); // LED

    // Setup for Slave mode, address 0x66, pins 18/19, external pullups, 400kHz
    Wire.begin(I2C_SLAVE, 0x66, I2C_PINS_18_19, I2C_PULLUP_INT, 400000);

    // Data init
    received = 0;
    memset(databuf, 0, sizeof(databuf));

    // register events
    Wire.onReceive(receiveEvent);
    Wire.onRequest(requestEvent);

    Serial.begin(115200);

    Serial.begin(9600);
  Serial.println("starting");

  for (int i=0; i < 150; i++) {
    leds.setPixel(i, GREEN);
    leds.show();
    delayMicroseconds(200000/150);
  }
  for (int i=0; i < 150; i++) {
    leds.setPixel(150-i, 0);
    leds.show();
    delayMicroseconds(200000/150);
  }
}

void loop()
{
    // print received data - this is done in main loop to keep time spent in I2C ISR to minimum
    
    
    /*if(received)
    {
        digitalWrite(LED_BUILTIN,HIGH);
        Serial.printf("Slave received: '%s' %d\n", databuf, received);
        int i = 0;
        while (i < 20) {
          Serial.printf("%d\n", colorMem[i]);
          i++;
        }
        received = 0;
        digitalWrite(LED_BUILTIN,LOW);
    }*/
    for(int j = 0; j < 150; j++) {
      leds.setPixel(j, colorMem[j]);
      
    }
    

    leds.show();
    delayMicroseconds(100000);
    
}

//
// handle Rx Event (incoming I2C data)
//
void receiveEvent(size_t count)
{
    Wire.read(databuf, count);  // copy Rx data to databuf
    int comm = databuf[0];
    if (comm == 0x0) {
      on = false; 
    }
    else if (comm == 0x01) {
      on = true;
    } 
    else {
      int pos = databuf[2];
      for (int i = 0; i < 30; i+=3) {
        int color = (databuf[i+3+1] << 16) | (databuf[i+3] << 8) | databuf[i+3+2];
        colorMem[pos] = color;
        pos++;
      }
    }
    
    received = count;           // set received flag to count, this triggers print in main loop
    
}

//
// handle Tx Event (outgoing I2C data)
//
void requestEvent(void)
{
    Wire.write(databuf, MEM_LEN); // fill Tx buffer (send full mem)
}
