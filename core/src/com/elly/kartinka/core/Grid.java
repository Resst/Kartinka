package com.elly.kartinka.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.elly.kartinka.GameClass;
import com.elly.kartinka.ui.screens.GameScreen;
import com.elly.kartinka.utils.RandomToolProvider;

import java.util.Arrays;

public class Grid {

    //Grid dimensions
    private float gridOffsetX, gridOffsetY;

    //divided texture
    private final Sprite[][] sprites;
    //field with fallen blocks
    private final Sprite[][] field;

    //Coordinates for current falling block
    private int curX, curY;
    private float offsetY;

    private final float fallingSpeed = 1.5f;

    //how many blocks are built in every column
    private final int[] built;

    //main texture
    private final Texture texture;
    private final Texture bgTexture;
    private Sprite textureSprite;
    private float textureSize;

    //how many rows(height) and columns(width)
    private final int width, height;

    private Texture frameBottom, frameTop;
    private Sprite frameTopSprite;
    private float frameX, frameY, frameSize;

    private int lives = 5;

    public boolean completed = false;

    private GameScreen screen;

    public Grid(Texture texture, Texture bgTexture, GameScreen screen) {
        this.texture = texture;
        this.bgTexture = bgTexture;
        width = GameScreen.GRID_WIDTH;
        height = GameScreen.GRID_HEIGHT;
        this.screen = screen;

        built = new int[width];
        field = new Sprite[width][height + 5];
        sprites = new Sprite[width][height];

        init();
    }

    private void init() {
        textureSprite = new Sprite(bgTexture);
        textureSize = Gdx.graphics.getWidth() * 4 / 5f;
        textureSprite.setSize(textureSize, textureSize);

        //filling with zeros
        Arrays.fill(built, 0);

        //field should contain null
        for (Sprite[] arr : field)
            Arrays.fill(arr, null);

        //dividing texture
        split();

        //temporary instantiate first block
        instantiate();
    }

    private void split() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sprites[i][j] = new Sprite(texture, i * texture.getWidth() / width, j * texture.getHeight() / height,
                        texture.getWidth() / width, texture.getHeight() / height);
                sprites[i][j].setSize(textureSize / width, textureSize / height);
                sprites[i][j].setOriginCenter();
            }
        }
    }

    public void draw(SpriteBatch batch) {
        textureSprite.draw(batch);
        for (Sprite[] value : field) {
            for (Sprite sprite : value) {
                if (sprite != null) {
                    sprite.draw(batch);
                }
            }
        }
        batch.draw(frameBottom, frameX, frameY, frameSize, frameSize);
        if (frameTopSprite != null)
            frameTopSprite.draw(batch);
    }

    public void update(float dt) {
        if (frameTopSprite != null) {
            if (frameTopSprite.getY() <= frameY) {
                completed = true;
                screen.getParticleSystem().squareWithAngles(gridOffsetX, gridOffsetY, textureSize, 5);
                screen.playFallingSound(true);
            } else {
                frameTopSprite.translateY(-fallingSpeed * 2 * textureSize / width * dt);
                if (frameTopSprite.getY() < frameY)
                    frameTopSprite.setY(frameY);
            }
            return;
        }

        offsetY -= fallingSpeed * dt;
        if (offsetY <= -1) {
            offsetY = 0;
            drop();
        }

        handleInput();
        updatePositions();
    }

    private void updatePositions() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                Sprite s = field[i][j];
                if (s != null) {
                    s.setPosition(gridOffsetX + i * s.getWidth(), gridOffsetY + j * s.getHeight());
                }
            }
        }

        getCurrent().setY(getCurrent().getY() + offsetY * getCurrent().getHeight());
    }

    private void handleInput() {

        //rotate
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            onRotate();
        //left
        if (Gdx.input.isKeyJustPressed(Input.Keys.A))
            onLeft();

        //right
        if (Gdx.input.isKeyJustPressed(Input.Keys.D))
            onRight();

    }

    public void onLeft() {
        if (curX > 0 && field[curX - 1][curY - 1] == null)
            translate(-1, 0);
    }

    public void onRight() {
        if (curX < field.length - 1 && field[curX + 1][curY - 1] == null)
            translate(1, 0);
    }

    public void onRotate() {
        if (!completed)
            getCurrent().setRotation((getCurrent().getRotation() - 90) % 360);
    }

    public void setDimensions(float x, float y, float size) {
        gridOffsetX = x;
        gridOffsetY = y;
        textureSize = size;

        frameX = gridOffsetX - textureSize * GameClass.FRAME_SCALE / 2;
        frameY = gridOffsetY - textureSize * GameClass.FRAME_SCALE / 2;
        frameSize = textureSize * (1 + GameClass.FRAME_SCALE);

        textureSprite.setSize(textureSize, textureSize);
        textureSprite.setPosition(gridOffsetX, gridOffsetY);

        for (Sprite[] arr : sprites)
            for (Sprite s : arr) {
                s.setSize(textureSize / this.width, textureSize / this.height);
                s.setOriginCenter();
            }
    }

    private void translate(int x, int y) {
        Sprite cur = getCurrent();
        field[curX][curY] = null;
        curY += y;
        curX += x;
        field[curX][curY] = cur;
    }

    private void drop() {
        translate(0, -1);
        if (curY == 0 || field[curX][curY - 1] != null) {
            boolean b = check();
            screen.playFallingSound(b);
            if (b) {
                screen.getParticleSystem().squareWithAngles(gridOffsetX + curX * textureSize / width, gridOffsetY + curY * textureSize / width, textureSize / width, 3);
                built[curX]++;
            } else {
                field[curX][curY] = null;
                lives--;
            }
            if (!isCompleted()) {
                instantiate();
            } else
                complete();
        }
    }

    private void complete() {
        frameTopSprite = new Sprite(frameTop);
        frameTopSprite.setSize(frameSize, frameSize);
        frameTopSprite.setOrigin(0, frameSize - GameClass.FRAME_SCALE / 2);
        frameTopSprite.setOriginBasedPosition(frameX, screen.getStage().getHeight());
    }


    private boolean check() {
        return curY < height && getCurrent() == sprites[curX][height - curY - 1] && getCurrent().getRotation() == 0;
    }

    private void instantiate() {
        int xCut;
        xCut = RandomToolProvider.random.nextInt(width);
        while (built[xCut] >= height)
            xCut = RandomToolProvider.random.nextInt(width);


        curX = RandomToolProvider.random.nextInt(width);
        curY = field[curX].length - 1;

        System.out.println(textureSize);
        System.out.println(width);
        float delta = Gdx.graphics.getHeight() / textureSize * width - curY;
        if (delta > 0)
            offsetY = delta;

        Sprite res = sprites[xCut][height - built[xCut] - 1];
        field[curX][curY] = res;
        res.setRotation(RandomToolProvider.random.nextInt(4) * 90);
    }

    private boolean isCompleted() {
        for (int i : built)
            if (i < height)
                return false;
        return true;
    }

    public void restart() {
        lives = 5;

        //filling with zeros
        Arrays.fill(built, 0);

        //field should contain null
        for (Sprite[] arr : field)
            Arrays.fill(arr, null);

        instantiate();
    }

    private Sprite getCurrent() {
        return field[curX][curY];
    }

    public int getLives() {
        return lives;
    }

    public void setFrame(Texture frameBottom, Texture frameTop) {
        this.frameBottom = frameBottom;
        this.frameTop = frameTop;
    }
}
