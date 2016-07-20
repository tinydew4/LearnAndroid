LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ndk-exam
LOCAL_SRC_FILES := first.c second.c

include $(BUILD_SHARED_LIBRARY)

