package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParty implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	private Player p;
	
	public CommandParty(kSkyBlock instance){
		this.instance=instance;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "party",alias = {"feier"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		p=(Player)cs;
			if(args.length==0){
				p.sendMessage(Text.SKYBLOCK_PARTY_PREFIX.getText());
				p.sendMessage("ß6/party erstellen ß8|ß7 Erstellt eine Party.");
				p.sendMessage("ß6/party home ß8|ß7 Teleportiere dich zur Party.");
				p.sendMessage("ß6/party verlassen ß8|ß7 Party verlassen.");
				p.sendMessage("ß6/party annehmen ß8|ß7 Annehmen von Einladungen.");
				p.sendMessage("ß6/party einladen [Player] ß8|ß7 Einladen zur Party.");
				p.sendMessage("ß6/party schlieﬂen ß8|ß7 Schlieﬂt die Party.");
				p.sendMessage("ß6/party kicken [Player] ß8|ß7 Kickt aus der Party.");
			}else{
				if(args[0].equalsIgnoreCase("erstellen")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world.createParty(p)){
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_ERSTELLT.getText());
						}else{
							p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_IN.getText());
						}
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("einladen")){
					if(args.length==2){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world!=null){
							world.einladenParty(p, args[1]);
						}else{
							p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
						}
					}else{
						p.sendMessage("ß6/party einladen [Player] ß8|ß7 Einladen zur Party.");
					}
				}else if(args[0].equalsIgnoreCase("annehmen")){
					boolean b = false;
					for(SkyBlockWorld world : getInstance().getManager().getWorlds()){
						if(world.getParty_einladungen().containsKey(p.getName().toLowerCase())){
							b=true;
							world.annehmenParty(p);
							break;
						}
					}
					if(!b)p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_EINLADEN_NO.getText());
				}else if(args[0].equalsIgnoreCase("schlieﬂen")||args[0].equalsIgnoreCase("verlassen")){
					SkyBlockWorld world = getInstance().getManager().getParty(p);
					if(world!=null){
						world.verlassenParty(p,true);
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
					}
				}else if(args[0].equalsIgnoreCase("home")){
					SkyBlockWorld world = getInstance().getManager().getParty(p);
					if(world!=null){
						world.homeParty(p);
					}else{
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
					}
				}else if(args[0].equalsIgnoreCase("kicken")){
					if(args.length==2){
						SkyBlockWorld world = getInstance().getManager().getParty(p);
						if(world!=null){
							world.kickenParty(p, args[1]);
						}else{
							p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
						}
					}else{
						p.sendMessage("ß6/party kicken [Player] ß8|ß7 Kickt aus der Party.");
					}
				}else{
					p.sendMessage(Text.PREFIX.getText()+Text.NO_BEFEHL.getText());
				}
			}
		
		return false;
	}

}
