package com.xkingdark.betterbonemeal;

import com.xkingdark.betterbonemeal.helpers.Registry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String MOD_ID = "xkingdark";
    public static final Logger LOGGER = LoggerFactory.getLogger("Better Bone Meal");
    
    @Override
    public void onInitialize() {
        LOGGER.info("Hello, world!");
    };

    static {
        Registry.initialize();
    };
};