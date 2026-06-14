package com.antrax.gy.renders;

import com.badlogic.gdx.graphics.g3d.Attribute;
public class SelectionAttribute extends Attribute {
    public static final String Alias = "SelectionOutline";
    public static final long Type = register(Alias);

    public boolean isSelected;

    public SelectionAttribute(boolean isSelected) {
        super(Type);
        this.isSelected = isSelected;
    }

    @Override
    public Attribute copy() {
        return new SelectionAttribute(isSelected);
    }

    @Override
    protected boolean equals(Attribute other) {
        return ((SelectionAttribute)other).isSelected == isSelected;
    }

	@Override
	public int compareTo(Attribute o) {
		// TODO Auto-generated method stub
		return 0;
	}
}