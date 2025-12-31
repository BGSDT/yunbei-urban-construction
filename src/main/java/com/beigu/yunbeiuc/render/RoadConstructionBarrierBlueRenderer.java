package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.RoadConstructionBarrierBlueEntity;
import com.beigu.yunbeiuc.entity.custom.RoadConstructionBarrierBlueModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RoadConstructionBarrierBlueRenderer extends GeoBlockRenderer<RoadConstructionBarrierBlueEntity> {
    public RoadConstructionBarrierBlueRenderer(BlockEntityRendererFactory.Context context) {
        super(new RoadConstructionBarrierBlueModel());
    }
}
