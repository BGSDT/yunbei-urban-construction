package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CrashBarrierConcreteEntity;
import com.beigu.yunbeiuc.entity.custom.CrashBarrierConcreteItemModel;
import com.beigu.yunbeiuc.entity.custom.CrashBarrierConcreteModel;
import com.beigu.yunbeiuc.item.custom.CrashBarrierConcreteItem;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CrashBarrierConcreteItemRenderer extends GeoItemRenderer<CrashBarrierConcreteItem> {

    public CrashBarrierConcreteItemRenderer() {
        super(new CrashBarrierConcreteItemModel());
    }
}
