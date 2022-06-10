#include <SD.h>
#include <TFT.h>  
#include <SPI.h>


// pin definition for Arduino UNO
#define cs   6
#define dc   7
#define rst  5

#define sd_cs  10


// create an instance of the library
TFT TFTscreen = TFT(cs, dc, rst);
PImage logo;

void setup() {

  pinMode(sd_cs, OUTPUT);
  digitalWrite(sd_cs, HIGH);

  Serial.begin(9600);

  // initialize and clear the GLCD screen
  //TFTscreen.begin();
  TFTscreen.initR(INITR_BLACKTAB);
  //TFTscreen.background(255, 255, 255);

  TFTscreen.setRotation(3);
  TFTscreen.invertDisplay(1);

  // try to access the SD card. If that fails (e.g.
  // no card present), the setup process will stop.
  Serial.print(F("Initializing SD card..."));

  if (!SD.begin(sd_cs)) {
    Serial.println(F("failed!"));
    return;
  }
  Serial.println(F("OK!"));

  // now that the SD card can be access, try to load the
  // image file.
  logo = TFTscreen.loadImage("image01.bmp");
  if (!logo.isValid()) {
    Serial.println(F("error while loading image"));
    return;
  }

  TFTscreen.image(logo, 20, 20);

  Serial.print("Height: ");
  Serial.println(TFTscreen.height());
  
  Serial.print("Width: ");
  Serial.println(TFTscreen.width());
}

void loop() {

  /*
  //generate a random color
  int redRandom = random(0, 255);
  int greenRandom = random (0, 255);
  int blueRandom = random (0, 255);
  
  // set a random font color
  TFTscreen.stroke(redRandom, greenRandom, blueRandom);
  
  // print Hello, World! in the middle of the screen
  TFTscreen.text("Hello, World!", 6, 57);
  
  // wait 200 miliseconds until change to next color
  delay(200);*/
}
