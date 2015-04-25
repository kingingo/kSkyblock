package me.kingingo.kSkyblock.Commands;

import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHomedelete implements CommandExecutor{
	
	private Player player;
	private Player target;
	private Location loc;
	private CommandHomeaccept cmda;
	
	public CommandHomedelete(CommandHomeaccept cmda){
		this.cmda=cmda;
	}

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "homedelete",alias={"homeremove","hr"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		player = (Player)cs;
		if(args.length==0){
			player.sendMessage(Text.PREFIX.getText()+"/homedelete [Name]");
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
					player.sendMessage(Text.PREFIX.getText()+Text.HOME_SKYBLOCK_DELETE.getText(target.getName()));
				}
			}else{
				player.sendMessage(Text.PREFIX.getText()+Text.PLAYER_IS_OFFLINE.getText());
			}
		}
		return false;
	}
	
}