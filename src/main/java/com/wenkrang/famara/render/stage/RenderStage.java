package com.wenkrang.famara.render.stage;

import com.wenkrang.famara.render.RenderContext;
import com.wenkrang.famara.render.RenderTemp;

public interface RenderStage {
    void render(RenderContext renderContext, RenderTemp renderTemp);
}
