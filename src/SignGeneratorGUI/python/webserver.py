from flask import Flask, request

from generate_timetable import generate_timetable_from_eva_nr

app = Flask(__name__)

@app.route('/')
def index():
    return 'Hello world'

@app.get('/generate_timetable')
def single_converter():
    eva_nr = request.args.get('eva_nr', type=int, default=0)
    rotate = request.args.get('rotate', type=int, default=0)

    generate_timetable_from_eva_nr(eva_nr, "/home/pi/Projects/TrainDisplay/src/SignGeneratorGUI/python/credentials.json", bool(rotate))
    return str(rotate)

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8000, debug=True)
