package com.starfish_studios.naturalist.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.starfish_studios.naturalist.core.registry.NaturalistRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * A "virtual" recipe used by the bug net: it isn't placed in any crafting grid.
 * It maps an entity type to the item dropped when that entity is caught with the net.
 * BugNetItem queries these recipes on interaction (see BugNetItem#interactLivingEntity).
 *
 * Behavioural port of the 1.21.1 version to the 1.21.11 Recipe API:
 *   - getResultItem(...) / canCraftInDimensions(...) were removed from Recipe
 *   - placementInfo() and recipeBookCategory() are now required
 * Since this recipe never appears in a crafting grid or the recipe book,
 * placement is NOT_PLACEABLE and we use the generic CRAFTING_MISC category.
 */
public record BugNetInteractionRecipe(EntityType<?> entityType, ItemStack dropStack) implements Recipe<RecipeInput> {

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        // Not matched via crafting input; BugNetItem filters by entityType directly.
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput input, HolderLookup.@NotNull Provider registries) {
        return dropStack.copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return NaturalistRecipes.BUG_NET_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return NaturalistRecipes.BUG_NET;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        // Never placed in a crafting grid.
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        // Not shown in the recipe book; any valid category satisfies the contract.
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static class Serializer implements RecipeSerializer<BugNetInteractionRecipe> {
        public static final MapCodec<BugNetInteractionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(BugNetInteractionRecipe::entityType),
                ItemStack.CODEC.fieldOf("result").forGetter(BugNetInteractionRecipe::dropStack)
            ).apply(instance, BugNetInteractionRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BugNetInteractionRecipe> STREAM_CODEC =
            StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeIdentifier(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.entityType()));
                    ItemStack.STREAM_CODEC.encode(buf, recipe.dropStack());
                },
                buf -> {
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(buf.readIdentifier());
                    ItemStack dropStack = ItemStack.STREAM_CODEC.decode(buf);
                    return new BugNetInteractionRecipe(entityType, dropStack);
                }
            );

        @Override
        public @NotNull MapCodec<BugNetInteractionRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BugNetInteractionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
