package me.kingingo.kSkyblock.Commands;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kSkyblock.SkyBlockManager;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.kConfig.kConfig;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDelivery implements CommandExecutor{
	
	private kConfig config;
	@Setter
	@Getter
	private static Location delivery;
	
	public CommandDelivery(kSkyBlock manager){
		this.config=new kConfig(new File("plugins"+File.separator+manager.getPlugin(manager.getClass()).getName()+File.separator+"locations.yml"));
		
		if(config.getString("DeliveryPet")!=null&&Bukkit.getWorld(config.getString("DeliveryPet.world"))!=null){
			if(config.isSet("DeliveryPet")){
				delivery=config.getLocation("DeliveryPet");
			}else{
				delivery=Bukkit.getWorld("world").getSpawnLocation();
			}
		}
	}

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "setdelivery", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		if(p.isOp()){
			if(args.length==0){
				config.setLocation("DeliveryPet", p.getLocation());
				config.save();
				setDelivery(p.getLocation());
				p.sendMessage(Language.getText(p, "PREFIX")+"§a Die Location für das DeliveryPet wurde gesetzt!");
			}
		}
		return false;
	}
	
}
