package com.example.p.engine;

import org.joml.Vector3f;

public interface Movable {

	void move(Vector3f d);

	void rotate(Vector3f d);

}
