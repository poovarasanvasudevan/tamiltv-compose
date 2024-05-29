#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_poovarasan_tamiltv_ConfigHelper_apiUrl(JNIEnv *env, jclass clazz) {
    std::string apiURL = "https://tavapi.inditechman.com/api/tamiltv/tvand.json";
    return env->NewStringUTF(apiURL.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_poovarasan_tamiltv_ConfigHelper_getMUrl(JNIEnv *env, jclass clazz) {
    std::string apiUrl = "https://tavapi.inditechman.com/api/ttv";
    return env->NewStringUTF(apiUrl.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_poovarasan_tamiltv_ConfigHelper_getRUrl(JNIEnv *env, jclass clazz) {
    std::string apiUrl = "https://tavapi.inditechman.com/api/tamiltv/radio.json";
    return env->NewStringUTF(apiUrl.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_poovarasan_tamiltv_ConfigHelper_getUserAgent(JNIEnv *env, jclass clazz) {
    std::string userAgent = "PV9789";
    return env->NewStringUTF(userAgent.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_poovarasan_tamiltv_ConfigHelper_getSettings(JNIEnv *env, jclass clazz) {
    std::string settings = "https://tavapi.inditechman.com/api/tamiltv/tamiltvsetting40.json";
    return env->NewStringUTF(settings.c_str());
}