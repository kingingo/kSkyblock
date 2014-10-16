package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.Command.CommandHandler.Sender;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommadSkyBlock implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	
	public CommadSkyBlock(kSkyBlock instance){
		this.instance=instance;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "skyblock",alias = {"sb","sk","is","island"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(cs instanceof Player){
			Player p = (Player)cs;
			if(args.length==0){
				p.sendMessage("/skyblock erstellen");
				p.sendMessage("/skyblock entfernen");
				p.sendMessage("/skyblock home");
				p.sendMessage("/skyblock fixhome");
			}else{
				if(args[0].equalsIgnoreCase("erstellen")){
					if(getInstance().getManager().haveIsland(p)){
						p.sendMessage("");
					}else{
						SkyBlockWorld world = getInstance().getManager().addIsland(p);
						p.teleport(world.getIslandHome(p));
						p.sendMessage("");
					}
				}else if(args[0].equalsIgnoreCase("entfernen")){
					if(getInstance().getManager().haveIsland(p)){
						p.teleport(Bukkit.getWorld("world").getSpawnLocation());
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						world.removeIsland(p);
						p.sendMessage("");
					}else{
						p.sendMessage("");
					}
				}else if(args[0].equalsIgnoreCase("home")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().addIsland(p);
						p.teleport(world.getIslandHome(p));
						p.sendMessage("");
					}else{
						p.sendMessage("");
					}
				}else if(args[0].equalsIgnoreCase("fixhome")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().addIsland(p);
						p.teleport(world.getIslandFixHome(p));
						p.sendMessage("");
					}else{
						p.sendMessage("");
					}
				}
			}
		}
		return false;
	}
	
}

