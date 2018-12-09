#version 300 es

#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES camera;

in vec2 f_uv;

out vec4 out_color;

void main(void)
{
	out_color = texture2D(camera, f_uv);
	//out_color = vec4(1.0);
}
