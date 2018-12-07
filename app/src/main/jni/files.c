#include "files.h"

#include <stdlib.h>

#include <android/asset_manager.h>

char *read_file(const char *filename, off_t *len)
{
	AAsset *asset = AAssetManager_open(asset_manager,
		filename, AASSET_MODE_UNKNOWN);

	if (!asset) {
		return NULL;
	}

	off_t asset_len = AAsset_getLength(asset);

	char *content = malloc(asset_len * sizeof(char) + 1);
	asset_len = AAsset_read(asset, content, (size_t) asset_len);
	content[asset_len] = '\0';

	if (len) {
		*len = asset_len;
	}

	return content;
}
