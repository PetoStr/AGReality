#include "loader.h"

#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include <GLES3/gl31.h>

#include <assimp/cimport.h>
#include <assimp/scene.h>
#include <assimp/postprocess.h>
#include <jni.h>

#include "agrlog.h"
#include "assimpio.h"

static struct texture_info *find_texture(const char *fname,
					 struct model_info *model)
{
	int i;
	for (i = 0; i < model->num_meshes; i++) {
		struct mesh_info *mesh = &model->meshes[i];

		int n;
		for (n = 0; n < mesh->num_textures; n++) {
			struct texture_info *texture = &mesh->textures[n];

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

	int offset = mesh->num_textures;

	size_t new_size = sizeof(*mesh->textures)
		* (mesh->num_textures + textures);
	mesh->textures = realloc(mesh->textures, new_size);

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
			mesh->textures[curr] = *tex;
			mesh->textures[curr].is_copy = 1;
		} else {
			mesh->textures[curr] = create_texture(fname,
				type_name);
			mesh->textures[curr].is_copy = 0;
		}
	}
	mesh->num_textures += textures;
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

	//struct aiLogStream stream = aiGetPredefinedLogStream(aiDefaultLogStream_FILE, "assimp_log.txt");
	//aiAttachLogStream(&stream);

	const struct aiScene *scene;
	scene = aiImportFileEx(model_name,
			     aiProcess_Triangulate |
			     aiProcess_JoinIdenticalVertices |
			     aiProcess_FixInfacingNormals |
			     aiProcess_GenUVCoords |
			     aiProcess_GenNormals |
			     aiProcess_FlipUVs, &custom_fileio);
	if (!scene) {
		AGR_ERROR("failed to load model %s\n", model_name);
		return LOAD_FAILED;
	}

	model->num_meshes = scene->mNumMeshes;
	model->meshes = calloc(model->num_meshes, sizeof(struct mesh_info));

	unsigned int i;
	for (i = 0; i < model->num_meshes; i++) {
		const struct aiMesh *mesh = scene->mMeshes[i];
		int num_indices;
		int num_vertices;

		num_indices = mesh->mNumFaces * 3;
		num_vertices = mesh->mNumVertices * 3;

		/*size_t ind_size = sizeof(unsigned int) * num_indices;
		unsigned int *ind = malloc(ind_size);

		unsigned int t;
		for (t = 0; t < mesh->mNumFaces; t++) {
		    const struct aiFace *face = &mesh->mFaces[t];

		    ind[t * 3 + 0] = face->mIndices[0];
		    ind[t * 3 + 1] = face->mIndices[1];
		    ind[t * 3 + 2] = face->mIndices[2];
		}
		struct mesh_info m;
		m = create_buffers((float *) mesh->mVertices, sizeof(float) * num_vertices,
			   (float *) mesh->mNormals, sizeof(float) * num_vertices,
			   (float *) mesh->mTextureCoords[0], sizeof(float) * mesh->mNumFaces * 2,
			   ind, ind_size);

		free(ind);*/

		size_t pos_size = sizeof(float) * num_vertices;
		float *pos = malloc(pos_size);

		size_t norm_size = sizeof(float) * num_vertices;
		float *norms = malloc(norm_size);

		size_t tc_size = sizeof(float) * mesh->mNumVertices * 2;
		float *tc = malloc(tc_size);

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
		}

		create_vertices_buffer(&model->meshes[i],
				       pos, pos_size,
				       norms, norm_size,
				       tc, tc_size,
				       ind, ind_size);

		struct aiMaterial *material;
		material = scene->mMaterials[mesh->mMaterialIndex];

		load_material_textures(material, model, &model->meshes[i],
				       aiTextureType_DIFFUSE, "texture_diffuse",
				       dir);
		load_material_textures(material, model, &model->meshes[i],
				       aiTextureType_SPECULAR, "texture_specular",
				       dir);

		free(pos);
		free(norms);
		free(tc);
		free(ind);
	}

/*
	model->num_textures = scene->mNumTextures;
	model->textures = malloc(sizeof(*model->textures) * model->num_textures);

	for (i = 0; i < model->num_textures; i++) {
		glGenTextures(1, &model->textures[i].id);

		glBindTexture(GL_TEXTURE_2D, model->textures[i].id);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glGenerateMipmap(GL_TEXTURE_2D);

		struct aiTexture *tex = scene->mTextures[i];
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, tex->mWidth, tex->mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, tex->pcData);

		glBindTexture(GL_TEXTURE_2D, 0);
	}
*/

	aiReleaseImport(scene);
	//aiDetachAllLogStreams();

	return LOAD_SUCCESSFUL;
}

JNIEXPORT void JNICALL
Java_com_example_p_engine_Model_load(JNIEnv *env, jobject instance)
{
	jclass mclass = (*env)->GetObjectClass(env, instance);

	jmethodID get_path = (*env)->GetMethodID(env, mclass, "getPath", "()[C");
	jmethodID set_id = (*env)->GetMethodID(env, mclass, "setId", "(I)V");

	jcharArray jpathArray = NULL;
	jpathArray = (*env)->CallObjectMethod(env, instance, get_path);
	jsize path_len = (*env)->GetArrayLength(env, jpathArray);

	jchar *jpath = (*env)->GetCharArrayElements(env, jpathArray, NULL);

	char path[path_len];
	jsize i;
	for (i = 0; i < path_len; i++) {
		path[i] = (char) jpath[i];
	}

	(*env)->ReleaseCharArrayElements(env, jpathArray, jpath, 0);

	int id = get_model_id(path);
	if (id == -1) {
		models = realloc(models, (models_len + 1) * sizeof(*models));
		enum LOAD_STATUS status;
		status = load_model((char *) path, &models[models_len]);
		if (status != LOAD_SUCCESSFUL) {
			return;
		}

		id = models_len;
		memcpy(models[id].path, path, path_len);

		AGR_INFO("model id = %d", id);

		models_len++;
	}

	(*env)->CallVoidMethod(env, instance, set_id, id);
}
