#ifndef __AGREALITY_SCENE_H__
#define __AGREALITY_SCENE_H__

#include <stdint.h>

#include "model.h"
#include "texture.h"

struct texture_info *imgs;
int imgs_len;

struct model_info *models;
int models_len;

int get_model_id(const char *path, size_t path_len);
struct model_info *get_model(int id);
void free_model(struct model_info *model);
void free_models(void);

int get_img_id(const char *path, size_t path_len);
void free_imgs(void);

#endif
