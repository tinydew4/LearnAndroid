LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ndk-socket

LOCAL_SRC_FILES := \
	main.c \
	common.cpp \
	onload.cpp

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
