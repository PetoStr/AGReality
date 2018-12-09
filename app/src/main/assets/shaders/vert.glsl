#version 300 es

uniform mat4 MMatrix;
uniform mat4 VMatrix;
uniform mat4 PMatrix;

in vec3 in_pos;
in vec3 in_norm;
in vec2 in_uv;

out vec2 f_uv;
out vec3 f_norm;
out vec3 f_pos;

void main(void)
{
    vec4 mpos = MMatrix * vec4(in_pos, 1.0f);

    f_uv = in_uv;
    f_norm = in_norm;
    f_pos = mpos.xyz;

    gl_Position = PMatrix * VMatrix *  mpos;
}
