#include <assert.h>
#include <stdlib.h>

#include "agrlog.h"
#include "assimpio.h"

struct file_info {
	off_t pos;
	AAsset *aasset;
};

const int ofiles_size = 20;

struct open_files {
	char fname[64];
	struct aiFile *file;
} ofiles[ofiles_size];

aiFileReadProc custom_read(C_STRUCT aiFile *file, char *ptr,
			   size_t size_of_elements,
			   size_t number_of_elements)
{
	assert(ptr != NULL);
	struct file_info *fi = (struct file_info *) file->UserData;
	size_t count = size_of_elements * number_of_elements;
	fi->pos += count;
	return (aiFileReadProc) (size_t) AAsset_read(fi->aasset, ptr, count);
}

aiFileWriteProc custom_write(C_STRUCT aiFile *file, const char *ptr,
			     size_t size_of_elements, size_t number_of_elements)
{
	AGR_ERROR("called unsupported function custom_write()");
	return 0;
}

aiFileTellProc custom_tell(C_STRUCT aiFile *file)
{
	struct file_info *fi = (struct file_info *) file->UserData;
	return (aiFileTellProc) fi->pos;
}

aiFileTellProc custom_size(C_STRUCT aiFile *file)
{
	struct file_info *fi = (struct file_info *) file->UserData;
	return (aiFileTellProc) AAsset_getLength(fi->aasset);
}

aiFileSeek custom_seek(C_STRUCT aiFile *file,
		       size_t offset, C_ENUM aiOrigin whence)
{
	struct file_info *fi = (struct file_info *) file->UserData;

	switch (whence) {
		case aiOrigin_CUR:
			fi->pos += offset;
			break;
		case aiOrigin_END:
			fi->pos = AAsset_getLength(fi->aasset) + offset;
			break;
		case aiOrigin_SET:
			fi->pos = offset;
			break;
		case _AI_ORIGIN_ENFORCE_ENUM_SIZE:
			break;
	}

	off_t ret = AAsset_seek(fi->aasset, offset, whence);

	return (aiFileSeek) ret;
}


aiFileFlushProc custom_flush(C_STRUCT aiFile *file)
{
	AGR_ERROR("called unsupported function custom_flush()");
	return NULL;
}

aiFileOpenProc custom_open(C_STRUCT aiFileIO *fileIO,
			   const char* filename, const char* mode)
{
	assert(filename != NULL);
	assert(mode != NULL);

	int i;
	for (i = 0; i < ofiles_size; i++) {
		if (strcmp(filename, ofiles[i].fname) == 0) {
			return (aiFileOpenProc) ofiles[i].file;
		}
	}

	AAsset *aasset = AAssetManager_open(asset_manager, filename,
		AASSET_MODE_RANDOM);

	struct file_info *fi = malloc(sizeof(struct file_info));
	fi->aasset = aasset;
	fi->pos = 0;

	struct aiFile *file = malloc(sizeof(struct aiFile));
	file->ReadProc = (aiFileReadProc) custom_read;
	file->WriteProc = (aiFileWriteProc) custom_write;
	file->TellProc = (aiFileTellProc) custom_tell;
	file->FileSizeProc = (aiFileTellProc) custom_size;
	file->SeekProc = (aiFileSeek) custom_seek;
	file->FlushProc = (aiFileFlushProc) custom_flush;
	file->UserData = (void *) fi;

	if (fi->aasset) {
		return (aiFileOpenProc) file;
	}

	AGR_ERROR("asset %s not found", filename);

	return NULL;
}

aiFileCloseProc custom_close(C_STRUCT aiFileIO* fileIO, C_STRUCT aiFile* file)
{
	struct file_info *fi = (struct file_info *) file->UserData;

	AAsset_seek(fi->aasset, 0, aiOrigin_SET);

	AAsset_close(fi->aasset);
	free(fi);
	free(file);

	return NULL;
}

void assimpio_init(void)
{
	custom_fileio.OpenProc = (aiFileOpenProc) custom_open;
	custom_fileio.CloseProc = (aiFileCloseProc) custom_close;
}
