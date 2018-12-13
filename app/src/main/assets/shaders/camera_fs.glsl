//#version 300 es

#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES camera;

varying vec2 f_uv;

//out vec4 out_color;

void main(void)
{
	vec4 out_color = texture2D(camera, f_uv);
	gl_FragColor = out_color;
	//out_color = vec4(1.0);
}
