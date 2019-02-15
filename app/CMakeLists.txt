cmake_minimum_required(VERSION 3.4.1)

add_compile_options(-Wall -Werror -Wextra -Wno-unused-parameter -Wno-gnu-zero-variadic-macro-arguments)# -pedantic)

add_library(
        native-lib

        SHARED

        src/main/jni/external/stb_image/stb_image.c

        src/main/jni/assimpio.c
        src/main/jni/files.c
        src/main/jni/loader.c
        src/main/jni/mesh.c
        src/main/jni/shader_program.c
        src/main/jni/renderer.c
        src/main/jni/text_renderer.c
        src/main/jni/img_renderer.c
        src/main/jni/jni_helper.c
        src/main/jni/scene.c
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


# stb_image

include_directories(${CMAKE_SOURCE_DIR}/src/main/jni/external/stb_image/include)


# FreeType 2

include_directories(${CMAKE_SOURCE_DIR}/src/main/jni/external/ft2/include)
target_link_libraries(native-lib ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libfreetype.a)


# Assimp

include_directories(${CMAKE_SOURCE_DIR}/src/main/jni/external/assimp/include)

target_link_libraries(
        native-lib
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libassimp.so
)
