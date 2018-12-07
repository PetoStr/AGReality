cmake_minimum_required(VERSION 3.4.1)

add_compile_options(-Wall -Werror)

include_directories(src/main/jni/include)
include_directories(src/main/jni/external/stb_image/include)

add_library(
        native-lib

        SHARED

        src/main/jni/assimpio.c
        src/main/jni/files.c
        src/main/jni/stb_image.c
        src/main/jni/loader.c
        src/main/jni/mesh.c
        src/main/jni/shader_program.c
        src/main/jni/renderer.c
)

target_link_libraries(
        native-lib
        android
)

find_library(
        log-lib
        log
)

target_link_libraries(
        native-lib
        ${log-lib}
)

find_library(
        gles-lib
        GLESv3
)

target_link_libraries(
        native-lib
        ${gles-lib}
)


# Assimp

# It is allowed to use a library elsewhere than specified by the toolchain
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE BOTH)

set(ASSIMP_INCLUDE_DIR src/main/jni/external/assimp/include)
include_directories(${ASSIMP_INCLUDE_DIR})

target_link_libraries(
        native-lib
        /home/peto/git/AGReality/app/src/main/jniLibs/armeabi-v7a/libassimp.so
)
