#version 310 es

#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES camera;

layout (location = 0) in vec2 uv;

out vec4 out_color;

void main(void)
{
	out_color = texture2D(camera, uv);
	//out_color = vec4(1.0);
}
