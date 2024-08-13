# Additional clean files
cmake_minimum_required(VERSION 3.16)

if("${CONFIG}" STREQUAL "" OR "${CONFIG}" STREQUAL "Debug")
  file(REMOVE_RECURSE
  "CMakeFiles/SignGeneratorGUI_autogen.dir/AutogenUsed.txt"
  "CMakeFiles/SignGeneratorGUI_autogen.dir/ParseCache.txt"
  "SignGeneratorGUI_autogen"
  )
endif()
