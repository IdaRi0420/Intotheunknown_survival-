package com.ida.survivors.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.ida.survivors.entities.human.player.PlayerEntity;

public class InputHandler implements InputProcessor {

    private PlayerEntity player;
    private float speed = 200f;

    private boolean moveUp = false, moveDown = false, moveLeft = false, moveRight = false;
    private float deltaX = 0, deltaY = 0;

    private float mouseX = 0, mouseY = 0;

    private boolean isShooting = false;
    private int shootCooldown = 0;
    private static final int SHOOT_DELAY_FRAMES = 10;

    private boolean debugMode = false;

    public InputHandler() {
        Gdx.input.setInputProcessor(this);
    }

    public void update(float delta) {
        if (player == null) return;

        deltaX = 0;
        deltaY = 0;

        if (moveUp) deltaY += 1;
        if (moveDown) deltaY -= 1;
        if (moveRight) deltaX += 1;
        if (moveLeft) deltaX -= 1;

        if (deltaX != 0 && deltaY != 0) {
            float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            deltaX /= length;
            deltaY /= length;
        }

        float moveX = deltaX * speed * delta;
        float moveY = deltaY * speed * delta;

        if (moveX != 0 || moveY != 0) {
            player.move(moveX, moveY);
        }

        if (shootCooldown > 0) {
            shootCooldown--;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            System.out.println("Debug mode: " + (debugMode ? "ON" : "OFF"));

            if (debugMode && player != null) {
                System.out.println("Player position: (" + player.getX() + ", " + player.getY() + ")");
            }
        }

        if (isShooting && shootCooldown == 0) {
            if (debugMode) {
                System.out.println("Shoot at: (" + mouseX + ", " + mouseY + ")");
            }
            shootCooldown = SHOOT_DELAY_FRAMES;
        }
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void reset() {
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
        isShooting = false;
        deltaX = 0;
        deltaY = 0;
    }

    // ===== INPUT PROCESSOR METHODS =====

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W: moveUp = true; return true;
            case Input.Keys.S: moveDown = true; return true;
            case Input.Keys.A: moveLeft = true; return true;
            case Input.Keys.D: moveRight = true; return true;
            case Input.Keys.ESCAPE: Gdx.app.exit(); return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W: moveUp = false; return true;
            case Input.Keys.S: moveDown = false; return true;
            case Input.Keys.A: moveLeft = false; return true;
            case Input.Keys.D: moveRight = false; return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isShooting = true;
            mouseX = screenX;
            mouseY = screenY;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isShooting = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseX = screenX;
        mouseY = screenY;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseX = screenX;
        mouseY = screenY;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (debugMode) {
            System.out.println("Mouse scrolled: " + amountX + ", " + amountY);
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}