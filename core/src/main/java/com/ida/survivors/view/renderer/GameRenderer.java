// FILE: view/renderer/GameRenderer.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1

package com.ida.survivors.view.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ida.survivors.lib.sprite.BaseSprite;

import java.util.ArrayList;
import java.util.List;

/**
 * GameRenderer - Simple renderer that draws all sprites.
 * 
 * Phase 1: Just collects sprites and renders them in order.
 * Phase 2: Add layers, sorting, culling.
 */
public class GameRenderer {
    
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private List<BaseSprite> sprites;
    
    public GameRenderer(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
        this.sprites = new ArrayList<>();
    }
    
    public void addSprite(BaseSprite sprite) {
        sprites.add(sprite);
    }
    
    public void removeSprite(BaseSprite sprite) {
        sprites.remove(sprite);
    }
    
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        for (BaseSprite sprite : sprites) {
            if (sprite.isVisible()) {
                sprite.render(batch);
            }
        }
        
        batch.end();
    }
    
    public void resize(int width, int height) {
        // Camera handles this, nothing needed here
    }
    
    public void dispose() {
        for (BaseSprite sprite : sprites) {
            sprite.dispose();
        }
        sprites.clear();
    }
}