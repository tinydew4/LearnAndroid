#include "common.h"
#pragma hdrstop

#include "common.h"

#include <stdio.h>

#include <assert.h>
#include "ndkecho_log.h"

#include "kr_co_softbridge_NDKEcho_NDKSocket.h"

static const JNINativeMethod gMethods[] = {
//	{"add", "(II)I", (void*)Hello_add}
};

/*
 * Register native JNI-callable methods.
 *
 * "clasName" looks like "java/lang/String".
 */
static int jniRegisterNativeMethods(JNIEnv *env, const char* className,
	const JNINativeMethod *gMethods, int numMethods)
{
	jclass clazz;

	LOGV("Registering %s natives", className);
	clazz = env->FindClass(className);

	if (clazz == NULL) {
		LOGE("Native registration unable to find class '%s'", className);
		return JNI_ERR;
	}
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		LOGE("RegisterNatives failed for '%s'", className);
		return JNI_ERR;
	}
	return JNI_OK;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
	LOGV("JNI_OnLoad Begin");

	g_VM = vm;

	JNIEnv *env = NULL;
	jint result = JNI_ERR;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed");
		goto finish;
	}

	assert(env != NULL);

	if (jniRegisterNativeMethods(env, "kr/co/softbridge/NDKEcho/NDKSocket",
		gMethods, sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK) {
		LOGE("ERROR: native registration failed");
		goto finish;
	}

	result = JNI_VERSION_1_4;

finish:
	LOGV("JNI_OnLoad End");
	return result;
}

