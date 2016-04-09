package eu.epicpvp.kSkyblock.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kSkyblock.kSkyBlock;
import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.TeleportManager.Teleporter;
import eu.epicpvp.kcore.Translation.TranslationManager;
import eu.epicpvp.kcore.Util.UtilPlayer;
import lombok.Getter;

public class CommadSkyBlock implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	private Player p;
	private Player target;
	
	public CommadSkyBlock(kSkyBlock instance){
		this.instance=instance;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "skyblock",alias = {"sb","sk","is","island","s"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(cs instanceof Player){
			p = (Player)cs;
			if(args.length==0){
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_PREFIX"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD1"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD2"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD3"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD4"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD5"));
				if(p.hasPermission(PermissionType.GILDE_NEWISLAND.getPermissionToString()))p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD6"));
				if(p.isOp())p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD7"));
				if(p.isOp())p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD8"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD9"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD10"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD11"));
				p.sendMessage(TranslationManager.getText(p, "SKYBLOCK_CMD12"));
			}else{
				if(args[0].equalsIgnoreCase("erstellen")||args[0].equalsIgnoreCase("create")){
					if(getInstance().getManager().haveIsland(p)){
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_HAVE_ISLAND"));
					}else{
						SkyBlockWorld world = getInstance().getManager().addIsland(p);
						if(world!=null){
							p.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
							p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET,2));
							p.getInventory().addItem(new ItemStack(61,2));
							p.getInventory().addItem(new ItemStack(362,2));
							p.getInventory().addItem(new ItemStack(295,2));
							p.getInventory().addItem(new ItemStack(351,1,(byte)3));
							p.getInventory().addItem(new ItemStack(6,1));
							p.getInventory().addItem(new ItemStack(6,1,(byte)2));
							p.getInventory().addItem(new ItemStack(40,2));
							p.getInventory().addItem(new ItemStack(32,2));
							p.getInventory().addItem(new ItemStack(260,10));
							p.getInventory().addItem(new ItemStack(141,10));
							p.getInventory().addItem(new ItemStack(360,15));
							p.getInventory().addItem(new ItemStack(287,10));
							p.getInventory().addItem(new ItemStack(352,10));
							p.teleport(world.getIslandHome(p));
						}else{
							System.out.println("[SkyBlock] WORLD == NULL");
						}
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_CREATE_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("kick")){
					if(args.length>=2){
						if(UtilPlayer.isOnline(args[1])){
							if(getInstance().getManager().haveIsland(p)){
								SkyBlockWorld world = getInstance().getManager().getIsland(p);
								target=Bukkit.getPlayer(args[1]);
								
								if(world.isInIsland(p, target.getLocation())){
									target.teleport(Bukkit.getWorld("world").getSpawnLocation());
									target.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_PLAYER_KICKED",p.getName()));
									p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_PLAYER_KICK",target.getName()));
								}else{
									p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_PLAYER_NOT_ON_YOUR_ISLAND",target.getName()));
								}
							}else{
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_NO_ISLAND"));
							}
						}else{
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "PLAYER_IS_OFFLINE",args[1]));
						}
					}else{
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§6/skyblock kick [Player]");
					}
				}else if(args[0].equalsIgnoreCase("entfernen")||args[0].equalsIgnoreCase("delete")||args[0].equalsIgnoreCase("remove")){
					if(getInstance().getManager().haveIsland(p)){
						p.teleport(Bukkit.getWorld("world").getSpawnLocation());
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world.removeIsland(p))p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_REMOVE_ISLAND"));
					}else{
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("home")){
					if(args.length==1){
						if(!getInstance().getAntiLogout().is(p)){
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausf§hren!");
							return false;
						}
						if(getInstance().getManager().haveIsland(p)){
							SkyBlockWorld world = getInstance().getManager().getIsland(p);
							getInstance().getTeleport().getTeleport().add(new Teleporter(p, world.getIslandHome(p), 3));
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_TELEPORT_HOME"));
						}else{
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_NO_ISLAND"));
						}
					}else if(p.hasPermission(PermissionType.SKYBLOCK_HOME_OTHER.getPermissionToString())){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								SkyBlockWorld world = getInstance().getManager().getIsland(tp);
								getInstance().getTeleport().getTeleport().add(new Teleporter(p,tp, world.getIslandHome(tp), 3));
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§aDu wurdest zur Insel teleportiert.");
							}else{
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "PLAYER_IS_OFFLINE",args[1]));
							UUID uuid = UtilPlayer.getUUID(args[1], instance.getMysql());
							if(!getInstance().getManager().haveIsland(uuid)){
								for(SkyBlockWorld world : instance.getManager().getWorlds())world.loadIslandPlayer( uuid );
							}
							
							if(getInstance().getManager().haveIsland(uuid)){
								SkyBlockWorld world = getInstance().getManager().getIsland(uuid);
								p.teleport(world.getIslandHome(uuid));
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§aDu wurdest zur Insel teleportiert.");
							}else{
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+" Insel konnte nicht geladen werden.");
							}
						}
					}
				}else if(args[0].equalsIgnoreCase("fixhome")){
					if(getInstance().getManager().haveIsland(p)){
						if(!getInstance().getAntiLogout().is(p)){
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausf§hren!");
							return false;
						}
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						getInstance().getTeleport().getTeleport().add(new Teleporter(p, world.getIslandFixHome(p), 3));
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_TELEPORT_HOME"));
					}else{
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("biome")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						world.setBiome(UtilPlayer.getRealUUID(p).toString(), Biome.JUNGLE);
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_NO_ISLAND",Biome.JUNGLE.name()));
					}else{
						p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("newisland")&&p.hasPermission(PermissionType.GILDE_NEWISLAND.getPermissionToString())){
					if(args.length==2){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								SkyBlockWorld world = getInstance().getManager().getIsland(tp);
								world.newIsland(tp);
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§aDie Insel wurde erneuert.");
							}else{
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "PLAYER_IS_OFFLINE",args[1]));
							UUID uuid = UtilPlayer.getUUID(args[1], instance.getMysql());
							if(!getInstance().getManager().haveIsland(uuid)){
								for(SkyBlockWorld world : instance.getManager().getWorlds())world.loadIslandPlayer( uuid );
							}
							
							if(getInstance().getManager().haveIsland(uuid)){
								SkyBlockWorld world = getInstance().getManager().getIsland(uuid);
								world.newIsland(uuid);
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§aDie Insel wurde erneuert.");
							}else{
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+" Insel konnte nicht geladen werden.");
							}
						}
					}
				}else if(args[0].equalsIgnoreCase("check")&&p.isOp()){
					int sky=0;
					int g=0;
					for(SkyBlockWorld world : getInstance().getManager().getWorlds()){
						for(String uuid : world.getIslands().keySet()){
							if(uuid.startsWith("!")){
								world.newIsland(uuid);
								sky++;
							}
						}
						world.getWorld().save();
					}
					
					if(getInstance().getManager().getGilden_world()!=null){
						for(String gilde : getInstance().getManager().getGilden_world().getIslands().keySet()){
							if(gilde.startsWith("!")){
								getInstance().getManager().getGilden_world().newIsland(gilde);
								g++;
							}
						}
						getInstance().getManager().getGilden_world().getWorld().save();
					}
					System.out.println("[SkyBlock] Check fertig SkyBlockWorld("+sky+") / GildenWorld("+g+")");
				}else if(args[0].equalsIgnoreCase("entities")&&p.isOp()){
					if(args.length==2){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								SkyBlockWorld world = getInstance().getManager().getIsland(tp);
								int entities = 0;
								for(Entity e : world.getIslandHome(tp).getWorld().getEntities()){
									if(!(e instanceof Player)&&world.isInIsland(tp, e.getLocation())){
										entities++;
										e.remove();
									}
								}
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"§aEs wurden "+entities+" entfernt.");
							}else{
								p.sendMessage(TranslationManager.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(TranslationManager.getText(p, "PREFIX")+TranslationManager.getText(p, "PLAYER_IS_OFFLINE",args[1]));
						}
					}
				}else if(args[0].equalsIgnoreCase("info")&&p.hasPermission(PermissionType.GILDE_NEWISLAND.getPermissionToString())){
					if(args.length==1){
						
					}
				}
			}
		}
		return false;
	}
	
}

