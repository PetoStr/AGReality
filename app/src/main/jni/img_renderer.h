#ifndef __AGREALITY_IMG_RENDERER_H__
#define __AGREALITY_IMG_RENDERER_H__

#include <GLES3/gl3.h>

#include "linmath.h"
#include "texture.h"

void img_renderer_init(void);
void render_img(const struct texture_info *img,  const GLfloat *mmatrix,
		float opacity, int selected);

#endif
