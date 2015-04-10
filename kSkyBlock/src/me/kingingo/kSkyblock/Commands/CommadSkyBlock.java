package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommadSkyBlock implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	private Player p;
	
	public CommadSkyBlock(kSkyBlock instance){
		this.instance=instance;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "skyblock",alias = {"sb","sk","is","island","s"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(cs instanceof Player){
			p = (Player)cs;
			if(args.length==0){
				p.sendMessage(Text.SKYBLOCK_PREFIX.getText());
				p.sendMessage("§6/skyblock erstellen §8|§7 Erstelle deine Insel.");
				p.sendMessage("§6/skyblock entfernen §8|§7 Lösche deine Insel.");
				p.sendMessage("§6/skyblock home §8|§7 Teleportiere dich zu deiner Insel.");
				p.sendMessage("§6/skyblock fixhome §8|§7 Teleportiere dich zu deiner Insel.");
				p.sendMessage("§6/party §8|§7 Party Menue.");
			}else{
				if(args[0].equalsIgnoreCase("erstellen")){
					if(getInstance().getManager().haveIsland(p)){
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_HAVE_ISLAND.getText());
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
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_CREATE_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("entfernen")){
					if(getInstance().getManager().haveIsland(p)){
						p.teleport(Bukkit.getWorld("world").getSpawnLocation());
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world.removeIsland(p))p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_REMOVE_ISLAND.getText());
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("home")){
					if(args.length==1){
						if(getInstance().getManager().haveIsland(p)){
							SkyBlockWorld world = getInstance().getManager().getIsland(p);
							p.teleport(world.getIslandHome(p));
							p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_TELEPORT_HOME.getText());
						}else{
							p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
						}
					}else if(p.hasPermission(kPermission.SKYBLOCK_HOME_OTHER.getPermissionToString())){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								SkyBlockWorld world = getInstance().getManager().getIsland(tp);
								p.teleport(world.getIslandHome(tp));
								p.sendMessage(Text.PREFIX.getText()+"§aDu wurdest zur Insel teleportiert.");
							}else{
								p.sendMessage(Text.PREFIX.getText()+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(Text.PREFIX.getText()+Text.PLAYER_IS_OFFLINE.getText());
						}
					}
				}else if(args[0].equalsIgnoreCase("fixhome")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						p.teleport(world.getIslandFixHome(p));
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_TELEPORT_HOME.getText());
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("biome")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						world.setBiome(UtilPlayer.getRealUUID(p).toString(), Biome.JUNGLE);
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_CHANGE_BIOME.getText(Biome.JUNGLE.name()));
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("newisland")&&p.hasPermission(kPermission.GILDE_NEWISLAND.getPermissionToString())){
					if(args.length==2){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								SkyBlockWorld world = getInstance().getManager().getIsland(tp);
								world.newIsland(tp);
								p.sendMessage(Text.PREFIX.getText()+"§aDie Insel wurde erneuert.");
							}else{
								p.sendMessage(Text.PREFIX.getText()+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(Text.PREFIX.getText()+Text.PLAYER_IS_OFFLINE.getText());
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
				}
			}
		}
		return false;
	}
	
}

