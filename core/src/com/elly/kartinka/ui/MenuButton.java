package com.elly.kartinka.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class MenuButton extends Button {

    private final float changeScale = .85f;
    private float unchangedWidth;
    private float unchangedHeight;
    private float changedWidth;
    private float changedHeight;
    private Sound sound;

    public MenuButton(Skin skin, String styleName) {
        super(skin, styleName);
        InputListener checkingListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setSuperSize(changedWidth, changedHeight);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setSuperSize(unchangedWidth, unchangedHeight);
            }
        };
        addListener(checkingListener);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        unchangedWidth = width;
        unchangedHeight = height;
        changedWidth = width * changeScale;
        changedHeight = height * changeScale;
    }

    private void setSuperSize(float width, float height){
        float x = getX(Align.center);
        float y = getY(Align.center);
        super.setSize(width, height);
        setPosition(x, y, Align.center);
    }
}
