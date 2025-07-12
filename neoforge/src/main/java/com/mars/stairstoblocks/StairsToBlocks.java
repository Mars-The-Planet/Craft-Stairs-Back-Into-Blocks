package com.mars.stairstoblocks;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class StairsToBlocks {
    public StairsToBlocks(IEventBus eventBus) {
        CommonClass.init();
    }
}
