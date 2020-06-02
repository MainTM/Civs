package org.redcastlemedia.multitallented.civs.spells.effects.particles;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.redcastlemedia.multitallented.civs.spells.effects.ParticleEffect;

public class FairyWings extends CivParticleEffect {
    boolean x = true;
    boolean o = false;
    private boolean[][] shape = {
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, x, x, x, x, o, o, o, o, o, o, o, x, x, x, x, o, o},
            {o, o, x, x, x, x, x, o, o, o, x, x, x, x, x, o, o, o},
            {o, o, o, x, x, x, x, x, x, x, x, x, x, x, o, o, o, o},
            {o, o, o, o, x, x, x, x, x, x, x, x, x, o, o, o, o, o},
            {o, o, o, o, x, x, x, x, o, x, x, x, x, o, o, o, o, o},
            {o, o, o, o, o, x, x, x, o, x, x, x, o, o, o, o, o, o},
            {o, o, o, o, o, x, x, o, o, o, x, x, o, o, o, o, o, o},
            {o, o, o, o, x, x, o, o, o, o, o, x, x, o, o, o, o, o}
    };

    @Override
    public void update(LivingEntity livingEntity, ParticleEffect particleEffect) {
        drawShape(livingEntity, particleEffect, shape);
    }

    @Override
    public long getRepeatDelay(ParticleEffect particleEffect) {
        return 2;
    }
}
