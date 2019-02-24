package com.petostr.p.agreality;

import android.util.SparseArray;

import com.petostr.p.engine.Scene;
import com.petostr.p.engine.entities.ImageEntity;
import com.petostr.p.engine.entities.ModeledEntity;

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
