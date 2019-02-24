#include "mesh.h"

#include <assert.h>
#include <stdlib.h>

#include "texture.h"
#include "util.h"

void create_vertices_buffer(struct mesh_info *mesh,
			    const float *pos, size_t pos_size,
			    const float *norms, size_t norm_size,
			    const float *tc, size_t tc_size,
			    const unsigned int *ind, size_t ind_size)
{
	glGenVertexArrays(1, &mesh->vao);
	glBindVertexArray(mesh->vao);

	mesh->nvbos = 0;

	if (pos) {
		glGenBuffers(1, &mesh->vbos[mesh->nvbos]);
		glBindBuffer(GL_ARRAY_BUFFER, mesh->vbos[mesh->nvbos]);
		glBufferData(GL_ARRAY_BUFFER, pos_size, pos, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, NULL);
		mesh->nvbos++;
	}

	if (norms) {
		glGenBuffers(1, &mesh->vbos[mesh->nvbos]);
		glBindBuffer(GL_ARRAY_BUFFER, mesh->vbos[mesh->nvbos]);
		glBufferData(GL_ARRAY_BUFFER, norm_size, norms,
			     GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, NULL);
		mesh->nvbos++;
	}

	if (tc) {
		glGenBuffers(1, &mesh->vbos[mesh->nvbos]);
		glBindBuffer(GL_ARRAY_BUFFER, mesh->vbos[mesh->nvbos]);
		glBufferData(GL_ARRAY_BUFFER, tc_size, tc, GL_STATIC_DRAW);
		glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 0, NULL);
		mesh->nvbos++;
	}

	assert(ind != NULL);
	glGenBuffers(1, &mesh->vbos[mesh->nvbos]);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh->vbos[mesh->nvbos]);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind_size, ind, GL_STATIC_DRAW);
	mesh->nvbos++;

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);

	mesh->vert_count = (GLsizei) ind_size / sizeof(*ind);
}

static inline void begin_mesh_render(GLuint program_id,
				     const struct mesh_info *mesh)
{
	int n;
	for (n = 0; n < mesh->ntexts; n++) {
		struct texture_info *texture = &mesh->texts[n];

		glActiveTexture((GLenum) (GL_TEXTURE0 + n));

		GLint sampler =
			glGetUniformLocation(program_id, texture->type);
		glUniform1i(sampler, n);

		glBindTexture(texture->target, texture->id);
	}

	glBindVertexArray(mesh->vao);
	for (n = 0; n < mesh->nvbos; n++) {
		glEnableVertexAttribArray((GLuint) n);
	}
}

static inline void end_mesh_render(const struct mesh_info *mesh) {
	int n;
	for (n = 0; n < mesh->nvbos; n++) {
		glDisableVertexAttribArray((GLuint) n);
	}
	glBindVertexArray(0);

	if (mesh->ntexts > 0) {
		glBindTexture(mesh->texts[0].target, 0);
	}
}

void render_mesh(GLuint program_id, const struct mesh_info *mesh)
{
	begin_mesh_render(program_id, mesh);

	glDrawElements(GL_TRIANGLES, mesh->vert_count, GL_UNSIGNED_INT, 0);

	end_mesh_render(mesh);
}

void free_mesh(struct mesh_info *mesh)
{
	int n;
	for (n = 0; n < mesh->ntexts; n++) {
		free_texture(&mesh->texts[n]);
	}
	free(mesh->texts);
}
