#include <stdlib.h>
#include <string.h>

#include <jni.h>
#include <android/asset_manager_jni.h>
#include <assert.h>

#include "assimpio.h"
#include "linmath.h"
#include "loader.h"
#include "shader_program.h"
#include "util.h"

static struct program_info *program;
static struct program_info camera_program;

static int window_width;
static int window_height;

static mat4x4 projection_matrix;
static mat4x4 ortho_matrix;

static int mmatrix = 0;
static int vmatrix = 1;
static int pmatrix = 2;
static int has_texture = 3;

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
Java_com_example_p_engine_AGRenderer_init(JNIEnv *env, jobject instance,
					  jobject assetManager)
{
	asset_manager = AAssetManager_fromJava(env, assetManager);
	assimpio_init();

	check_gl_error("unknown");

	glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	//glEnable(GL_CULL_FACE);

	program = malloc(sizeof(*program));

	strcpy((char *) program->vs.file_name, "shaders/vert.glsl");
	strcpy((char *) program->fs.file_name, "shaders/frag.glsl");
	create_program(program);
	check_gl_error("create_program");

	program->uniform_locations[mmatrix] = glGetUniformLocation(program->id, "MMatrix");
	check_gl_error("glGetUniformLocation> MMatrix");
	program->uniform_locations[vmatrix] = glGetUniformLocation(program->id, "VMatrix");
	check_gl_error("glGetUniformLocation> VMatrix");
	program->uniform_locations[pmatrix] = glGetUniformLocation(program->id, "PMatrix");
	check_gl_error("glGetUniformLocation> PMatrix");
	program->uniform_locations[has_texture] = glGetUniformLocation(program->id, "has_texture");
	check_gl_error("glGetUniformLocation> has_texture");

	strcpy((char *) camera_program.vs.file_name, "shaders/camera_vs.glsl");
	strcpy((char *) camera_program.fs.file_name, "shaders/camera_fs.glsl");
	create_program(&camera_program);
	check_gl_error("create_program");

	camera_program.uniform_locations[mmatrix] = glGetUniformLocation(camera_program.id, "MMatrix");
	check_gl_error("glGetUniformLocation> MMatrix");
	camera_program.uniform_locations[pmatrix] = glGetUniformLocation(camera_program.id, "PMatrix");
	check_gl_error("glGetUniformLocation> PMatrix");

	AGR_INFO("GL_VENDOR: %s", glGetString(GL_VENDOR));
	AGR_INFO("GL_RENDERER: %s", glGetString(GL_RENDERER));
	AGR_INFO("GL_VERSION: %s", glGetString(GL_VERSION));
	AGR_INFO("GL_SHADING_LANGUAGE_VERSION: %s", glGetString(GL_SHADING_LANGUAGE_VERSION));
	AGR_INFO("GL_EXTENSIONS: %s", glGetString(GL_EXTENSIONS));
}

JNIEXPORT void JNICALL
Java_com_example_p_engine_AGRenderer_clear(JNIEnv *env, jobject instance)
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

JNIEXPORT void JNICALL
Java_com_example_p_engine_AGRenderer_surface_1changed(JNIEnv *env, jobject instance, jint width,
						      jint height)
{
	if (height == 0) {
		height = 1;
	}

	glViewport(0, 0, width, height);

	float ratioW;
	float ratioH;
	float ratio = (float) width / (float) height;;
	if (width > height) {
		ratioW = (float) width / (float) height;
		ratioH = 1.0f;
	} else {
		ratioW = 1.0f;
		ratioH = (float) height / (float) width;
	}

	mat4x4_perspective(projection_matrix, 0.45f, ratio, 1.0f, 1000.0f);
	//mat4x4_frustum(projection_matrix, -ratioW, ratioW, -ratioH, ratioH, 1.0f, 100.0f);
	mat4x4_ortho(ortho_matrix, -ratioW, ratioW, -ratioH, ratioH, -1.0f, 1.0f);

	window_width = width;
	window_height = height;
}

JNIEXPORT void JNICALL
Java_com_example_p_engine_AGRenderer_on_1destroy(JNIEnv *env,
						 jobject instance)
{
	AGR_INFO("cleaning up");
	delete_models();

	delete_texture(&camera_mesh.textures[0]);
	free(camera_mesh.textures);

	free(program);
	program = NULL;
}

JNIEXPORT jint JNICALL
Java_com_example_p_engine_AGRenderer_create_1oes_1texture(JNIEnv *env, jobject instance)
{
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

	camera_mesh.textures = malloc(sizeof(*camera_mesh.textures));
	camera_mesh.num_textures = 1;

	char type[] = "camera";

	camera_mesh.textures[0] = create_oes_texture();
	camera_mesh.textures[0].type = malloc(sizeof(type));
	strcpy(camera_mesh.textures[0].type, type);

	return camera_mesh.textures[0].id;
}

static jobject scene_get_camera(JNIEnv *env, jobject scene)
{
	jclass sclass = (*env)->GetObjectClass(env, scene);

	jmethodID get_camera = (*env)->GetMethodID(env, sclass,
		"getCamera", "()Lcom/example/p/engine/Camera;");

	return (*env)->CallObjectMethod(env, scene, get_camera);
}

static void scene_view_matrix(JNIEnv *env, jobject scene, float vm[16])
{
	jobject jcamera = scene_get_camera(env, scene);
	jclass cclass = (*env)->GetObjectClass(env, jcamera);

	jmethodID get_vm = (*env)->GetMethodID(env, cclass,
		"getViewMatrix", "()[F");

	jfloatArray j_vmArray = NULL;
	j_vmArray = (*env)->CallObjectMethod(env, jcamera, get_vm);

	jfloat *j_vm = (*env)->GetFloatArrayElements(env, j_vmArray, NULL);

	memcpy(vm, j_vm, 16 * sizeof(float));

	(*env)->ReleaseFloatArrayElements(env, j_vmArray, j_vm, 0);
}

static jobjectArray scene_get_entities(JNIEnv *env, jobject scene, jsize *len)
{
	jclass sclass = (*env)->GetObjectClass(env, scene);

	jmethodID get_entities = (*env)->GetMethodID(env, sclass,
		"getEntitiesArray", "()[Lcom/example/p/engine/Entity;");

	jobjectArray jentities = (*env)->CallObjectMethod(env, scene, get_entities);

	*len = (*env)->GetArrayLength(env, jentities);

	return jentities;
}

static void entity_model_matrix(JNIEnv *env, jobject entity, float mm[16])
{
	jclass eclass = (*env)->GetObjectClass(env, entity);

	jmethodID get_mm = (*env)->GetMethodID(env, eclass,
					       "getModelMatrix", "()[F");

	jfloatArray jmmArray = NULL;
	jmmArray = (*env)->CallObjectMethod(env, entity, get_mm);

	jfloat *jmm = (*env)->GetFloatArrayElements(env, jmmArray, NULL);

	memcpy(mm, jmm, 16 * sizeof(float));

	(*env)->ReleaseFloatArrayElements(env, jmmArray, jmm, 0);
}

static jobject entity_get_model(JNIEnv *env, jobject entity)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);

	jmethodID get_model = (*env)->GetMethodID(env, eclass,
		"getModel", "()Lcom/example/p/engine/Model;");

	return (*env)->CallObjectMethod(env, entity, get_model);
}

static jint get_entity_model_id(JNIEnv *env, jobject entity)
{
	jobject jmodel = entity_get_model(env, entity);
	jclass mclass = (*env)->GetObjectClass(env, jmodel);

	jmethodID get_id = (*env)->GetMethodID(env, mclass, "getId", "()I");
	return (*env)->CallIntMethod(env, jmodel, get_id);
}

static void render_model(struct model_info *model)
{
	int i;
	for (i = 0; i < model->num_meshes; i++) {
		struct mesh_info *mesh = &model->meshes[i];

		glUniform1i(program->uniform_locations[has_texture], mesh->num_textures > 0);
		check_gl_error("glUniform1i");

		render_mesh(program->id, mesh);
	}
}

JNIEXPORT void JNICALL
Java_com_example_p_engine_AGRenderer_draw(JNIEnv *env, jobject instance,
					  jobject scene)
{
	enable_depth_test();
	shader_program_bind(program);


	glUniformMatrix4fv(program->uniform_locations[pmatrix], 1, GL_FALSE, (const GLfloat *) projection_matrix);
	check_gl_error("glUniformMatrix4fv");


	float vm[16];
	scene_view_matrix(env, scene, vm);
	glUniformMatrix4fv(program->uniform_locations[vmatrix], 1, GL_FALSE, (const GLfloat *) vm);
	check_gl_error("glUniformMatrix4fv");


	jsize entities_len;
	jobjectArray entities = scene_get_entities(env, scene, &entities_len);
	int i;
	for (i = 0; i < entities_len; i++) {
		jobject entity = (*env)->GetObjectArrayElement(env, entities, i);
		float mm[16];

		jint id = get_entity_model_id(env, entity);
		struct model_info *model = get_model(id);
		if (model == NULL) {
			AGR_WARN("model(%d) not loaded, skipping...", id);
			continue;
		}

		entity_model_matrix(env, entity, mm);

		glUniformMatrix4fv(program->uniform_locations[mmatrix], 1, GL_FALSE, (const GLfloat *) mm);
		check_gl_error("glUniformMatrix4fv");

		render_model(model);
	}

	shader_program_unbind();
	disable_depth_test();
}

JNIEXPORT void JNICALL
Java_com_example_p_engine_AGRenderer_draw_1camera(JNIEnv *env, jobject instance,
						  jfloat rotation, jint width, jint height)
{
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
	mat4x4_scale_aniso(camera_mmatrix, camera_mmatrix, ratioW, ratioH, 1.0f);
	mat4x4_rotate_Z(camera_mmatrix, camera_mmatrix, rotation);

	check_gl_error("unknown");
	shader_program_bind(&camera_program);

	glUniformMatrix4fv(camera_program.uniform_locations[pmatrix], 1, GL_FALSE, (const GLfloat *) ortho_matrix);
	check_gl_error("glUniformMatrix4fv");

	glUniformMatrix4fv(camera_program.uniform_locations[mmatrix], 1, GL_FALSE, (const GLfloat *) camera_mmatrix);
	check_gl_error("glUniformMatrix4fv");

	render_mesh(camera_program.id, &camera_mesh);

	shader_program_unbind();
}
