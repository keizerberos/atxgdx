package com.antrax.gy.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HudManager {
    private final Stage stage;
    
    // Contenedores lógicos de Scene2D (Tablas)
    private Table rtsTable;
    private Table shooterTable;
    
    // Elementos dinámicos que se actualizarán desde el bucle
    private Label healthLabel;
    private Label resourceLabel;
    private Texture whiteTexture; // Textura básica para fondos y barras de vida
    private Skin skin;
    public HudManager() {
        // 1. Inicializar el Stage con un Viewport independiente para la UI
        this.stage = new Stage(new ScreenViewport());
        
        // Generar una textura blanca sólida de 1x1 píxel para crear barras e interfaces sin cargar imágenes externas
        createWhiteTexture();
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json")); 
        // 2. Construir las dos capas de la interfaz
        buildRtsLayout();
        buildShooterLayout();
        
        // Iniciar por defecto en modo RTS
        setRtsMode();
    }

    private void createWhiteTexture() {
    	 Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    	    
    	    // 2. 🚨 LA SOLUCIÓN: Definimos el color gris oscuro transparente AQUÍ directamente
    	    // (Rojo, Verde, Azul, Alpha). 0.85f le da un 85% de opacidad (15% transparente)
    	    pixmap.setColor(new Color(0.15f, 0.15f, 0.15f, 0.85f));
    	    pixmap.fill(); // Rellenamos el píxel de memoria con este color pre-mezclado
    	    
    	    // 3. Subir el píxel a la tarjeta de video
    	    whiteTexture = new Texture(pixmap);
    	    
    	    // Liberación obligatoria de la memoria RAM del sistema
    	    pixmap.dispose(); 
    }

    private void buildRtsLayout() {
        rtsTable = new Table();
        rtsTable.setFillParent(true);
        rtsTable.bottom(); // Alinear todo el contenedor en la parte inferior

        // Tintamos el fondo con un color gris oscuro y un Alpha del 85% (0.85f)
        // El último parámetro controla la transparencia: 0f es invisible, 1f es sólido.
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(whiteTexture));
        backgroundDrawable.tint(new Color(0.15f, 0.15f, 0.15f, 0.85f)); 

        // Asignamos el fondo directamente a la tabla
        rtsTable.setBackground(backgroundDrawable);
        
        // Etiqueta de recursos de supervivencia / construcción
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        resourceLabel = new Label("MINERALES: 150 | ENERGIA: 45", labelStyle);

        // Diseñar la cuadrícula de la tabla RTS
        rtsTable.add(resourceLabel).expandX().left().pad(20);
        //rtsTable.setBackground(bottomBarBackground.getDrawable());
        
        stage.addActor(rtsTable);
    }

    private void buildShooterLayout() {
        shooterTable = new Table();
        shooterTable.setFillParent(true);

        // 1. RETÍCULA CENTRAL (Crosshair)
        // Generamos una cruz simple usando una imagen a partir de nuestra textura blanca
        Image crosshairHorizontal = new Image(whiteTexture);
        crosshairHorizontal.setColor(Color.GREEN);
        
        // Añadir la retícula exactamente en el centro de la pantalla
        shooterTable.center();
        // Creamos un punto o cruz diminuta en medio de la pantalla
        Label.LabelStyle crossstyle = new Label.LabelStyle(new BitmapFont(), Color.GREEN);
        Label crosshair = new Label("+", crossstyle);
        crosshair.setFontScale(2f);
        shooterTable.add(crosshair).expand().center();
        
        // 2. ESTADÍSTICAS RPG (Parte inferior izquierda en modo Shooter)
        Table statsTable = new Table();
        statsTable.bottom().left();
        
        Label.LabelStyle fpsLabelStyle = new Label.LabelStyle(new BitmapFont(), Color.RED);
        healthLabel = new Label("VIDA: 100 / 100", fpsLabelStyle);
        statsTable.add(healthLabel).pad(20);
        
        // Añadir la tabla de estadísticas al contenedor principal del Shooter
        shooterTable.addActor(statsTable);
        stage.addActor(shooterTable);
    }

    public void setRtsMode() {
        rtsTable.setVisible(true);
        shooterTable.setVisible(false);
    }

    public void setShooterMode() {
        rtsTable.setVisible(false);
        shooterTable.setVisible(true);
    }

    /**
     * Permite actualizar dinámicamente los valores numéricos desde Java o Lua
     */
    public void updateStats(float salud, int recursos) {
        healthLabel.setText("VIDA: " + (int)salud + " / 100");
        resourceLabel.setText("MINERALES: " + recursos + " | ENERGIA: 45");
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.getBatch().enableBlending();
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
        whiteTexture.dispose();
    }
}