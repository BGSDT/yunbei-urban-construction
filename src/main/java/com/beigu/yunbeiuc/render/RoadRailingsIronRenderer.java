package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CrashBarrierConcreteEntity;
import com.beigu.yunbeiuc.entity.RoadRailingsIronEntity;
import com.beigu.yunbeiuc.entity.custom.CrashBarrierConcreteModel;
import com.beigu.yunbeiuc.entity.custom.RoadRailingsIronModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RoadRailingsIronRenderer extends GeoBlockRenderer<RoadRailingsIronEntity> {
    public RoadRailingsIronRenderer(BlockEntityRendererFactory.Context context) {
        super(new RoadRailingsIronModel());
    }
}
