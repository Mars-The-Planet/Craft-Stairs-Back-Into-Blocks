package com.mars.stairstoblocks.mixin;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.mars.deimos.datagen.DeimosRecipeGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

import static com.mars.stairstoblocks.StairsToBlocksConfig.add_recipes_manually;
import static com.mars.stairstoblocks.StairsToBlocksConfig.block_amount;

@Mixin(value = RecipeManager.class, priority = 900)
public class RecipeManagerMixin {
    @Inject(method = "prepare", at = @At("TAIL"))
    private void onRecipesLoaded(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> cir) {
        if(add_recipes_manually)
            return;

        SortedMap<ResourceLocation, JsonElement> rawJsonMap = new TreeMap<>();
        String recipesPath = Registries.elementsDirPath(Registries.RECIPE);

        Map<ResourceLocation, Resource> allRecipeResources = resourceManager.listResources(
                recipesPath,
                // filter predicate: only keep paths ending in ".json"
                rl -> rl.getPath().endsWith(".json")
        );


        // Iterate over each ResourceLocation key in that map
        for (ResourceLocation loc : allRecipeResources.keySet()) {
            Optional<Resource> optRes = resourceManager.getResource(loc);
            if (optRes.isEmpty()) {
                // If getResource couldn’t actually open it, skip
                continue;
            }

            try (Reader reader = optRes.get().openAsReader()) {
                JsonElement je = JsonParser.parseReader(reader);
                rawJsonMap.put(loc, je);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        for (Map.Entry<ResourceLocation, JsonElement> entry : rawJsonMap.entrySet()) {
            JsonElement je = entry.getValue();
            if (hasStairsPattern(je)) {
                String[] set = extractKeyAndResult(je);
                System.out.println(Arrays.toString(extractKeyAndResult(je)));
                if(set != null){
                    DeimosRecipeGenerator.createShapedRecipeJson(
                            Lists.newArrayList('#'),
                            Lists.newArrayList(ResourceLocation.parse(set[1])),
                            Lists.newArrayList("item"),
                            Lists.newArrayList("##",
                                    "##"),
                            ResourceLocation.parse(set[0]), block_amount);
                }
            }
        }
    }

    @Unique
    private static String[] extractKeyAndResult(JsonElement root) {
        JsonObject obj = root.getAsJsonObject();

        // Extract the staircase character 'c' from pattern[0].charAt(0)
        JsonArray pattArr = obj.getAsJsonArray("pattern");
        String row0 = pattArr.get(0).getAsString();
        char c = row0.charAt(0);
        String charKey = String.valueOf(c);

        // Read key[c], which may be a JsonPrimitive (string) or a JsonArray of strings
        if (!obj.has("key")) {
            return null;
        }
        JsonObject keyObj = obj.getAsJsonObject("key");
        if (!keyObj.has(charKey)) {
            return null;
        }
        JsonElement charEntry = keyObj.get(charKey);

        List<String> baseItems = new ArrayList<>();

        if (charEntry.isJsonPrimitive()) {
            JsonPrimitive prim = charEntry.getAsJsonPrimitive();
            if (!prim.isString()) {
                return null;
            }
            baseItems.add(prim.getAsString());

        } else if (charEntry.isJsonArray()) {
            JsonArray arr = charEntry.getAsJsonArray();
            if (arr.size() == 0) {
                return null;
            }
            for (JsonElement elt : arr) {
                if (!elt.isJsonPrimitive()) {
                    return null;
                }
                JsonPrimitive p = elt.getAsJsonPrimitive();
                if (!p.isString()) {
                    return null;
                }
                baseItems.add(p.getAsString());
            }

        } else {
            return null;
        }

        // Extract result.id
        if (!obj.has("result")) {
            return null;
        }
        JsonObject resObj = obj.getAsJsonObject("result");
        if (!resObj.has("id")) {
            return null;
        }
        JsonElement idElem = resObj.get("id");
        if (!idElem.isJsonPrimitive() || !((JsonPrimitive) idElem).isString()) {
            return null;
        }
        String resultId = idElem.getAsString();

        // Build the final array: [ baseItems..., resultId ]
        int total = baseItems.size() + 1;
        String[] output = new String[total];
        for (int i = 0; i < baseItems.size(); i++) {
            output[i] = baseItems.get(i);
        }
        output[total - 1] = resultId;
        return output;
    }

    @Unique
    private static boolean hasStairsPattern(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        if (!obj.has("pattern")) {
            return false;
        }

        JsonElement patternElem = obj.get("pattern");
        if (!patternElem.isJsonArray()) {
            return false;
        }

        JsonArray patternArray = patternElem.getAsJsonArray();
        // Must have exactly 3 rows
        if (patternArray.size() != 3) {
            return false;
        }

        // Each row must be a string of length 3
        String[] rows = new String[3];
        for (int i = 0; i < 3; i++) {
            JsonElement rowElem = patternArray.get(i);
            if (!rowElem.isJsonPrimitive() || !((JsonPrimitive) rowElem).isString()) {
                return false;
            }
            rows[i] = rowElem.getAsString();
            if (rows[i].length() != 3) {
                return false;
            }
        }

        // Extract the candidate character from row[0].charAt(0)
        char c = rows[0].charAt(0);
        if (c == ' ') {
            return false;
        }

        // Build the three expected strings for this c:
        String expected0 = "" + c + "  ";     // c + two spaces
        String expected1 = "" + c + c + " ";  // two c's + one space
        String expected2 = "" + c + c + c;    // three c's, no spaces

        return rows[0].equals(expected0)
                && rows[1].equals(expected1)
                && rows[2].equals(expected2);
    }
}