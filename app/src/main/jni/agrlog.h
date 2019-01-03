#ifndef __LOG_H__
#define __LOG_H__

#include <string.h>

#include <android/log.h>

#ifdef NDEBUG

#define AGR_ERROR(fmt, ...)
#define AGR_DEBUG(fmt, ...)
#define AGR_WARN(fmt, ...)
#define AGR_INFO(fmt, ...)

#else

#define FNAME (strrchr(__FILE__, '/') ? strrchr(__FILE__, '/') + 1 : __FILE__)

#define AGR_ERROR(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_ERROR,			\
			FNAME,						\
			"%s(%s:%d) " fmt,				\
			__func__, FNAME, __LINE__,		\
			##__VA_ARGS__);					\
	} while (0)

#define AGR_DEBUG(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_DEBUG,			\
			FNAME,						\
			fmt, ##__VA_ARGS__);				\
	} while (0)

#define AGR_WARN(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_WARN,			\
			FNAME,						\
			fmt, ##__VA_ARGS__);				\
	} while (0)

#define AGR_INFO(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_INFO,			\
			FNAME,						\
			fmt, ##__VA_ARGS__);				\
	} while (0)

#endif

#endif
