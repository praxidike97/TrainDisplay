import argparse
import json



if __name__ == "__main__":
   parser = argparse.ArgumentParser()
   parser.add_argument('-f','--file', help='file from the deutsche bahn api to parse from', required=True)
   args = vars(parser.parse_args())

   mapping = {}

   with open(args["file"]) as json_file:
    json_string = json.load(json_file)

    for e in json_string:
       mapping[e["NAME"]] = e["EVA_NR"]
    
    map_json_string = json.dumps(mapping)
    
   with open("name_eva_mapping.json", "w+") as f:
      f.write(map_json_string)

