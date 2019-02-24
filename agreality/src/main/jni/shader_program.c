#include "shader_program.h"

#include <stdlib.h>

#include "agrlog.h"
#include "files.h"
#include "util.h"

void shader_program_bind(const struct program_info *p)
{
	glUseProgram(p->id);
	check_gl_error("glUseProgram");
}

void shader_program_unbind(void)
{
	glUseProgram(0);
}

static void print_shader_info(struct shader_info *shdr)
{
	GLint len = 0;
	glGetShaderiv(shdr->id, GL_INFO_LOG_LENGTH, &len);
	GLchar m[len];
	glGetShaderInfoLog(shdr->id, len, &len, m);
	AGR_ERROR("%s compilation failed: %s\n", shdr->fname, m);
}

static void print_program_info(GLuint pid)
{
	GLint len = 0;
	glGetProgramiv(pid, GL_INFO_LOG_LENGTH, &len);
	GLchar m[len];
	glGetProgramInfoLog(pid, len, &len, m);
	AGR_ERROR("%s\n", m);
}

static void create_shader(struct shader_info *shdr, GLenum type)
{
	shdr->id = glCreateShader(type);

	char *src = NULL;
	read_file(&src, shdr->fname);
	AGR_INFO("compiling shader %s", shdr->fname);
	glShaderSource(shdr->id, 1, (const GLchar **) &src, NULL);
	glCompileShader(shdr->id);

	GLint compiled = GL_FALSE;
	glGetShaderiv(shdr->id, GL_COMPILE_STATUS, &compiled);

	if (compiled == GL_FALSE) {
		print_shader_info(shdr);
	}

	free(src);
}

static void link_shaders(GLuint p, GLuint vs, GLuint fs)
{
	glAttachShader(p, vs);
	glAttachShader(p, fs);
	glLinkProgram(p);

	GLint status = GL_FALSE;
	glGetProgramiv(p, GL_LINK_STATUS, &status);
	if (status == GL_FALSE) {
		print_program_info(p);
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

void create_program(struct program_info *p,
		    const struct shader_attrib *atbs, size_t atbs_len)
{
	create_shader(&p->vs, GL_VERTEX_SHADER);
	create_shader(&p->fs, GL_FRAGMENT_SHADER);

	p->id = glCreateProgram();

	size_t i = atbs_len;
	while (i--) {
		glBindAttribLocation(p->id, atbs[i].index, atbs[i].name);
		check_gl_error("glBindAttribLocation");
	}

	link_shaders(p->id, p->vs.id, p->fs.id);
}