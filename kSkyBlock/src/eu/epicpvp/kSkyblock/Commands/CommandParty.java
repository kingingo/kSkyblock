package eu.epicpvp.kSkyblock.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kSkyblock.kSkyBlock;
import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilFirework;
import eu.epicpvp.kcore.Util.UtilMath;
import lombok.Getter;

public class CommandParty implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	private Player p;
	
	public CommandParty(kSkyBlock instance){
		this.instance=instance;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "party",alias = {"feier"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		p=(Player)cs;
			if(args.length==0){
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_PREFIX"));
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_CMD1"));
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_CMD2"));
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_CMD3"));
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_CMD4"));
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_CMD5"));
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_CMD6"));
				p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PARTY_CMD7"));
			}else{
				if(args[0].equalsIgnoreCase("erstellen")||args[0].equalsIgnoreCase("create")){
					if(getInstance().getManager().haveIsland(p)){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world.createParty(p)){
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							UtilFirework.start(p.getLocation().add(UtilMath.r(4),UtilMath.RandomInt(10, 5),UtilMath.r(4)), UtilFirework.RandomColor(), UtilFirework.RandomType());
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_ERSTELLT"));
						}else{
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_IN"));
						}
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("einladen")||args[0].equalsIgnoreCase("invite")){
					if(args.length==2){
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world!=null){
							world.einladenParty(p, args[1]);
						}else{
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND"));
						}
					}else{
						p.sendMessage("§6/party einladen [Player] §8|§7 Einladen zur Party.");
					}
				}else if(args[0].equalsIgnoreCase("annehmen")||args[0].equalsIgnoreCase("accept")){
					boolean b = false;
					for(SkyBlockWorld world : getInstance().getManager().getWorlds()){
						if(world.getParty_einladungen().containsKey(p.getName().toLowerCase())){
							b=true;
							world.annehmenParty(p);
							break;
						}
					}
					if(!b)p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_EINLADEN_NO"));
				}else if(args[0].equalsIgnoreCase("schlie§en")||args[0].equalsIgnoreCase("verlassen")||args[0].equalsIgnoreCase("leave")||args[0].equalsIgnoreCase("close")){
					SkyBlockWorld world = getInstance().getManager().getParty(p);
					if(world!=null){
						world.verlassenParty(p,true);
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_NO"));
					}
				}else if(args[0].equalsIgnoreCase("home")){
					SkyBlockWorld world = getInstance().getManager().getParty(p);
					if(world!=null){
						world.homeParty(p);
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_NO"));
					}
				}else if(args[0].equalsIgnoreCase("kicken")){
					if(args.length==2){
						SkyBlockWorld world = getInstance().getManager().getParty(p);
						if(world!=null){
							world.kickenParty(p, args[1]);
						}else{
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_NO"));
						}
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§6/party kicken [Player]");
					}
				}else{
					p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "NO_BEFEHL"));
				}
			}
		
		return false;
	}
}