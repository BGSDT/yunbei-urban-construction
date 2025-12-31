package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.custom.RoadConstructionBarrierBlueItemModel;
import com.beigu.yunbeiuc.item.custom.RoadConstructionBarrierBlueItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RoadConstructionBarrierBlueItemRenderer extends GeoItemRenderer<RoadConstructionBarrierBlueItem> {

    public RoadConstructionBarrierBlueItemRenderer() {
        super(new RoadConstructionBarrierBlueItemModel());
    }
}
