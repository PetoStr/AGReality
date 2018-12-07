#include <assert.h>
#include "mesh.h"

#include "texture.h"
#include "util.h"

void create_vertices_buffer(struct mesh_info *mesh,
			    float *pos, size_t pos_size,
			    float *norms, size_t norm_size,
			    float *tc, size_t tc_size,
			    unsigned int *ind, size_t ind_size)
{
	glGenVertexArrays(1, &mesh->vao);
	glBindVertexArray(mesh->vao);

	mesh->vbos_len = 0;

	if (pos) {
		glGenBuffers(1, &mesh->vbos[mesh->vbos_len]);
		glBindBuffer(GL_ARRAY_BUFFER, mesh->vbos[mesh->vbos_len]);
		glBufferData(GL_ARRAY_BUFFER, pos_size, pos, GL_STATIC_DRAW);
		glVertexAttribPointer(mesh->vbos_len, 3, GL_FLOAT, GL_FALSE, 0, NULL);
		mesh->vbos_len++;
	}

	if (norms) {
		glGenBuffers(1, &mesh->vbos[mesh->vbos_len]);
		glBindBuffer(GL_ARRAY_BUFFER, mesh->vbos[mesh->vbos_len]);
		glBufferData(GL_ARRAY_BUFFER, norm_size, norms, GL_STATIC_DRAW);
		glVertexAttribPointer(mesh->vbos_len, 3, GL_FLOAT, GL_FALSE, 0, NULL);
		mesh->vbos_len++;
	}

	if (tc) {
		glGenBuffers(1, &mesh->vbos[mesh->vbos_len]);
		glBindBuffer(GL_ARRAY_BUFFER, mesh->vbos[mesh->vbos_len]);
		glBufferData(GL_ARRAY_BUFFER, tc_size, tc, GL_STATIC_DRAW);
		glVertexAttribPointer(mesh->vbos_len, 2, GL_FLOAT, GL_FALSE, 0, NULL);
		mesh->vbos_len++;
	}

	assert(ind != NULL);
	glGenBuffers(1, &mesh->vbos[mesh->vbos_len]);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh->vbos[mesh->vbos_len]);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind_size, ind, GL_STATIC_DRAW);
	mesh->vbos_len++;

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);

	mesh->vert_count = ind_size / sizeof(*ind);
}

static inline void begin_mesh_render(GLint program_id, struct mesh_info *mesh)
{
	int n;
	for (n = 0; n < mesh->num_textures; n++) {
		struct texture_info *texture = &mesh->textures[n];

		glActiveTexture(GL_TEXTURE0 + n);

		GLint sampler = glGetUniformLocation(program_id, texture->type);
		glUniform1i(sampler, n);

		glBindTexture(texture->target, texture->id);
	}

	glBindVertexArray(mesh->vao);
	for (n = 0; n < mesh->vbos_len; n++) {
		glEnableVertexAttribArray(n);
	}
}

static inline void end_mesh_render(struct mesh_info *mesh) {
	int n;
	for (n = 0; n < mesh->vbos_len; n++) {
		glDisableVertexAttribArray(n);
	}
	glBindVertexArray(0);

	if (mesh->num_textures > 0) {
		glBindTexture(mesh->textures[0].target, 0);
	}
}

void render_mesh(GLint program_id, struct mesh_info *mesh)
{
	begin_mesh_render(program_id, mesh);

	glDrawElements(GL_TRIANGLES, mesh->vert_count, GL_UNSIGNED_INT, 0);

	end_mesh_render(mesh);
}

void delete_vertices_buffer(struct mesh_info *mesh)
{
	glDisableVertexAttribArray(0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);

	glDeleteBuffers(ARRAY_LENGTH(mesh->vbos), mesh->vbos);

	glBindVertexArray(0);
	glDeleteVertexArrays(1, &mesh->vao);
}

void delete_mesh(struct mesh_info *mesh)
{
	int n;
	for (n = 0; n < mesh->num_textures; n++) {
		delete_texture(&mesh->textures[n]);
	}
	free(mesh->textures);
}

