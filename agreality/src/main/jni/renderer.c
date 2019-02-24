#include <stdlib.h>
#include <string.h>

#include <android/asset_manager_jni.h>
#include <jni.h>

#include <linmath.h>

#include "agrlog.h"
#include "assimpio.h"
#include "img_renderer.h"
#include "jni_helper.h"
#include "loader.h"
#include "model.h"
#include "pv_matrices.h"
#include "shader_program.h"
#include "text_renderer.h"
#include "util.h"
#include "scene.h"

static struct program_info program;
static struct program_info camera_program;

static int window_width;
static int window_height;

static const int mmatrix_loc = 0;
static const int vmatrix_loc = 1;
static const int pmatrix_loc = 2;
static const int has_texture_loc = 3;
static const int dcolor_loc = 4;
static const int scolor_loc = 5;
static const int opacity_loc = 6;
static const int selected_loc = 7;
static const int view_pos_loc = 8;
static const int dlight_dir_loc = 9;

static float dlight_dir[] = { 0.0f, 0.0f, -1.0f };

static struct mesh_info camera_mesh;

static void enable_depth_test(void)
{
	glEnable(GL_DEPTH_TEST);
}

static void disable_depth_test(void)
{
	glDisable(GL_DEPTH_TEST);
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_AGRenderer_init(JNIEnv *env, jobject instance,
					  jobject assetManager)
{
	UNUSED(instance);

	asset_manager = AAssetManager_fromJava(env, assetManager);
	assimpio_init();

	check_gl_error("unknown");

	AGR_INFO("GL_VENDOR: %s", glGetString(GL_VENDOR));
	AGR_INFO("GL_RENDERER: %s", glGetString(GL_RENDERER));
	AGR_INFO("GL_VERSION: %s", glGetString(GL_VERSION));
	AGR_INFO("GL_SHADING_LANGUAGE_VERSION: %s",
		 glGetString(GL_SHADING_LANGUAGE_VERSION));
	AGR_INFO("GL_EXTENSIONS: %s", glGetString(GL_EXTENSIONS));

	glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	const int atbs_len = 4;
	struct shader_attrib atbs[atbs_len] = {
		{ 0, "in_pos" },
		{ 1, "in_norm" },
		{ 2, "in_uv" },
		{ 3, "in_tang" }
	};

	strcpy((char *) program.vs.fname, "shaders/vert.glsl");
	strcpy((char *) program.fs.fname, "shaders/frag.glsl");
	create_program(&program, atbs, atbs_len);
	check_gl_error("create_program");

	program.uflocs[mmatrix_loc] =
		glGetUniformLocation(program.id, "MMatrix");
	check_gl_error("glGetUniformLocation> MMatrix");
	program.uflocs[vmatrix_loc] =
		glGetUniformLocation(program.id, "VMatrix");
	check_gl_error("glGetUniformLocation> VMatrix");
	program.uflocs[pmatrix_loc] =
		glGetUniformLocation(program.id, "PMatrix");
	check_gl_error("glGetUniformLocation> PMatrix");
	program.uflocs[has_texture_loc] =
		glGetUniformLocation(program.id, "has_texture");
	check_gl_error("glGetUniformLocation> has_texture");
	program.uflocs[dcolor_loc] =
		glGetUniformLocation(program.id, "dcolor");
	check_gl_error("glGetUniformLocation> dcolor");
	program.uflocs[scolor_loc] =
		glGetUniformLocation(program.id, "scolor");
	check_gl_error("glGetUniformLocation> scolor");
	program.uflocs[opacity_loc] =
		glGetUniformLocation(program.id, "opacity");
	check_gl_error("glGetUniformLocation> opacity");
	program.uflocs[selected_loc] =
		glGetUniformLocation(program.id, "selected");
	check_gl_error("glGetUniformLocation> selected");
	program.uflocs[view_pos_loc] =
		glGetUniformLocation(program.id, "view_pos");
	check_gl_error("glGetUniformLocation> view_pos");
	program.uflocs[dlight_dir_loc] =
		glGetUniformLocation(program.id, "dlight_dir");
	check_gl_error("glGetUniformLocation> dlight_dir");


	const int cam_atbs_len = 2;
	struct shader_attrib cam_atbs[atbs_len] = {
		{ 0, "in_pos" },
		{ 2, "in_uv" }
	};

	strcpy((char *) camera_program.vs.fname, "shaders/camera_vs.glsl");
	strcpy((char *) camera_program.fs.fname, "shaders/camera_fs.glsl");
	create_program(&camera_program, cam_atbs, cam_atbs_len);
	check_gl_error("create_program");

	camera_program.uflocs[mmatrix_loc] =
		glGetUniformLocation(camera_program.id, "MMatrix");
	check_gl_error("glGetUniformLocation> MMatrix");
	camera_program.uflocs[pmatrix_loc] =
		glGetUniformLocation(camera_program.id, "PMatrix");
	check_gl_error("glGetUniformLocation> PMatrix");

	text_renderer_init();
	img_renderer_init();
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_AGRenderer_clear(JNIEnv *env, jobject instance)
{
	UNUSED(env);
	UNUSED(instance);

	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_Scene_set_1dir_1light_1dir(JNIEnv *env,
						     jobject instance,
						     jfloatArray dld)
{
	UNUSED(instance);
	jfloat *dir_light_dir = (*env)->GetFloatArrayElements(env, dld, NULL);

	memcpy(dlight_dir, dir_light_dir, sizeof(dlight_dir));

	(*env)->ReleaseFloatArrayElements(env, dld, dir_light_dir, 0);
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_AGRenderer_surface_1changed(JNIEnv *env,
						      jobject instance,
						      jint width, jint height)
{
	UNUSED(env);
	UNUSED(instance);

	if (height == 0) {
		height = 1;
	}

	glViewport(0, 0, width, height);

	float ratioW;
	float ratioH;
	float ratio = (float) width / (float) height;
	if (width > height) {
		ratioW = (float) width / (float) height;
		ratioH = 1.0f;
	} else {
		ratioW = 1.0f;
		ratioH = (float) height / (float) width;
	}

	mat4x4_perspective(projection_matrix, (float) M_PI_4, ratio,
			   1.0f, 1000.0f);
	mat4x4_ortho(ratio_ortho_matrix, -ratioW, ratioW, -ratioH, ratioH,
		     -1.0f, 1.0f);
	mat4x4_ortho(size_ortho_matrix, 0, width, 0, height, -1.0f, 1.0f);

	window_width = width;
	window_height = height;
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_AGRenderer_on_1destroy(JNIEnv *env,
						 jobject instance)
{
	UNUSED(env);
	UNUSED(instance);

	AGR_INFO("cleaning up");
	free_models();
	free_imgs();

	free_texture(&camera_mesh.texts[0]);
	free(camera_mesh.texts);
}

JNIEXPORT jint JNICALL
Java_com_petostr_p_engine_AGRenderer_create_1oes_1texture(JNIEnv *env,
							  jobject instance)
{
	UNUSED(env);
	UNUSED(instance);

	float coords[] = {
		-1.0f, 1.0f, 0.0f,
		-1.0f, -1.0f, 0.0f,
		1.0f, -1.0f, 0.0f,
		1.0f, 1.0f, 0.0f
	};

	float textureCoords[] = {
		1.0f, 1.0f,
		1.0f, 0.0f,
		0.0f, 0.0f,
		0.0f, 1.0f
	};

	unsigned int indices[] = {
		0, 1, 2,
		2, 3, 0
	};

	create_vertices_buffer(&camera_mesh, coords, sizeof(coords),
			       NULL, 0,
			       textureCoords, sizeof(textureCoords),
			       indices, sizeof(indices));

	camera_mesh.texts = calloc(1, sizeof(*camera_mesh.texts));
	camera_mesh.ntexts = 1;

	char type[] = "camera";

	camera_mesh.texts[0] = create_oes_texture();
	camera_mesh.texts[0].type = malloc(sizeof(type));
	strcpy(camera_mesh.texts[0].type, type);

	return camera_mesh.texts[0].id;
}

static void draw_imgs(JNIEnv *env, jobject scene)
{
	jsize ientities_len;
	jobjectArray ientities = jscene_get_ientities(env, scene,
						      &ientities_len);
	int i;
	for (i = 0; i < ientities_len; i++) {
		jobject ientity =
			(*env)->GetObjectArrayElement(env, ientities, i);

		int vis = jentity_is_visible(env, ientity);
		if (!vis) {
			continue;
		}

		jobject resource = jget_resource(env, ientity);
		int selected = jentity_is_selected(env, ientity);

		int id = jget_resource_id(env, resource);

		if (id <= -1 || id > imgs_len) {
			AGR_WARN("resource not loaded, skipping...");
			return;
		}

		float mm[16];
		jentity_model_matrix(env, ientity, mm);

		float opacity = jientity_get_opacity(env, ientity);

		render_img(&imgs[id], mm, opacity, selected);
	}
}

static void draw_model(struct model_info *model)
{
	int i;
	for (i = 0; i < model->num_meshes; i++) {
		struct mesh_info *mesh = &model->meshes[i];

		glUniform1i(program.uflocs[has_texture_loc],
			    mesh->has_texture);
		check_gl_error("glUniform1i");
		glUniform4fv(program.uflocs[dcolor_loc], 1, mesh->dcolor);
		check_gl_error("glUniform4fv");
		glUniform4fv(program.uflocs[scolor_loc], 1, mesh->scolor);
		check_gl_error("glUniform4fv");
		glUniform1f(program.uflocs[opacity_loc], mesh->opacity);
		check_gl_error("glUniform1f");

		render_mesh(program.id, mesh);
	}
}

static void draw_models(JNIEnv *env, jobject scene)
{
	jsize mentities_len;
	jobjectArray mentities =
		jscene_get_mentities(env, scene, &mentities_len);
	int i;
	for (i = 0; i < mentities_len; i++) {
		jobject entity =
			(*env)->GetObjectArrayElement(env, mentities, i);

		int vis = jentity_is_visible(env, entity);
		if (!vis) {
			continue;
		}

		jobject resource = jget_resource(env, entity);
		int selected = jentity_is_selected(env, entity);

		jint id = jget_resource_id(env, resource);
		struct model_info *model = get_model(id);
		if (model == NULL) {
			AGR_WARN("resource(%d) not loaded, skipping...", id);
			return;
		}

		float mm[16];
		jentity_model_matrix(env, entity, mm);

		float vpos[3];
		jscene_get_camera_pos(env, scene, vpos);

		glUniformMatrix4fv(program.uflocs[mmatrix_loc], 1, GL_FALSE,
				   (const GLfloat *) mm);
		check_gl_error("glUniformMatrix4fv");

		glUniform3fv(program.uflocs[view_pos_loc], 1, vpos);
		check_gl_error("glUniform3fv");

		glUniform3fv(program.uflocs[dlight_dir_loc], 1, dlight_dir);
		check_gl_error("glUniform3fv");

		glUniform1i(program.uflocs[selected_loc], selected);
		check_gl_error("glUniform1i");

		draw_model(model);
	}
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_AGRenderer_draw(JNIEnv *env, jobject instance,
					  jobject scene)
{
	UNUSED(instance);

	enable_depth_test();
	shader_program_bind(&program);


	glUniformMatrix4fv(program.uflocs[pmatrix_loc], 1, GL_FALSE,
			   (const GLfloat *) projection_matrix);
	check_gl_error("glUniformMatrix4fv");


	float vm[16];
	jscene_get_view_matrix(env, scene, vm);
	glUniformMatrix4fv(program.uflocs[vmatrix_loc], 1, GL_FALSE,
			   (const GLfloat *) vm);
	check_gl_error("glUniformMatrix4fv");


	draw_models(env, scene);

	disable_depth_test();
	draw_imgs(env, scene);


	shader_program_unbind();
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_AGRenderer_draw_1camera(JNIEnv *env,
						  jobject instance,
						  jfloat rotation,
						  jint width, jint height)
{
	UNUSED(env);
	UNUSED(instance);

	float ratioW;
	float ratioH;
	float ratio = (float) width / (float) height;
	if (window_width > window_height) {
		float wratio = (float) window_width / (float) window_height;
		ratioW = wratio;
		ratioH = ratioW / ratio;
	} else {
		float wratio = (float) window_height / (float) window_width;
		ratioH = wratio;
		ratioW = ratioH / ratio;
	}

	static mat4x4 camera_mmatrix;
	mat4x4_identity(camera_mmatrix);
	mat4x4_scale_aniso(camera_mmatrix, camera_mmatrix, ratioW, ratioH,
			   1.0f);
	mat4x4_rotate_Z(camera_mmatrix, camera_mmatrix, rotation);

	disable_depth_test();
	check_gl_error("unknown");
	shader_program_bind(&camera_program);

	glUniformMatrix4fv(camera_program.uflocs[pmatrix_loc], 1, GL_FALSE,
			   (const GLfloat *) ratio_ortho_matrix);
	check_gl_error("glUniformMatrix4fv");

	glUniformMatrix4fv(camera_program.uflocs[mmatrix_loc], 1, GL_FALSE,
			   (const GLfloat *) camera_mmatrix);
	check_gl_error("glUniformMatrix4fv");

	render_mesh(camera_program.id, &camera_mesh);

	shader_program_unbind();
}

JNIEXPORT void JNICALL
Java_com_petostr_p_engine_AGRenderer_draw_1text(JNIEnv *env, jobject instance,
						jstring text_,
						jfloat x, jfloat y,
						jfloat scale,
						jfloatArray color_)
{
	UNUSED(instance);
	const char *text = (*env)->GetStringUTFChars(env, text_, 0);
	jfloat *color = (*env)->GetFloatArrayElements(env, color_, NULL);

	render_text(text, x, y, scale, color);

	(*env)->ReleaseStringUTFChars(env, text_, text);
	(*env)->ReleaseFloatArrayElements(env, color_, color, 0);
}
