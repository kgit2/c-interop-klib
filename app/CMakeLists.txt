cmake_minimum_required(VERSION 3.23)
project(app C)

set(CMAKE_C_STANDARD 99)

add_executable(app main.c)

target_link_libraries(app libnative libapi)

add_library(libnative SHARED IMPORTED)

add_library(libapi STATIC IMPORTED)

set_target_properties(libnative PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/../libnative/build/bin/native/debugShared/libnative.dylib)

set_target_properties(libapi PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/../libapi/cmake-build-debug/lib/libapi.a)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../libnative/build/bin/native/debugShared ${CMAKE_CURRENT_SOURCE_DIR}/../libapi/cmake-build-debug/include)
