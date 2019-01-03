#ifndef __AGREALITY_TEXT_RENDERER_H__
#define __AGREALITY_TEXT_RENDERER_H__

#include <GLES3/gl3.h>

#include "linmath.h"

void text_renderer_init(void);
void render_text(const char *str, GLfloat x, GLfloat y,
		 GLfloat scale, vec3 color);

#endif
