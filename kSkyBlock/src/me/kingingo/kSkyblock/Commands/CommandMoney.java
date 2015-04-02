package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.PlayerStats.Stats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMoney implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	
	public CommandMoney(kSkyBlock instance){
		this.instance=instance;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "money",alias = {"geld","konto","kontostand","stand"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(cs instanceof Player){
			Player p = (Player)cs;
			if(args.length==0){
				p.sendMessage(Text.PREFIX_GAME.getText(GameType.SKYBLOCK.getTyp())+"Dein Kontostand beträgt:§3 " + instance.getStatsManager().getDouble(Stats.MONEY, p));
			}else if(p.isOp()||instance.getPermissionManager().hasPermission(p, kPermission.ADMIN_SERVICE)){
				p.sendMessage(Text.PREFIX_GAME.getText(GameType.SKYBLOCK.getTyp())+"Der Kontostand von "+args[0]+" beträgt:§3 " + instance.getStatsManager().getDoubleWithUUID(Stats.MONEY, args[0]));
			}
		}
		return false;
	}
	
}

