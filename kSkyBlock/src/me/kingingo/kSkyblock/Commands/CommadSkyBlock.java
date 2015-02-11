package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;

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
				p.sendMessage(Text.SKYBLOCK_PREFIX.getText());
				p.sendMessage("§6/skyblock erstellen §8|§7 Erstelle deine Insel.");
				p.sendMessage("§6/skyblock entfernen §8|§7 Lösche deine Insel.");
				p.sendMessage("§6/skyblock home §8|§7 Teleportiere dich zu deiner Insel.");
				p.sendMessage("§6/skyblock fixhome §8|§7 Teleportiere dich zu deiner Insel.");
			}else{
				if(args[0].equalsIgnoreCase("erstellen")){
					if(getInstance().getManager().haveIsland(p)){
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_HAVE_ISLAND);
					}else{
						SkyBlockWorld world = getInstance().getManager().addIsland(p);
						p.teleport(world.getIslandHome(p));
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_CREATE_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("entfernen")){
					if(getInstance().getManager().haveIsland(p)){
						p.teleport(Bukkit.getWorld("world").getSpawnLocation());
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						world.removeIsland(p);
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_REMOVE_ISLAND.getText());
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("home")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						p.teleport(world.getIslandHome(p));
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_TELEPORT_HOME.getText());
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("fixhome")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						p.teleport(world.getIslandFixHome(p));
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_TELEPORT_HOME.getText());
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
					}
				}
			}
		}
		return false;
	}
	
}

