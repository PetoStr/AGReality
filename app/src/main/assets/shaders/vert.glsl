#version 300 es

in vec3 in_pos;
in vec3 in_norm;
in vec2 in_uv;
in vec3 in_tangent;

out vec2 f_uv;
out vec3 f_norm;
out vec3 f_pos;
out mat3 f_TBN;

uniform mat4 MMatrix;
uniform mat4 VMatrix;
uniform mat4 PMatrix;

uniform bool has_nmap;

void main(void)
{
	vec4 mvpos = VMatrix * MMatrix * vec4(in_pos, 1.0f);
	mat4 mmatrix = MMatrix;
	vec3 tangent = in_tangent;

	f_uv = in_uv;

	f_norm = mat3(transpose(inverse(mmatrix))) * in_norm;

	f_pos = mvpos.xyz;

	if (has_nmap) {
		vec3 T = normalize(vec3(mmatrix * vec4(tangent, 0.0)));
		vec3 N = normalize(vec3(mmatrix * vec4(in_norm, 0.0)));
		// re-orthogonalize T with respect to N
		T = normalize(T - dot(T, N) * N);
		vec3 B = cross(N, T);
		f_TBN = mat3(T, B, N);
	}

	gl_Position = PMatrix * mvpos;
}
