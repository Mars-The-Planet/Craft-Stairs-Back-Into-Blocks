package com.mars.stairstoblocks;

import com.google.common.collect.Lists;
import com.mars.deimos.config.DeimosConfig;
import com.mars.deimos.datagen.DeimosRecipeGenerator;
import net.minecraft.resources.ResourceLocation;

import static com.mars.stairstoblocks.StairsToBlocksConfig.*;

public class CommonClass {
    public static void init() {
        DeimosConfig.init(Constants.MOD_ID, StairsToBlocksConfig.class);

        if(add_recipes_manually){
            for (int i = 0; i < block_stairs_list.size(); i++) {
                String current = block_stairs_list.get(i);
                String[] set = ((current).replaceAll("\\s","")).split(",");

                DeimosRecipeGenerator.createShapedRecipeJson(
                        Lists.newArrayList(
                                '#'
                        ),
                        Lists.newArrayList(ResourceLocation.parse(set[1])),
                        Lists.newArrayList("item"),
                        Lists.newArrayList(
                                "##",
                                "##"
                        ),
                        ResourceLocation.parse(set[0]), block_amount);
            }
        }
    }
}
