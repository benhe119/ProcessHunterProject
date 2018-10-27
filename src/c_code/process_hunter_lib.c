/*
 * The MIT License
 *
 * Copyright 2018 Fadi Nassereddine.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
 
#include "process_hunter_lib.h"

#include <Windows.h>
#include <TlHelp32.h>

#include <stdlib.h>
#include <string.h>

#ifndef PHL_ERROR
#define PHL_ERROR (jlong)-1
#else
#warning PHL_ERROR define is changed
#endif

#ifndef PHL_NO_MORE
#define PHL_NO_MORE (jlong)-2
#else
#warning PHL_NO_MORE define is changed
#endif

struct proc_info {
	char *proc_name;
	long pid;
};

static HANDLE snapshot;
static BOOL new_snapshot = FALSE;

static void clear_info(struct proc_info *ptr)
{
	if (ptr) {
		if (ptr->proc_name)
			free(ptr->proc_name);
		free(ptr);
	}
}

JNIEXPORT jboolean JNICALL Java_processhunter_nativecontrol_NativeControlImpl_createProcessSnapshot(JNIEnv *env, jobject obj)
{
	snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, NULL);

	if (snapshot == INVALID_HANDLE_VALUE) {
		new_snapshot = FALSE;
		return FALSE;
	} else {
		new_snapshot = TRUE;
		return TRUE;
	}

}

JNIEXPORT void JNICALL Java_processhunter_nativecontrol_NativeControlImpl_destroyProcessSnapshot(JNIEnv *env, jobject obj)
{
	if (!snapshot)
		return;

	CloseHandle(snapshot);
}

JNIEXPORT jlong JNICALL Java_processhunter_nativecontrol_NativeControlImpl_getNextProcessAddress(JNIEnv *env, jobject obj)
{
	if (!snapshot)
		return PHL_ERROR;

	PROCESSENTRY32 proc;
	proc.dwSize = sizeof(proc);

	if (!snapshot) 
		return PHL_ERROR;

	if (new_snapshot) {
		if (!Process32First(snapshot, &proc)) 
			return PHL_NO_MORE;
		
		new_snapshot = FALSE;
	} else {
		if (!Process32Next(snapshot, &proc)) 
			return PHL_NO_MORE;
		
	}

	struct proc_info *info = malloc(sizeof(struct proc_info));
	info->proc_name = strdup(proc.szExeFile);
	if (!info->proc_name) {
		clear_info(info);
		return PHL_ERROR;
	}

	info->pid = proc.th32ProcessID;

	return (jlong)info;
}

JNIEXPORT jstring JNICALL Java_processhunter_nativecontrol_NativeControlImpl_getProcessName(JNIEnv *env, jobject obj, jlong proc_addr)
{
	struct proc_info *info = (struct proc_info *)proc_addr;
	if (!info)
		return PHL_ERROR;

	return (*env)->NewStringUTF(env, info->proc_name);
}

JNIEXPORT jlong JNICALL Java_processhunter_nativecontrol_NativeControlImpl_getPID(JNIEnv *env, jobject obj, jlong proc_addr)
{
	struct proc_info *info = (struct proc_info *)proc_addr;
	if (!info)
		return PHL_ERROR;
	return info->pid;
}

JNIEXPORT void JNICALL Java_processhunter_nativecontrol_NativeControlImpl_freeProcessNativeInfo(JNIEnv *env, jobject obj, jlong proc_addr)
{
	struct proc_info *info = (struct proc_info *)proc_addr;
	if (info)
		clear_info(info);
}

JNIEXPORT jboolean JNICALL Java_processhunter_nativecontrol_NativeControlImpl_isProcessRunning(JNIEnv *env, jobject obj, jlong pid)
{
	BOOL ret = FALSE;

	HANDLE snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, NULL);
	PROCESSENTRY32 proc;

	if (snapshot == INVALID_HANDLE_VALUE)
		return ret;

	if (!Process32First(snapshot, &proc))
		return ret;

	do {
		if (proc.th32ProcessID == pid) {
			ret = TRUE;
			break;
		}
	} while (Process32Next(snapshot, &proc));

	CloseHandle(snapshot);
	return ret;
}

JNIEXPORT jboolean JNICALL Java_processhunter_nativecontrol_NativeControlImpl_killProcessByPid(JNIEnv *env, jobject obj, jlong pid)
{
	BOOL ret = FALSE;
	HANDLE handle = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);
	if (handle == INVALID_HANDLE_VALUE)
		return ret;
	
	ret = TerminateProcess(handle, 0);
	CloseHandle(handle);

	return ret;
}
