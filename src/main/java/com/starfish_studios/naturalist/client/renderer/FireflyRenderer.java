package com.starfish_studios.naturalist.client.renderer;

import com.starfish_studios.naturalist.client.model.FireflyModel;
import com.starfish_studios.naturalist.client.renderer.layers.FireflyGlowLayer;
import com.starfish_studios.naturalist.common.entity.Alligator;
import com.starfish_studios.naturalist.common.entity.Firefly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class FireflyRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Firefly, R> {
    public FireflyRenderer(EntityRendererProvider.@NotNull Context renderManager) {
        super(renderManager, new FireflyModel());
        this.shadowRadius = 0.4F;
        this.withRenderLayer(new FireflyGlowLayer(this));
    }

    @Override
    public float getMotionAnimThreshold(Firefly animatable) {
        return 0.000001f;
}
}
