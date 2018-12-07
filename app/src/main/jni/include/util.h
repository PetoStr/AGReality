#ifndef __UTIL_H__
#define __UTIL_H__

#define check_gl_error(op) 					\
	do {							\
		int error;					\
		while ((error = glGetError()) != GL_NO_ERROR) {	\
			AGR_ERROR("%s: %d", op, error);		\
		}						\
	} while (0)

#define ARRAY_LENGTH(arr) (sizeof(arr) / sizeof(arr[0]))

#define ABS(x) ((x) < 0 ? -(x) : (x))

#endif

