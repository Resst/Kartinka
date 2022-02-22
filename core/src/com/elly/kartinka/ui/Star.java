package com.elly.kartinka.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Star extends Image {

    private final Skin skin;
    private final String drawableOn, drawableOff;
    private boolean enabled;

    public Star(Skin skin, String drawableOn, String drawableOff) {
        super();
        this.skin = skin;
        this.drawableOn = drawableOn;
        this.drawableOff = drawableOff;
    }

    public void update(boolean on){
        this.enabled = on;
        this.setDrawable(skin, enabled ? drawableOn : drawableOff);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
