package com.beigu.yunbeiuc.autoassign;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 标准十字路口布局：4 个方向，每个方向 2 个普通红绿灯 + 2 个人行道红绿灯，共 4 相位。
 * 相位约定（索引从0开始，对应现实中的相位1~4）：
 * 0=南北直行（相位1），1=南北左转（相位2），2=东西直行（相位3），3=东西左转（相位4）。
 * 每个方向的人行道红绿灯相位与同方向直行普通红绿灯相位一致。
 * 左/右以驶入路口车辆的视角判断（例如北侧车辆面朝南，左手为东侧）。
 */
public class FourDirectionStraightLeftPattern implements TrafficLightsLayoutPattern {

    private static final int NS_STRAIGHT_PHASE = 0;
    private static final int NS_LEFT_PHASE = 1;
    private static final int EW_STRAIGHT_PHASE = 2;
    private static final int EW_LEFT_PHASE = 3;

    private enum Direction { NORTH, SOUTH, EAST, WEST }

    @Override
    public boolean tryApply(World world, List<TrafficLightsBlockEntity> members, List<BlockPos> positions, int phaseCount, @Nullable PlayerEntity notifyPlayer) {
        if (phaseCount != 4 || members.size() != 16 || positions.size() != members.size()) {
            return false;
        }

        Set<Block> normalBlocks = Set.of(
                MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_VERTICAL.get(),
                MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_VERTICAL.get(),
                MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_HORIZONTAL.get(),
                MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_HORIZONTAL.get(),
                MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get(),
                MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()
        );
        Set<Block> pavementBlocks = Set.of(
                MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get(),
                MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
        );

        List<Member> normal = new ArrayList<>();
        List<Member> pavement = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            TrafficLightsBlockEntity tl = members.get(i);
            BlockPos pos = positions.get(i);
            Block block = tl.getCachedState().getBlock();
            if (normalBlocks.contains(block)) {
                normal.add(new Member(tl, pos));
            } else if (pavementBlocks.contains(block)) {
                pavement.add(new Member(tl, pos));
            } else {
                return false;
            }
        }

        if (normal.size() != 8 || pavement.size() != 8) {
            return false;
        }

        double centroidX = 0, centroidZ = 0;
        for (BlockPos pos : positions) {
            centroidX += pos.getX();
            centroidZ += pos.getZ();
        }
        centroidX /= positions.size();
        centroidZ /= positions.size();

        Map<Direction, List<Member>> normalByDirection = new EnumMap<>(Direction.class);
        Map<Direction, List<Member>> pavementByDirection = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            normalByDirection.put(dir, new ArrayList<>());
            pavementByDirection.put(dir, new ArrayList<>());
        }

        for (Member m : normal) {
            normalByDirection.get(classify(m.pos, centroidX, centroidZ)).add(m);
        }
        for (Member m : pavement) {
            pavementByDirection.get(classify(m.pos, centroidX, centroidZ)).add(m);
        }

        for (Direction dir : Direction.values()) {
            if (normalByDirection.get(dir).size() != 2 || pavementByDirection.get(dir).size() != 2) {
                return false;
            }
        }

        for (Direction dir : Direction.values()) {
            List<Member> pair = normalByDirection.get(dir);
            Member a = pair.get(0);
            Member b = pair.get(1);

            int straightPhase;
            int leftPhase;
            Member leftMember;
            Member straightMember;

            switch (dir) {
                case NORTH -> {
                    // 车辆从北向南驶入，面朝南：左手为东(+X)，右手为西(-X)
                    straightPhase = NS_STRAIGHT_PHASE;
                    leftPhase = NS_LEFT_PHASE;
                    if (a.pos.getX() >= b.pos.getX()) {
                        leftMember = b;
                        straightMember = a;
                    } else {
                        leftMember = a;
                        straightMember = b;
                    }
                }
                case SOUTH -> {
                    // 车辆从南向北驶入，面朝北：左手为西(-X)，右手为东(+X)
                    straightPhase = NS_STRAIGHT_PHASE;
                    leftPhase = NS_LEFT_PHASE;
                    if (a.pos.getX() <= b.pos.getX()) {
                        leftMember = b;
                        straightMember = a;
                    } else {
                        leftMember = a;
                        straightMember = b;
                    }
                }
                case EAST -> {
                    // 车辆从东向西驶入，面朝西：左手为南(+Z)，右手为北(-Z)
                    straightPhase = EW_STRAIGHT_PHASE;
                    leftPhase = EW_LEFT_PHASE;
                    if (a.pos.getZ() >= b.pos.getZ()) {
                        leftMember = b;
                        straightMember = a;
                    } else {
                        leftMember = a;
                        straightMember = b;
                    }
                }
                default -> {
                    // WEST：车辆从西向东驶入，面朝东：左手为北(-Z)，右手为南(+Z)
                    straightPhase = EW_STRAIGHT_PHASE;
                    leftPhase = EW_LEFT_PHASE;
                    if (a.pos.getZ() <= b.pos.getZ()) {
                        leftMember = b;
                        straightMember = a;
                    } else {
                        leftMember = a;
                        straightMember = b;
                    }
                }
            }

            leftMember.entity.setDirectionType(TrafficLightsBlockEntity.DirectionType.LEFT_TURN);
            leftMember.entity.setPhaseIndices(List.of(leftPhase), null);
            straightMember.entity.setDirectionType(TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE);
            straightMember.entity.setPhaseIndices(List.of(straightPhase), null);

            for (Member p : pavementByDirection.get(dir)) {
                p.entity.setDirectionType(TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE);
                p.entity.setPhaseIndices(List.of(straightPhase), null);
            }
        }

        if (notifyPlayer != null && !world.isClient()) {
            notifyPlayer.sendMessage(Text.literal("§a已自动识别为十字路口标准布局，相位与图案已自动分配"), false);
        }

        return true;
    }

    private static Direction classify(BlockPos pos, double centroidX, double centroidZ) {
        double dx = pos.getX() - centroidX;
        double dz = pos.getZ() - centroidZ;
        if (Math.abs(dz) >= Math.abs(dx)) {
            return dz < 0 ? Direction.NORTH : Direction.SOUTH;
        } else {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        }
    }

    private static class Member {
        final TrafficLightsBlockEntity entity;
        final BlockPos pos;

        Member(TrafficLightsBlockEntity entity, BlockPos pos) {
            this.entity = entity;
            this.pos = pos;
        }
    }
}
