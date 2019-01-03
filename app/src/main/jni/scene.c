#include "scene.h"
#include "util.h"

#include <stdlib.h>
#include <string.h>

int get_model_id(const char *path, size_t path_len)
{
	int i;
	for (i = 0; i < models_len; i++) {
		if (strncmp(models[i].path, path, path_len) == 0) {
			return i;
		}
	}

	return -1;
}

struct model_info *get_model(int id)
{
	if (id >= models_len || id < 0) {
		/* TODO: use error model */
		return NULL;
	}

	return &models[id];
}

void free_model(struct model_info *model)
{
	int i;
	for (i = 0; i < model->num_meshes; i++) {
		free_mesh(&model->meshes[i]);
	}
	free(model->meshes);
}

void free_models(void)
{
	int i;
	for (i = 0; i < models_len; i++) {
		free_model(&models[i]);
	}
	free(models);
	models_len = 0;
	models = NULL;
}

int get_img_id(const char *path, size_t path_len)
{
	int i;
	for (i = 0; i < imgs_len; i++) {
		if (strncmp(imgs[i].path, path, path_len) == 0) {
			return i;
		}
	}

	return -1;
}

struct texture_info *get_img(int id)
{
	if (id >= imgs_len || id < 0) {
		/* TODO: use error img */
		return NULL;
	}

	return &imgs[id];
}

void free_imgs(void)
{
	int i;
	for (i = 0; i < imgs_len; i++) {
		free_texture(&imgs[i]);
	}
	free(imgs);
	imgs_len = 0;
	imgs = NULL;
}
