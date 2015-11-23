LOCAL_PATH:= $(call my-dir)

# dmtd
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= flashlight_jni.c
LOCAL_MODULE := flashlight_jni
LOCAL_MODULE_TAGS := platform
LOCAL_SHARED_LIBRARIES := libc libm libutils libcutils
include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= flashlight_jni.c
LOCAL_MODULE := libflashlight_jni
LOCAL_MODULE_TAGS := platform
LOCAL_SHARED_LIBRARIES := libc libm libutils libcutils
include $(BUILD_SHARED_LIBRARY)
