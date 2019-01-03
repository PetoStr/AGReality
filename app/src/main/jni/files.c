#include "files.h"

#include <stdlib.h>

#include <android/asset_manager.h>

#include "agrlog.h"

off_t read_file(char **buf, const char *filename)
{
	AAsset *asset = AAssetManager_open(asset_manager,
		filename, AASSET_MODE_UNKNOWN);

	if (!asset) {
		AGR_ERROR("file %s not found", filename);
		return 0;
	}

	off_t asset_len = AAsset_getLength(asset);

	*buf = malloc((asset_len + 1) * sizeof(char));
	asset_len = AAsset_read(asset, *buf, (size_t) asset_len);
	(*buf)[asset_len] = '\0';

	return asset_len;
}
