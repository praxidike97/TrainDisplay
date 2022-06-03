cmake_minimum_required(VERSION 3.22)
project(SignGeneratorGUI)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

set(CMAKE_PREFIX_PATH "/home/fabian/Qt/5.15.2")

find_package(Qt5 COMPONENTS
        Core
        Gui
        Widgets
        REQUIRED)
find_package(OpenCV REQUIRED)

add_executable(SignGeneratorGUI main.cpp PlatformInfo.cpp PlatformInfo.h)
include_directories(${OpenCV_INCLUDE_DIRS})
target_link_libraries(SignGeneratorGUI
        Qt5::Core
        Qt5::Gui
        Qt5::Widgets
        ${OpenCV_LIBS})

