#include <jni.h>
#include <android/log.h>

extern "C"
JNIEXPORT void JNICALL
Java_io_fourth_1finger_scrabble_dictionary_DictionaryUtility_00024Companion_nativeInit(
        JNIEnv *env,
        jobject thiz,
        jobject assetManager,
        jobject buffer,
        jint size
) {
    auto *bufferPtr = static_cast<char *>(
            env->GetDirectBufferAddress(buffer)
    );
    if (!bufferPtr) {
        __android_log_print(ANDROID_LOG_ERROR, "NativeCode", "GetDirectBufferAddress returned NULL");
        return;
    }

    char* charData = static_cast<char*>(bufferPtr);
    size++;

}
