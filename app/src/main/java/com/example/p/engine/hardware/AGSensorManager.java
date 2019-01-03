package com.example.p.engine.hardware;

import java.util.ArrayList;
import java.util.List;

public class AGSensorManager {

	List<AGSensor> sensors = new ArrayList<>();

	public void addAGSensor(AGSensor sensor) {
		sensors.add(sensor);
	}

	public void removeAGSensor(AGSensor sensor) {
		sensors.remove(sensor);
	}

	public void startSensors() {
		for (AGSensor sensor : sensors) {
			sensor.start();
		}
	}

	public void pauseSensors() {
		for (AGSensor sensor : sensors) {
			sensor.stop();
		}
	}

}
