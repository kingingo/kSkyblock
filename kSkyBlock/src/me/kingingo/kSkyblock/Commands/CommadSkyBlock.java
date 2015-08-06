package me.kingingo.kSkyblock.Commands;

import java.util.UUID;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommadSkyBlock implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	private Player p;
	private Player target;
	
	public CommadSkyBlock(kSkyBlock instance){
		this.instance=instance;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "skyblock",alias = {"sb","sk","is","island","s"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(cs instanceof Player){
			p = (Player)cs;
			if(args.length==0){
				p.sendMessage(Language.getText(p, "SKYBLOCK_PREFIX"));
				p.sendMessage("�6/skyblock erstellen �8|�7 Erstelle deine Insel.");
				p.sendMessage("�6/skyblock entfernen �8|�7 L�sche deine Insel.");
				p.sendMessage("�6/skyblock home �8|�7 Teleportiere dich zu deiner Insel.");
				p.sendMessage("�6/skyblock fixhome �8|�7 Teleportiere dich zu deiner Insel.");
				p.sendMessage("�6/skyblock kick [Player] �8|�7 Kicke Spieler von deiner Insel.");
				if(p.hasPermission(kPermission.GILDE_NEWISLAND.getPermissionToString()))p.sendMessage("�6/skyblock newisland [Player] �8|�7 Erneurt die Insel.");
				if(p.isOp())p.sendMessage("�6/skyblock check �8|�7 Erneuert die alten Gilden u. Islands.");
				if(p.isOp())p.sendMessage("�6/skyblock entities [Player] �8|�7 Die Entitys werden entfernt.");
				p.sendMessage("�6/homedelete [Player] �8|�7 L�schen von Homes auf deiner Insel.");
				p.sendMessage("�6/homeaccept �8|�7 Annehmen von Homes.");
				p.sendMessage("�6/homedeny �8|�7 Ablehnen von Homes.");
				p.sendMessage("�6/party �8|�7 Party Menue.");
			}else{
				if(args[0].equalsIgnoreCase("erstellen")){
					if(getInstance().getManager().haveIsland(p)){
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_HAVE_ISLAND"));
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
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_CREATE_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("kick")){
					if(args.length>=2){
						if(UtilPlayer.isOnline(args[1])){
							if(getInstance().getManager().haveIsland(p)){
								SkyBlockWorld world = getInstance().getManager().getIsland(p);
								target=Bukkit.getPlayer(args[1]);
								
								if(world.isInIsland(p, target.getLocation())){
									target.teleport(Bukkit.getWorld("world").getSpawnLocation());
									target.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_PLAYER_KICKED",p.getName()));
									p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_PLAYER_KICK",target.getName()));
								}else{
									p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_PLAYER_NOT_ON_YOUR_ISLAND",target.getName()));
								}
							}else{
								p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_NO_ISLAND"));
							}
						}else{
							p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "PLAYER_IS_OFFLINE",args[1]));
						}
					}else{
						p.sendMessage(Language.getText(p, "PREFIX")+"�6/skyblock kick [Player]");
					}
				}else if(args[0].equalsIgnoreCase("entfernen")){
					if(getInstance().getManager().haveIsland(p)){
						p.teleport(Bukkit.getWorld("world").getSpawnLocation());
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world.removeIsland(p))p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_REMOVE_ISLAND"));
					}else{
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("home")){
					if(args.length==1){
						if(!getInstance().getAntiLogout().is(p)){
							p.sendMessage(Language.getText(p, "PREFIX")+"�cDu kannst den Befehl �b"+cmd+"�c nicht in Kampf ausf�hren!");
							return false;
						}
						if(getInstance().getManager().haveIsland(p)){
							SkyBlockWorld world = getInstance().getManager().getIsland(p);
							p.teleport(world.getIslandHome(p));
							p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_TELEPORT_HOME"));
						}else{
							p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_NO_ISLAND"));
						}
					}else if(p.hasPermission(kPermission.SKYBLOCK_HOME_OTHER.getPermissionToString())){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								SkyBlockWorld world = getInstance().getManager().getIsland(tp);
								p.teleport(world.getIslandHome(tp));
								p.sendMessage(Language.getText(p, "PREFIX")+"�aDu wurdest zur Insel teleportiert.");
							}else{
								p.sendMessage(Language.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "PLAYER_IS_OFFLINE",args[1]));
							UUID uuid = UtilPlayer.getUUID(args[1], instance.getMysql());
							if(!getInstance().getManager().haveIsland(uuid)){
								for(SkyBlockWorld world : instance.getManager().getWorlds())world.loadIslandPlayer( uuid );
							}
							
							if(getInstance().getManager().haveIsland(uuid)){
								SkyBlockWorld world = getInstance().getManager().getIsland(uuid);
								p.teleport(world.getIslandHome(uuid));
								p.sendMessage(Language.getText(p, "PREFIX")+"�aDu wurdest zur Insel teleportiert.");
							}else{
								p.sendMessage(Language.getText(p, "PREFIX")+" Insel konnte nicht geladen werden.");
							}
						}
					}
				}else if(args[0].equalsIgnoreCase("fixhome")){
					if(getInstance().getManager().haveIsland(p)){
						if(!getInstance().getAntiLogout().is(p)){
							p.sendMessage(Language.getText(p, "PREFIX")+"�cDu kannst den Befehl �b"+cmd+"�c nicht in Kampf ausf�hren!");
							return false;
						}
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						p.teleport(world.getIslandFixHome(p));
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_TELEPORT_HOME"));
					}else{
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("biome")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						world.setBiome(UtilPlayer.getRealUUID(p).toString(), Biome.JUNGLE);
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_NO_ISLAND",Biome.JUNGLE.name()));
					}else{
						p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("newisland")&&p.hasPermission(kPermission.GILDE_NEWISLAND.getPermissionToString())){
					if(args.length==2){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								SkyBlockWorld world = getInstance().getManager().getIsland(tp);
								world.newIsland(tp);
								p.sendMessage(Language.getText(p, "PREFIX")+"�aDie Insel wurde erneuert.");
							}else{
								p.sendMessage(Language.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "PLAYER_IS_OFFLINE",args[1]));
							UUID uuid = UtilPlayer.getUUID(args[1], instance.getMysql());
							if(!getInstance().getManager().haveIsland(uuid)){
								for(SkyBlockWorld world : instance.getManager().getWorlds())world.loadIslandPlayer( uuid );
							}
							
							if(getInstance().getManager().haveIsland(uuid)){
								SkyBlockWorld world = getInstance().getManager().getIsland(uuid);
								world.newIsland(uuid);
								p.sendMessage(Language.getText(p, "PREFIX")+"�aDie Insel wurde erneuert.");
							}else{
								p.sendMessage(Language.getText(p, "PREFIX")+" Insel konnte nicht geladen werden.");
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
								p.sendMessage(Language.getText(p, "PREFIX")+"�aEs wurden "+entities+" entfernt.");
							}else{
								p.sendMessage(Language.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "PLAYER_IS_OFFLINE",args[1]));
						}
					}
				}else if(args[0].equalsIgnoreCase("info")&&p.hasPermission(kPermission.GILDE_NEWISLAND.getPermissionToString())){
					if(args.length==1){
						
					}
				}
			}
		}
		return false;
	}
	
}

