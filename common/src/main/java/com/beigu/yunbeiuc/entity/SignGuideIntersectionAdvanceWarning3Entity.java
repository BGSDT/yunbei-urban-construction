package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SignGuideIntersectionAdvanceWarning3Entity extends BlockEntity {
    private String text1 = "";
    private String cnText2 = "";
    private String enText2 = "";
    private String cnText3 = "";
    private String enText3 = "";
    private String cnText4 = "";
    private String enText4 = "";
    private String cnText5 = "";
    private String enText5 = "";
    private String cnText6 = "";
    private String enText6 = "";
    private String cnText7 = "";
    private String enText7 = "";

    public SignGuideIntersectionAdvanceWarning3Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.cnText2 = nbt.getString("cnText2");
        this.enText2 = nbt.getString("enText2");
        this.cnText3 = nbt.getString("cnText3");
        this.enText3 = nbt.getString("enText3");
        this.cnText4 = nbt.getString("cnText4");
        this.enText4 = nbt.getString("enText4");
        this.cnText5 = nbt.getString("cnText5");
        this.enText5 = nbt.getString("enText5");
        this.cnText6 = nbt.getString("cnText6");
        this.enText6 = nbt.getString("enText6");
        this.cnText7 = nbt.getString("cnText7");
        this.enText7 = nbt.getString("enText7");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("cnText2", this.cnText2);
        nbt.putString("enText2", this.enText2);
        nbt.putString("cnText3", this.cnText3);
        nbt.putString("enText3", this.enText3);
        nbt.putString("cnText4", this.cnText4);
        nbt.putString("enText4", this.enText4);
        nbt.putString("cnText5", this.cnText5);
        nbt.putString("enText5", this.enText5);
        nbt.putString("cnText6", this.cnText6);
        nbt.putString("enText6", this.enText6);
        nbt.putString("cnText7", this.cnText7);
        nbt.putString("enText7", this.enText7);
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

    public String getText1() {
        return text1;
    }
    public String getCnText2() {
        return cnText2;
    }
    public String getEnText2() {
        return enText2;
    }
    public String getCnText3() {
        return cnText3;
    }
    public String getEnText3() {
        return enText3;
    }
    public String getCnText4() {
        return cnText4;
    }
    public String getEnText4() {
        return enText4;
    }
    public String getCnText5() {
        return cnText5;
    }
    public String getEnText5() {
        return enText5;
    }
    public String getCnText6() {
        return cnText6;
    }
    public String getEnText6() {
        return enText6;
    }
    public String getCnText7() {
        return cnText7;
    }
    public String getEnText7() {
        return enText7;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public void setCnText2(String cnText2) {
        this.cnText2 = cnText2;
        markDirtyAndUpdate();
    }
    public void setEnText2(String enText2) {
        this.enText2 = enText2;
        markDirtyAndUpdate();
    }
    public void setCnText3(String cnText3) {
        this.cnText3 = cnText3;
        markDirtyAndUpdate();
    }
    public void setEnText3(String enText3) {
        this.enText3 = enText3;
        markDirtyAndUpdate();
    }
    public void setCnText4(String cnText4) {
        this.cnText4 = cnText4;
        markDirtyAndUpdate();
    }
    public void setEnText4(String enText4) {
        this.enText4 = enText4;
        markDirtyAndUpdate();
    }
    public void setCnText5(String cnText5) {
        this.cnText5 = cnText5;
        markDirtyAndUpdate();
    }
    public void setEnText5(String enText5) {
        this.enText5 = enText5;
        markDirtyAndUpdate();
    }
    public void setCnText6(String cnText6) {
        this.cnText6 = cnText6;
        markDirtyAndUpdate();
    }
    public void setEnText6(String enText6) {
        this.enText6 = enText6;
        markDirtyAndUpdate();
    }
    public void setCnText7(String cnText7) {
        this.cnText7 = cnText7;
        markDirtyAndUpdate();
    }
    public void setEnText7(String enText7) {
        this.enText7 = enText7;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}