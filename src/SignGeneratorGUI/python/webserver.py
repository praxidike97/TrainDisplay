from flask import Flask

from generate_timetable import generate_timetable_from_eva_nr

app = Flask(__name__)

@app.route('/')
def index():
    return 'Hello world'

@app.get('/generate_timetable/<eva_nr>')
def single_converter(eva_nr):
    generate_timetable_from_eva_nr(int(eva_nr), "/home/pi/Projects/TrainDisplay/src/SignGeneratorGUI/python/credentials.json")
    return "Generated"

@app.get('/test/<number1>/<number2>')
def single_converter(number1, number2):
    return str(number1) + ":" + str(number2)

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8000, debug=True)
