#ifndef __LOG_H__
#define __LOG_H__

#include <string.h>

#include <android/log.h>

#define __FILENAME__ (strrchr(__FILE__, '/') ? strrchr(__FILE__, '/') + 1 : __FILE__)

#define AGR_ERROR(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_ERROR,			\
			__FILENAME__,					\
			"%s(%s:%d) " fmt,				\
			__func__, __FILENAME__, __LINE__,		\
			##__VA_ARGS__);					\
	} while (0)

#define AGR_DEBUG(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_DEBUG,			\
			__FILENAME__,					\
			fmt, ##__VA_ARGS__);				\
	} while (0)

#define AGR_WARN(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_WARN,			\
			__FILENAME__,					\
			fmt, ##__VA_ARGS__);				\
	} while (0)

#define AGR_INFO(fmt, ...) 						\
	do {								\
		__android_log_print(ANDROID_LOG_INFO,			\
			__FILENAME__,					\
			fmt, ##__VA_ARGS__);				\
	} while (0)

#endif
