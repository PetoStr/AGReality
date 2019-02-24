#version 300 es

in vec3 in_pos;
in vec3 in_norm;
in vec2 in_uv;

out vec2 f_uv;
out vec3 f_norm;
out vec3 f_pos;

uniform mat4 MMatrix;
uniform mat4 VMatrix;
uniform mat4 PMatrix;

uniform bool has_nmap;

void main(void)
{
	vec4 mvpos = VMatrix * MMatrix * vec4(in_pos, 1.0f);
	mat4 mmatrix = MMatrix;

	f_uv = in_uv;

	f_norm = mat3(transpose(inverse(mmatrix))) * in_norm;

	f_pos = mvpos.xyz;

	gl_Position = PMatrix * mvpos;
}
