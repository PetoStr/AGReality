#version 310 es

precision mediump float;

uniform bool has_texture;

uniform sampler2D texture_diffuse;
uniform sampler2D texture_specular;

layout (location = 0) in vec2 uv;
layout (location = 1) in vec3 norm;
layout (location = 2) in vec3 vpos;

out vec4 out_color;

struct dir_light {
	vec3 direction;
	vec3 color;
	float intensity;
};

vec4 calc_light_color(dir_light light, vec3 normal, vec4 dc, vec4 sc, vec3 pos)
{
	normal = normalize(normal);
	light.direction = normalize(-light.direction);

	vec4 ambient_color = vec4(light.color * vec3(0.1, 0.1, 0.1), 1.0);

	float diffuse_factor = max(dot(normal, light.direction), 0.0);
	vec4 diffuse_color = vec4(light.color, 1.0) * (diffuse_factor * dc);

	vec3 cam_dir = normalize(-pos);
	vec3 reflected_light = normalize(reflect(light.direction, normal));
	float specular_factor = max(dot(cam_dir, reflected_light), 0.0);
	specular_factor = pow(specular_factor, 32.0);
	//vec4 specular_color = sc * light.intensity * specular_factor * 4.0 * vec4(light.color, 1.0);
	vec4 specular_color = vec4(light.color, 1.0) * (sc * specular_factor);

	return ambient_color + diffuse_color + specular_color;
}

void main(void)
{
	vec4 color;
	if (has_texture) {
		vec4 dc = texture(texture_diffuse, uv);
		vec4 sc = texture(texture_specular, uv);

		dir_light dlight;
		dlight.direction = vec3(0.0, 1.0, -1.0);
		dlight.color = vec3(1.0);
		dlight.intensity = 0.3;

		//color = calc_light_color(dlight, norm, dc, sc, vpos);
		color = dc - vec4(0.15) + sc;
	} else {
		color = vec4(0.7, 0.7, 0.7, 1.0);
	}

	out_color = vec4(color.rgb, 0.7);
}
