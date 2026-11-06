package com.larsb6.endrite.server;

import com.larsb6.endrite.Endrite;
import com.larsb6.endrite.ModBlocks;
import com.larsb6.endrite.ModItems;
import com.larsb6.endrite.ModWorldGen;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

/**
 * Server-side entrypoint: registers the same content as the client mod, but shows it to vanilla
 * clients through Polymer and replaces the networked double jump with a purely server-side one.
 */
public class EndriteServer implements ModInitializer {
	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModItems.initialize();
		ModWorldGen.initialize();
		PolymerOverlays.register();
		ServerDoubleJump.register();

		PolymerResourcePackUtils.addModAssets(Endrite.MOD_ID);
		PolymerResourcePackUtils.markAsRequired();
		Endrite.LOGGER.info("Endrite (server-side) loaded");
	}
}
