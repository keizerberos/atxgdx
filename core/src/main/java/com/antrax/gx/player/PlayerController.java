package com.antrax.gx.player;
import com.antrax.gx.objects.GameObject;
import com.antrax.gx.scenes.collisions.CollisionController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

public class PlayerController {

	public float mouseY0 = 0;
	public float mouseX0 = 0;
	public float mouseX = 0;
	public float mouseXd = 0;
	public float mouseY = 0;
	public float mouseYd = 0;

	PlayerEntity playerEntity;
	GameObject goHelperHandHold;
	boolean isholdingGO = false;
	
	Vector3 posCam = new Vector3();
	
	public boolean isHolding() {
		return isholdingGO;
	}
	
	public void setPlayerEntity(PlayerEntity playerEntity){
		this.playerEntity = playerEntity;
	}
    public void setHelperHandHold(GameObject gameObject) {
    	this.goHelperHandHold = gameObject;
    }
	void moveHandObject(){

		   if (goHelperHandHold !=null) {
			   posCam.set(camera.position);
			   posCam.add(camera.direction.scl(8f));
			   goHelperHandHold.setTranslate(posCam);
			   
			   // prevent fall down
			   posCam.add(0,0,0);
			   if (isholdingGO && playerEntity.getObjectHand()!=null && playerEntity.getObjectFront() == null) {
	        	  playerEntity.getObjectHand().setTranslate1(posCam);
			   }
			   
			   if (isholdingGO && playerEntity.getObjectHand()!=null && playerEntity.getObjectFront() != null) {

				   posCam.set(playerEntity.getCollidePoint());
				   posCam.add(0,0,0);
	        	  playerEntity.getObjectHand().setTranslate1(posCam);
			   }
		   }
		}
	public class MyInputProcessor implements InputProcessor {
		
		@Override
		   public boolean touchDown (int x, int y, int pointer, int button) {
			   //System.out.println("button:"+button);

		      if (button == Input.Buttons.LEFT && !isholdingGO) {
		          if (playerEntity.getObjectFront()!=null ){
		        	 // playerEntity.getObjectHand().translate(-1f,0,0);
		        	  playerEntity.setObjectHand(playerEntity.getObjectFront());
		        	  playerEntity.getObjectHand().desactive();
		        	  isholdingGO = true;
					  //posCam.set (camera.position);
					  //posCam.add(camera.direction.scl(3f));	
		        	  //playerEntity.getObjectHand().setTranslate1(posCam);
		          }		         
		          return true;     
		      }
		      if (button == Input.Buttons.LEFT && isholdingGO) {
	    	      if (playerEntity.getObjectHand()!=null ){
	    	    	  posCam.set(camera.position);
	   			   	  posCam.add(camera.direction.scl(3f));
	   			   	  //playerEntity.getObjectHand().active();
	    	    	  //playerEntity.getObjectHand().setTranslate1(playerEntity.getCollidePoint());
	    	    	  playerEntity.setObjectHand(null);
	    	    	  isholdingGO = false;
	    	      }

    	    	  isholdingGO = false;
		         	         
		          return true;     
		      }
		      return false;
		   }
		   @Override
		public boolean mouseMoved(int screenX, int screenY) {
			// TODO Auto-generated method stub
			/*	mouseYd = Gdx.input.getY() - Gdx.graphics.getHeight()/2;
				mouseY -= mouseYd/70f;
				mouseXd = Gdx.input.getX() - Gdx.graphics.getWidth()/2;
				mouseX += mouseXd/70f; */
			   mouseXd = screenX - mouseX0;
			   mouseYd = screenY - mouseY0;
			   mouseXd = mouseXd/10f;
			   mouseY -= mouseYd/10f;
			   mouseX += mouseXd/10f;
			   
			   mouseX0 = screenX;
			   mouseY0 = screenY;
			   moveHandObject();
			   //if (screenX > Gdx.graphics.getWidth())  Gdx.input.setCursorPosition(0,Gdx.input.getY());
			   //if (screenY > Gdx.graphics.getHeight())  Gdx.input.setCursorPosition(Gdx.input.getX(),0);
			  // System.out.println("screen:"+screenX+" " +screenY);
			   
			 //  Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
			return false;
		}
		   
		   
		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean keyUp(int keycode) {
			return false;
		}
		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			//isholdingGO = false;
			return false;
		}
		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			// TODO Auto-generated method stub

			   mouseXd = screenX - mouseX0;
			   mouseYd = screenY - mouseY0;
			   mouseXd = mouseXd/10f;
			   mouseY -= mouseYd/10f;
			   mouseX += mouseXd/10f;
			   
			   mouseX0 = screenX;
			   mouseY0 = screenY;
			   //moveHandObject();

			return false;
		}
		@Override
		public boolean scrolled(float amountX, float amountY) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	CollisionController collisionController;
	public  Vector3[] playerRay = {new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3()};
	boolean staticCam = false;
	Vector3 camVec = new Vector3();
	Camera camera;
	
	public PlayerController (Camera camera) {
		this.camera = camera;

		Gdx.input.setCursorCatched(true);
		//Gdx.input.setCursorPosition(100,100);
		
		Pixmap pixmap = new Pixmap(Gdx.files.internal("badcursor.png"));
		int xHotspot = 15, yHotspot = 15;
		Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
		pixmap.dispose(); 
		Gdx.graphics.setCursor(cursor);
		MyInputProcessor inputProcessor = new MyInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
	}
	public void setWorldCollision(CollisionController collisionController){
		this.collisionController = collisionController;
	}
	public void update(){
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			collisionController.characterTransform.rotate(0, 1, 0, 5f);
			collisionController.ghostObject.setWorldTransform(collisionController.characterTransform);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			collisionController.characterTransform.rotate(0, 1, 0, -5f);
			collisionController.ghostObject.setWorldTransform(collisionController.characterTransform);
		}

		if (Gdx.input.isKeyPressed(Keys.SPACE) && collisionController.characterController.canJump() ){		
		/*	characterTransform.getTranslation(playerRay[0]);	
			playerRay[0].y+=0.3f;
			//walkDirection.add(0,characterDirection.y,0);
			
			characterTransform.setTranslation(playerRay[0]);
			ghostObject.setWorldTransform(characterTransform);
			*/

			collisionController.characterController.jump(new Vector3(0f,25f,0f));
		}
		collisionController.characterDirection.set(-1,0,0).rot(collisionController.characterTransform).nor();
		collisionController.characterDirection2.set(0,0,-1).rot(collisionController.characterTransform).nor();
		collisionController.walkDirection.set(0,0,0);
		if (Gdx.input.isKeyPressed(Keys.S))
			collisionController.walkDirection.add(collisionController.characterDirection);
		if (Gdx.input.isKeyPressed(Keys.W))
			collisionController.walkDirection.add(-collisionController.characterDirection.x, -collisionController.characterDirection.y, -collisionController.characterDirection.z);

		if (Gdx.input.isKeyPressed(Keys.A)) {
			collisionController.walkDirection.add(collisionController.characterDirection2);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			collisionController.walkDirection.add(-collisionController.characterDirection2.x, -collisionController.characterDirection2.y, -collisionController.characterDirection2.z);
		}
		collisionController.walkDirection.scl(12f * Gdx.graphics.getDeltaTime());
		collisionController.characterController.setWalkDirection(collisionController.walkDirection);

		//characterDirection.rotate(-mouseY,1, 0, 0).nor();
		//characterDirection.rotate(-mouseX,0, 1, 0).nor();

		collisionController.characterTransform.getTranslation(playerRay[0]);
		playerRay[1].set(collisionController.characterDirection.cpy().scl(-2.5f)) ;
		playerRay[2].set(collisionController.characterDirection.cpy().scl(2.5f)) ;
		playerRay[1].add(playerRay[0]);
		playerRay[2].add(playerRay[0]);
		playerRay[1].y += mouseY/9;
		//playerRay[1].y += (float) (playerRay[1].z * Math.tan( Math.toRadians(mouseY)));
		//playerRay[1].z +=mouseX;
		playerRay[3].set(playerRay[0]);
		playerRay[3].add(0f,5f,0f);
		if (staticCam){	//3thperson
			camVec.x = playerRay[0].x-11.5f/2;
			
			camera.up.x = 0.0f;
			camera.up.z = 0.0f;
			camera.lookAt(playerRay[0]);

		}else{
			/*camVec.x = playerRay[0].x-11.5f/2;
			camVec.y = playerRay[0].y+15f/3;
			camVec.z = playerRay[0].z+11.5f/2;*/
			camVec.x = playerRay[0].x-13.5f/1.5f;
			camVec.y = playerRay[0].y+35f/2.5f;
			camVec.z = playerRay[0].z+17.5f/1.5f;

			
			//camera.position.set(camVec);
			playerRay[0].y += 2f;
			
			
			camera.position.set(playerRay[0]);
			camera.lookAt(playerRay[1]);
			camera.up.set(Vector3.Y);
		}
		//Gdx.input.setCursorPosition(Gdx.input.getX(), Gdx.input.getY());
		
		//Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
		

		collisionController.ghostObject.getWorldTransform(collisionController.characterTransform);
		collisionController.characterTransform.rotate(0, 1, 0, -mouseXd);
		mouseXd=0;

		collisionController.ghostObject.setWorldTransform(collisionController.characterTransform);
		//moveHandObject();
	}
}
