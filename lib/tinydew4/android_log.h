#ifndef __ANDROID_LOG_H__
#define __ANDROID_LOG_H__

#include <android/log.h>

#define _LOGV(LOG_TAG, ...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))
#define _LOGD(LOG_TAG, ...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define _LOGI(LOG_TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define _LOGW(LOG_TAG, ...) ((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define _LOGE(LOG_TAG, ...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

#endif /* __ANDIROID_LOG_H__ */
