package net.violetunderscore.netherrun;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherRun implements ModInitializer {
	public static final String MOD_ID = "netherrun";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("doing absolutely nothing");
	}
}