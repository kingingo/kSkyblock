package eu.epicpvp.kSkyblock.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Translation.TranslationManager;
import lombok.Getter;

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
					player.sendMessage(TranslationManager.getText(player, "PREFIX")+TranslationManager.getText(player, "DENY"));
					cmda.list_name.remove(player);
					cmda.list_loc.remove(player);
					cmda.list.remove(player);
				}
			}else{
				player.sendMessage(TranslationManager.getText(player, "PREFIX")+TranslationManager.getText(player, "NO_ANFRAGE"));
			}
		}
		return false;
	}
	
}