#version 300 es

in vec2 vertex;

out vec2 f_uv;

uniform mat4 MMatrix;
uniform mat4 PMatrix;

void main(void)
{
	gl_Position = PMatrix * MMatrix * vec4(vertex.xy, 0.0, 1.0);

	f_uv = vertex.xy;
}
