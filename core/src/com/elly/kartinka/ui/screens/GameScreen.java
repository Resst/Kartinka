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
import com.elly.kartinka.core.Grid;
import com.elly.kartinka.ui.MenuButton;
import com.elly.kartinka.ui.Star;
import com.elly.kartinka.utils.ParticleSystem;

public class GameScreen implements Screen {

    private final GameClass game;

    private Grid grid;
    private MenuButton bLeft, bRight, bRotate, bHome;

    public static final int GRID_WIDTH = 3, GRID_HEIGHT = 3;

    //win group
    private MenuButton bExit, bRestart;
    private Star[] stars;
    private Image winPanel;
    private Group winGroup;

    private final Stage stage;
    private final Texture bg;
    private final Skin skin;
    private final Preferences prefs;
    private int number;
    private final ParticleSystem particleSystem;

    private int rating = 0;
    private boolean finished = false;

    public GameScreen(GameClass game) {
        this.game = game;
        prefs = game.getPrefs();
        skin = game.getSkin();
        stage = new Stage();
        bg = game.getTexture("bg.png");
        particleSystem = new ParticleSystem();
        particleSystem.setParticleTexture(game.getTexture("star.png"));
        particleSystem.setParticleParameters(500, .3f, 50);

        initButtons();
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        winGroup.setVisible(false);
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(game.getBatch());
    }

    public void draw(SpriteBatch batch) {
        ScreenUtils.clear(1, 1, 1, 1);

        batch.begin();
        batch.draw(bg, 0, 0, stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight());
        grid.draw(batch);
        batch.end();

        stage.draw();

        batch.begin();
        particleSystem.draw(batch);
        batch.end();
    }

    public void update(float dt) {
        if (!grid.completed) {
            grid.update(dt);
        } else if (!finished) {
            finish();
        }

        stage.act();
        particleSystem.update(dt);
    }

    private void initButtons() {
        //Left
        bLeft = new MenuButton(skin, "arrow_left");
        stage.addActor(bLeft);
        bLeft.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                grid.onLeft();
            }
        });

        //Right
        bRight = new MenuButton(skin, "arrow_right");
        stage.addActor(bRight);
        bRight.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                grid.onRight();
            }
        });

        //Rotate
        bRotate = new MenuButton(skin, "arrow_rotate");
        stage.addActor(bRotate);
        bRotate.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!grid.completed)
                    grid.onRotate();
            }
        });

        //home
        bHome = new MenuButton(skin, "home");
        bHome.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.getScreen(GameClass.Screens.LEVEL_CHOOSE));
                game.playSound("click");
                game.showAd();
            }
        });
        stage.addActor(bHome);

        //win group
        winGroup = new Group(){
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                if (visible)
                    for (int i = 0; i < stars.length; i++) {
                        stars[i].update(rating > i);
                    }
            }
        };
        winPanel = new Image(skin.getDrawable("empty"));
        winPanel.setColor(0, 0, 0, .7f);

        bExit = new MenuButton(skin, "exit2");
        bExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("click");
                game.setScreen(game.getScreen(GameClass.Screens.LEVEL_CHOOSE));
                game.showAd();
            }
        });

        bRestart = new MenuButton(skin, "arrow_rotate");
        bRestart.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playSound("click");
                setGrid(number);
                grid.setDimensions(
                        stage.getViewport().getScreenWidth() / 10f,
                        stage.getViewport().getScreenWidth() * 3 / 10f,
                        stage.getViewport().getScreenWidth() * 4 / 5f
                );
                finished = false;
                winGroup.setVisible(false);
                game.showAd();
            }
        });

        stars = new Star[3];

        winGroup.addActor(winPanel);
        winGroup.addActor(bExit);
        winGroup.addActor(bRestart);
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(skin, "stark3", "stark1");
            winGroup.addActor(stars[i]);
        }
        winGroup.setVisible(false);

        stage.addActor(winGroup);
    }


    private void updateButtons() {
        float width = stage.getViewport().getWorldWidth();
        float height = stage.getViewport().getWorldHeight();
        float bSize = width / 5f;
        bLeft.setSize(bSize * 1.2f, bSize * 1.2f);
        bRight.setSize(bSize * 1.2f, bSize * 1.2f);
        bHome.setSize(bSize / 2, bSize / 2);
        bRotate.setSize(bSize * 1.2f, bSize * 1.2f);
        //win group
        winPanel.setSize(width, height);
        bExit.setSize(bSize * 1.2f, bSize * 1.2f);
        bRestart.setSize(bSize * 1.2f, bSize * 1.2f);
        for (int i = 0; i < stars.length; i++) {
            stars[i].setSize(bSize * .75f, bSize * .75f);
            stars[i].setPosition(width / 2f + bSize * (i - 1) * 5 / 4, height / 2f - bSize / 2, Align.center);
        }

        bLeft.setPosition(0, 0, Align.bottomLeft);
        bRight.setPosition(width, 0, Align.bottomRight);
        bHome.setPosition(0, height, Align.topLeft);
        bRotate.setPosition(width / 2f, 0, Align.bottom);
        //win group
        winPanel.setPosition(0, 0);
        bExit.setPosition(width / 2f - bSize * 3 / 4, height / 2f + bSize * 3 / 4, Align.center);
        bRestart.setPosition(width / 2f + bSize * 3 / 4, height / 2f + bSize * 3 / 4, Align.center);
    }

    public void setGrid(int n) {
        number = n;
        grid = new Grid(game.getTexture("cat" + n + ".png"), game.getTexture("catb" + n + ".png"), this);
        grid.setFrame(game.getTexture("frameBottom.png"), game.getTexture("frameTop.png"));
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().setWorldSize(width, height);
        stage.getViewport().update(width, height, true);
        updateButtons();
        grid.setDimensions(width / 10f, width * 3 / 10f, width * 4 / 5f);
        particleSystem.setParticleParameters(-1, -1, width / 15f);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        grid = null;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void playFallingSound(boolean right) {
        game.playSound(String.valueOf(right));
    }

    public void finish() {
        String key = "cat" + number;
        if (grid.getLives() <= 0)
            rating = 0;
        else if (grid.getLives() <= 2)
            rating = 1;
        else if (grid.getLives() <= 4)
            rating = 2;
        else
            rating = 3;


        if (!prefs.contains(key) || prefs.getInteger(key) < rating) {
            prefs.putInteger(key, rating);
            prefs.flush();
        }

        winGroup.setVisible(true);

        finished = true;
    }

    public Stage getStage() {
        return stage;
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }
}
