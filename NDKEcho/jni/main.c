#include "common.h"
#pragma hdrstop

#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>
#include <pthread.h>

//#include <sys/types.h>
//#include <arpa/inet.h>
//#include <unistd.h>

#define PATH_SEPARATOR ';'
#define USER_CLASSPATH "."

const char *sClsChat = "kr/co/softbridge/NDKEcho/NDKEchoShell";
pthread_t tids[1];

static int tcp_sockfd = -1;
static int active = 0;

static int CallVoidEvent(JNIEnv *env, const char *className, const char *methodName)
{
	if (env == NULL)
		return -1;

	jclass cls = (*env)->FindClass(env, className);
	if (cls != NULL) {
		const char *sMethodType = "()V";
		jmethodID mid = (*env)->GetMethodID(env, cls, methodName, sMethodType);
		if (mid != NULL) {
			LOGV("CallVoidMethod: %s", methodName);
			(*env)->CallVoidMethod(env, cls, mid);
		} else {
			LOGE("Can not find method ID: %s %s", sMethodType, methodName);
		}
		(*env)->DeleteLocalRef(env, cls);
	} else {
		LOGE("Can not find class %s", className);
	}

	return 0;
}

static int CallAddChat(JNIEnv *env, const char *fmt, ...)
{
	if (env == NULL)
		return -1;

	const char *sMethodAddChat = "addMessage";
	jclass cls = (*env)->FindClass(env, sClsChat);
	if (cls != NULL) {
		const char *sMethodType = "(Ljava/lang/String;)V";
		jmethodID mid = (*env)->GetMethodID(env, cls, sMethodAddChat, sMethodType);
		if (mid != NULL) {

			char szBuf[255];
			bzero(szBuf, sizeof(szBuf));
			va_list list;
			va_start(list, fmt);
			vsnprintf(szBuf, sizeof(szBuf) - 1, fmt, list);
			LOGV("CallVoidMethod: %s", sMethodAddChat);
			(*env)->CallVoidMethod(env, cls, mid, (*env)->NewStringUTF(env, szBuf));

			va_end(list);
		} else {
			LOGE("Can not find method ID: %s %s", sMethodType, sMethodAddChat);
		}
		(*env)->DeleteLocalRef(env, cls);
	} else {
		LOGE("Can not find class %s", sClsChat);
	}

	return 0;
}

void* thread_socket(void *i)
{
	int index = ((int*)i)[0];
	int sockfd = ((int*)i)[1];
	JNIEnv *env = (JNIEnv*)((int*)i)[2];

	#define MAX_BUF 512
	char buf[MAX_BUF];
	fd_set rset;
	int nRead;

	for (;;) {
/*
		JNIEnv *env = NULL;

		if (g_VM != NULL) {
			if ((*g_VM)->GetEnv(g_VM, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
				LOGE("ERROR: GetEnv failed");
				continue;
			}
		}
*/
		FD_SET(sockfd, &rset);
		select(sockfd + 1, &rset, NULL, NULL, NULL);
		if (FD_ISSET(sockfd, &rset))
		{
			bzero(buf, MAX_BUF);

			if ((nRead = read(sockfd, buf, MAX_BUF - 1)) > 0)
			{
				// Read
				LOGV("Read: %s", buf);
				CallAddChat(env, buf);
			}
			else
			{
				if (nRead != -1)
				{
					// maybe disconnected
					LOGV("maybe disconnected");
				}
				else
				{
					// error
				}
			}
		}
	}
}

jint Java_kr_co_softbridge_NDKEcho_NDKSocket_TCPConnect(JNIEnv *env, jclass self, jstring host, jint port)
{
	LOGV("TCPConnect Begin");

	jint result = -1;
	int iResult;
	int iErrNo = -1;

/*
	jclass cls = (*env)->FindClass(env, sClsChat);
	if (cls != NULL) {
		const char *sMethodType = "()V";
		const char *sMethodName = "TCPOnConnect";
		jmethodID mid = (*env)->GetMethodID(env, cls, sMethodName, sMethodType);
		if (mid != NULL) {
			LOGV("CallVoidMethod: %s", sMethodName);
			(*env)->CallVoidMethod(env, cls, mid);
		} else {
			LOGE("Can not find method ID: %s %s", sMethodType, sMethodName);
		}
		(*env)->DeleteLocalRef(env, cls);
	} else {
		LOGE("Can not find class %s", sClsChat);
	}
/*
	vm->AttachCurrentThread(&env, NULL);
	env->CallStaticIntMethod(jNativeCls, jMultiMethod);
	vm->DetachCurrentThread();
*/

	if (tcp_sockfd == -1)
	{
		if ((tcp_sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) != -1)
		{
			struct sockaddr_in servAddr;
			bzero(&servAddr, sizeof(servAddr));
			servAddr.sin_family = AF_INET;
			servAddr.sin_port = htons(port);

			const char *lpszHost = (*env)->GetStringUTFChars(env, host, 0);
			result = inet_pton(servAddr.sin_family, lpszHost, &servAddr.sin_addr.s_addr);
			if (result == 1) {
				CallAddChat(env, "Trying connect to %s:%d", lpszHost, port);
				LOGV("Trying connect to %s:%d", lpszHost, port);

				servAddr.sin_addr.s_addr = inet_addr(lpszHost);
				iResult = connect(tcp_sockfd, (struct sockaddr*)&servAddr, sizeof(servAddr));
				if (iResult > 0) {
					CallVoidEvent(env, sClsChat, "TCPOnConnect");
					result = 0;
					active = 1;
				} else if (iResult == 0) {
					result = -4; // Invalid address string
					iErrNo = errno;
				} else {
					result = -5;
					iErrNo = errno;
				}
			} else if (result == 0) {
				result = -3; // source does not contain a character string
				iErrNo = errno;
			} else {
				result = -2; // result == -1 then errno = EAFNOSUPPORT
				iErrNo = errno;
			}
			(*env)->ReleaseStringUTFChars(env, host, lpszHost);
		} else {
			result = -1;
			iErrNo = errno;
		}
	} else {
		result = 1;
	}

	LOGV("TCPConnect: tcp_sockfd = %d", tcp_sockfd);
	LOGV("TCPConnect: result = %d", result);
	if (iErrNo >= 0)
		LOGV("errno: %d", iErrNo);

	if (result == 0) {
		int vals[] = {
			0,
			tcp_sockfd,
			(int)env,
		};
		pthread_create(tids, NULL, thread_socket, (void*)vals);
	}

	LOGV("TCPConnect End");

	if ((*env)->ExceptionOccurred(env)) {
		(*env)->ExceptionDescribe(env);
		(*env)->ExceptionClear(env);
	}

	return result;
}

jint Java_kr_co_softbridge_NDKEcho_NDKSocket_TCPDisconnect(JNIEnv *env, jclass self)
{
	LOGV("TCPDisconnect Begin");

	jint result = 0;
	if (tcp_sockfd >= 0) {
		if (close(tcp_sockfd) == -1)
			result = -1;

		int i = sizeof(tids) / sizeof(tids[0]);
		for (; i-- > 0; )
			if (tids[i] != (pthread_t)NULL) {
				pthread_exit((void*)tids[i]);
				tids[i] = (pthread_t)NULL;
			}
		CallVoidEvent(env, sClsChat, "TCPOnDisconnect");
	}
	active = 0;

	LOGV("TCPDisconnect End");

	return result;
}

int Send(int fd, const char *vptr, int len, int flags)
{
	size_t nwritten;
	size_t nleft = len;
	const char *ptr = (char*)vptr;
	LOGV("writen(%d, %s, %d)", fd, vptr, len);
	while (nleft > 0)
	{
		nwritten = send(fd, ptr, nleft, flags);
		LOGV("write: %d bytes", nwritten);

		if (nwritten != (ssize_t)-1)
		{
			nleft -= nwritten;
			ptr += nwritten;
			LOGV("written = %d, nleft = %d", nwritten, nleft);
		}
		else
		{
			LOGV("errno = %d", errno);
			if (errno == EINTR)
				nwritten = 0;
			else
			{
				LOGE("%s(%d): error on write in writen", __FILE__, __LINE__);
				return -1;
			}
		}
	}
	return len;
}

jint Java_kr_co_softbridge_NDKEcho_NDKSocket_TCPSendString(JNIEnv *env, jclass self, jstring message)
{
	LOGV("TCPSendString Begin");
	const char *lpszMessage = (*env)->GetStringUTFChars(env, message, 0);
	int nBytes = sizeof(lpszMessage);
	if (Send(tcp_sockfd, lpszMessage, nBytes, 0) != nBytes)
		LOGE("메세지 전송이 불완전합니다.");
	(*env)->ReleaseStringUTFChars(env, message, lpszMessage);
	LOGV("TCPSendString End");
}

