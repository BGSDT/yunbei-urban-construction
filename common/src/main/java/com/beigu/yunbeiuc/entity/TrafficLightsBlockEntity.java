package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.TrafficLightsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrafficLightsBlockEntity extends BlockEntity {
    private int phaseIndex = -1;
    private String groupId = null;
    private List<BlockPos> groupPositions = new ArrayList<>();

    private int[] phaseTimes = null;
    private int phaseCount = 0;

    private int currentTick = 0;
    private int currentActivePhase = 0;
    private boolean cycleActive = false;

    private static final int YELLOW_DURATION = 3 * 20;
    private static final int FLASH_DURATION = 3 * 20;
    private static final int FLASH_INTERVAL = 10;

    public TrafficLightsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAFFIC_LIGHTS_BLOCK_ENTITY.get(), pos, state);
    }

    public void tick() {
        // 安全检查：避免除以零
        if (world == null || world.isClient() || !cycleActive || groupId == null || phaseTimes == null || phaseCount <= 0) {
            return;
        }

        currentTick++;

        int totalTicks = phaseTimes[currentActivePhase] * 20;

        if (currentTick >= totalTicks) {
            currentActivePhase = (currentActivePhase + 1) % phaseCount;
            currentTick = 0;
            updateGroupPhase();
        }

        updateLightState();
    }

    private void updateLightState() {
        if (world == null || world.isClient() || phaseTimes == null || phaseCount <= 0) return;

        BlockState currentState = getCachedState();
        TrafficLightsBlock.LightState lightState;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int yellowStartTick = totalTicks - YELLOW_DURATION;
        int flashStartTick = yellowStartTick - FLASH_DURATION;

        if (phaseIndex == currentActivePhase) {
            if (currentTick < flashStartTick) {
                lightState = TrafficLightsBlock.LightState.GREEN;
            } else if (currentTick < yellowStartTick) {
                int flashTick = currentTick - flashStartTick;
                int flashPhase = flashTick / FLASH_INTERVAL;
                lightState = (flashPhase % 2 == 0) ? TrafficLightsBlock.LightState.GRAY : TrafficLightsBlock.LightState.GREEN;
            } else {
                lightState = TrafficLightsBlock.LightState.YELLOW;
            }
        } else {
            lightState = TrafficLightsBlock.LightState.RED;
        }

        if (currentState.get(TrafficLightsBlock.LIGHT_STATE) != lightState) {
            world.setBlockState(pos, currentState.with(TrafficLightsBlock.LIGHT_STATE, lightState), Block.NOTIFY_ALL);
        }
    }

    private void updateGroupPhase() {
        if (groupId == null || groupPositions.isEmpty() || world == null || world.isClient()) return;

        for (BlockPos pos : groupPositions) {
            if (pos.equals(this.pos)) continue;
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TrafficLightsBlockEntity tl) {
                tl.currentActivePhase = this.currentActivePhase;
                tl.currentTick = this.currentTick;
                tl.updateLightState();
            } else {
                unloadGroup();
                return;
            }
        }
    }

    public boolean setPhaseIndex(int index, PlayerEntity player) {
        if (phaseTimes == null || phaseCount <= 0) {
            if (player != null && !world.isClient()) {
                player.sendMessage(Text.literal("§c请先使用命令设置时间！"), true);
            }
            return false;
        }
        if (groupId == null) {
            if (player != null && !world.isClient()) {
                player.sendMessage(Text.literal("§c此红绿灯未链接到任何组！"), true);
            }
            return false;
        }
        if (index < 0 || index >= phaseCount) {
            if (player != null && !world.isClient()) {
                player.sendMessage(Text.literal("§c无效的相位索引！范围：1-" + phaseCount), true);
            }
            return false;
        }
        this.phaseIndex = index;
        if (player != null && !world.isClient()) {
            player.sendMessage(Text.literal("§a相位已设置为 §6" + (index + 1) + " §7(共" + phaseCount + "个相位)"), true);
        }
        markDirtyAndUpdate();
        return true;
    }

    public void startCycle() {
        if (phaseTimes == null || phaseCount <= 0) return;
        this.cycleActive = true;
        this.currentTick = 0;
        this.currentActivePhase = 0;
        updateLightState();
        markDirtyAndUpdate();
    }

    public void stopCycle() {
        this.cycleActive = false;
        markDirtyAndUpdate();
    }

    public void unloadGroup() {
        if (groupId == null) return;

        stopCycle();

        if (world != null && !world.isClient() && groupPositions != null) {
            for (BlockPos pos : groupPositions) {
                if (pos.equals(this.pos)) continue;
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof TrafficLightsBlockEntity tl && tl != this) {
                    tl.groupId = null;
                    tl.groupPositions.clear();
                    tl.phaseTimes = null;
                    tl.phaseCount = 0;
                    tl.phaseIndex = -1;
                    tl.stopCycle();
                    tl.markDirtyAndUpdate();
                }
            }
        }

        groupId = null;
        groupPositions.clear();
        phaseTimes = null;
        phaseCount = 0;
        phaseIndex = -1;
        markDirtyAndUpdate();
    }

    public void setTimings(int phaseCount, int[] timings) {
        this.phaseCount = phaseCount;
        this.phaseTimes = timings;
        this.phaseIndex = -1;
        startCycle();
        markDirtyAndUpdate();
    }

    public void setGroup(String groupId, List<BlockPos> positions) {
        this.groupId = groupId;
        this.groupPositions = new ArrayList<>(positions);
        markDirtyAndUpdate();
    }

    public boolean hasTimings() {
        return phaseTimes != null && phaseCount > 0;
    }

    public boolean isInGroup() {
        return groupId != null;
    }

    public int getPhaseCount() {
        return phaseCount;
    }

    public int getPhaseIndex() {
        return phaseIndex;
    }

    public int[] getPhaseTimes() {
        return phaseTimes;
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.phaseIndex = nbt.getInt("phaseIndex");
        this.groupId = nbt.contains("groupId") ? nbt.getString("groupId") : null;
        this.phaseCount = nbt.getInt("phaseCount");

        if (nbt.contains("phaseTimes")) {
            this.phaseTimes = nbt.getIntArray("phaseTimes");
            if (this.phaseTimes.length == 0) {
                this.phaseTimes = null;
            }
        }

        this.currentActivePhase = nbt.getInt("currentActivePhase");
        this.currentTick = nbt.getInt("currentTick");
        this.cycleActive = nbt.getBoolean("cycleActive");

        if (nbt.contains("groupPositions")) {
            NbtCompound positionsTag = nbt.getCompound("groupPositions");
            int size = positionsTag.getInt("size");
            groupPositions.clear();
            for (int i = 0; i < size; i++) {
                BlockPos pos = NbtHelper.toBlockPos(positionsTag.getCompound("pos" + i));
                groupPositions.add(pos);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("phaseIndex", this.phaseIndex);

        if (groupId != null) {
            nbt.putString("groupId", groupId);
        }

        nbt.putInt("phaseCount", phaseCount);

        if (phaseTimes != null) {
            nbt.putIntArray("phaseTimes", phaseTimes);
        }

        nbt.putInt("currentActivePhase", currentActivePhase);
        nbt.putInt("currentTick", currentTick);
        nbt.putBoolean("cycleActive", cycleActive);

        if (groupPositions != null && !groupPositions.isEmpty()) {
            NbtCompound positionsTag = new NbtCompound();
            positionsTag.putInt("size", groupPositions.size());
            for (int i = 0; i < groupPositions.size(); i++) {
                positionsTag.put("pos" + i, NbtHelper.fromBlockPos(groupPositions.get(i)));
            }
            nbt.put("groupPositions", positionsTag);
        }

        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}