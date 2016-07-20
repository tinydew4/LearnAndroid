#ifndef __ndkecho_log_h__
#define __ndkecho_log_h__

#include "/home/arus/workspace/lib/tinydew4/android_log.h"

#define LOG_TAG "ndk-echo"

#define LOGV(...) _LOGV(LOG_TAG, __VA_ARGS__)
#define LOGD(...) _LOGD(LOG_TAG, __VA_ARGS__)
#define LOGI(...) _LOGI(LOG_TAG, __VA_ARGS__)
#define LOGW(...) _LOGW(LOG_TAG, __VA_ARGS__)
#define LOGE(...) _LOGE(LOG_TAG, __VA_ARGS__)

#endif /* __ndkecho_log_h__ */

