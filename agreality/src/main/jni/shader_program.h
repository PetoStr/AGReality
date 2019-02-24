#ifndef __SHADER_PROGRAM_H__
#define __SHADER_PROGRAM_H__

#include <GLES3/gl3.h>

struct shader_info {
	GLuint id;

	const char fname[100];
};

struct program_info {
	GLuint id;

	struct shader_info vs;
	struct shader_info fs;

	GLint uflocs[20];
};

struct shader_attrib {
	GLuint index;
	GLchar name[24];
};

extern void shader_program_bind(const struct program_info *p);
extern void shader_program_unbind(void);
extern void create_program(struct program_info *p,
			   const struct shader_attrib *atbs, size_t atbs_len);

#endif
