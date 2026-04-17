package com.wenkrang.famara.render.stage;

import com.wenkrang.famara.render.RenderContext;
import com.wenkrang.famara.render.RenderTemp;

public class BlockFaceLightStage implements RenderStage{
    @Override
    public void render(RenderContext renderContext, RenderTemp rt) {
        if (rt.hit.getHitBlockFace() != null) {
            switch (rt.hit.getHitBlockFace()) {
                case NORTH -> rt.color.addRGB(-5, -5, -5);
                case SOUTH -> rt.color.addRGB(5, 5, 5);
                case UP -> rt.color.addRGB(20, 20, 20);
                case DOWN -> rt.color.addRGB(-15, -15, -15);
                case EAST -> rt.color.addRGB(10, 10, 10);
                case WEST -> rt.color.addRGB(-10, -10, -10);
            }
        }
    }
}
