#ifndef __FILES_H__
#define __FILES_H__

#include <android/asset_manager.h>

AAssetManager *asset_manager;

extern off_t read_file(char **buf, const char *filename);

#endif
