

/*  OctoWS2811 BasicTest.ino - Basic RGB LED Test
    http://www.pjrc.com/teensy/td_libs_OctoWS2811.html
    Copyright (c) 2013 Paul Stoffregen, PJRC.COM, LLC

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.

  Required Connections
  --------------------
    pin 2:  LED Strip #1    OctoWS2811 drives 8 LED Strips.
    pin 14: LED strip #2    All 8 are the same length.
    pin 7:  LED strip #3
    pin 8:  LED strip #4    A 100 ohm resistor should used
    pin 6:  LED strip #5    between each Teensy pin and the
    pin 20: LED strip #6    wire to the LED strip, to minimize
    pin 21: LED strip #7    high frequency ringining & noise.
    pin 5:  LED strip #8
    pin 15 & 16 - Connect together, but do not use
    pin 4 - Do not use
    pin 3 - Do not use as PWM.  Normal use is ok.

  This test is useful for checking if your LED strips work, and which
  color config (WS2811_RGB, WS2811_GRB, etc) they require.
*/

#include <OctoWS2811.h>
#include <movingAvg.h>
//#include <ArduinoSTL.h>


#define RED    0x160000
#define GREEN  0x001600
#define BLUE   0x000016
#define YELLOW 0x101400
#define PINK   0x120009
#define ORANGE 0x100400
#define WHITE  0x101010

#define PURPLE 0x90217a
#define LIGHTPURPLE 0x505050

const int ledsPerStrip = 150;

DMAMEM int displayMemory[ledsPerStrip*6];
int drawingMemory[ledsPerStrip*6];

const int config = WS2811_GRB | WS2811_800kHz;

OctoWS2811 leds(ledsPerStrip, displayMemory, drawingMemory, config);


movingAvg red_val(50);
movingAvg blue_val(50);
movingAvg green_val(50);

int color1 = 0x0;
int color2 = 0x0;


void setup() {
  leds.begin();
  leds.show();

  //pinMode(10, INPUT);
  pinMode(11, INPUT);
  pinMode(12, INPUT);
  
  
  Serial.begin(9600);
  Serial.println("starting");
  //analogReadRes(8);
  analogReadAveraging(32);
  red_val.begin();
  blue_val.begin();
  green_val.begin();
  /*for (int i=0; i < leds.numPixels(); i++) {
    if(i % 4 < 2) {
      leds.setPixel(i, RED);
    } else {
      leds.setPixel(i, GREEN);
    }
    leds.show();
    delayMicroseconds(200000/150);
  }*/
  
}





// Less intense...



void loop() {
  int microsec = 2000 / leds.numPixels();  // change them all in 2 seconds

  int r = red_val.reading(map(analogRead(A3), 0, 755, 0, 255));
  int g = blue_val.reading(map(analogRead(A4), 0, 755, 0, 255));
  int b = green_val.reading(map(analogRead(A5), 0, 755, 0, 255));
  Serial.print("R: ");
  Serial.print(r);
  Serial.print(" G: ");
  Serial.print(g);
  Serial.print(" B: ");
  Serial.println(b);
  //printf("r: %d g: %d b: %d\n", r, g, b);
  int color = 0x0;
  int rr = r << 16;
  int gg = g << 8;
  color =  rr | gg | b;

  if(digitalRead(11) == 1) {
    color1 = color;
  } else if (digitalRead(12) == 1) {
    color2 = color;
  }
  
  for (int i=0; i < 150; i++) {
    if(i % 4 < 2) {
      leds.setPixel(i, color1);
    } else {
      leds.setPixel(i, color2);
    }

    
    
    
    //delayMicroseconds(wait);
    
  }
  leds.show();
  Serial.println(color);
  delayMicroseconds(10000);
  Serial.print(color1);
  Serial.print(" ");
  Serial.println(color2);
  //Serial.println("setting");
  /*unsigned int rgbColour[3];
//2621752 2633524

  // Start off with red.
  rgbColour[0] = 25;
  rgbColour[1] = 0;
  rgbColour[2] = 0;  

  // Choose the colours to increment and decrement.
  for (int decColour = 0; decColour < 3; decColour += 1) {
    int incColour = decColour == 2 ? 0 : decColour + 1;

    // cross-fade the two colours.
    for(int i = 0; i < 25; i += 1) {
      rgbColour[decColour] -= 1;
      rgbColour[incColour] += 1;
      
      //setColourRgb(rgbColour[0], rgbColour[1], rgbColour[2]);
      int rr = rgbColour[0] << 16;
      int gg = rgbColour[1] << 8;
      int color =  rr | gg | rgbColour[2];
      for (int i=0; i < 150; i++) {
        leds.setPixel(i, color);
        
        //delayMicroseconds(wait);
        
      }
      delay(50);
      leds.show();
      
    }
  }*/

  /*
  for(int i = 1; i < 150; i++) {
    leds.setPixel(i-1, 0x000000);
    leds.setPixel(i, 0x0f0f0f);
    leds.show();
    delay(10);
  }
  for(int i = 150; i > 0; i--) {
    leds.setPixel(i+1, 0x000000);
    leds.setPixel(i, 0x0f0f0f);
    leds.show();
    delay(random(10));
  }*/
  
  
  /*
  // uncomment for voltage controlled speed
  // millisec = analogRead(A9) / 40;
  colorWipe(RED, microsec);
  Serial.println("RED");
  colorWipe(GREEN, microsec);
  Serial.println("GREEN");
  colorWipe(BLUE, microsec);
  Serial.println("BLUE");
  colorWipe(YELLOW, microsec);
  Serial.println("YELLOW");
  colorWipe(PINK, microsec);
  Serial.println("PINK");
  colorWipe(ORANGE, microsec);
  Serial.println("ORANGE");
  colorWipe(WHITE, microsec);
  Serial.println("WHITE");
  */
  /*
  for (int i=0; i < leds.numPixels(); i++) {
    leds.setPixel(i, 0x808080);
    leds.show();
    delayMicroseconds(200);
  }*/
}

void colorWipe(int color, int wait)
{
  for (int i=0; i < leds.numPixels(); i++) {
    leds.setPixel(i, color);
    leds.show();
    delayMicroseconds(wait);
  }
}
