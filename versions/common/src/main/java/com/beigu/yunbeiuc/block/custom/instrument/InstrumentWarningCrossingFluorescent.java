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

/**
 * 道口警示荧光（行人优先）。
 *
 * <p>继承 {@link TrafficLightsBlock}，因此天然支持红绿灯链接：
 * <ul>
 *   <li>连接魔杖（{@code link_wand}）可以把它和红绿灯连进同一个链接组——魔杖判定的是
 *       「方块实体是不是 {@code TrafficLightsBlockEntity}」，本方块用的正是这个方块实体；</li>
 *   <li>普通魔杖右键会打开与红绿灯一致的界面（未分组 → 静态状态；已分组未设时间 → 时间设置；
 *       已设时间 → 显示界面），相位分配、时序、图案预设全部复用；</li>
 *   <li>组内相位变化时 {@code TrafficLightsBlockEntity#updateLightState()} 会把灯色写进
 *       {@link #LIGHT_STATE}，方块状态据此在「点亮」与「熄灭」两套模型间切换。</li>
 * </ul>
 *
 * <p>与普通红绿灯的区别只有两点：模型是整根立杆（不是挂在杆上的灯头），
 * 以及「点亮」只认自己那一种灯色——红色警示在红灯相位亮，绿色警示在绿灯相位亮，
 * 于是同一组里的红/绿两块会随相位交替点亮。
 */
public class InstrumentWarningCrossingFluorescent extends TrafficLightsBlock {

    // 模型包围盒：x[3,13] y[-12.4,30.5] z[4.5,11.88]（北向基准）
    private static final VoxelShape SHAPE_N = Block.box(3.0, -12.4, 4.5, 13.0, 30.5, 11.9);
    private static final VoxelShape SHAPE_S = Block.box(3.0, -12.4, 4.1, 13.0, 30.5, 11.5);
    private static final VoxelShape SHAPE_EW = Block.box(4.1, -12.4, 3.0, 11.5, 30.5, 13.0);

    /** 本方块「点亮」时对应的灯色：红色警示在红灯相位亮，绿色警示在绿灯相位亮。 */
    private final LightState litState;

    public InstrumentWarningCrossingFluorescent(BlockBehaviour.Properties properties, LightState litState) {
        super(properties);
        this.litState = litState;
        // 未接入链接组时保持常亮：默认灯色即自身灯色
        registerDefaultState(defaultBlockState().setValue(LIGHT_STATE, litState));
    }

    /** 点亮本方块所需的灯色。 */
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

    /**
     * 使用本方块专属的方块实体类型。
     *
     * <p>必须显式指定类型：{@code TrafficLightsBlockEntity} 的默认构造函数把类型写死成红绿灯那一个，
     * 若走默认构造，本方块会顶着「红绿灯」的类型——存档重载时类型校验不过（方块实体被丢弃、链接组丢失），
     * 客户端还会按红绿灯去找渲染器，在本方块上画箭头/圆点图案。
     */
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
