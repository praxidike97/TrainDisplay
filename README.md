# TrainDisplay

## Android
The Android app is located under the ```build```directory.

## Raspberry Pi
In order to make this project runnable, the ```.ttf``` file from ```assets```has to be placed under ```/usr/local/share/fonts/```because that is one of the default paths where PIL is looking for font files. Furthermore, the file ```credentials.json``` has to be placed under ```/etc/train_display```. This file has the credentials to access the Deutsche Bahn API and has the following format:

```
{
   "id": <put id here>,
   "key": <put key here>
}
```
