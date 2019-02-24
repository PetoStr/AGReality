#include "jni_helper.h"

#include <string.h>

#include "pv_matrices.h"
#include "util.h"

#define BASE_PACKAGE "com/petostr/p/"

#define CAMERA_CLASS BASE_PACKAGE "engine/Camera"
#define RESOURCE_CLASS BASE_PACKAGE "engine/entities/Resource"
#define MENTITY_CLASS BASE_PACKAGE "engine/entities/ModeledEntity"
#define IENTITY_CLASS BASE_PACKAGE "engine/entities/ImageEntity"

jobject jget_resource(JNIEnv *env, jobject entity)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);
	jmethodID gres =(*env)->GetMethodID(env, eclass, "getResource",
					    "()L" RESOURCE_CLASS ";");

	return (*env)->CallObjectMethod(env, entity, gres);
}

int jget_resource_type(JNIEnv *env, jobject resource)
{
	jclass rclass = (*env)->GetObjectClass(env, resource);
	jmethodID get_type =
		(*env)->GetMethodID(env, rclass, "getType", "()I");

	return (*env)->CallIntMethod(env, resource, get_type);;
}

jint jget_resource_id(JNIEnv *env, jobject resource)
{
	jclass rclass = (*env)->GetObjectClass(env, resource);
	jmethodID get_id = (*env)->GetMethodID(env, rclass, "getId", "()I");

	return (*env)->CallIntMethod(env, resource, get_id);
}

void jset_resource_id(JNIEnv *env, jobject resource, int id)
{
	jclass rclass = (*env)->GetObjectClass(env, resource);
	jmethodID sid = (*env)->GetMethodID(env, rclass, "setId", "(I)V");
	(*env)->CallVoidMethod(env, resource, sid, id);
}

jobject jscene_get_camera(JNIEnv *env, jobject scene)
{
	jclass sclass = (*env)->GetObjectClass(env, scene);

	jmethodID get_camera = (*env)->GetMethodID(env, sclass,
						   "getCamera",
						   "()L" CAMERA_CLASS ";");

	return (*env)->CallObjectMethod(env, scene, get_camera);
}

void jscene_get_view_matrix(JNIEnv *env, jobject scene, float *vm)
{
	jobject jcamera = jscene_get_camera(env, scene);
	jclass cclass = (*env)->GetObjectClass(env, jcamera);

	jmethodID get_vm = (*env)->GetMethodID(env, cclass,
					       "getViewMatrix", "()[F");

	jfloatArray j_vmArray = NULL;
	j_vmArray = (*env)->CallObjectMethod(env, jcamera, get_vm);

	jfloat *j_vm = (*env)->GetFloatArrayElements(env, j_vmArray, NULL);

	memcpy(vm, j_vm, 16 * sizeof(float));

	(*env)->ReleaseFloatArrayElements(env, j_vmArray, j_vm, 0);
}

void jscene_get_camera_pos(JNIEnv *env, jobject scene, float *pos)
{
	jobject jcamera = jscene_get_camera(env, scene);
	jclass cclass = (*env)->GetObjectClass(env, jcamera);

	jmethodID get_pos = (*env)->GetMethodID(env, cclass,
					        "getPositionArray", "()[F");

	jfloatArray j_posArray = NULL;
	j_posArray = (*env)->CallObjectMethod(env, jcamera, get_pos);

	jfloat *j_pos = (*env)->GetFloatArrayElements(env, j_posArray, NULL);

	memcpy(pos, j_pos, 3 * sizeof(float));

	(*env)->ReleaseFloatArrayElements(env, j_posArray, j_pos, 0);
}

jobjectArray jscene_get_mentities(JNIEnv *env, jobject scene, jsize *len)
{
	jclass sclass = (*env)->GetObjectClass(env, scene);

	jmethodID get_entities =
		(*env)->GetMethodID(env, sclass,
				    "getModeledEntitiesArray",
				    "()[L" MENTITY_CLASS ";");

	jobjectArray jmentities =
		(*env)->CallObjectMethod(env, scene, get_entities);

	if (len) {
		*len = (*env)->GetArrayLength(env, jmentities);
	}

	return jmentities;
}

jobjectArray jscene_get_ientities(JNIEnv *env, jobject scene, jsize *len)
{
	jclass sclass = (*env)->GetObjectClass(env, scene);

	jmethodID get_entities =
		(*env)->GetMethodID(env, sclass,
				    "getImageEntitiesArray",
				    "()[L" IENTITY_CLASS ";");

	jobjectArray jientities =
		(*env)->CallObjectMethod(env, scene, get_entities);

	if (len) {
		*len = (*env)->GetArrayLength(env, jientities);
	}

	return jientities;
}

void jentity_model_matrix(JNIEnv *env, jobject entity, float *mm)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);

	jmethodID get_mm = (*env)->GetMethodID(env, eclass,
					       "getModelMatrix", "()[F");

	jfloatArray jmmArray = NULL;
	jmmArray = (*env)->CallObjectMethod(env, entity, get_mm);

	jfloat *jmm = (*env)->GetFloatArrayElements(env, jmmArray, NULL);

	memcpy(mm, jmm, 16 * sizeof(float));

	(*env)->ReleaseFloatArrayElements(env, jmmArray, jmm, 0);
}

int jentity_is_visible(JNIEnv *env, jobject entity)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);
	jmethodID isv = (*env)->GetMethodID(env, eclass, "isVisible", "()Z");

	return (*env)->CallBooleanMethod(env, entity, isv);
}

int jentity_is_selected(JNIEnv *env, jobject entity)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);
	jmethodID iss = (*env)->GetMethodID(env, eclass, "isSelected", "()Z");

	return (*env)->CallBooleanMethod(env, entity, iss);
}

void jentity_set_min(JNIEnv *env, jobject entity, float x, float y, float z)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);
	jmethodID sm = (*env)->GetMethodID(env, eclass, "setMin", "(FFF)V");

	(*env)->CallVoidMethod(env, entity, sm, x, y, z);
}

void jentity_set_max(JNIEnv *env, jobject entity, float x, float y, float z)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);
	jmethodID sm = (*env)->GetMethodID(env, eclass, "setMax", "(FFF)V");

	(*env)->CallVoidMethod(env, entity, sm, x, y, z);
}

void jentity_set_center(JNIEnv *env, jobject entity,
			float x, float y, float z)
{
	jclass eclass = (*env)->GetObjectClass(env, entity);
	jmethodID sc =
		(*env)->GetMethodID(env, eclass, "setCenter", "(FFF)V");

	(*env)->CallVoidMethod(env, entity, sc, x, y, z);
}

float jientity_get_opacity(JNIEnv *env, jobject ientity)
{
	jclass ieclass = (*env)->GetObjectClass(env, ientity);
	jmethodID gopct =
		(*env)->GetMethodID(env, ieclass, "getOpacity", "()F");

	return (*env)->CallFloatMethod(env, ientity, gopct);
}

void jientity_set_size(JNIEnv *env, jobject ientity,
		       float width, float height)
{
	jclass ieclass = (*env)->GetObjectClass(env, ientity);

	jmethodID swidth =
		(*env)->GetMethodID(env, ieclass, "setWidth", "(F)V");
	jmethodID sheight =
		(*env)->GetMethodID(env, ieclass, "setHeight", "(F)V");

	(*env)->CallVoidMethod(env, ientity, swidth, width);
	(*env)->CallVoidMethod(env, ientity, sheight, height);
}

JNIEXPORT jfloatArray JNICALL
Java_com_petostr_p_engine_Screen_get_1pmatrix(JNIEnv *env, jclass type)
{
	UNUSED(type);
	jfloatArray pm = (*env)->NewFloatArray(env, 16);

	(*env)->SetFloatArrayRegion(env, pm, 0, 16,
				    (const jfloat *) projection_matrix);

	return pm;
}
