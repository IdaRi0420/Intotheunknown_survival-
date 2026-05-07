package com.ida.survivors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.ida.survivors.core.GameOrchestrator;

public class FirstScreen implements Screen {
    
    private GameOrchestrator game;
    
    @Override
    public void show() {
        game = GameOrchestrator.getInstance();
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update and render
        game.update(delta);
        game.render();
    }
    
    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        if (game != null) {
            game.resize(width, height);
        }
    }
    
    @Override
    public void pause() {
        if (game != null) game.pause();
    }
    
    @Override
    public void resume() {
        if (game != null) game.resume();
    }
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        if (game != null) game.dispose();
    }
}