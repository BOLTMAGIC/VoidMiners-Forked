package com.leo.voidminers.multiblock.solar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SolarPatternLayers {

    private static final Gson GSON = new Gson();

    private SolarPatternLayers() {
    }

    static List<List<String>> load(String structureId) {
        String resourcePath = "/voidminers/patterns/solar/" + structureId + ".json";
        InputStream stream = SolarPatternLayers.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Missing solar panel pattern resource: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("layers")) {
                throw new JsonParseException("Pattern file " + resourcePath + " lacks 'layers' array");
            }

            JsonArray layersArray = root.getAsJsonArray("layers");
            List<List<String>> layers = new ArrayList<>(layersArray.size());

            layersArray.forEach(layerElement -> {
                JsonArray layerArray = layerElement.getAsJsonArray();
                List<String> rows = new ArrayList<>(layerArray.size());
                layerArray.forEach(rowElement -> rows.add(rowElement.getAsString()));
                layers.add(Collections.unmodifiableList(rows));
            });

            return Collections.unmodifiableList(layers);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read solar panel pattern resource: " + resourcePath, e);
        }
    }
}
