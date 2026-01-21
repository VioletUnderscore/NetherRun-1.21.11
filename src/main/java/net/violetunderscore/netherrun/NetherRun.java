package net.violetunderscore.netherrun;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.violetunderscore.netherrun.command.Commands;
import net.violetunderscore.netherrun.game.GameLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherRun implements ModInitializer {
	public static final String MOD_ID = "netherrun";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static GameLogic game;

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Netherrun");

        Commands.registerCommands();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Loading GameLogic");
            game = new GameLogic(server);
        });
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            game.tickMaster();
        });
	}

    public static GameLogic getGame() {
        return game;
    }
}