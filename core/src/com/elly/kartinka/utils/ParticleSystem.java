package com.elly.kartinka.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class ParticleSystem {
    Array<Particle> particles = new Array<>();
    protected float particleSpeed, particleSize, particleLifeTime;
    private Texture particleTexture;

    public void update(float dt) {
        Array.ArrayIterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update(dt);
            if (p.ended())
                iterator.remove();
        }
    }

    public void draw(SpriteBatch batch){
        for (int i = 0; i < particles.size; i++){
            particles.get(i).sprite.draw(batch);
        }
    }

    public void squareWithAngles(Sprite s, int frequency) {
        squareWithAngles(s.getX(), s.getY(), s.getWidth(), frequency);
    }

    public void squareWithAngles(float x, float y, float size, int frequency) {
        Sprite s = new Sprite(particleTexture);
        s.setSize(particleSize, particleSize);

        s.setOriginCenter();
        s.setOriginBasedPosition(x, y);

        particles.add(new Particle(s, -135, particleSpeed));

        s.translateX(size);
        particles.add(new Particle(s, -45, particleSpeed));

        s.translateY(size);
        particles.add(new Particle(s, 45, particleSpeed));

        s.translateX(-size);
        particles.add(new Particle(s, 135, particleSpeed));

        square(x, y, size, frequency);
    }

    public void square(Sprite sprite, int frequency){
        square(sprite.getX(), sprite.getY(), sprite.getWidth(), frequency);
    }

    public void square(float x, float y, float size, int frequency){
        float space = size / (frequency + 1);
        float angleSpace = 90f / (frequency + 1);

        float angle = -135;

        Sprite s = new Sprite(particleTexture);
        s.setSize(particleSize, particleSize);

        s.setOriginCenter();
        s.setOriginBasedPosition(x + space, y);
        angle += angleSpace;

        for (int i = 0; i < frequency; i++){
            particles.add(new Particle(s, angle, particleSpeed));
            s.translateX(space);
            angle += angleSpace;
        }

        s.translateY(space);
        angle += angleSpace;

        for (int i = 0; i < frequency; i++){
            particles.add(new Particle(s, angle, particleSpeed));
            s.translateY(space);
            angle += angleSpace;
        }

        s.translateX(-space);
        angle += angleSpace;

        for (int i = 0; i < frequency; i++){
            particles.add(new Particle(s, angle, particleSpeed));
            s.translateX(-space);
            angle += angleSpace;
        }

        s.translateY(-space);
        angle += angleSpace;

        for (int i = 0; i < frequency; i++){
            particles.add(new Particle(s, angle, particleSpeed));
            s.translateY(-space);
            angle += angleSpace;
        }

    }

    public void setParticleParameters(float speed, float lifetime, float size) {
        if (speed != -1)
            particleSpeed = speed;
        if (size != -1)
            particleSize = size;
        if (lifetime != -1)
            particleLifeTime = lifetime;
    }

    public void setParticleTexture(Texture texture){
        particleTexture = texture;
    }

    protected class Particle {
        protected final Sprite sprite;
        protected final float speedX, speedY;
        protected float lifeTime;

        public Particle(Sprite sprite, float angle, float speed) {
            this.sprite = new Sprite(sprite);
            this.lifeTime = particleLifeTime;
            speedX = speed * (float) Math.cos(Math.toRadians(angle));
            speedY = speed * (float) Math.sin(Math.toRadians(angle));
        }

        public void update(float dt) {
            lifeTime -= dt;
            sprite.translate(speedX * dt, speedY * dt);
        }

        public boolean ended(){
            return lifeTime <= 0;
        }
    }
}
