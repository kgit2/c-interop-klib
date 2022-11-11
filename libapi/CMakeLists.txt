cmake_minimum_required(VERSION 3.23)
project(api C)

set(CMAKE_C_STANDARD 99)

add_library(api api.c api.h)

set_target_properties(api PROPERTIES PUBLIC_HEADER api.h)

install(TARGETS api
        LIBRARY DESTINATION lib
        PUBLIC_HEADER DESTINATION include)
