package me.kingingo.kSkyblock.Commands;

import java.util.HashMap;

import me.kingingo.kSkyblock.SkyBlockManager;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.UserDataConfig.UserDataConfig;

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

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "homeaccept",alias={"ha","homeyes"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		player = (Player)cs;
		if(args.length==0){
			if(list.containsKey(player)){
				if(list.get(player).isOnline()){
					userData.getConfig(list.get(player)).setLocation("homes."+list_name.get(player), list_loc.get(player));
					list.get(player).sendMessage(Text.PREFIX.getText()+Text.HOME_SET.getText(list_name.get(player)));
					player.sendMessage(Text.PREFIX.getText()+Text.ACCEPT.getText());
					list_name.remove(player);
					list_loc.remove(player);
					list.remove(player);
				}
			}else{
				player.sendMessage(Text.PREFIX.getText()+Text.NO_ANFRAGE.getText());
			}
		}
		return false;
	}
	
}