#include "img_renderer.h"

#include <string.h>

#include "pv_matrices.h"
#include "shader_program.h"
#include "util.h"

static struct program_info sprog;

static GLuint vao;
static GLuint vbo;

static const int pmatrix_loc = 0;
static const int mmatrix_loc = 1;
static const int img_loc = 2;
static const int opac_loc = 3;
static const int selected_loc = 4;

static void init_vobject(void)
{
	GLfloat vertex[6][2] = {
		{ 0.0f, 0.0f },
		{ 0.0f, 1.0f },
		{ 1.0f, 1.0f },
		{ 1.0f, 0.0f },
		{ 0.0f, 0.0f },
		{ 1.0f, 1.0f }
	};

	glGenVertexArrays(1, &vao);
	glBindVertexArray(vao);

	glGenBuffers(1, &vbo);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);

	glBufferData(GL_ARRAY_BUFFER, sizeof(vertex), vertex, GL_STATIC_DRAW);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(GLfloat), 0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);
}

void img_renderer_init(void)
{
	struct shader_attrib atbs[] = {
		{ 0, "vertex" }
	};

	strcpy((char *) sprog.vs.fname, "shaders/img_vs.glsl");
	strcpy((char *) sprog.fs.fname, "shaders/img_fs.glsl");
	create_program(&sprog, atbs, 1);

	sprog.uflocs[pmatrix_loc] = glGetUniformLocation(sprog.id, "PMatrix");
	sprog.uflocs[mmatrix_loc] = glGetUniformLocation(sprog.id, "MMatrix");
	sprog.uflocs[img_loc] = glGetUniformLocation(sprog.id, "img");
	sprog.uflocs[opac_loc] = glGetUniformLocation(sprog.id, "opacity");
	sprog.uflocs[selected_loc] = glGetUniformLocation(sprog.id, "selected");

	init_vobject();
	check_gl_error("init_vobject");
}

void render_img(const struct texture_info *img, const GLfloat *mmatrix,
		float opacity, int selected)
{
	shader_program_bind(&sprog);

	glUniformMatrix4fv(sprog.uflocs[pmatrix_loc], 1, GL_FALSE, (const GLfloat *) size_ortho_matrix);
	glUniformMatrix4fv(sprog.uflocs[mmatrix_loc], 1, GL_FALSE, mmatrix);
	glUniform1i(sprog.uflocs[img_loc], 0);
	glUniform1f(sprog.uflocs[opac_loc], opacity);
	glUniform1i(sprog.uflocs[selected_loc], selected);

	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, img->id);

	glBindVertexArray(vao);
	glEnableVertexAttribArray(0);

	glDrawArrays(GL_TRIANGLES, 0, 6);

	glDisableVertexAttribArray(0);
	glBindVertexArray(0);
	glBindTexture(GL_TEXTURE_2D, 0);

	shader_program_unbind();
}
