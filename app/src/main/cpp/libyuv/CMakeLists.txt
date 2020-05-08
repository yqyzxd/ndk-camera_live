cmake_minimum_required(VERSION 3.4.1)

aux_source_directory(source YUV_SOURCE_FILES)
include_directories(include)

add_library(
        yuv

        STATIC

        ${YUV_SOURCE_FILES})