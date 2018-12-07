#version 310 es

uniform mat4 MMatrix;
uniform mat4 VMatrix;
uniform mat4 PMatrix;

layout (location = 0) in vec3 vPos;
layout (location = 1) in vec3 norm;
layout (location = 2) in vec2 in_uv;

layout (location = 0) out vec2 out_uv;
layout (location = 1) out vec3 out_norm;
layout (location = 2) out vec3 out_pos;

void main(void)
{
    vec4 mpos = MMatrix * vec4(vPos, 1.0f);

    out_uv = in_uv;
    out_norm = norm;
    out_pos = mpos.xyz;

    gl_Position = PMatrix * VMatrix *  mpos;
}
