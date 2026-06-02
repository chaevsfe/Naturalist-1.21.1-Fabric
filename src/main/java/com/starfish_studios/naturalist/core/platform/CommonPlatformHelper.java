package com.starfish_studios.naturalist.core.platform;

import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.common.item.fabric.*;
import com.starfish_studios.naturalist.core.registry.NaturalistMenus;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CommonPlatformHelper {

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(Naturalist.MOD_ID, name);
    }

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, id(name));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, id(name));
    }

    private static ResourceKey<EntityType<?>> entityKey(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, id(name));
    }

    public static <T extends Block> Supplier<T> registerBlock(@NotNull String name, Supplier<T> block) {
        T registry = Registry.register(BuiltInRegistries.BLOCK, id(name), block.get());
        return () -> registry;
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String name, @NotNull Supplier<BlockEntityType<T>> factory) {
        BlockEntityType<T> registry = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(name), factory.get());
        return () -> registry;
    }

    public static <T extends Item> Supplier<T> registerItem(String name, @NotNull Supplier<T> item) {
        T registry = Registry.register(BuiltInRegistries.ITEM, id(name), item.get());
        return () -> registry;
    }

    public static <T extends Mob> Supplier<SpawnEggItem> registerSpawnEggItem(@NotNull String name, Supplier<EntityType<T>> entityType, int backgroundColor, int highlightColor) {
        // In 1.21.11, SpawnEggItem takes only Properties with .spawnEgg(entityType).
        // Background/highlight colors are now data-driven via item model JSON.
        return registerItem(name, () -> new SpawnEggItem(new Item.Properties().setId(itemKey(name)).spawnEgg(entityType.get())));
    }

    public static Supplier<Item> registerNoFluidMobBucketItem(String name, Supplier<? extends EntityType<?>> entitySupplier, @NotNull Supplier<? extends Fluid> fluidSupplier, Supplier<? extends SoundEvent> soundSupplier) {
        return registerItem(name, () -> new NoFluidMobBucketItem(entitySupplier.get(), fluidSupplier.get(), soundSupplier.get(), new Item.Properties().stacksTo(1).setId(itemKey(name))));
    }

    public static Supplier<Item> registerNoFluidMobBucketItem(String name, @NotNull Supplier<? extends EntityType<?>> entitySupplier, Supplier<? extends Fluid> fluidSupplier, @NotNull Supplier<? extends SoundEvent> soundSupplier, int color) {
        return registerItem(name, () -> new NoFluidMobBucketWithVariantsItem(entitySupplier, fluidSupplier.get(), soundSupplier.get(), color, new Item.Properties().stacksTo(1).setId(itemKey(name))));
    }

    @SuppressWarnings("unchecked")
    public static @NotNull Supplier<Item> registerMobBucketItem(@NotNull String name, Supplier<? extends EntityType<?>> entitySupplier, @NotNull Supplier<? extends Fluid> fluidSupplier, Supplier<? extends SoundEvent> soundSupplier) {
        return registerItem(name, () -> new MobBucketItem((EntityType<? extends Mob>) entitySupplier.get(), fluidSupplier.get(), soundSupplier.get(), new Item.Properties().stacksTo(1).setId(itemKey(name))));
    }

    public static @NotNull Supplier<Item> registerCaughtMobItem(String name, Supplier<? extends EntityType<?>> entitySupplier, Supplier<? extends Fluid> fluidSupplier, Supplier<? extends SoundEvent> soundSupplier) {
        return registerItem(name, () -> new CaughtMobItem(entitySupplier.get(), fluidSupplier.get(), soundSupplier.get(), new Item.Properties().stacksTo(1).setId(itemKey(name))));
    }

    public static Supplier<Item> registerCaughtMobItem(String name, Supplier<? extends EntityType<?>> entitySupplier, Supplier<? extends Fluid> fluidSupplier, Supplier<? extends SoundEvent> soundSupplier, int variantAmount) {
        return registerItem(name, () -> new CaughtMobWithVariantsItem(entitySupplier.get(), fluidSupplier.get(), soundSupplier.get(), variantAmount, new Item.Properties().stacksTo(1).setId(itemKey(name))));
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        T registry = Registry.register(BuiltInRegistries.SOUND_EVENT, id(name), soundEvent.get());
        return () -> registry;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> @NotNull Supplier<EntityType<T>> registerEntityType(@NotNull String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange) {
        ResourceKey<EntityType<?>> key = entityKey(name);
        EntityType<T> registry = Registry.register(BuiltInRegistries.ENTITY_TYPE, id(name),
                EntityType.Builder.of(factory, category).sized(width, height).clientTrackingRange(clientTrackingRange).build(key));
        return () -> registry;
    }

    public static <T extends AbstractContainerMenu> @NotNull Supplier<MenuType<T>> registerMenuType(String name, @NotNull Supplier<MenuType<T>> menu) {
        var registry = Registry.register(BuiltInRegistries.MENU, id(name), menu.get());
        return () -> registry;
    }

    public static void openMenu(ServerPlayer player, MenuProvider provider) {
        player.openMenu(provider);
    }

    public static <T extends Potion> Supplier<T> registerPotion(String name, Supplier<T> potion) {
        T registry = Registry.register(BuiltInRegistries.POTION, id(name), potion.get());
        return () -> registry;
    }

    public static <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacementType placementType, Heightmap.Types heightMapType, SpawnPlacements.SpawnPredicate<T> decoratorPredicate) {
        SpawnPlacements.register(entityType, placementType, heightMapType, decoratorPredicate);
    }

    public static void registerCompostable(float chance, ItemLike item) {
        CompostingChanceRegistry.INSTANCE.add(item, chance);
    }

    public static void registerRecipes(String name, Supplier<RecipeType<?>> type, Supplier<RecipeSerializer<?>> serializer) {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id(name), type.get());
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(name), serializer.get());
    }

    public static TagKey<Item> getShearsTag() {
        return ConventionalItemTags.SHEARS_TOOLS;
    }

    /**
     * Helper to get an item key for use in Item.Properties.setId() from registration code.
     */
    public static ResourceKey<Item> getItemKey(String name) {
        return itemKey(name);
    }

    /**
     * Helper to get a block key for use in BlockBehaviour.Properties.setId() from registration code.
     */
    public static ResourceKey<Block> getBlockKey(String name) {
        return blockKey(name);
    }
}
