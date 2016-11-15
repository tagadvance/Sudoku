package com.tagadvance.geometry;

public class Dimension implements ImmutableDimension {

	private final int width, height;

	public Dimension(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

}
