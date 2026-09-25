package com.beigu.yunbeiuc.block.custom.pole;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.entity.FlagBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.FlagSelectionScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoadPoleFlag extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<PoleType> POLE_TYPE = EnumProperty.create("pole_type", PoleType.class);

    private static final VoxelShape SHAPE_N = Shapes.join(Block.box(5, 0, 5, 11, 16, 11), Block.box(-9.75, -5.25, 7.75, 25.75, 21.25, 8.25), BooleanOp.OR);
    private static final VoxelShape SHAPE_S = Shapes.join(Block.box(5, 0, 5, 11, 16, 11), Block.box(-9.75, -5.25, 7.75, 25.75, 21.25, 8.25), BooleanOp.OR);
    private static final VoxelShape SHAPE_E = Shapes.join(Block.box(5, 0, 5, 11, 16, 11), Block.box(7.75, -5.25, -9.75, 8.25, 21.25, 25.75), BooleanOp.OR);
    private static final VoxelShape SHAPE_W = Shapes.join(Block.box(5, 0, 5, 11, 16, 11), Block.box(7.75, -5.25, -9.75, 8.25, 21.25, 25.75), BooleanOp.OR);

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.road_pole_flag.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    public RoadPoleFlag(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POLE_TYPE, PoleType.ROAD_POLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POLE_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        PoleType poleType = getPoleTypeForPosition(world, pos);

        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(POLE_TYPE, poleType);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                                LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            PoleType correctType = getPoleTypeForPosition((Level) world, pos);
            if (state.getValue(POLE_TYPE) != correctType) {
                return state.setValue(POLE_TYPE, correctType);
            }
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    private PoleType getPoleTypeForPosition(Level world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.below()).getBlock();
        if (blockBelow == MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS ||
                blockBelow == MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS_SLAB ||
                blockBelow == MunicipalBlocks.ROAD_POLE_LIGHT_LONGITUDINAL) {
            return PoleType.ROAD_POLE_LIGHT;
        }
        return PoleType.ROAD_POLE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = player.getItemInHand(hand).getItem();
        if (item == ModItems.WAND.get()) {
            if (world .isClientSide) {
                openFlagScreen(pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openFlagScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new FlagSelectionScreen(com.beigu.yunbeiuc.api.text.Text.literal("选择旗帜"), pos));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlagBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    public enum PoleType implements StringRepresentable {
        ROAD_POLE("road_pole"),
        ROAD_POLE_LIGHT("road_pole_light");

        private final String name;

        PoleType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
