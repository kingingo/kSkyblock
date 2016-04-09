package eu.epicpvp.kSkyblock.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Translation.TranslationManager;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class CommandHomedelete implements CommandExecutor{
	
	private Player player;
	private Player target;
	private Location loc;
	private CommandHomeaccept cmda;
	
	public CommandHomedelete(CommandHomeaccept cmda){
		this.cmda=cmda;
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "homedelete",alias={"homeremove","hr"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		player = (Player)cs;
		if(args.length==0){
			player.sendMessage(TranslationManager.getText(player, "PREFIX")+"/homedelete [Name]");
		}else{
			if(UtilPlayer.isOnline(args[0])){
				if(cmda.manager.haveIsland(player)){
					SkyBlockWorld world = cmda.manager.getIsland(player);
					target=Bukkit.getPlayer(args[0]);
					for(String path : cmda.userData.getConfig(target).getPathList("homes").keySet()){
						loc=cmda.userData.getConfig(target).getLocation("homes."+path);
						if(loc.getWorld().getName().equalsIgnoreCase(world.getWorld().getName())){
							if(world.isInIsland(player, loc)){
								cmda.userData.getConfig(target).set("homes."+path, null);
							}
						}
					}
					cmda.userData.getConfig(target).save();
					player.sendMessage(TranslationManager.getText(player, "PREFIX")+TranslationManager.getText(player, "HOME_SKYBLOCK_DELETE",target.getName()));
				}
			}else{
				player.sendMessage(TranslationManager.getText(player, "PLAYER_IS_OFFLINE",args[0]));
			}
		}
		return false;
	}
	
}