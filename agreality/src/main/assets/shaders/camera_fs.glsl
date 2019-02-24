//#version 300 es
#version 100

#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 f_uv;

uniform samplerExternalOES camera;

void main(void)
{
	/* XXX function texture() is not found on every phone when the version is set to 300 es */
	vec4 out_color = texture2D(camera, f_uv);
	gl_FragColor = out_color;
}
