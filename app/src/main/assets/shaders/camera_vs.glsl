#version 310 es

uniform mat4 MMatrix;
uniform mat4 PMatrix;

layout (location = 0) in vec3 vPos;
layout (location = 1) in vec2 in_uv;

layout (location = 0) out vec2 out_uv;

void main(void)
{
    out_uv = in_uv;

    gl_Position = PMatrix * MMatrix * vec4(vPos, 1.0);
}
