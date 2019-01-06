#include "loader.h"

#include <stdlib.h>
#include <string.h>

#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>

#include <stb_image.h>

#include <assimp/cimport.h>
#include <assimp/scene.h>
#include <assimp/postprocess.h>
#include <jni.h>

#include "agrlog.h"
#include "assimpio.h"
#include "jni_helper.h"
#include "scene.h"

#define UNKNOWN_TMAP_MASK 0x01
#define DIFFUSE_TMAP_MASK 0x02
#define SPECULAR_TMAP_MASK 0x04

struct texture_info create_texture(const char *fname, const char *tname)
{
	struct texture_info t = { 0 };
	int comp;

	AGR_INFO("loading texture %s (%s)\n", fname, tname);

	char *content = NULL;
	off_t len = read_file(&content, fname);
	if (content == NULL) {
		AGR_ERROR("texture %s not found\n", fname);
		return t;
	}

	unsigned char *img;
	img = stbi_load_from_memory((const unsigned char *) content,
				    len, &t.width, &t.height, &comp, STBI_rgb_alpha);
	if (img == NULL) {
		AGR_ERROR("failed to load %s\n", fname);
		return t;
	}

	glGenTextures(1, &t.id);

	t.target = GL_TEXTURE_2D;

	glBindTexture(GL_TEXTURE_2D, t.id);

	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, t.width, t.height,
		     0, GL_RGBA, GL_UNSIGNED_BYTE, img);

	glGenerateMipmap(GL_TEXTURE_2D);

	/*long tr = 0;
	long tg = 0;
	long tb = 0;
	long ta = 0;

	int siz = t.width * t.height * 4;
	int i;
	for (i = 0; i < siz; i += 4) {
		tr += img[i];
		tg += img[i + 1];
		tb += img[i + 2];
		ta += img[i + 3];
	}

	long ar = tr / (siz / 4);
	long ag = tg / (siz / 4);
	long ab = tb / (siz / 4);
	long aa = ta / (siz / 4);

	AGR_INFO("average: (%d, %d, %d, %d)", ar, ag, ab, aa);*/

	glBindTexture(GL_TEXTURE_2D, 0);

	stbi_image_free(img);
	free(content);

	size_t flen = strlen(fname);
	t.path = malloc(flen + 1);
	strcpy(t.path, fname);

	size_t tlen = strlen(tname);
	t.type = malloc(tlen + 1);
	strcpy(t.type, tname);

	return t;
}

struct texture_info create_oes_texture(void)
{
	struct texture_info t = { 0 };

	glGenTextures(1, &t.id);

	t.target = GL_TEXTURE_EXTERNAL_OES;

	glBindTexture(GL_TEXTURE_EXTERNAL_OES, t.id);

	glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);

	return t;
}

void free_texture(struct texture_info *t)
{
	if (t->is_copy)
		return;

	AGR_INFO("freeing texture %s (%s)\n", t->path, t->type);
	//glDeleteTextures(1, &t->id);
	free(t->type);
	free(t->path);
}

static struct texture_info *find_texture(const char *fname,
					 struct model_info *model)
{
	int i;
	for (i = 0; i < model->num_meshes; i++) {
		struct mesh_info *mesh = &model->meshes[i];

		int n;
		for (n = 0; n < mesh->ntexts; n++) {
			struct texture_info *texture = &mesh->texts[n];

			if (strcmp(fname, texture->path) == 0) {
				return texture;
			}
		}
	}

	return NULL;
}

static void load_material_textures(struct aiMaterial *material,
			    struct model_info *model,
			    struct mesh_info *mesh,
			    enum aiTextureType type,
			    char *type_name, char *dir)
{
	unsigned int textures = aiGetMaterialTextureCount(material, type);

	if (textures == 0) {
		return;
	}

	switch (type) {
		case aiTextureType_DIFFUSE:
			mesh->has_texture |= DIFFUSE_TMAP_MASK;
			break;
		case aiTextureType_SPECULAR:
			mesh->has_texture |= SPECULAR_TMAP_MASK;
			break;
		default:
			mesh->has_texture |= UNKNOWN_TMAP_MASK;
	}

	int offset = mesh->ntexts;

	size_t new_size = sizeof(*mesh->texts)
		* (mesh->ntexts + textures);
	mesh->texts = realloc(mesh->texts, new_size);

	unsigned int i;
	for (i = 0; i < textures; i++) {
		int curr = offset + i;

		struct aiString str;
		aiGetMaterialTexture(material, type, i, &str,
				     NULL, NULL, NULL, NULL, NULL, NULL);

		size_t dlen = strlen(dir);
		size_t p = dlen + str.length + 1;
		char fname[p];
		strcpy(fname, dir);
		strcpy(fname + dlen, str.data);

		struct texture_info *tex = find_texture(fname, model);
		if (tex) {
			mesh->texts[curr] = *tex;
			mesh->texts[curr].is_copy = 1;
		} else {
			mesh->texts[curr] = create_texture(fname,
				type_name);
			mesh->texts[curr].is_copy = 0;
		}
	}
	mesh->ntexts += textures;
}


enum LOAD_STATUS load_model(const char *model_name, struct model_info *model)
{
	AGR_INFO("loading model %s", model_name);
	char dir[strlen(model_name)];
	size_t curr = 0;
	size_t lsl = 0;
	while (model_name[curr]) {
		if (model_name[curr] == '/') {
			lsl = curr;
		}
		curr++;
	}
	memcpy(dir, model_name, lsl + 1);
	dir[lsl + 1] = '\0';

#ifndef NDEBUG
	struct aiLogStream stream = aiGetPredefinedLogStream(aiDefaultLogStream_STDOUT, NULL);
	aiAttachLogStream(&stream);
#endif

	const struct aiScene *scene;
	scene = aiImportFileEx(model_name,
			     aiProcess_Triangulate |
			     aiProcess_JoinIdenticalVertices |
			     aiProcess_FixInfacingNormals |
			     aiProcess_GenUVCoords |
			     aiProcess_GenNormals |
			     aiProcess_FlipUVs |
			     aiProcess_OptimizeMeshes |
			     aiProcess_RemoveRedundantMaterials, &custom_fileio);
	if (!scene) {
		AGR_ERROR("failed to load model %s\n", model_name);
		return LOAD_FAILED;
	}

	model->num_meshes = scene->mNumMeshes;
	model->meshes = calloc(model->num_meshes, sizeof(struct mesh_info));

	struct aiVector3D *min = &model->min;
	struct aiVector3D *max = &model->max;
	struct aiVector3D *center = &model->center;
	min->x = min->y = min->z = 1e10f;
	max->x = max->y = max->z = -1e10f;

	int i;
	for (i = 0; i < model->num_meshes; i++) {
		const struct aiMesh *mesh = scene->mMeshes[i];
		int num_indices;
		int num_vertices;

		num_indices = mesh->mNumFaces * 3;
		num_vertices = mesh->mNumVertices * 3;

		size_t pos_size = sizeof(float) * num_vertices;
		float *pos = malloc(pos_size);

		size_t norm_size = sizeof(float) * num_vertices;
		float *norms = malloc(norm_size);

		size_t tc_size = sizeof(float) * mesh->mNumVertices * 2;
		float *tc = malloc(tc_size);

		size_t tangents_size = sizeof(float) * num_vertices;
		float *tangents = malloc(tangents_size);

		size_t ind_size = sizeof(unsigned int) * num_indices;
		unsigned int *ind = malloc(ind_size);

		unsigned int t;
		for (t = 0; t < mesh->mNumFaces; t++) {
			const struct aiFace *face = &mesh->mFaces[t];

			ind[t * 3 + 0] = face->mIndices[0];
			ind[t * 3 + 1] = face->mIndices[1];
			ind[t * 3 + 2] = face->mIndices[2];
		}

		for (t = 0; t < mesh->mNumVertices; t++) {
			pos[t * 3 + 0] = mesh->mVertices[t].x;
			pos[t * 3 + 1] = mesh->mVertices[t].y;
			pos[t * 3 + 2] = mesh->mVertices[t].z;

			norms[t * 3 + 0] = mesh->mNormals[t].x;
			norms[t * 3 + 1] = mesh->mNormals[t].y;
			norms[t * 3 + 2] = mesh->mNormals[t].z;

			if (mesh->mTextureCoords[0]) {
				tc[t * 2 + 0] = mesh->mTextureCoords[0][t].x;
				tc[t * 2 + 1] = mesh->mTextureCoords[0][t].y;
			}

			min->x = fminf(mesh->mVertices[t].x, min->x);
			min->y = fminf(mesh->mVertices[t].y, min->y);
			min->z = fminf(mesh->mVertices[t].z, min->z);

			max->x = fmaxf(mesh->mVertices[t].x, max->x);
			max->y = fmaxf(mesh->mVertices[t].y, max->y);
			max->z = fmaxf(mesh->mVertices[t].z, max->z);
		}

		create_vertices_buffer(&model->meshes[i],
				       pos, pos_size,
				       norms, norm_size,
				       tc, tc_size,
				       tangents, tangents_size,
				       ind, ind_size);

		struct aiMaterial *material;
		material = scene->mMaterials[mesh->mMaterialIndex];

		load_material_textures(material, model, &model->meshes[i],
				       aiTextureType_DIFFUSE, "texture_diffuse",
				       dir);
		load_material_textures(material, model, &model->meshes[i],
				       aiTextureType_SPECULAR, "texture_specular",
				       dir);

		int r;
		for (r = aiTextureType_NONE; r < aiTextureType_UNKNOWN; r++) {
			unsigned int textures = aiGetMaterialTextureCount(material, r);
			if (textures != 0) {
				AGR_INFO("%d texture%sof type %d",
					 textures, textures != 1 ? "s " : " ", r);
			}
		}

		AGR_INFO("has_texture = %d", model->meshes[i].has_texture);

		free(pos);
		free(norms);
		free(tc);
		free(ind);
	}

	center->x = (min->x + max->x) / 2.0f;
	center->y = (min->y + max->y) / 2.0f;
	center->z = (min->z + max->z) / 2.0f;

	aiReleaseImport(scene);
#ifndef NDEBUG
	aiDetachAllLogStreams();
#endif

	return LOAD_SUCCESSFUL;
}

static int handle_model(const char *path, size_t path_len, struct model_info *model)
{
	int id = get_model_id(path, path_len);
	if (id == -1) {
		models = realloc(models, (models_len + 1) * sizeof(*models));
		enum LOAD_STATUS status;
		status = load_model(path, &models[models_len]);
		if (status != LOAD_SUCCESSFUL) {
			return -1;
		}

		id = models_len;

		if (path_len < 128) {
			memcpy(models[id].path, path, path_len);
		} else {
			AGR_ERROR("path_len too long");
		}

		AGR_INFO("model id = %d", id);

		models_len++;
	}

	if (model) {
		*model = models[id];
	}

	return id;
}

static int handle_texture(const char *path, size_t path_len, JNIEnv *env, jobject ientity,
			  struct texture_info *tex)
{
	int id = get_img_id(path, path_len);
	if (id == -1) {
		imgs = realloc(imgs, (imgs_len + 1) * sizeof(*imgs));
		id = imgs_len;

		imgs[id] = create_texture(path, "img");

		AGR_INFO("img id = %d", id);

		imgs_len++;
	}

	jientity_set_size(env, ientity, imgs[id].width, imgs[id].height);

	if (tex) {
		*tex = imgs[id];
	}

	return id;
}

static void aiVector3D_to_float(const struct aiVector3D *vec, float *res)
{
	res[0] = vec->x;
	res[1] = vec->y;
	res[2] = vec->z;
}

JNIEXPORT void JNICALL
Java_com_example_p_engine_entities_Entity_load(JNIEnv *env, jobject instance)
{
	jobject resource = jget_resource(env, instance);
	jclass rclass = (*env)->GetObjectClass(env, resource);

	jmethodID get_path = (*env)->GetMethodID(env, rclass, "getPath", "()Ljava/lang/String;");

	jstring jpath = (*env)->CallObjectMethod(env, resource, get_path);
	const char *path = (*env)->GetStringUTFChars(env, jpath, NULL);
	size_t path_len = (size_t) (*env)->GetStringLength(env, jpath);

	int type = jget_resource_type(env, resource);

	float min[3];
	float max[3];
	float center[3];

	struct texture_info tex;
	struct model_info model;

	int id;
	switch (type) {
		case RESOURCE_TYPE_MODEL:
			id = handle_model(path, path_len, &model);

			aiVector3D_to_float(&model.min, min);
			aiVector3D_to_float(&model.max, max);
			aiVector3D_to_float(&model.center, center);

			break;
		case RESOURCE_TYPE_TEXTURE:
			id = handle_texture(path, path_len, env, instance, &tex);

			min[0] = 0.0f;
			min[0] = 0.0f;
			min[0] = 0.0f;

			max[0] = tex.width;
			max[1] = tex.height;
			max[2] = 0.0f;

			center[0] = 0.0f;
			center[1] = 0.0f;
			center[2] = 0.0f;

			break;
		default:
			AGR_ERROR("invalid resource type %d", type);
			id = -1;
	}

	jentity_set_min(env, instance, min[0], min[1], min[2]);
	jentity_set_max(env, instance, max[0], max[1], max[2]);
	jentity_set_center(env, instance, center[0], center[1], center[2]);

	jset_resource_id(env, resource, id);

	(*env)->ReleaseStringUTFChars(env, jpath, path);
}
