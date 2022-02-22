package com.elly.kartinka.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.elly.kartinka.GameClass;
import com.elly.kartinka.ui.LevelChooseButton;
import com.elly.kartinka.ui.MenuButton;
import com.elly.kartinka.ui.Star;

public class LevelChooseScreen implements Screen {

    private final GameClass game;
    private final Skin skin;
    private final Stage stage;
    private Texture bg;

    private final Preferences prefs;

    private int page;

    private MenuButton bLeft, bRight, bShop, bRate, bExit, bSettings;

    private MenuButton bSound, bMusic;
    private Image settingsPanel;
    private Group settingsGroup;

    private LevelChooseButton[] bLevels;
    private Star[] stars;

    public LevelChooseScreen(GameClass game) {
        this.game = game;
        this.skin = game.getSkin();
        this.stage = new Stage();
        prefs = game.getPrefs();
        bg = game.getTexture("bg.png");

        initButtons();
        updateButtons();
    }

    private void initButtons() {
        //Left
        bLeft = new MenuButton(skin, "arrow_left");
        final String s = "click";
        bLeft.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setPage(page - 1);
                game.playSound(s);
            }
        });
        stage.addActor(bLeft);

        //Right
        bRight = new MenuButton(skin, "arrow_right");
        stage.addActor(bRight);
        bRight.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setPage(page + 1);
                game.playSound(s);
            }
        });

        //Exit
        bExit = new MenuButton(skin, "exit");
        stage.addActor(bExit);
        bExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playSound(s);
                Gdx.app.exit();
            }
        });

        //Rate
        bRate = new MenuButton(skin, "rate");
        stage.addActor(bRate);

        //Settings
        bSettings = new MenuButton(skin, "settings");
        bSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playSound(s);
                settingsGroup.setVisible(!settingsGroup.isVisible());
            }
        });

        //Shop
        bShop = new MenuButton(skin, "shop");
        stage.addActor(bShop);

        //Stars
        stars = new Star[3];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(skin, "stark3", "stark1");
            stage.addActor(stars[i]);
        }

        initLevelButtons();

        //Settings group
        settingsGroup = new Group();
        bSound = new MenuButton(skin, "sound");
        bSound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toggleSound();
                game.playSound(s);
            }
        });
        bSound.setChecked(!prefs.getBoolean("sound"));

        bMusic = new MenuButton(skin, "music");
        bMusic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playSound(s);
                game.toggleMusic();
            }
        });
        bMusic.setChecked(!prefs.getBoolean("music"));

        settingsPanel = new Image(skin.getDrawable("empty"));
        settingsPanel.setColor(0, 0, 0, .7f);
        settingsPanel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsGroup.setVisible(!settingsGroup.isVisible());
                bSettings.setChecked(!bSettings.isChecked());
            }
        });

        settingsGroup.addActor(settingsPanel);
        settingsGroup.addActor(bSound);
        settingsGroup.addActor(bMusic);
        settingsGroup.setVisible(false);
        stage.addActor(settingsGroup);
        stage.addActor(bSettings);
    }

    private void initLevelButtons() {
        bLevels = new LevelChooseButton[GameClass.CATS];
        for (int i = 0; i < GameClass.CATS; i++)
            bLevels[i] = new LevelChooseButton(game, "frame.png", "catb" + (i + 1) + ".png",
                    "cat" + (i + 1) + ".png", i + 1);
        for (LevelChooseButton b : bLevels) {
            b.setStage(stage);
            b.setVisible(false);
        }
        setPage(0);
    }

    private void setPage(int p) {
        bLevels[page].setVisible(false);

        page = p;

        String key = "cat" + (page + 1);

        if (!prefs.contains(key)){
            prefs.putInteger(key, -1);
            prefs.flush();
        }

        int res = prefs.getInteger(key);
        bLevels[page].update(res != -1);
        bLevels[page].setVisible(true);

        bLeft.setVisible(p != 0);
        bRight.setVisible(p != (bLevels.length - 1));

        for (int i = 0; i < stars.length; i++) {
            stars[i].update(i < res);
        }
    }

    private void updateButtons() {
        float bSize = stage.getViewport().getScreenWidth() / 5f;
        float width = stage.getViewport().getScreenWidth();
        float height = stage.getViewport().getScreenHeight();
        //resize
        //top buttons
        bExit.setSize(bSize, bSize);
        bRate.setSize(bSize, bSize);
        bShop.setSize(bSize, bSize);
        bSettings.setSize(bSize, bSize);

        //settings group
        bSound.setSize(bSize * 1.5f, bSize * 1.5f);
        bMusic.setSize(bSize * 1.5f, bSize * 1.5f);
        settingsPanel.setSize(width, height);

        //bottom buttons
        bLeft.setSize(bSize * 1.5f, bSize * 1.5f);
        bRight.setSize(bSize * 1.5f, bSize * 1.5f);

        //level buttons
        for (LevelChooseButton b : bLevels) {
            b.setSize(bSize * 4, bSize * 4);
        }

        //stars
        for (Star s : stars)
            s.setSize(bSize, bSize);

        //reposition
        //top buttons
        bExit.setPosition(0, height, Align.topLeft);
        bRate.setPosition(bSize + width / 15f, height, Align.topLeft);
        bShop.setPosition(width * 14 / 15f - bSize, height, Align.topRight);
        bSettings.setPosition(width, height, Align.topRight);

        //bottom buttons
        bLeft.setPosition(0, 0, Align.bottomLeft);
        bRight.setPosition(width, 0, Align.bottomRight);

        //settings group
        bSound.setPosition(width / 2f - bSize, height / 2f, Align.center);
        bMusic.setPosition(width / 2f + bSize, height / 2f, Align.center);
        settingsPanel.setPosition(0, 0);

        //level buttons
        for (LevelChooseButton bLevel : bLevels) {
            bLevel.setPosition(width / 2f, height / 2f + bSize / 2f);
        }

        //stars
        float levelX = bLevels[0].getButton().getX(Align.bottom);
        float levelY = bLevels[0].getButton().getY(Align.bottom);
        for (int i = 0; i < stars.length; i++) {
            stars[i].setPosition(levelX + bSize * 1.2f * (i - 1), levelY, Align.top);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        setPage(page);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(.5f, .5f, .5f, 1);
        stage.getBatch().begin();
        stage.getBatch().draw(bg, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        for (LevelChooseButton b : bLevels)
            b.draw((SpriteBatch) stage.getBatch());
        stage.getBatch().end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setWorldSize(width, height);
        stage.getViewport().update(width, height, true);
        updateButtons();
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
        stage.dispose();
    }
}
