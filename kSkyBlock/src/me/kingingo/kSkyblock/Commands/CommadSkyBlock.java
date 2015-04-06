package me.kingingo.kSkyblock.Commands;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilMath;

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
				p.sendMessage("�6/skyblock erstellen �8|�7 Erstelle deine Insel.");
				p.sendMessage("�6/skyblock entfernen �8|�7 L�sche deine Insel.");
				p.sendMessage("�6/skyblock home �8|�7 Teleportiere dich zu deiner Insel.");
				p.sendMessage("�6/skyblock fixhome �8|�7 Teleportiere dich zu deiner Insel.");
				p.sendMessage("�6/skyblock party �8|�7 Party Menue.");
			}else{
				if(args[0].equalsIgnoreCase("party")){
					if(args.length==1){
						p.sendMessage(Text.SKYBLOCK_PARTY_PREFIX.getText());
						p.sendMessage("�6/skyblock party erstellen �8|�7 Erstellt eine Party.");
						p.sendMessage("�6/skyblock party home �8|�7 Teleportiere dich zur Party.");
						p.sendMessage("�6/skyblock party verlassen �8|�7 Party verlassen.");
						p.sendMessage("�6/skyblock party annehmen �8|�7 Annehmen von Einladungen.");
						p.sendMessage("�6/skyblock party einladen [Player] �8|�7 Einladen zur Party.");
						p.sendMessage("�6/skyblock party schlie�en �8|�7 Schlie�t die Party.");
						p.sendMessage("�6/skyblock party kicken [Player] �8|�7 Kickt aus der Party.");
					}else{
						if(args[1].equalsIgnoreCase("erstellen")){
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
						}else if(args[1].equalsIgnoreCase("einladen")){
							if(args.length==3){
								SkyBlockWorld world = getInstance().getManager().getIsland(p);
								if(world!=null){
									world.einladenParty(p, args[2]);
								}else{
									p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_NO_ISLAND.getText());
								}
							}else{
								p.sendMessage("�6/skyblock party einladen [Player] �8|�7 Einladen zur Party.");
							}
						}else if(args[1].equalsIgnoreCase("annehmen")){
							boolean b = false;
							for(SkyBlockWorld world : getInstance().getManager().getWorlds()){
								if(world.getParty_einladungen().containsKey(p.getName().toLowerCase())){
									b=true;
									world.annehmenParty(p);
									break;
								}
							}
							if(!b)p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_EINLADEN_NO.getText());
						}else if(args[1].equalsIgnoreCase("schlie�en")||args[1].equalsIgnoreCase("verlassen")){
							SkyBlockWorld world = getInstance().getManager().getParty(p);
							if(world!=null){
								world.verlassenParty(p,true);
							}else{
								p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
							}
						}else if(args[1].equalsIgnoreCase("home")){
							SkyBlockWorld world = getInstance().getManager().getParty(p);
							if(world!=null){
								world.homeParty(p);
							}else{
								p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
							}
						}else if(args[1].equalsIgnoreCase("kicken")){
							if(args.length==3){
								SkyBlockWorld world = getInstance().getManager().getParty(p);
								if(world!=null){
									world.kickenParty(p, args[2]);
								}else{
									p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
								}
							}else{
								p.sendMessage("�6/skyblock party kicken [Player] �8|�7 Kickt aus der Party.");
							}
						}else{
							p.sendMessage(Text.PREFIX.getText()+Text.NO_BEFEHL.getText());
						}
					}
				}else if(args[0].equalsIgnoreCase("erstellen")){
					if(getInstance().getManager().haveIsland(p)){
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_HAVE_ISLAND.getText());
					}else{
						SkyBlockWorld world = getInstance().getManager().addIsland(p);
						if(world!=null){
							p.teleport(world.getIslandHome(p));
						}else{
							System.out.println("[SkyBlock] WORLD == NULL");
						}
						p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_CREATE_ISLAND.getText());
					}
				}else if(args[0].equalsIgnoreCase("entfernen")){
					if(getInstance().getManager().haveIsland(p)){
						p.teleport(Bukkit.getWorld("world").getSpawnLocation());
						SkyBlockWorld world = getInstance().getManager().getIsland(p);
						if(world.removeIsland(p))p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_REMOVE_ISLAND.getText());
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

