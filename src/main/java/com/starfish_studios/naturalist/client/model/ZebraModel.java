package com.starfish_studios.naturalist.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import org.jetbrains.annotations.NotNull;

@Environment(value= EnvType.CLIENT)
public class ZebraModel extends HorseModel {
    private final @NotNull ModelPart leftChest;
    private final @NotNull ModelPart rightChest;

    public ZebraModel(ModelPart root) {
        super(root);
        this.leftChest = this.body.getChild("left_chest");
        this.rightChest = this.body.getChild("right_chest");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = HorseModel.createBodyMesh(CubeDeformation.NONE);
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition partDefinition2 = partDefinition.getChild("body");
        CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(26, 21).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        partDefinition2.addOrReplaceChild("left_chest", cubeListBuilder, PartPose.offsetAndRotation(6.0f, -8.0f, 0.0f, 0.0f, -1.5707964f, 0.0f));
        partDefinition2.addOrReplaceChild("right_chest", cubeListBuilder, PartPose.offsetAndRotation(-6.0f, -8.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(EquineRenderState renderState) {
        super.setupAnim(renderState);
        // Note: In 1.21.11 RenderState architecture, hasChest() would need to come from render state data
        // For now, always hide chests - this can be improved later with extractRenderState
        this.leftChest.visible = false;
        this.rightChest.visible = false;
    }
}
