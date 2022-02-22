package com.elly.kartinka.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.elly.kartinka.GameClass;
import com.elly.kartinka.ui.screens.GameScreen;

public class LevelChooseButton {

    private final Sprite texture;
    private final Texture colored, vanilla;
    private final Button button;
    private final Sprite frame;
    private final float checkedColor = .75f;

    public LevelChooseButton(final GameClass game, final String frame, final String locked, final String texture,
                             final int number) {
        this.button = new Button(new Button.ButtonStyle());
        this.frame = new Sprite(game.getTexture(frame));
        this.colored = game.getTexture(texture);
        this.vanilla = game.getTexture(locked);
        this.texture = new Sprite(colored);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen s = (GameScreen) game.getScreen(GameClass.Screens.GAME);
                s.setGrid(number);
                game.setScreen(s);
            }
        });

        button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getSprite().setColor(checkedColor, checkedColor, checkedColor, 1);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getSprite().setColor(1, 1, 1, 1);
            }
        });

        getSprite().setOriginCenter();
    }

    public void update(boolean completed){
        getSprite().setTexture(completed ? colored : vanilla);
    }

    public void setStage(Stage stage) {
        stage.addActor(button);
    }

    public void draw(SpriteBatch batch) {
        if (button.isVisible()){
            getSprite().draw(batch);
            frame.draw(batch);
        }
    }

    public void setPosition(float x, float y) {
        button.setPosition(x, y, Align.center);
        getSprite().setOriginCenter();
        getSprite().setOriginBasedPosition(x, y);
        frame.setOriginCenter();
        frame.setOriginBasedPosition(x, y);
    }

    public void setSize(float width, float height) {
        button.setSize(width, height);
        getSprite().setSize(width / (1 + GameClass.FRAME_SCALE), height / (1 + GameClass.FRAME_SCALE));
        frame.setSize(width, height);
    }

    public Sprite getSprite() {
        return texture;
    }

    public void setVisible(boolean b){
        button.setVisible(b);
    }

    public Button getButton(){
        return button;
    }
}
