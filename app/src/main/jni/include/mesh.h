#ifndef __MESH_H__
#define __MESH_H__

#include <GLES3/gl31.h>

#include "texture.h"

struct mesh_info {
	GLuint vao;
	GLuint vbos[4];

	int vbos_len;

	GLsizei vert_count;
	
	struct texture_info *textures;
	int num_textures;
};

extern void create_vertices_buffer(struct mesh_info *mesh,
				   float *pos, size_t pos_size,
				   float *norms, size_t norm_size,
				   float *tc, size_t tc_size,
				   unsigned int *ind, size_t ind_size);

extern void render_mesh(GLint program_id, struct mesh_info *mesh);

extern void delete_mesh(struct mesh_info *mesh);

#endif

