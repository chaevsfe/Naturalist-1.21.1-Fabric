package com.starfish_studios.naturalist.fabric;

import com.starfish_studios.naturalist.NaturalistClient;
import com.starfish_studios.naturalist.client.model.ZebraModel;
import com.starfish_studios.naturalist.client.renderer.ZebraRenderer;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class NaturalistFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NaturalistClient.init();
        registerEntityRenders();
        EntityModelLayerRegistry.registerModelLayer(ZebraRenderer.LAYER_LOCATION, ZebraModel::createBodyLayer);

        // TODO: ItemProperties.register() no longer exists in 1.21.11.
        // Variant-based item model properties are now data-driven via item model JSON files.
        // The butterfly variant and snail color predicates need to be migrated to the new system.
    }

    private void registerEntityRenders() {
        EntityRendererRegistry.register(NaturalistEntityTypes.DUCK_EGG.get(), (context) -> new ThrownItemRenderer<>(context, 1.0F, false));
    }
}
