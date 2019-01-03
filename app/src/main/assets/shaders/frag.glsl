#version 300 es

precision mediump float;

in vec2 f_uv;
in vec3 f_norm;
in vec3 f_pos;
in mat3 f_TBN;

out vec4 out_color;

uniform bool has_texture;
uniform bool selected;
uniform bool has_nmap;
uniform bool has_smap;

uniform vec3 view_pos;
uniform vec3 dlight_dir;

uniform sampler2D texture_diffuse;
uniform sampler2D texture_specular;
uniform sampler2D texture_normals;

struct dir_light {
	vec3 direction;
	vec3 color;
	float intensity;
};

float ambient_intensity = 0.5;

vec4 calc_light_color(vec3 light_dir, vec3 norm, vec3 dc, vec3 sc, vec3 pos)
{
	vec3 ambient = ambient_intensity * dc;

	float diff = max(dot(norm, light_dir), 0.0);
	vec3 diffuse = 0.1 * diff * dc;

	vec3 view_dir = normalize(view_pos - pos);
	vec3 reflect_dir = reflect(-light_dir, norm);
	float spec = pow(max(dot(view_dir, reflect_dir), 0.0), 8.0);
	vec3 specular = 0.1 * spec * sc;

	return vec4(ambient + diffuse + specular, 1.0);
}

void main(void)
{
	if (selected) {
		ambient_intensity = 0.7;
	}

	vec4 color;
	vec4 dc;
	vec4 sc;
	if (has_texture) {
		dc = texture(texture_diffuse, f_uv);
	} else {
		dc = vec4(0.1, 0.1, 0.1, 1.0);
	}

	if (has_smap) {
		sc = texture(texture_specular, f_uv);
	} else {
		sc = vec4(0.0, 0.0, 0.0, 1.0);
	}

	vec3 norm;
	if (has_nmap) {
		norm = texture(texture_normals, f_uv).rgb;
		norm = normalize(norm * 2.0 - 1.0);
		norm = normalize(f_TBN * norm);
	} else {
		norm = f_norm;
	}

	out_color = calc_light_color(dlight_dir, norm, dc.rgb, sc.rgb, f_pos);
}
