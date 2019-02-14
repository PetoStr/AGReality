#ifndef __UTIL_H__
#define __UTIL_H__

#include "agrlog.h"

#ifdef NDEBUG

#define check_gl_error(op)

#else

#define check_gl_error(op) 					\
	do {							\
		int error;					\
		while ((error = glGetError()) != GL_NO_ERROR) {	\
			AGR_ERROR("%s: %d", op, error);		\
		}						\
	} while (0)

#endif


#define ARRAY_LENGTH(arr) (sizeof(arr) / sizeof(arr[0]))

#define ABS(x) ((x) < 0 ? -(x) : (x))

static inline void fix_path_slashes(char *path) {
	char *c = path;
	while (*c) {
		if (*c == '\\') {
			*c = '/';
		}
		c++;
	}
}

#endif
