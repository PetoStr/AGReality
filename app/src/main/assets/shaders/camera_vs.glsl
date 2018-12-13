//#version 300 es

uniform mat4 MMatrix;
uniform mat4 PMatrix;

attribute vec3 in_pos;
attribute vec2 in_uv;

varying vec2 f_uv;

void main(void)
{
    f_uv = in_uv;

    gl_Position = PMatrix * MMatrix * vec4(in_pos, 1.0);
}
