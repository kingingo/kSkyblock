package eu.epicpvp.Skyblock.Commands;

import lombok.Getter;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Language.Language;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHomedeny implements CommandExecutor{
	
	@Getter
	private Player player;
	private CommandHomeaccept cmda;
	
	public CommandHomedeny(CommandHomeaccept cmda){
		this.cmda=cmda;
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "homedeny",alias={"hd","homeno"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		player = (Player)cs;
		if(args.length==0){
			if(cmda.list.containsKey(player)){
				if(cmda.list.get(player).isOnline()){
					player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "DENY"));
					cmda.list_name.remove(player);
					cmda.list_loc.remove(player);
					cmda.list.remove(player);
				}
			}else{
				player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "NO_ANFRAGE"));
			}
		}
		return false;
	}
	
}