package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Inventory.InventoryBase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandSkyBlockInventory implements CommandExecutor{
	
	@Getter
	private Player player;
	private InventoryBase base;
	
	public CommandSkyBlockInventory(JavaPlugin instance){
		this.base=new InventoryBase(instance, "SkyBlock Men�");
	}

	@me.kingingo.kcore.Command.CommandHandler.Command(command = "si",alias={"skyblockinv"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		player = (Player)cs;
		if(args.length==0){
		}
		return false;
	}
	
}