// FILE: core/GameOrchestrator.java
package com.ida.survivors.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ida.survivors.entities.human.player.PlayerEntity;
import com.ida.survivors.entities.human.player.PlayerSprite;
import com.ida.survivors.view.camera.GameCamera;
import com.ida.survivors.view.renderer.GameRenderer;

public class GameOrchestrator {

    private static GameOrchestrator instance;

    public static GameOrchestrator getInstance() {
        if (instance == null) {
            instance = new GameOrchestrator();
        }
        return instance;
    }

    private PlayerEntity player;
    private PlayerSprite playerSprite;
    private InputHandler inputHandler;
    private GameCamera camera;
    private SpriteBatch batch;
    private GameRenderer renderer;

    private boolean isRunning = true;

    private GameOrchestrator() {
        batch = new SpriteBatch();
        camera = new GameCamera();
        renderer = new GameRenderer(batch, camera.getCamera());
        inputHandler = new InputHandler();

        float startX = Gdx.graphics.getWidth() / 2f;
        float startY = Gdx.graphics.getHeight() / 2f;

        player = new PlayerEntity(startX, startY, "Survivor");
        playerSprite = new PlayerSprite(player);

        // Safe wiring
        if (camera != null) {
            camera.setTarget(player);
        }

        if (inputHandler != null) {
            inputHandler.setPlayer(player);
        }

        if (renderer != null) {
            renderer.addSprite(playerSprite);
        }
    }

    public void update(float delta) {
        if (!isRunning) return;

        if (inputHandler != null) {
            inputHandler.update(delta);
        }

        if (playerSprite != null) {
            playerSprite.update(delta);
        }

        if (camera != null) {
            camera.update(delta);
        }
    }

    public void render() {
        if (!isRunning || renderer == null) return;
        renderer.render();
    }

    public void resize(int width, int height) {
        if (camera != null) camera.resize(width, height);
        if (renderer != null) renderer.resize(width, height);
    }

    public void pause() {
        isRunning = false;
    }

    public void resume() {
        isRunning = true;
    }

    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (batch != null) batch.dispose();
        isRunning = false;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public GameCamera getCamera() {
        return camera;
    }
}