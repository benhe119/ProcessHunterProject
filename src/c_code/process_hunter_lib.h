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
 
#ifndef _PROCESS_HUNTER_LIB
#define _PROCESS_HUNTER_LIB

#include <jni.h>

JNIEXPORT jboolean JNICALL Java_processhunter_nativecontrol_NativeControlImpl_createProcessSnapshot(JNIEnv *, jobject);
JNIEXPORT void JNICALL Java_processhunter_nativecontrol_NativeControlImpl_destroyProcessSnapshot(JNIEnv *, jobject);
JNIEXPORT jlong JNICALL Java_processhunter_nativecontrol_NativeControlImpl_getNextProcessAddress(JNIEnv *, jobject);
JNIEXPORT jstring JNICALL Java_processhunter_nativecontrol_NativeControlImpl_getProcessName(JNIEnv *env, jobject obj, jlong proc_addr);
JNIEXPORT jlong JNICALL Java_processhunter_nativecontrol_NativeControlImpl_getPID(JNIEnv *, jobject, jlong);
JNIEXPORT void JNICALL Java_processhunter_nativecontrol_NativeControlImpl_freeProcessNativeInfo(JNIEnv *, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_processhunter_nativecontrol_NativeControlImpl_isProcessRunning(JNIEnv *, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_processhunter_nativecontrol_NativeControlImpl_killProcessByPid(JNIEnv *, jobject, jlong);

#endif