#ifndef __SHADER_PROGRAM_H__
#define __SHADER_PROGRAM_H__

#include <GLES3/gl31.h>

struct shader_info {
	GLuint id;

	const char file_name[100];
};

struct program_info {
	GLuint id;

	struct shader_info vs;
	struct shader_info fs;

	GLint uniform_locations[10];
};

extern void shader_program_bind(struct program_info *p);

extern void shader_program_unbind(void);

extern void create_program(struct program_info *p);

extern void delete_program(struct program_info *p);

#endif
