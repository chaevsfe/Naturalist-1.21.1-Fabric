package com.starfish_studios.naturalist;

import com.starfish_studios.naturalist.client.renderer.*;
import com.starfish_studios.naturalist.core.platform.ClientPlatformHelper;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import com.starfish_studios.naturalist.core.registry.NaturalistRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class NaturalistClient {
    public static void init() {
        // Block render layers
        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.TRANSLUCENT,
                NaturalistRegistry.AZURE_FROGLASS.get(),
                NaturalistRegistry.VERDANT_FROGLASS.get(),
                NaturalistRegistry.CRIMSON_FROGLASS.get(),
                NaturalistRegistry.AZURE_FROGLASS_PANE.get(),
                NaturalistRegistry.VERDANT_FROGLASS_PANE.get(),
                NaturalistRegistry.CRIMSON_FROGLASS_PANE.get()
        );
        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.CUTOUT,
                NaturalistRegistry.CHRYSALIS_BLOCK.get(),
                NaturalistRegistry.GLOW_GOOP_BLOCK.get(),
                NaturalistRegistry.SNAIL_EGGS.get()
        );

        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.SNAIL, SnailRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.BEAR, BearRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.BUTTERFLY, ButterflyRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.FIREFLY, FireflyRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.SNAKE, SnakeRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.CORAL_SNAKE, SnakeRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.RATTLESNAKE, SnakeRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.DEER, DeerRenderer::new);

        // BIRDS
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.BLUEJAY, BirdRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.CARDINAL, BirdRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.CANARY, BirdRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.ROBIN, BirdRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.FINCH, BirdRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.SPARROW, BirdRenderer::new);

        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.CATERPILLAR, CaterpillarRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.RHINO, RhinoRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.LION, LionRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.ELEPHANT, ElephantRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.ZEBRA, ZebraRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.GIRAFFE, GiraffeRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.HIPPO, HippoRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.VULTURE, VultureRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.BOAR, BoarRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.DRAGONFLY, DragonflyRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.CATFISH, CatfishRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.ALLIGATOR, AlligatorRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.BASS, BassRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.LIZARD, LizardRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.LIZARD_TAIL, LizardTailRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.MOOSE, MooseRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.CAPYBARA, CapybaraRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.MAMMOTH, MammothRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.FENNEC_FOX, FennecFoxRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.TORTOISE, TortoiseRenderer::new);
        ClientPlatformHelper.registerEntityRenderers(NaturalistEntityTypes.DUCK, DuckRenderer::new);
    }
}
