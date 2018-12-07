#include "shader_program.h"

#include <stdlib.h>

#include "agrlog.h"
#include "files.h"
#include "util.h"

void shader_program_bind(struct program_info *p)
{
	glUseProgram(p->id);
	check_gl_error("glUseProgram");
}

void shader_program_unbind(void)
{
	glUseProgram(0);
}


void create_shader(struct shader_info *shdr, GLenum type)
{
	shdr->id = glCreateShader(type);

	char *src = read_file(shdr->file_name, NULL);
	glShaderSource(shdr->id, 1, (const GLchar **) &src, NULL);
	glCompileShader(shdr->id);

	GLint compiled = GL_FALSE;
	glGetShaderiv(shdr->id, GL_COMPILE_STATUS, &compiled);

	if (compiled == GL_FALSE) {
		GLint len = 0;
		glGetShaderiv(shdr->id, GL_INFO_LOG_LENGTH, &len);
		GLchar m[len];
		glGetShaderInfoLog(shdr->id, len, &len, m);
		AGR_ERROR("%s compilation failed: %s\n", shdr->file_name, m);
	}

	free(src);
}

void link_shaders(GLint p, GLint vs, GLint fs)
{
	glAttachShader(p, vs);
	glAttachShader(p, fs);
	glLinkProgram(p);

	GLint status = GL_FALSE;
	glGetProgramiv(p, GL_LINK_STATUS, &status);
	if (status == GL_FALSE) {
		GLint len = 0;
		glGetProgramiv(p, GL_INFO_LOG_LENGTH, &len);
		GLchar m[len];
		glGetProgramInfoLog(p, len, &len, m);
		AGR_ERROR("%s\n", m);
	}

	glValidateProgram(p);
	glGetProgramiv(p, GL_VALIDATE_STATUS, &status);
	if (status == GL_FALSE) {
		AGR_ERROR("shader program validation failed\n");
	}

	glDetachShader(p, vs);
	check_gl_error("glDetachShader");
	glDetachShader(p, fs);
	check_gl_error("glDetachShader");

	glDeleteShader(vs);
	check_gl_error("glDeleteShader");
	glDeleteShader(fs);
	check_gl_error("glDeleteShader");
}

void create_program(struct program_info *p)
{
	create_shader(&p->vs, GL_VERTEX_SHADER);
	create_shader(&p->fs, GL_FRAGMENT_SHADER);

	p->id = glCreateProgram();

	link_shaders(p->id, p->vs.id, p->fs.id);
}

void delete_program(struct program_info *p)
{
	shader_program_unbind();
	glDeleteProgram(p->id);
	check_gl_error("glDeleteProgram");
}
