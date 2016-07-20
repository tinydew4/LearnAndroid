#include "first.h"
#include <jni.h>
#include <stdio.h>

jint Java_org_example_ndk_NDKExam_add
  (JNIEnv *env, jobject this, jint x, jint y)
{
	int result = -1;

	FILE *fp = fopen("/sdcard/a.txt", "w");
	if (fp != NULL) {
		fprintf(fp, "%d", first(x, y));
		fclose(fp);
	}

	fp = fopen("/sdcard/a.txt", "r");
	if (fp != NULL) {
		fseek(fp, 0, SEEK_SET);
		fscanf(fp, "%d", &result);
	}
	fclose(fp);

	return result;
}

