package com.beigu.yunbeiuc.block.custom.instrument;

import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class InstrumentWarningCrossingFluorescent extends TrafficLightsBlock {

    private static final VoxelShape SHAPE_N = Block.box(3.0, -12.4, 4.5, 13.0, 30.5, 11.9);
    private static final VoxelShape SHAPE_S = Block.box(3.0, -12.4, 4.1, 13.0, 30.5, 11.5);
    private static final VoxelShape SHAPE_EW = Block.box(4.1, -12.4, 3.0, 11.5, 30.5, 13.0);

    private final LightState litState;

    public InstrumentWarningCrossingFluorescent(BlockBehaviour.Properties properties, LightState litState) {
        super(properties);
        this.litState = litState;
        registerDefaultState(defaultBlockState().setValue(LIGHT_STATE, litState));
    }

    public LightState getLitState() {
        return litState;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST, WEST -> SHAPE_EW;
            case SOUTH -> SHAPE_S;
            default -> SHAPE_N;
        };
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new TrafficLightsBlockEntity(
                ModBlockEntities.INSTRUMENT_WARNING_CROSSING_FLUORESCENT_ENTITY.get(), pos, state);
    }

    @Override
    public void appendHoverTextCompat(ItemStack stack, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Text.translatable("block.yunbeiuc.instrument_warning_crossing_fluorescent.tooltip"));
    }
}
