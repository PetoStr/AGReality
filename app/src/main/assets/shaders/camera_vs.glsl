#version 300 es

uniform mat4 MMatrix;
uniform mat4 PMatrix;

in vec3 in_pos;
in vec2 in_uv;

out vec2 f_uv;

void main(void)
{
    f_uv = in_uv;

    gl_Position = PMatrix * MMatrix * vec4(in_pos, 1.0);
}
