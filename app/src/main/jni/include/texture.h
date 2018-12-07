#ifndef __TEXTURE_H__
#define __TEXTURE_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <GLES3/gl31.h>
#include <GLES2/gl2ext.h>

#include <stb_image.h>

#include "agrlog.h"
#include "files.h"

struct texture_info {
	GLuint id;
	GLenum target;

	char *type;
	char *path;

	int width;
	int height;

	uint8_t is_copy;
};

static inline struct texture_info create_texture(const char *fname,
	const char *tname)
{
	struct texture_info t = { };
	int comp;

	AGR_INFO("loading texture %s (%s)\n", fname, tname);

	off_t len;
	char *content = read_file(fname, &len);
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

static inline struct texture_info create_oes_texture(void)
{
	struct texture_info t = {};

	glGenTextures(1, &t.id);

	t.target = GL_TEXTURE_EXTERNAL_OES;

	glBindTexture(GL_TEXTURE_EXTERNAL_OES, t.id);

	glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	//glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);

	return t;
}

static inline void delete_texture(struct texture_info *t)
{
	if (t->is_copy)
		return;

	AGR_INFO("deleting texture %s (%s)\n", t->path, t->type);
	//glDeleteTextures(1, &t->id);
	free(t->type);
	free(t->path);
}

#endif
