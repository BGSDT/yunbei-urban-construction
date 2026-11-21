package com.beigu.yunbeiuc.block.custom.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SignImageBlock extends CustomSignTypeBlock {
    private final String imageId;

    public SignImageBlock(BlockBehaviour.Properties properties, String imageId) {
        super(properties);
        this.imageId = imageId;
    }

    public String imageId() {
        return imageId;
    }

    public SignImageDefinitions.Def definition() {
        return SignImageDefinitions.get(imageId);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            SignImageMigration.ensure(level, pos);
        }
    }

    @Override
    protected BlockState updateShapeCompat(BlockState state, Direction direction, BlockState neighborState,
                                           LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        BlockState next = super.updateShapeCompat(state, direction, neighborState, world, pos, neighborPos);
        if (next != state && !world.isClientSide() && world instanceof Level level) {
            SignImageMigration.ensure(level, pos, next);
        }
        return next;
    }
}
