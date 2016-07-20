#include <jni.h>

jstring Java_kr_co_tinydew4_errno_1list_ErrnoListActivity_NDKErrnoToString(JNIEnv *env, jclass self, jint AErrno)
{
	return (*env)->NewStringUTF(env, (char*)strerror(AErrno));
}


