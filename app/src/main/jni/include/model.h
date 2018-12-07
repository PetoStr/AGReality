#ifndef __ENTITY_H__
#define __ENTITY_H__

#include "mesh.h"
#include "texture.h"

struct model_info {
	char path[32];

	struct mesh_info *meshes;
	int num_meshes;
};

struct model_info *models;
int models_len;

static inline int get_model_id(char *path)
{
	int i;
	for (i = 0; i < models_len; i++) {
		if (strcmp(models[i].path, path) == 0) {
			return i;
		}
	}

	return -1;
}

static inline struct model_info *get_model(int id)
{
	if (id >= models_len || id < 0) {
		/* TODO: use error model */
		return NULL;
	}

	return &models[id];
}

static inline void delete_model(struct model_info *model)
{
	int i;
	for (i = 0; i < model->num_meshes; i++) {
		delete_mesh(&model->meshes[i]);
	}
	free(model->meshes);
}

static inline void delete_models(void)
{
	int i;
	for (i = 0; i < models_len; i++) {
		delete_model(&models[i]);
	}
	free(models);
	models_len = 0;
	models = NULL;
}

#endif

