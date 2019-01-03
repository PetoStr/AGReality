#ifndef __LOADER_H__
#define __LOADER_H__

#include "model.h"

enum LOAD_STATUS {
	LOAD_SUCCESSFUL,
	LOAD_FAILED
};

extern enum LOAD_STATUS load_model(const char *model_name,
	struct model_info *model);

#endif
