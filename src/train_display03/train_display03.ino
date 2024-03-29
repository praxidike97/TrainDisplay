/*
 * Draw Bitmap images on ST7735 TFT display using Arduino.
 * The Arduino loads BMP format images from SD card and display
 *   them on the ST7735 TFT.
 * Reference: Adafruit spitftbitmap.ino example.
 * This is a free software with NO WARRANTY.
 * https://simple-circuit.com/
 */
 
#include <Adafruit_GFX.h>    // include Adafruit graphics library
#include <Adafruit_ST7735.h> // include Adafruit ST7735 display library
#include <SPI.h>             // include Arduino SPI library
#include <SD.h>              // include Arduino SD library
 
// define ST7735 TFT display connections
#define TFT_RST01  6
#define TFT_CS01   8
#define TFT_DC01   7

#define TFT_RST02  3
#define TFT_CS02   5
#define TFT_DC02   4

#define TFT_RST03  0
#define TFT_CS03   2
#define TFT_DC03   1

#define TFT_RST04  A4
#define TFT_CS04   A2
#define TFT_DC04   A3

int numFiles = 0;
 
// initialize Adafruit ST7735 TFT library
Adafruit_ST7735 tft01 = Adafruit_ST7735(TFT_CS01, TFT_DC01, TFT_RST01);
Adafruit_ST7735 tft02 = Adafruit_ST7735(TFT_CS02, TFT_DC02, TFT_RST02);
Adafruit_ST7735 tft03 = Adafruit_ST7735(TFT_CS03, TFT_DC03, TFT_RST03);
//Adafruit_ST7735 tft04 = Adafruit_ST7735(TFT_CS04, TFT_DC04, TFT_RST04);
 
void setup(void) {
  Serial.begin(9600);

  // Initialize the displays
  pinMode(TFT_CS01, OUTPUT);
  digitalWrite(TFT_CS01, HIGH);

  pinMode(TFT_CS02, OUTPUT);
  digitalWrite(TFT_CS02, HIGH);

  pinMode(TFT_CS03, OUTPUT);
  digitalWrite(TFT_CS03, HIGH);

  pinMode(TFT_CS04, OUTPUT);
  digitalWrite(TFT_CS04, HIGH);
 
  tft01.initR(INITR_GREENTAB);
  tft01.invertDisplay(1);
  tft01.fillScreen(ST77XX_BLUE);
  tft01.setRotation(2);

  tft02.initR(INITR_GREENTAB);
  tft02.invertDisplay(1);
  tft02.fillScreen(ST77XX_BLUE);
  tft02.setRotation(2);

  tft03.initR(INITR_GREENTAB);
  tft03.invertDisplay(1);
  tft03.fillScreen(ST77XX_BLUE);
  tft03.setRotation(2);

  //tft04.initR(INITR_GREENTAB);
  //tft04.invertDisplay(1);
  //tft04.fillScreen(ST77XX_BLUE);
 
  Serial.print("Initializing SD card...");
  if (!SD.begin()) {
    Serial.println("failed!");
    while(1);  // stay here
  }
  Serial.println("OK!");
 
  //File root = SD.open("/");  // open SD card main root
  //printDirectory(root, 0);   // print all files names and sizes
  //root.close();              // close the opened root

  // Random seed = read floating pin
  randomSeed(analogRead(A0));

  numFiles = countNumberOfFiles();
}
 
void loop() {
  //File root = SD.open("/");  // open SD card main root

  Serial.print("Number of files: ");
  Serial.println(numFiles);
 
  while (true) {
    /*File entry =  root.openNextFile();  // open file
 
    if (! entry) {
      // no more files
      root.close();
      return;
    }
 
    uint8_t nameSize = String(entry.name()).length();  // get file name size
    String str1 = String(entry.name()).substring( nameSize - 4 );  // save the last 4 characters (file extension)
    */
    //if ( str1.equalsIgnoreCase(".bmp") ) {
      
      String randomFileName = getRandomFileName(numFiles);
      char buf[randomFileName.length()+1];
      Serial.print("Random file name: ");
      Serial.println(randomFileName);
      randomFileName.toCharArray(buf, randomFileName.length()+1);
      bmpDraw(buf, 24, 0, tft01);

      randomFileName = getRandomFileName(numFiles);
      Serial.print("Random file name: ");
      Serial.println(randomFileName);
      randomFileName.toCharArray(buf, randomFileName.length()+1);
      bmpDraw(buf, 24, 0, tft02);

      //randomFileName = getRandomFileName(numFiles);
      //randomFileName.toCharArray(buf, randomFileName.length()+1);
      //bmpDraw(buf, 24, 0, tft03);

      //randomFileName = getRandomFileName(numFiles);
      //randomFileName.toCharArray(buf, randomFileName.length()+1);
      //bmpDraw(buf, 24, 0, tft04);

      //bmpDraw(entry.name(), 24, 0, tft04);
    //}
 
    //entry.close();  // close the file
    Serial.println("----------------------");
        
    delay(5000);

    /*int pot_value = analogRead(PIN_POTENTIOMETER);
    Serial.print("Potentiometer value: ");
    Serial.println(pot_value);*/
  }
}

int countNumberOfFiles(){
    int numFiles = 0;
    
    File root = SD.open("/");

    if(!root){
        //Serial.println("Failed to open directory");
    }
    
    if(!root.isDirectory()){
        //Serial.println("Not a directory");
    }

    File file = root.openNextFile();
    while(file){
        //Serial.println(file.name());
        if(!file.isDirectory()){
          numFiles = numFiles + 1;
        }
        file.close();
        file = root.openNextFile();
    }
    root.close();
    return numFiles;
}

String getRandomFileName(int numFiles){
  int fileNumber = random(numFiles);

  String fileNumberStr = "";

  if (fileNumber < 10){
    fileNumberStr = "00" + String(fileNumber);
  } else if (fileNumber < 100){
    fileNumberStr = "0" + String(fileNumber);
  }else{
    fileNumberStr = String(fileNumber);
  }

  fileNumberStr = "/image" + fileNumberStr + ".bmp";

  return fileNumberStr;
}
 
// This function opens a Windows Bitmap (BMP) file and
// displays it at the given coordinates.  It's sped up
// by reading many pixels worth of data at a time
// (rather than pixel by pixel).  Increasing the buffer
// size takes more of the Arduino's precious RAM but
// makes loading a little faster.  20 pixels seems a
// good balance.
 
#define BUFFPIXEL 20
 
void bmpDraw(char *filename, uint8_t x, uint16_t y, Adafruit_ST7735 tft) {
 
  File     bmpFile;
  int      bmpWidth, bmpHeight;   // W+H in pixels
  uint8_t  bmpDepth;              // Bit depth (currently must be 24)
  uint32_t bmpImageoffset;        // Start of image data in file
  uint32_t rowSize;               // Not always = bmpWidth; may have padding
  uint8_t  sdbuffer[3*BUFFPIXEL]; // pixel buffer (R+G+B per pixel)
  uint8_t  buffidx = sizeof(sdbuffer); // Current position in sdbuffer
  boolean  goodBmp = false;       // Set to true on valid header parse
  boolean  flip    = true;        // BMP is stored bottom-to-top
  int      w, h, row, col;
  uint8_t  r, g, b;
  uint32_t pos = 0, startTime = millis();
 
  if((x >= tft.width()) || (y >= tft.height())) return;
 
  Serial.println();
  Serial.print(F("Loading image '"));
  Serial.print(filename);
  Serial.println('\'');
 
  // Open requested file on SD card
  if ((bmpFile = SD.open(filename)) == NULL) {
    Serial.print(F("File not found"));
    return;
  }
 
  // Parse BMP header
  //Serial.println(read16(bmpFile));
  uint16_t bmp_signature = read16(bmpFile);
  //Serial.println(bmp_signature);
  
  if(bmp_signature == 0x4D42) { // BMP signature
    Serial.print(F("File size: ")); Serial.println(read32(bmpFile));
    (void)read32(bmpFile); // Read & ignore creator bytes
    bmpImageoffset = read32(bmpFile); // Start of image data
    Serial.print(F("Image Offset: ")); Serial.println(bmpImageoffset, DEC);
    // Read DIB header
    Serial.print(F("Header size: ")); Serial.println(read32(bmpFile));
    bmpWidth  = read32(bmpFile);
    bmpHeight = read32(bmpFile);
    if(read16(bmpFile) == 1) { // # planes -- must be '1'
      bmpDepth = read16(bmpFile); // bits per pixel
      Serial.print(F("Bit Depth: ")); Serial.println(bmpDepth);
      if((bmpDepth == 24) && (read32(bmpFile) == 0)) { // 0 = uncompressed
 
        goodBmp = true; // Supported BMP format -- proceed!
        Serial.print(F("Image size: "));
        Serial.print(bmpWidth);
        Serial.print('x');
        Serial.println(bmpHeight);
 
        // BMP rows are padded (if needed) to 4-byte boundary
        rowSize = (bmpWidth * 3 + 3) & ~3;
 
        // If bmpHeight is negative, image is in top-down order.
        // This is not canon but has been observed in the wild.
        if(bmpHeight < 0) {
          bmpHeight = -bmpHeight;
          flip      = false;
        }
 
        // Crop area to be loaded
        w = bmpWidth;
        h = bmpHeight;
        if((x+w-1) >= tft.width())  w = tft.width()  - x;
        if((y+h-1) >= tft.height()) h = tft.height() - y;
 
        // Set TFT address window to clipped image bounds
        tft.startWrite();
        tft.setAddrWindow(x, y, w, h);
 
        for (row=0; row<h; row++) { // For each scanline...
 
          // Seek to start of scan line.  It might seem labor-
          // intensive to be doing this on every line, but this
          // method covers a lot of gritty details like cropping
          // and scanline padding.  Also, the seek only takes
          // place if the file position actually needs to change
          // (avoids a lot of cluster math in SD library).
          if(flip) // Bitmap is stored bottom-to-top order (normal BMP)
            pos = bmpImageoffset + (bmpHeight - 1 - row) * rowSize;
          else     // Bitmap is stored top-to-bottom
            pos = bmpImageoffset + row * rowSize;
          if(bmpFile.position() != pos) { // Need seek?
            tft.endWrite();
            bmpFile.seek(pos);
            buffidx = sizeof(sdbuffer); // Force buffer reload
          }
 
          for (col=0; col<w; col++) { // For each pixel...
            // Time to read more pixel data?
            if (buffidx >= sizeof(sdbuffer)) { // Indeed
              bmpFile.read(sdbuffer, sizeof(sdbuffer));
              buffidx = 0; // Set index to beginning
              tft.startWrite();
            }
 
            // Convert pixel from BMP to TFT format, push to display
            b = sdbuffer[buffidx++];
            g = sdbuffer[buffidx++];
            r = sdbuffer[buffidx++];
            tft.pushColor(tft.color565(r,g,b));
          } // end pixel
        } // end scanline
        tft.endWrite();
        Serial.print(F("Loaded in "));
        Serial.print(millis() - startTime);
        Serial.println(" ms");
      } // end goodBmp
    }
  }

  Serial.println("Vor close");
  bmpFile.close();
  Serial.println("Nach close");
  if(!goodBmp) Serial.println(F("BMP format not recognized."));
}
 
 
// These read 16- and 32-bit types from the SD card file.
// BMP data is stored little-endian, Arduino is little-endian too.
// May need to reverse subscript order if porting elsewhere.
 
uint16_t read16(File f) {
  uint16_t result;
  ((uint8_t *)&result)[0] = f.read(); // LSB
  ((uint8_t *)&result)[1] = f.read(); // MSB
  return result;
}
 
uint32_t read32(File f) {
  uint32_t result;
  ((uint8_t *)&result)[0] = f.read(); // LSB
  ((uint8_t *)&result)[1] = f.read();
  ((uint8_t *)&result)[2] = f.read();
  ((uint8_t *)&result)[3] = f.read(); // MSB
  return result;
}
 
 
void printDirectory(File dir, int numTabs) {
  while (true) {
 
    File entry =  dir.openNextFile();
    if (! entry) {
      // no more files
      break;
    }
    for (uint8_t i = 0; i < numTabs; i++) {
      Serial.print('\t');
    }
    Serial.print(entry.name());
    if (entry.isDirectory()) {
      Serial.println("/");
      printDirectory(entry, numTabs + 1);
    } else {
      // files have sizes, directories do not
      Serial.print("\t\t");
      Serial.println(entry.size(), DEC);
    }
    entry.close();
  }
}
 
// end of code.
