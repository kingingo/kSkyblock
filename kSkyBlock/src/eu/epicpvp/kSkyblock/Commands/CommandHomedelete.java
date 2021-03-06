package eu.epicpvp.kSkyblock.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

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
			player.sendMessage(TranslationHandler.getText(player, "PREFIX")+"/homedelete [Name]");
		}else{
			if(UtilPlayer.isOnline(args[0])){
				if(cmda.manager.haveIsland(player)){
					Island island = cmda.manager.getIsland(player);
					target= UtilServer.getNickedPlayer(args[0]);
					for(String path : cmda.userData.getConfig(target).getPathList("homes").keySet()){
						loc=cmda.userData.getConfig(target).getLocation("homes."+path);
						if(loc.getWorld().getName().equalsIgnoreCase(island.getLocation().getWorld().getName())){
							if(island.contains(loc)){
								cmda.userData.getConfig(target).set("homes."+path, null);
							}
						}
					}
					cmda.userData.getConfig(target).save();
					player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "HOME_SKYBLOCK_DELETE",target.getName()));
				}
			}else{
				player.sendMessage(TranslationHandler.getText(player, "PLAYER_IS_OFFLINE",args[0]));
			}
		}
		return false;
	}
	
}