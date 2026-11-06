package com.larsb6.endrite.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * Polymer only serves its resource pack when auto-host is enabled, which is off by default.
 * On the first start this writes a config that enables it and requires the pack, because without
 * it Endrite blocks would show up as note blocks. An existing config is never touched.
 */
public class AutoHostDefaults implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		Path config = FabricLoader.getInstance().getConfigDir().resolve("polymer").resolve("auto-host.json");
		if (Files.exists(config)) {
			return;
		}
		try {
			Files.createDirectories(config.getParent());
			Files.writeString(config, "{\n  \"enabled\": true,\n  \"require\": true\n}\n");
		} catch (IOException e) {
			System.err.println("[Endrite] Could not write default Polymer auto-host config: " + e);
		}
	}
}
