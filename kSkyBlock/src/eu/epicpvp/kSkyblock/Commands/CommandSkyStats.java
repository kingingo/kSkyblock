package eu.epicpvp.kSkyblock.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Listener.kListener;

public class CommandSkyStats extends kListener implements CommandExecutor{
	
	private SkyBlockManager manager;
	
	public CommandSkyStats(SkyBlockManager manager){
		super(manager.getInstance(),"CommandSkyStats");
		this.manager=manager;
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "monitoring", sender = Sender.CONSOLE)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		
		System.err.println("--------------------------------------");
		System.err.println("Player List: "+manager.getPlayers().size());
		System.err.println("Worlds:");
		for(SkyBlockWorld world : manager.getWorlds()){
			System.err.println("        "+world.getMinecraftWorld().getName()+" loaded Islands: "+world.getIslands().size());
		}
		System.err.println("--------------------------------------");
		
		return false;
	}
	
}