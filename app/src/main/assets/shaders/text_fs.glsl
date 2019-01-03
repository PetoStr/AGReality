#version 300 es

precision mediump float;

in vec2 f_uv;

out vec4 out_color;

uniform sampler2D text;
uniform vec3 text_color;

void main(void)
{
	vec4 sampled = vec4(1.0, 1.0, 1.0, texture(text, f_uv).r);
	out_color = vec4(text_color, 1.0) * sampled;
}
