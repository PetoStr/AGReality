package com.example.p.agreality;

import android.util.SparseArray;

import com.example.p.engine.Scene;
import com.example.p.engine.entities.ImageEntity;
import com.example.p.engine.entities.ModeledEntity;

public class CustomScene extends Scene {

	private SparseArray<ImageEntity> selectedImageEntities = new SparseArray<>();
	private ModeledEntity selectedModeledEntity;

	public SparseArray<ImageEntity> getSelectedImageEntities() {
		return selectedImageEntities;
	}

	public ModeledEntity getSelectedModeledEntity() {
		return selectedModeledEntity;
	}

	public void setSelectedModeledEntity(ModeledEntity selectedModeledEntity) {
		this.selectedModeledEntity = selectedModeledEntity;
	}

}
