package eu.epicpvp.kcore.Gilden;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Gilden.Events.GildenPlayerTeleportEvent;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;

public class SkyBlockGildenManager extends GildenManager {

	@Getter
	private SkyBlockManager sky;
	@Getter
	private StatsManager stats;

	public SkyBlockGildenManager(SkyBlockManager manager, MySQL mysql, CommandHandler cmd, StatsManager stats) {
		super(mysql, GildenType.SKY, cmd, stats);
		this.sky = manager;
		this.stats = stats;
		manager.addGildenWorld("gilde", this);
	}

	@EventHandler
	public void Teleport(GildenPlayerTeleportEvent ev) {
		if (!isPlayerInGilde(ev.getPlayer())) {
			ev.setReason(TranslationHandler.getText(ev.getPlayer(), "GILDE_PLAYER_IS_NOT_IN_GILDE"));
			ev.setCancelled(true);
		} else {
			ev.setReason(TranslationHandler.getText(ev.getPlayer(), "SKYBLOCK_NO_ISLAND"));
			ev.setCancelled(sky.getGilden_world().getIslandHome(getPlayerGilde(ev.getPlayer())) == null);
		}
	}

	@Override
	public void removeGildenEintrag(Player player, String name) {
		super.removeGildenEintrag(player, name);
		sky.getGilden_world().removeIsland(player, name);
	}

	@Override
	public void LoadRanking(boolean b) {
//		if (ranking.isEmpty() || b) {
//			extra_prefix.clear();
//			try {
//				ResultSet rs = getMysql().Query("SELECT `money`,`gilde` FROM `list_gilden_" + typ.getKuerzel() + "_data` ORDER BY money DESC LIMIT 15;");
//
//				int platz = 1;
//
//				while (rs.next()) {
//					int value = rs.getInt(1);
//					String gilde = rs.getString(2);
//					switch (platz) {
//						case 1: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§4§l " + gilde);
//							break;
//						}
//						case 2: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§2§l " + gilde);
//							break;
//						}
//						case 3: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§e§l " + gilde);
//							break;
//						}
//						case 4:
//						case 5:
//						case 6: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§3 " + gilde);
//							break;
//						}
//						case 7:
//						case 8:
//						case 9: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§d " + gilde);
//							break;
//						}
//						case 10:
//						case 11:
//						case 12: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§a " + gilde);
//							break;
//						}
//						case 13:
//						case 14:
//						case 15: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§b " + gilde);
//							break;
//						}
//						default: {
//							ranking.put(platz, "§b#§6" + platz + "§b | §6" + value + " §b|§6 " + gilde);
//							break;
//						}
//					}
//					extra_prefix.put(gilde.toLowerCase(), platz);
//					platz++;
//				}
//
//				rs.close();
//			} catch (Exception err) {
//				System.out.println("MySQL-Error: " + err.getMessage());
//			}
//		}
	}

	@Override
	public void TeleportToHome(Player p) {
		if (!isPlayerInGilde(p)) {
			p.sendMessage(TranslationHandler.getText(p, "GILDE_PREFIX") + TranslationHandler.getText(p, "GILDE_PLAYER_IS_NOT_IN_GILDE"));
			return;
		}
		String g = getPlayerGilde(p);

		if (getTyp() == GildenType.SKY) {
			p.teleport(getSky().getGilden_world().getIslandHome(g));
			p.sendMessage(TranslationHandler.getText(p, "PREFIX") + TranslationHandler.getText(p, "GILDE_TELEPORTET"));
			UtilPlayer.sendPacket(p, sky.getGilden_world().getIslandBorder(p));
		} else {
			String w = getString(StatsKey.WORLD, g, getTyp());
			int x = getInt(StatsKey.LOC_X, g, super.getTyp());
			int y = getInt(StatsKey.LOC_Y, g, super.getTyp());
			int z = getInt(StatsKey.LOC_Z, g, super.getTyp());
			if (Bukkit.getWorld(w) == null) return;
			if (x == 0 && y == 0 && z == 0 && g.equalsIgnoreCase("0")) return;
			Location loc = new Location(Bukkit.getWorld(w), x, y, z);
			p.teleport(loc);
			p.sendMessage(TranslationHandler.getText(p, "PREFIX") + TranslationHandler.getText(p, "GILDE_TELEPORTET"));
		}
	}

	@Override
	public void onKick(String kick_o, int kick_id) {
		kConfig config;

		if (getSky().getInstance().getUserData().getConfigs().containsKey(kick_id) && UtilPlayer.isOnline(kick_o)) {
			config = getSky().getInstance().getUserData().getConfig(Bukkit.getPlayer(kick_o));
			if (Bukkit.getPlayer(kick_o).getWorld().getUID() != Bukkit.getWorld("world").getUID())
				Bukkit.getPlayer(kick_o).teleport(Bukkit.getWorld("world").getSpawnLocation());
		} else {
			config = getSky().getInstance().getUserData().loadConfig(kick_id);
		}

		for (String path : config.getPathList("homes").keySet()) {
			if (config.getLocation("homes." + path).getWorld().getName().equalsIgnoreCase(getSky().getGilden_world().getWorld().getName())) {
				config.set("homes." + path, null);
			}
		}
		config.save();
	}

	@Override
	public boolean onCreate(Player p) {
		if(getStats().getDouble(p, StatsKey.MONEY)>=500.0){
			getStats().add(p, StatsKey.MONEY,-500.0);
		}else{
			p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"Du brauchst 500 Epics um eine Gilde zu erstellen.");
			return true;
		}
		return false;
	}

	@Override
	public boolean onHomeUseSet(Player p, String g) {
		if (!getSky().getGilden_world().getIslands().containsKey(g.toLowerCase())) {
			if(p.hasPermission(PermissionType.SKYBLOCK_GILDEN_ISLAND.getPermissionToString())){
				getSky().addGildenIsland(p, g);
				p.sendMessage(TranslationHandler.getText(p, "GILDE_PREFIX")+TranslationHandler.getText(p, "GILDE_SETISLAND"));
			}else{
				p.sendMessage(TranslationHandler.getText(p, "GILDE_PREFIX")+TranslationHandler.getText(p, "NO_RANG"));
			}
		}
		return true;
	}

	@Override
	public void onHomeAdminUse(Player p, String arg) {
		if (getSky().getGilden_world().getIslands().containsKey(arg.toLowerCase())) {
			p.teleport(getSky().getGilden_world().getIslandHome(arg.toLowerCase()));
			p.sendMessage(TranslationHandler.getText(p, "PREFIX") + "§aDu wurdest Teleporiert.");
		} else {
			p.sendMessage(TranslationHandler.getText(p, "PREFIX") + "§cGilde nicht gefunden");
		}
	}

	@Override
	public void onOwnerLeave(String g) {
		getMember(g);
		kConfig config;
		for (int n : getGilden_player().keySet()) {
			if (getSky().getInstance().getUserData().getConfigs().containsKey(n)) {
				Player plr = UtilPlayer.searchExact(n);
				if (plr != null) {
					config = getSky().getInstance().getUserData().getConfig(n);
					if (plr.getWorld().getName().equalsIgnoreCase(getSky().getGilden_world().getWorld().getName()))
						plr.teleport(Bukkit.getWorld("world").getSpawnLocation());
				} else {
					config = getSky().getInstance().getUserData().loadConfig(n);
				}
			} else {
				config = getSky().getInstance().getUserData().loadConfig(n);
			}

			for (String path : config.getPathList("homes").keySet()) {
				if (config.getLocation("homes." + path).getWorld().getName().equalsIgnoreCase(getSky().getGilden_world().getWorld().getName())) {
					config.set("homes." + path, null);
				}
			}
			config.save();
		}
	}

	@Override
	public void onMemberLeave(Player p) {
		kConfig config = getSky().getInstance().getUserData().getConfig(p);
		for (String path : config.getPathList("homes").keySet()) {
			if (config.getLocation("homes." + path).getWorld().getName().equalsIgnoreCase(getSky().getGilden_world().getWorld().getName())) {
				config.set("homes." + path, null);
			}
		}
		config.save();
		if (p.getWorld().getName().equalsIgnoreCase(getSky().getGilden_world().getWorld().getName()))
			p.teleport(Bukkit.getWorld("world").getSpawnLocation());
	}
}
