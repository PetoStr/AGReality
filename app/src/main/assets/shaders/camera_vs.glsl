//#version 300 es
#version 100

attribute vec3 in_pos;
attribute vec2 in_uv;

varying vec2 f_uv;

uniform mat4 MMatrix;
uniform mat4 PMatrix;

void main(void)
{
    f_uv = in_uv;

    gl_Position = PMatrix * MMatrix * vec4(in_pos, 1.0);
}
