package com.starfish_studios.naturalist.common.item;

import com.starfish_studios.naturalist.common.recipe.BugNetInteractionRecipe;
import com.starfish_studios.naturalist.core.registry.NaturalistRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BugNetItem extends Item {
    public BugNetItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {
        // Recipe logic is server-authoritative; client just defers to the server result.
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
        }

        // 1.21.11: RecipeManager#getAllRecipesFor(type) was removed. Iterate all loaded
        // recipes, keep ours (BUG_NET type), and match the targeted entity.
        Optional<BugNetInteractionRecipe> match = server.getRecipeManager().getRecipes().stream()
                .map(holder -> holder.value())
                .filter(recipe -> recipe.getType() == NaturalistRecipes.BUG_NET)
                .map(recipe -> (BugNetInteractionRecipe) recipe)
                .filter(recipe -> recipe.entityType() == interactionTarget.getType())
                .findFirst();

        if (match.isPresent()) {
            ItemStack dropItem = match.get().dropStack().copy();
            Containers.dropItemStack(player.level(), interactionTarget.getX(), interactionTarget.getY(), interactionTarget.getZ(), dropItem);
            interactionTarget.discard();
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }
}
