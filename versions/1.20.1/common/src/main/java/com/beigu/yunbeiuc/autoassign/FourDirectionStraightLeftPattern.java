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
import java.util.List;
import java.util.Set;

/**
 * 标准十字路口布局：4 个方向，每个方向 2 个普通红绿灯 + 1 个人行道红绿灯，共 4 相位。
 * 相位13（索引0、2）分配为直行，相位24（索引1、3）分配为左转；人行道红绿灯默认直行。
 */
public class FourDirectionStraightLeftPattern implements TrafficLightsLayoutPattern {

    private static final List<Integer> STRAIGHT_PHASES = List.of(0, 2);
    private static final List<Integer> LEFT_TURN_PHASES = List.of(1, 3);

    @Override
    public boolean tryApply(World world, List<TrafficLightsBlockEntity> members, List<BlockPos> positions, int phaseCount, @Nullable PlayerEntity notifyPlayer) {
        if (phaseCount != 4 || members.size() != 12 || positions.size() != members.size()) {
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

        if (normal.size() != 8 || pavement.size() != 4) {
            return false;
        }

        double centroidX = 0, centroidZ = 0;
        for (BlockPos pos : positions) {
            centroidX += pos.getX();
            centroidZ += pos.getZ();
        }
        centroidX /= positions.size();
        centroidZ /= positions.size();

        List<List<Member>> buckets = new ArrayList<>();
        for (int i = 0; i < 4; i++) buckets.add(new ArrayList<>());

        List<Member> all = new ArrayList<>();
        all.addAll(normal);
        all.addAll(pavement);

        for (Member m : all) {
            double angle = Math.atan2(m.pos.getZ() - centroidZ, m.pos.getX() - centroidX);
            int bucketIndex = (int) Math.floor(((angle + Math.PI) / (Math.PI / 2))) % 4;
            if (bucketIndex < 0) bucketIndex += 4;
            buckets.get(bucketIndex).add(m);
        }

        List<Member[]> normalPairs = new ArrayList<>();
        List<Member> pavementSingles = new ArrayList<>();

        for (List<Member> bucket : buckets) {
            List<Member> bucketNormal = new ArrayList<>();
            List<Member> bucketPavement = new ArrayList<>();
            for (Member m : bucket) {
                if (normalBlocks.contains(m.entity.getCachedState().getBlock())) {
                    bucketNormal.add(m);
                } else {
                    bucketPavement.add(m);
                }
            }
            if (bucketNormal.size() != 2 || bucketPavement.size() != 1) {
                return false;
            }
            normalPairs.add(new Member[]{bucketNormal.get(0), bucketNormal.get(1)});
            pavementSingles.add(bucketPavement.get(0));
        }

        double finalCentroidX = centroidX;
        double finalCentroidZ = centroidZ;

        for (Member[] pair : normalPairs) {
            double bucketCentroidX = (pair[0].pos.getX() + pair[1].pos.getX()) / 2.0;
            double bucketCentroidZ = (pair[0].pos.getZ() + pair[1].pos.getZ()) / 2.0;

            double axisX = bucketCentroidX - finalCentroidX;
            double axisZ = bucketCentroidZ - finalCentroidZ;
            double leftX = axisZ;
            double leftZ = -axisX;

            double v0x = pair[0].pos.getX() - bucketCentroidX;
            double v0z = pair[0].pos.getZ() - bucketCentroidZ;
            double v1x = pair[1].pos.getX() - bucketCentroidX;
            double v1z = pair[1].pos.getZ() - bucketCentroidZ;

            double dot0 = v0x * leftX + v0z * leftZ;
            double dot1 = v1x * leftX + v1z * leftZ;

            pair[0].leftTurn = dot0 > dot1;
            pair[1].leftTurn = dot1 >= dot0;
        }

        for (Member m : pavementSingles) {
            m.entity.setDirectionType(TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE);
            m.entity.setPhaseIndices(STRAIGHT_PHASES, null);
        }
        for (Member[] pair : normalPairs) {
            for (Member m : pair) {
                if (m.leftTurn) {
                    m.entity.setDirectionType(TrafficLightsBlockEntity.DirectionType.LEFT_TURN);
                    m.entity.setPhaseIndices(LEFT_TURN_PHASES, null);
                } else {
                    m.entity.setDirectionType(TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE);
                    m.entity.setPhaseIndices(STRAIGHT_PHASES, null);
                }
            }
        }

        if (notifyPlayer != null && !world.isClient()) {
            notifyPlayer.sendMessage(Text.literal("§a已自动识别为十字路口标准布局，相位与图案已自动分配"), false);
        }

        return true;
    }

    private static class Member {
        final TrafficLightsBlockEntity entity;
        final BlockPos pos;
        boolean leftTurn;

        Member(TrafficLightsBlockEntity entity, BlockPos pos) {
            this.entity = entity;
            this.pos = pos;
        }
    }
}
