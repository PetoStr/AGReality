#ifndef __ENTITY_H__
#define __ENTITY_H__

#include <assimp/vector3.h>
#include "mesh.h"

struct model_info {
	char path[128];

	struct mesh_info *meshes;
	int num_meshes;

	struct aiVector3D min;
	struct aiVector3D max;
	struct aiVector3D center;
};

#endif

