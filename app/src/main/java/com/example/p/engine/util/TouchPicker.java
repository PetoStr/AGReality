package com.example.p.engine.util;

import com.example.p.engine.entities.NullImageEntity;
import com.example.p.engine.entities.NullModelEntity;
import com.example.p.engine.Camera;
import com.example.p.engine.Screen;
import com.example.p.engine.entities.ImageEntity;
import com.example.p.engine.entities.ModeledEntity;

import org.joml.AABBf;
import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Rayf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class TouchPicker {

	/* https://math.stackexchange.com/a/190373 */
	public static ImageEntity handleScreenTouchedEntity(List<ImageEntity> entities,
														float tx, float ty) {
		float minZ = Float.POSITIVE_INFINITY;

		ImageEntity touchedEntity = new NullImageEntity();

		for (ImageEntity e : entities) {
			if (!e.isVisible()) {
				continue;
			}

			float w = e.getScale().x;
			float h = e.getScale().y;
			float a = (float) Math.toRadians(e.getOrientation().z);

			Vector3f D = e.getPosition();
			float ax = (float) (D.x + w * Math.sin(a));
			float ay = (float) (D.y + h * Math.cos(a));
			Vector3f A = new Vector3f(ax, ay, D.z);
			float bx = (float) (A.x + w * Math.cos(a));
			float by = (float) (A.y - h * Math.sin(a));
			Vector3f B = new Vector3f(bx, by, D.z);
			Vector3f M = new Vector3f(tx, ty, 0.0f);

			Vector3f AM = new Vector3f(M.x - A.x, M.y - A.y, M.z - A.z);
			Vector3f AB = new Vector3f(B.x - A.x, B.y - A.y, B.z - A.z);
			Vector3f AD = new Vector3f(D.x - A.x, D.y - A.y, D.z - A.z);

			float AMABdot = AM.dot(AB);
			float ABABdot = AB.dot(AB);
			float AMADdot = AM.dot(AD);
			float ADADdot = AD.dot(AD);

			float zCoord = e.getPosition().z;
			if (0.0f < AMABdot
					&& AMABdot < ABABdot
					&& 0.0f < AMADdot
					&& AMADdot < ADADdot
					&& zCoord < minZ) {
				minZ = zCoord;
				touchedEntity = e;
			}
		}

		touchedEntity.onTouch();

		return touchedEntity;
	}

	/* http://antongerdelan.net/opengl/raycasting.html */
	public static ModeledEntity handleWorldTouchedEntity(List<ModeledEntity> entities,
														 Camera camera, float tx, float ty) {

		ModeledEntity touchedEntity = new NullModelEntity();

		Vector3f camPos = camera.getPosition();

		float[] pmatrix = Screen.get_pmatrix();
		float[] vmatrix = camera.getViewMatrix();

		Matrix4f invPm = new Matrix4f().set(pmatrix).invert();
		Matrix4f invVm = new Matrix4f().set(vmatrix).invert();

		float w = Screen.getWidth();
		float h = Screen.getHeight();

		float x = (2.0f * tx) / w - 1.0f;
		float y = (2.0f * ty) / h - 1.0f;
		float z = 1.0f;

		Vector3f rayNds = new Vector3f(x, y, z);
		Vector4f rayClip = new Vector4f(rayNds.x, rayNds.y, -1.0f, 1.0f);

		Vector4f rayEye = new Vector4f(rayClip.mul(invPm));
		rayEye.z = -1.0f;
		rayEye.w = 0.0f;

		rayEye.mul(invVm);
		Vector3f rayWor = new Vector3f(rayEye.x, rayEye.y, rayEye.z);
		rayWor.normalize();

		float closestDist = Float.POSITIVE_INFINITY;

		Vector2f nearFar = new Vector2f();
		for (ModeledEntity me : entities) {
			if (!me.isVisible()) {
				continue;
			}

			AABBf aabbf = me.getAabbf();
			Rayf ray = new Rayf(camPos, rayWor);
			if (Intersectionf.intersectRayAab(ray, aabbf, nearFar)
					&& nearFar.x < closestDist) {
				closestDist = nearFar.x;
				touchedEntity = me;
			}
		}

		return touchedEntity;
	}

}
