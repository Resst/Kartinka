package com.elly.kartinka.ui.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.elly.kartinka.GameClass;

public class TitleScreen implements Screen {

    private final GameClass game;
    private final Sprite elly;
    private float timer = 5;

    public TitleScreen(GameClass game){
        this.game = game;
        elly = new Sprite(new Texture("elly.jpg"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        update(delta);
        draw(game.getBatch());
    }
    public void draw(SpriteBatch batch){
        batch.begin();
        elly.draw(batch);
        batch.end();
    }
    public void update(float dt){
        timer -= dt;
        if (timer <= 0)
            game.setScreen(game.getScreen(GameClass.Screens.LEVEL_CHOOSE));
    }

    @Override
    public void resize(int width, int height) {
        elly.setSize(width, width);
        elly.setOriginCenter();
        elly.setOriginBasedPosition(width / 2f, height / 2f);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        elly.getTexture().dispose();
    }
}
