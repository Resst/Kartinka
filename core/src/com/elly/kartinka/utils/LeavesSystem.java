package com.elly.kartinka.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.elly.kartinka.GameClass;

public class LeavesSystem extends ParticleSystem {

    int width, height;
    GameClass game;

    public LeavesSystem(int width, int height, GameClass game){
        this.width = width;
        this.height = height;
        this.game = game;
        setParticleParameters(1,1,width / 30f);
    }

    public void resize(int width, int height){
        this.width = width;
        this.height = height;
        setParticleParameters(1,1,width / 30f);
    }

    public void newLeaf(){
        int num = RandomToolProvider.random.nextInt(6) + 1;
        Texture tLeaf = game.getTexture("leaves/" + num + ".png");
        Sprite sLeaf = new Sprite(tLeaf);

        sLeaf.setSize(particleSize, particleSize);

        sLeaf.setOriginCenter();

        float lX = 0, lY = 0;
        if (RandomToolProvider.random.nextInt(4) == 0) {
            lY = height;
            lX = RandomToolProvider.random.nextInt(width);
        } else {
            lX = 0;
            lY = RandomToolProvider.random.nextInt(height);
        }

        sLeaf.setOriginBasedPosition(lX - particleSize / 2, lY + particleSize / 2);

        particles.add(new Leaf(sLeaf, -45, 100));

    }

    private class Leaf extends Particle{

        public Leaf(Sprite sprite, float angle, float speed) {
            super(sprite, angle, speed);
        }

        @Override
        public void update(float dt) {
            sprite.translate(speedX * dt, speedY * dt);
            sprite.rotate(RandomToolProvider.random.nextFloat());
        }

        @Override
        public boolean ended() {
            return sprite.getY() < 0 - 2 * particleSize || sprite.getX() > width + particleSize;
        }
    }
}
