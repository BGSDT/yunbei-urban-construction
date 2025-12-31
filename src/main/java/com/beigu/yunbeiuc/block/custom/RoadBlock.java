package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.block.custom.data.DirectionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RoadBlock extends Block {
    public static final EnumProperty<DirectionType> DIRECTION_TYPE = EnumProperty.of("direction", DirectionType.class);

    public RoadBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(DIRECTION_TYPE, DirectionType.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION_TYPE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction playerFacing = ctx.getHorizontalPlayerFacing();
        DirectionType directionType = switch (playerFacing) {
            case NORTH -> DirectionType.NORTH;
            case EAST -> DirectionType.EAST;
            case SOUTH -> DirectionType.SOUTH;
            case WEST -> DirectionType.WEST;
            default -> DirectionType.NORTH; // 默认值
        };

        return this.getDefaultState().with(DIRECTION_TYPE, directionType);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();
        if (item == Items.STICK && !world.isClient) {
            DirectionType current = state.get(DIRECTION_TYPE);
            DirectionType next = current.next();
            world.setBlockState(pos, state.with(DIRECTION_TYPE, next));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}