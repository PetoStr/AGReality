#ifndef __AGREALITY_JNI_HELPER_H__
#define __AGREALITY_JNI_HELPER_H__

#include <jni.h>

enum resource_type {
	RESOURCE_TYPE_MODEL,
	RESOURCE_TYPE_TEXTURE
};

jobject jget_resource(JNIEnv *env, jobject entity);
int jget_resource_type(JNIEnv *env, jobject resource);
jint jget_resource_id(JNIEnv *env, jobject resource);
void jset_resource_id(JNIEnv *env, jobject resource, int id);

jobject jscene_get_camera(JNIEnv *env, jobject scene);
void jscene_get_view_matrix(JNIEnv *env, jobject scene, float *vm);
void jscene_get_camera_pos(JNIEnv *env, jobject scene, float *pos);
jobjectArray jscene_get_mentities(JNIEnv *env, jobject scene, jsize *len);
jobjectArray jscene_get_ientities(JNIEnv *env, jobject scene, jsize *len);

void jentity_model_matrix(JNIEnv *env, jobject entity, float *mm);
int jentity_is_visible(JNIEnv *env, jobject entity);
int jentity_is_selected(JNIEnv *env, jobject entity);

void jentity_set_min(JNIEnv *env, jobject entity, float x, float y, float z);
void jentity_set_max(JNIEnv *env, jobject entity, float x, float y, float z);
void jentity_set_center(JNIEnv *env, jobject entity, float x, float y, float z);

float jientity_get_opacity(JNIEnv *env, jobject entity);
void jientity_set_size(JNIEnv *env, jobject ientity, float width, float height);

#endif
