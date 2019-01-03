#version 300 es

precision mediump float;

in vec2 f_uv;

out vec4 out_color;

uniform sampler2D img;
uniform float opacity;
uniform bool selected;

void main(void)
{
	vec2 uv = f_uv;
	uv.y = 1.0 - uv.y;

	vec4 tcolor = texture(img, uv);
	out_color = vec4(tcolor.rgb, opacity * tcolor.a);

	if (selected) {
		out_color += vec4(0.333, 0.333, 0.333, 0.333);
	}
}
