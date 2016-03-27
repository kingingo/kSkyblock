package eu.epicpvp.Skyblock.Commands;

import java.util.HashMap;

import eu.epicpvp.Skyblock.SkyBlockManager;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.UserDataConfig.UserDataConfig;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHomeaccept implements CommandExecutor{
	
	private Player player;
	public HashMap<Player,Player> list = new HashMap<>();
	public HashMap<Player,Location> list_loc = new HashMap<>();
	public HashMap<Player,String> list_name = new HashMap<>();
	public UserDataConfig userData;
	public SkyBlockManager manager;
	
	public CommandHomeaccept(SkyBlockManager manager){
		this.userData=manager.getInstance().getUserData();
		this.manager=manager;
		manager.getInstance().getCmd().register(this.getClass(), this);
		manager.getInstance().getCmd().register(CommandHomedelete.class, new CommandHomedelete(this));
		manager.getInstance().getCmd().register(CommandHomedeny.class, new CommandHomedeny(this));
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "homeaccept",alias={"ha","homeyes"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		player = (Player)cs;
		if(args.length==0){
			if(list.containsKey(player)){
				if(list.get(player).isOnline()){
					userData.getConfig(list.get(player)).setLocation("homes."+list_name.get(player), list_loc.get(player));
					list.get(player).sendMessage(Language.getText(list.get(player), "PREFIX")+Language.getText(list.get(player), "HOME_SET",list_name.get(player)));
					player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "ACCEPT"));
					list_name.remove(player);
					list_loc.remove(player);
					list.remove(player);
				}
			}else{
				player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "NO_ANFRAGE"));
			}
		}
		return false;
	}
	
}