import argparse
from datetime import datetime
import json
import random

from PIL import Image, ImageFont, ImageDraw

from deutsche_bahn_api.api_authentication import ApiAuthentication
from deutsche_bahn_api.station_helper import StationHelper
from deutsche_bahn_api.timetable_helper import TimetableHelper
from deutsche_bahn_api.train import Train

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


if __name__ == "__main__":
   parser = argparse.ArgumentParser()
   parser.add_argument('-s','--station', help='The name of the station to show departures from', default="Bremen Hbf")
   args = vars(parser.parse_args())

   with open('credentials.json') as f:
      auth_json = json.load(f)
   api = ApiAuthentication(auth_json["id"], auth_json["key"])
   success: bool = api.test_credentials()

   station_helper = StationHelper()
   found_stations_by_name = station_helper.find_stations_by_name(args["station"])

   timetable_helper = TimetableHelper(found_stations_by_name[0], api)
   trains_in_this_hour = timetable_helper.get_timetable()

   time_table_entries = [TimeTableEntry(train) for train in trains_in_this_hour]
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

   display_image.save("display_image.png")
