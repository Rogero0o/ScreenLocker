package com.roger.screenlocker.utils.faceutil;

public class FaceCompareResult {
	private float similar;

	public float getSimilar() {
		return similar;
	}

	public void setSimilar(float similar) {
		this.similar = similar;
	}

	@Override
	public String toString() {
		return "FaceDetectResult [similar=" + similar + "]";
	}
	

}
