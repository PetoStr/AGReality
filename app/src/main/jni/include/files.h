#ifndef __FILES_H__
#define __FILES_H__

#include <android/asset_manager.h>

AAssetManager *asset_manager;

extern char *read_file(const char *filename, off_t *len);

#endif
