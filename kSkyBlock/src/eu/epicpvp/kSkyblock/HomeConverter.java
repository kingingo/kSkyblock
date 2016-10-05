package eu.epicpvp.kSkyblock;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kcore.Util.UtilNumber;
import eu.epicpvp.kcore.kConfig.kConfig;

public class HomeConverter {

	public static void convert(SkyBlockManager manager) {
		Bukkit.getScheduler().runTaskAsynchronously(manager.getInstance(), new Runnable() {

			@Override
			public void run() {
				ArrayList<kConfig> configs = new ArrayList<>();
				File[] list = new File("plugins" + File.separator + manager.getInstance().getPlugin(manager.getInstance().getClass()).getName() + File.separator + "userdata").listFiles();
				System.out.println("[Converter] Load " + list.length + " files..");

				try {
					Thread.sleep(1000 * 5);
				} catch (InterruptedException e) {
				}

				int i = 0;
				for (File file : list) {
					i++;
					kConfig config = new kConfig(file);

					int playerId = UtilNumber.toInt(config.getFile().getName().replaceAll(".yml", ""));

					if (playerId == 333841) {
						System.out.println("CONFIG LOAD KINGINGO CONFIG!! " + config.contains("homes"));
						try {
							Thread.sleep(1000 * 5);
						} catch (InterruptedException e) {
						}
					}

					if (config.contains("homes")) {
						configs.add(config);
					}
					System.out.println("[Converter/" + config.getFile().getName() + "] " + i + "/" + list.length + " controll files " + configs.size() + "  " + playerId);
				}

				System.out.println("[Converter] Now load ALL Islands");
				try {
					Thread.sleep(1000 * 5);
				} catch (InterruptedException e) {
				}

				for (SkyBlockWorld world : manager.getWorlds()) {
					world.loadAllIslands();
					System.out.println("[Converter] All Islands loaded " + world.getMinecraftWorld().getName() + " " + world.getIslands().size());
				}

				System.out.println("[Converter] Start to controll all HOMES");
				try {
					Thread.sleep(1000 * 5);
				} catch (InterruptedException e) {
				}

				i = 0;

				int island_not_found = 0;
				int island_null = 0;
				for (kConfig config : configs) {
					i++;
					int playerId = UtilNumber.toInt(config.getFile().getName().replaceAll(".yml", ""));

					if (playerId == 333841) {
						System.out.println("KINGINGO CONFIG!!");
						try {
							Thread.sleep(1000 * 5);
						} catch (InterruptedException e) {
						}
					}

					for (String path : config.getPathList("homes").keySet()) {
						Location location = config.getLocation("homes." + path);

						if (playerId == 333841) {
							System.out.println("KINGINGO " + path);
							try {
								Thread.sleep(1000 * 3);
							} catch (InterruptedException e) {
							}
						}

						for (SkyBlockWorld world : manager.getWorlds()) {
							if (location.getWorld().getUID() == world.getMinecraftWorld().getUID()) {
								Island island = world.getIsland(location);

								if (island != null) {
									if (island.getPlayerId() != -1) {
										config.set("homes." + path + ".ownerId", island.getPlayerId());
										island.addHome(playerId);
									} else {
										config.set("homes." + path, null);
										island_null++;
										System.out.println("[Converter] Island null " + island_null + " " + location.getWorld().getName());
									}
								} else {
									island_not_found++;
									System.out.println("[Converter] Island not found " + island_not_found + " " + location.getWorld().getName());
								}
							}
						}

						if (playerId == 333841) {
							System.out.println("KINGINGO123 " + path);
							try {
								Thread.sleep(1000 * 3);
							} catch (InterruptedException e) {
							}
						}
					}
					config.save();
					System.out.println("[Converter] Player Configs Converted " + i + "/" + configs.size());
				}
			}
		});
	}

}
