#ifndef __MESH_H__
#define __MESH_H__

#include <GLES3/gl3.h>

#include "texture.h"

struct mesh_info {
	GLuint vao;
	GLuint vbos[12];

	int nvbos;

	GLsizei vert_count;
	
	struct texture_info *texts;
	int ntexts;

	int has_smap;
	int has_nmap;
};

extern void create_vertices_buffer(struct mesh_info *mesh,
				   const float *pos, size_t pos_size,
				   const float *norms, size_t norm_size,
				   const float *tc, size_t tc_size,
				   const float *tangents, size_t tangents_size,
				   const unsigned int *ind, size_t ind_size);
extern void render_mesh(GLuint program_id, const struct mesh_info *mesh);
extern void free_mesh(struct mesh_info *mesh);

#endif

