import argparse
from datetime import datetime, timedelta
import json
import os
import random

from PIL import Image, ImageFont, ImageDraw
import digitalio
import board
from adafruit_rgb_display import ili9341

from deutsche_bahn_api.api_authentication import ApiAuthentication
from deutsche_bahn_api.station_helper import StationHelper
from deutsche_bahn_api.timetable_helper import TimetableHelper
from deutsche_bahn_api.train import Train
from deutsche_bahn_api.station import Station

# Defined how many hours in the future trains should be displayed.
# If it is 19:26 now and HOURS_IN_THE_FUTURE = 2, all trains between
# 19:26 and 21:59 will be displayed
HOURS_IN_THE_FUTURE = 2


# Configuration for CS and DC pins (these are PiTFT defaults):
cs_pin = digitalio.DigitalInOut(board.CE0)
dc_pin = digitalio.DigitalInOut(board.D25)
reset_pin = digitalio.DigitalInOut(board.D24)

# Config for display baudrate (default max is 24mhz):
BAUDRATE = 24000000

# Setup SPI bus using hardware SPI:
spi = board.SPI()


disp = ili9341.ILI9341(
    spi,
    rotation=90,  # 2.2", 2.4", 2.8", 3.2" ILI9341
    cs=cs_pin,
    dc=dc_pin,
    rst=reset_pin,
    baudrate=BAUDRATE,
)


class TimeTableEntry:

    def __init__(self, train: Train):
        # Just take the final destination
        self.destination = train.stations.split("|")[-1][:15]

        # Take hour and minute from the departure time
        self.departure_time = datetime.strptime(train.departure, '%y%m%d%H%M')

        # Some platforms might contain letters, eg 11A-D, remove them
        self.platform = ''.join(filter(str.isdigit, train.platform))
        if self.platform == "":
            self.platform = str(random.randint(1, 10))

        self.train_id = ""
        # Some trains (e.g. IC) do not have the train_line attribute
        if hasattr(train, "train_line"):
            # Some train lines already contain the train type, e.g. RS30
            if any(c.isalpha() for c in train.train_line):
                self.train_id = train.train_line
            else:
                self.train_id = train.train_type + " " + train.train_line
        else:
            self.train_id = train.train_type + " " + train.train_number

def generate_timetable_from_eva_nr(eva_nr: int, credentials_file_path: str = "credentials.json", rotate: bool = False):
    station_helper = StationHelper()
    station: Station = station_helper.find_station_by_eva_nr(eva_nr)
    generate_timetable(station, credentials_file_path, rotate)

def generate_timetable(station: Station, credentials_file_path: str = "credentials.json", rotate: bool = False):
    with open(credentials_file_path) as f:
        auth_json = json.load(f)
    api = ApiAuthentication(auth_json["id"], auth_json["key"])
    success: bool = api.test_credentials()

    timetable_helper = TimetableHelper(station, api)
    time_now = datetime.now()
    trains_in_this_hour: list(Train) = []

    for hour_extension in range(HOURS_IN_THE_FUTURE + 1):
        trains_in_this_hour += timetable_helper.get_timetable((time_now + timedelta(hours=hour_extension)).hour)

    time_table_entries = [TimeTableEntry(train) for train in trains_in_this_hour]
    time_table_entries = list(filter(lambda t: t.departure_time > time_now, time_table_entries))
    time_table_entries = sorted(time_table_entries, key = lambda x: x.departure_time)

    display_image = Image.new("RGB", (320, 240), (20, 85, 192))
    draw = ImageDraw.Draw(display_image)
    font = ImageFont.truetype("NimbusSanL-BoldCond.ttf", 12)
    font_bold = ImageFont.truetype("NimbusSanL-BoldCond.ttf", 19)

    for i, entry in enumerate(time_table_entries[:8]):
        draw.text((15, 30*i+2), entry.departure_time.strftime("%H:%M"), (255,255,255), font=font_bold)
        draw.text((140, 30*i+2), entry.destination, (255,255,255), font=font_bold)
        draw.text((285, 30*i+2), entry.platform, (255,255,255), font=font_bold)
        draw.rectangle([70, 30*i+5, 120, 30*i+20], fill="#ffffff")
        draw.text((75, 30*i+5), entry.train_id, (20, 85, 192), font=font)

    if rotate:
       display_image = display_image.rotate(180, Image.NEAREST, expand=1)

    disp.image(display_image)
    #display_image.save(os.path.join(global_path, "display_image.png"))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-s','--station', help='The name of the station to show departures from', default="Bremen Hbf")
    parser.add_argument('-e','--eva_nr', help='The EVA number of the station')
    parser.add_argument('-r','--rotate', help='Whether to rotate the image by 180 degrees')
    args = vars(parser.parse_args())

    station_helper = StationHelper()
    station: Station = None

    if args["eva_nr"] is None:
        found_stations_by_name = station_helper.find_stations_by_name(args["station"])
        station = found_stations_by_name[0]
    else:
        station = station_helper.find_station_by_eva_nr(int(args["eva_nr"]))

    generate_timetable(station)
