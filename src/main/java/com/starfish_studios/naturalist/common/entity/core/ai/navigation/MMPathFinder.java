package com.starfish_studios.naturalist.common.entity.core.ai.navigation;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MMPathFinder extends PathFinder {
    public MMPathFinder(NodeEvaluator processor, int maxVisitedNodes) {
        super(processor, maxVisitedNodes);
    }

    @Override
    public Path findPath(@NotNull PathNavigationRegion regionIn, @NotNull Mob mob, @NotNull Set<BlockPos> targetPositions, float maxRange, int accuracy, float searchDepthMultiplier) {
        // Path is now final in 1.21.11, so we can't subclass it.
        // The original PatchedPath only overrode getEntityPosAtNode to adjust for entity width,
        // but the vanilla implementation already accounts for entity width, so we just return the path directly.
        return super.findPath(regionIn, mob, targetPositions, maxRange, accuracy, searchDepthMultiplier);
    }
}
