LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_CERTIFICATE := platform
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_STATIC_JAVA_LIBRARIES := pisenizy gson simplexml android-support-v4

LOCAL_PACKAGE_NAME := OTTSettings

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := pisenizy:libs/android-izy-v1.2.20150302.jar \
	gson:libs/gson-2.2.4.jar \
	simplexml:libs/simple-xml-2.6.2.jar
#LOCAL_SDK_VERSION := current

include $(BUILD_MULTI_PREBUILT)

