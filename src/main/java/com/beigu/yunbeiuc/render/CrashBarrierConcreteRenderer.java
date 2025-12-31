package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CrashBarrierConcreteEntity;
import com.beigu.yunbeiuc.entity.custom.CrashBarrierConcreteModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CrashBarrierConcreteRenderer extends GeoBlockRenderer<CrashBarrierConcreteEntity> {
    public CrashBarrierConcreteRenderer(BlockEntityRendererFactory.Context context) {
        super(new CrashBarrierConcreteModel());
    }
}
