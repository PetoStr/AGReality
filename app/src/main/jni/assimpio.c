#include <assert.h>
#include <stdlib.h>

#include <assimpio.h>
#include <agrlog.h>

aiFileReadProc custom_read(C_STRUCT aiFile* file, char* ptr,
			   size_t size_of_elements,
			   size_t number_of_elements)
{
	assert(ptr != NULL);
	size_t count = size_of_elements * number_of_elements;
	return (aiFileReadProc) AAsset_read((AAsset *) file->UserData,
					    ptr, count);
}

aiFileWriteProc custom_write(C_STRUCT aiFile* file, const char* ptr,
			     size_t size_of_elements, size_t number_of_elements)
{
	AGR_ERROR("called unsupported function custom_write()");
	return 0;
}

aiFileTellProc custom_tell(C_STRUCT aiFile* file)
{
	AGR_ERROR("called unsupported function custom_tell()");
	return 0;
}

aiFileTellProc custom_size(C_STRUCT aiFile* file)
{
	return (aiFileTellProc) AAsset_getLength((AAsset *) file->UserData);
}

aiFileSeek custom_seek(C_STRUCT aiFile* file,
		       size_t offset, C_ENUM aiOrigin whence)
{
	return (aiFileSeek) AAsset_seek((AAsset *) file->UserData,
					offset, whence);
}


aiFileFlushProc custom_flush(C_STRUCT aiFile* file)
{
	AGR_ERROR("called unsupported function custom_flush()");
	return NULL;
}

aiFileOpenProc custom_open(C_STRUCT aiFileIO* fileIO,
			   const char* filename, const char* mode)
{
	assert(filename != NULL);
	assert(mode != NULL);

	struct aiFile* file = malloc(sizeof(struct aiFile));
	file->ReadProc = (aiFileReadProc) custom_read;
	file->WriteProc = (aiFileWriteProc) custom_write;
	file->TellProc = (aiFileTellProc) custom_tell;
	file->FileSizeProc = (aiFileTellProc) custom_size;
	file->SeekProc = (aiFileSeek) custom_seek;
	file->FlushProc = (aiFileFlushProc) custom_flush;
	file->UserData = (void *) AAssetManager_open(asset_manager,
		filename, AASSET_MODE_UNKNOWN);

	if (file->UserData) {
		return (aiFileOpenProc) file;
	}

	AGR_ERROR("asset %s not found", filename);

	return NULL;
}

aiFileCloseProc custom_close(C_STRUCT aiFileIO* fileIO, C_STRUCT aiFile* file)
{
	AAsset_close((AAsset*) file->UserData);
	free(file);

	return NULL;
}

void assimpio_init(void)
{
	custom_fileio.OpenProc = (aiFileOpenProc) custom_open;
	custom_fileio.CloseProc = (aiFileCloseProc) custom_close;
}
