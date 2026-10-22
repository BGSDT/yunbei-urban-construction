package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.api.mapper.ItemCompat;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class MirrorWand extends ItemCompat {
    public MirrorWand(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverTextCompat(ItemStack stack, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Text.translatable("item.yunbeiuc.mirror_wand.tooltip"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return mirrorAt(context.getLevel(), context.getClickedPos(), context.getClickedFace(), context.getPlayer())
            ? InteractionResult.SUCCESS
            : InteractionResult.PASS;
    }

    private static boolean mirrorAt(Level level, BlockPos pos, Direction face, Player player) {
        final BlockState state = level.getBlockState(pos);
        final Mirror primary = mirrorOf(face, player);
        BlockState mirrored = mirrored(state, primary);
        if (mirrored.equals(state)) {
            mirrored = mirrored(state, primary == Mirror.FRONT_BACK ? Mirror.LEFT_RIGHT : Mirror.FRONT_BACK);
        }
        if (mirrored.equals(state)) {
            return false;
        }
        level.setBlock(pos, mirrored, VersionServices.blocks().updateAll());
        return true;
    }

    private static BlockState mirrored(BlockState state, Mirror mirror) {
        final BlockState result = state.mirror(mirror);
        if (!result.equals(state)) {
            return result;
        }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            final Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            final Direction reflected = mirror.mirror(facing);
            if (reflected != facing) {
                return state.setValue(BlockStateProperties.HORIZONTAL_FACING, reflected);
            }
        }
        return state;
    }

    private static Mirror mirrorOf(Direction face, Player player) {
        final Direction.Axis axis;
        if (face.getAxis().isVertical()) {
            axis = player == null ? Direction.Axis.Z : player.getDirection().getAxis();
        } else {
            axis = face.getAxis();
        }
        return axis == Direction.Axis.X ? Mirror.FRONT_BACK : Mirror.LEFT_RIGHT;
    }
}
