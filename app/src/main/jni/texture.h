#ifndef __TEXTURE_H__
#define __TEXTURE_H__

#include <GLES3/gl3.h>

struct texture_info {
	GLuint id;
	GLenum target;

	char *type;
	char *path;

	int width;
	int height;

	uint8_t is_copy;
};

struct texture_info create_texture(const char *fname, const char *tname);
struct texture_info create_oes_texture(void);
void free_texture(struct texture_info *t);

#endif
