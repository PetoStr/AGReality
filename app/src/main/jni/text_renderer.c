#include "text_renderer.h"

#include <ft2build.h>
#include FT_FREETYPE_H

#include "agrlog.h"
#include "files.h"
#include "pv_matrices.h"
#include "shader_program.h"
#include "util.h"

#define CHARS_MAX 128

struct text_char {
	GLuint tid;

	vec2 size;
	vec2 bearing;

	FT_Pos adv;
};

static struct text_char chars[CHARS_MAX];

static struct program_info sprog;

static GLuint vao;
static GLuint vbo;

static const int text_loc = 0;
static const int text_color_loc = 1;
static const int pmatrix_loc = 2;

static char *fbuf;

static void init_ft(char *font, FT_Library *flib, FT_Face *fface)
{
	if (FT_Init_FreeType(flib)) {
		AGR_ERROR("failed to init FreeType");
		return;
	}

	off_t len = read_file(&fbuf, font);
	AGR_INFO("len = %ld", len);
	if (FT_New_Memory_Face(*flib, (const FT_Byte *) fbuf, len, 0,
			       fface)) {
		AGR_ERROR("failed to load font %s", font);
		return;
	}

	FT_Set_Pixel_Sizes(*fface, 0, 28);
}

static void destroy_ft(FT_Library *flib, FT_Face *fface)
{
	FT_Done_Face(*fface);
	FT_Done_FreeType(*flib);
	free(fbuf);
}

static void init_chars(void)
{
	FT_Library flib;
	FT_Face fface;

	init_ft("fonts/DejaVuSansMono.ttf", &flib, &fface);

	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

	GLubyte c;
	for (c = 0; c < CHARS_MAX; c++) {
		if (FT_Load_Char(fface, c, FT_LOAD_RENDER)) {
			AGR_ERROR("failed to load glyph %d", c);
			continue;
		}

		glGenTextures(1, &chars[c].tid);
		check_gl_error("glGenTextures");
		glBindTexture(GL_TEXTURE_2D, chars[c].tid);
		check_gl_error("glBindTexture");

		/*
		 * XXX GL_RED does not work on every device
		 * (GL_INVALID_OPERATION)
		 */
		glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE,
			     fface->glyph->bitmap.width,
			     fface->glyph->bitmap.rows,
			     0, GL_LUMINANCE, GL_UNSIGNED_BYTE,
			     fface->glyph->bitmap.buffer);
		check_gl_error("glTexImage2D");

		glTexParameteri(GL_TEXTURE_2D,
				GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D,
				GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D,
				GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D,
				GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		check_gl_error("glTexParameteri");

		chars[c].adv = fface->glyph->advance.x;
		chars[c].size[0] = fface->glyph->bitmap.width;
		chars[c].size[1] = fface->glyph->bitmap.rows;
		chars[c].bearing[0] = fface->glyph->bitmap_left;
		chars[c].bearing[1] = fface->glyph->bitmap_top;
	}

	destroy_ft(&flib, &fface);
}

static void init_vobjects(void)
{
	glGenVertexArrays(1, &vao);
	glGenBuffers(1, &vbo);

	glBindVertexArray(vao);
	glBindBuffer(GL_ARRAY_BUFFER, vbo);
	glBufferData(GL_ARRAY_BUFFER, sizeof(GLfloat) * 6 * 4, NULL,
		     GL_DYNAMIC_DRAW);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE,
			      4 * sizeof(GLfloat), 0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);
}

void text_renderer_init(void)
{
	init_chars();
	check_gl_error("init_characters");

	struct shader_attrib atbs[] = {
		{ 0, "vertex" }
	};

	strcpy((char *) sprog.vs.fname, "shaders/text_vs.glsl");
	strcpy((char *) sprog.fs.fname, "shaders/text_fs.glsl");
	create_program(&sprog, atbs, 1);

	sprog.uflocs[text_loc] = glGetUniformLocation(sprog.id, "text");
	sprog.uflocs[text_color_loc] =
		glGetUniformLocation(sprog.id, "text_color");
	sprog.uflocs[pmatrix_loc] = glGetUniformLocation(sprog.id, "PMatrix");

	init_vobjects();
	check_gl_error("init_vobjects");
}

void render_text(const char *str, GLfloat x, GLfloat y,
		 GLfloat scale, vec3 color)
{
	shader_program_bind(&sprog);

	glUniform3fv(sprog.uflocs[text_color_loc], 1, color);
	check_gl_error("glUniform3f");
	glUniform1i(sprog.uflocs[text_loc], 0);
	check_gl_error("glUniform1i");
	glUniformMatrix4fv(sprog.uflocs[pmatrix_loc], 1, GL_FALSE,
			   (const GLfloat *) size_ortho_matrix);

	glActiveTexture(GL_TEXTURE0);
	glBindVertexArray(vao);

	for (char *c = (char *) str; *c; c++) {
		if (*c > CHARS_MAX) {
			continue;
		}

		struct text_char ch = chars[(int) *c];

		GLfloat xpos = x + ch.bearing[0] * scale;
		GLfloat ypos = y - (ch.size[1] - ch.bearing[1]) * scale;

		GLfloat w = ch.size[0] * scale;
		GLfloat h = ch.size[1] * scale;

		GLfloat vertices[6][4] = {
			{ xpos,     ypos + h,   0.0, 0.0 },
			{ xpos,     ypos,       0.0, 1.0 },
			{ xpos + w, ypos,       1.0, 1.0 },

			{ xpos,     ypos + h,   0.0, 0.0 },
			{ xpos + w, ypos,       1.0, 1.0 },
			{ xpos + w, ypos + h,   1.0, 0.0 }
		};

		glBindTexture(GL_TEXTURE_2D, ch.tid);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(0);
		glBufferSubData(GL_ARRAY_BUFFER, 0,
				sizeof(vertices), vertices);
		check_gl_error("glBufferSubData");
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glDrawArrays(GL_TRIANGLES, 0, 6);
		glDisableVertexAttribArray(0);

		x += (ch.adv >> 6) * scale;
	}

	glBindVertexArray(0);
	glBindTexture(GL_TEXTURE_2D, 0);

	shader_program_unbind();
}
