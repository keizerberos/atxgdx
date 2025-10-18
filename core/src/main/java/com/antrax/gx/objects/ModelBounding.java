package com.antrax.gx.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public final class ModelBounding {

	public static ModelInstance crear(float size,Color color) {
		BoundingBox bb = new BoundingBox();
		Vector3 vector_de = new Vector3(-size,-size,-size);
		Vector3 vector_a = new Vector3(size,size,size);
		bb.set(vector_de, vector_a);
		ModelBuilder modelBuilder = new ModelBuilder();

		//System.out.println(  "vector_de: " + vector_de.x + " " + vector_de.y + " " + vector_de.z + " " );
		//System.out.println(  "vector_a: " + vector_a.x + " " + vector_a.y + " " + vector_a.z + " " );
		
		modelBuilder.begin();
		MeshPartBuilder builderBox = modelBuilder.part("line", 1, 3, new Material());
		builderBox.setColor(color);
		bb.getCorner000(vector_de);
		bb.getCorner001(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner110 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner001 (vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner101 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000 (vector_de);
		bb.getCorner010(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100 (vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner011(vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner001(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000(vector_de);
		bb.getCorner100(vector_a);
		builderBox.line(vector_de,vector_a);
		Model model_line = modelBuilder.end();
		ModelInstance modelInstance_line = new ModelInstance(model_line);
/*
		System.out.println(  "--   width: " + bb.getWidth() );
		System.out.println(  "--   height: " + bb.getHeight() );
		System.out.println(  "--   depth: " + bb.getDepth() );*/
		return modelInstance_line;
	}
	public static ModelInstance crear(float size) {
		BoundingBox bb = new BoundingBox();
		Vector3 vector_de = new Vector3(-size,-size,-size);
		Vector3 vector_a = new Vector3(size,size,size);
		bb.set(vector_de, vector_a);
		ModelBuilder modelBuilder = new ModelBuilder();

		//System.out.println(  "vector_de: " + vector_de.x + " " + vector_de.y + " " + vector_de.z + " " );
		//System.out.println(  "vector_a: " + vector_a.x + " " + vector_a.y + " " + vector_a.z + " " );
		
		modelBuilder.begin();
		MeshPartBuilder builderBox = modelBuilder.part("line", 1, 3, new Material());
		builderBox.setColor(Color.BLUE);
		bb.getCorner000(vector_de);
		bb.getCorner001(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner110 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner001 (vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner101 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000 (vector_de);
		bb.getCorner010(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100 (vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner011(vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner001(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000(vector_de);
		bb.getCorner100(vector_a);
		builderBox.line(vector_de,vector_a);
		Model model_line = modelBuilder.end();
		ModelInstance modelInstance_line = new ModelInstance(model_line);
/*
		System.out.println(  "--   width: " + bb.getWidth() );
		System.out.println(  "--   height: " + bb.getHeight() );
		System.out.println(  "--   depth: " + bb.getDepth() );*/
		return modelInstance_line;
	}
	public static ModelInstance cargar(ModelInstance modelInstance) {
		BoundingBox bb = new BoundingBox();
		modelInstance.calculateBoundingBox(bb);
		Vector3 vector_de = new Vector3();
		Vector3 vector_a = new Vector3();
		ModelBuilder modelBuilder = new ModelBuilder();
		
		modelBuilder.begin();
		MeshPartBuilder builderBox = modelBuilder.part("line", 1, 3, new Material());
		builderBox.setColor(Color.YELLOW);
		bb.getCorner000(vector_de);
		bb.getCorner001(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner110 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner001 (vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner101 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000 (vector_de);
		bb.getCorner010(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100 (vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner011(vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner001(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000(vector_de);
		bb.getCorner100(vector_a);
		builderBox.line(vector_de,vector_a);
		Model model_line = modelBuilder.end();
		ModelInstance modelInstance_line = new ModelInstance(model_line);
/*
		System.out.println(  "   width: " + bb.getWidth() );
		System.out.println(  "   height: " + bb.getHeight() );
		System.out.println(  "   depth: " + bb.getDepth() );*/
		return modelInstance_line;
	}
	public static ModelInstance cargar(Node node) {
		BoundingBox bb = new BoundingBox();
		node.calculateBoundingBox(bb);
		Vector3 vector_de = new Vector3();
		Vector3 vector_a = new Vector3();
		ModelBuilder modelBuilder = new ModelBuilder();
		
		modelBuilder.begin();
		MeshPartBuilder builderBox = modelBuilder.part("line", 1, 3, new Material());
		builderBox.setColor(Color.YELLOW);
		bb.getCorner000(vector_de);
		bb.getCorner001(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner110 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner001 (vector_de);
		bb.getCorner011(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner101 (vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000 (vector_de);
		bb.getCorner010(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner100 (vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);

		bb.getCorner011(vector_de);
		bb.getCorner111(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner001(vector_de);
		bb.getCorner101(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner010(vector_de);
		bb.getCorner110(vector_a);
		builderBox.line(vector_de,vector_a);
		bb.getCorner000(vector_de);
		bb.getCorner100(vector_a);
		builderBox.line(vector_de,vector_a);
		Model model_line = modelBuilder.end();
		ModelInstance modelInstance_line = new ModelInstance(model_line);
/*
		System.out.println(  "   width: " + bb.getWidth() );
		System.out.println(  "   height: " + bb.getHeight() );
		System.out.println(  "   depth: " + bb.getDepth() );*/
		return modelInstance_line;
	}
}
