package com.elly.kartinka;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.elly.kartinka.ui.screens.GameScreen;
import com.elly.kartinka.ui.screens.LevelChooseScreen;
import com.elly.kartinka.ui.screens.TitleScreen;
import com.elly.kartinka.utils.LeavesSystem;
import com.elly.kartinka.utils.RandomToolProvider;

public class GameClass extends Game {

    //frame width(160) / frame texture width(2160); frame width = frame left border size(80) + frame right border size(80)
    public static final float FRAME_SCALE = 160 / 2160f;

    //preferences data
    public static final String PREFS_NAME = "comEllyKartinka";
    private Preferences prefs;

    SpriteBatch batch;
    private Skin skin;

    private TitleScreen titleScreen;
    private LevelChooseScreen levelChooseScreen;
    private GameScreen gameScreen;
    private Screen nextScreen;

    public static final int CATS = 9;
    public static final int LEAVES = 6;

    private AssetManager manager;

    private LeavesSystem leavesSystem;

    private Sound clickSound, fallRight, fallWrong;
    private Music music;
    public static final float musicVolume = .3f;
    public static final float clickVolume = 1;
    public static final float fallingVolume = .5f;

    private float screenChangeSpeed = 1.5f;
    private Sprite overScreen;

    private AdManager adManager;
    private int adCounter = 0;

    public GameClass() {
    }

    public GameClass(AdManager adManager) {
        this.adManager = adManager;
    }

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/ui.skin"));
        batch = new SpriteBatch();
        leavesSystem = new LeavesSystem(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), this);
        prefs = Gdx.app.getPreferences(GameClass.PREFS_NAME);
        manager = new AssetManager();
        loadAssets();
        updateMusic();
        overScreen = new Sprite(getTexture("ui/empty.png"));
        overScreen.setColor(0, 0, 0, 1);
        music.setLooping(true);
        music.play();

        initScreens();
        setScreen(titleScreen);
    }

    private void initScreens() {
        gameScreen = new GameScreen(this);
        levelChooseScreen = new LevelChooseScreen(this);
        titleScreen = new TitleScreen(this);
    }

    @Override
    public void render() {
        super.render();
        tint(Gdx.graphics.getDeltaTime());
        leavesSystem.update(Gdx.graphics.getDeltaTime());
        if (RandomToolProvider.random.nextInt(75) == 0)
            leavesSystem.newLeaf();

        batch.begin();
        leavesSystem.draw(batch);
        overScreen.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        overScreen.setSize(width, height);
        leavesSystem.resize(width, height);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void setScreen(Screen screen) {
        nextScreen = screen;
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        manager.dispose();
        gameScreen.dispose();
        levelChooseScreen.dispose();
        titleScreen.dispose();
    }

    private void loadAssets() {
        if (!prefs.contains("music"))
            prefs.putBoolean("music", true);
        if (!prefs.contains("sound"))
            prefs.putBoolean("sound", true);
        prefs.flush();
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.ogg"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.ogg"));
        fallRight = Gdx.audio.newSound(Gdx.files.internal("sounds/true.ogg"));
        fallWrong = Gdx.audio.newSound(Gdx.files.internal("sounds/false.ogg"));

        for (int i = 1; i <= CATS; i++) {
            manager.load("cat" + i + ".png", Texture.class);
            manager.load("catb" + i + ".png", Texture.class);
        }

        for (int i = 1; i <= LEAVES; i++) {
            manager.load("leaves/" + i + ".png", Texture.class);
        }

        manager.load("bg.png", Texture.class);
        manager.load("frame.png", Texture.class);
        manager.load("frameBottom.png", Texture.class);
        manager.load("frameTop.png", Texture.class);
        manager.load("star.png", Texture.class);
        manager.load("ui/empty.png", Texture.class);
        manager.finishLoading();
    }

    public void showAd() {
        if (adManager != null) {
            adCounter++;
            if (adCounter >= 2) {
                adCounter = 0;
                adManager.showAd();
            }
        }
    }

    public Screen getScreen(Screens screen) {
        switch (screen) {
            case GAME:
                return gameScreen;
            case LEVEL_CHOOSE:
                return levelChooseScreen;
            case TITLE:
                return titleScreen;
        }
        return null;
    }

    public void toggleMusic() {
        prefs.putBoolean("music", !prefs.getBoolean("music"));
        prefs.flush();
        updateMusic();
    }

    public void toggleSound() {
        prefs.putBoolean("sound", !prefs.getBoolean("sound"));
        prefs.flush();
    }

    public void playSound(String sound) {
        boolean b = prefs.getBoolean("sound");
        switch (sound) {
            case "click":
                clickSound.play(b ? clickVolume : 0);
                break;
            case "true":
            case "right":
                fallRight.play(b ? fallingVolume : 0);
                break;
            case "false":
            case "wrong":
                fallWrong.play(b ? fallingVolume : 0);
                break;
        }
    }

    public Music getMusic() {
        return music;
    }

    public void tint(float dt) {
        float a = overScreen.getColor().a;
        if (nextScreen != null) {
            if (screenChangeSpeed < 0)
                screenChangeSpeed = -screenChangeSpeed;
            if (a == 1) {
                super.setScreen(nextScreen);
                nextScreen = null;
                screenChangeSpeed = -screenChangeSpeed;
            } else {
                overScreen.setColor(0, 0, 0, a + screenChangeSpeed * dt);
            }
        } else if (screenChangeSpeed < 0) {
            if (a == 0) {
                screenChangeSpeed = -screenChangeSpeed;
            } else {
                overScreen.setColor(0, 0, 0, a + screenChangeSpeed * dt);
            }
        }
    }

    public void updateMusic() {
        music.setVolume(prefs.getBoolean("music") ? musicVolume : 0);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Skin getSkin() {
        return skin;
    }

    public Texture getTexture(String s) {
        return manager.get(s, Texture.class);
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public enum Screens {
        GAME,
        LEVEL_CHOOSE,
        TITLE
    }
}
