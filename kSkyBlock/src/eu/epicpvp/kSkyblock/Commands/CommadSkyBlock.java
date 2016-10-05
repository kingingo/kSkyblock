package eu.epicpvp.kSkyblock.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kSkyblock.kSkyBlock;
import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kSkyblock.World.Island.kPlayer;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.TeleportManager.Teleporter;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;

public class CommadSkyBlock implements CommandExecutor{
	
	@Getter
	private kSkyBlock instance;
	
	public CommadSkyBlock(kSkyBlock instance){
		this.instance=instance;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "skyblock",alias = {"sb","sk","is","island","s"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(cs instanceof Player){
			Player p = (Player)cs;
			if(args.length==0){
				getInstance().getManager().getSkyblockInventoryHandler().openInv(p);
			}else{
//				if(args[0].equalsIgnoreCase("convert")&&p.isOp()){
//					HomeConverter.convert(instance.getManager());
//				}
				if(args[0].equalsIgnoreCase("help")||args[0].equalsIgnoreCase("hilfe")){
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_PREFIX"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD1"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD2"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD3"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD4"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD5"));
					if(p.hasPermission(PermissionType.GILDE_NEWISLAND.getPermissionToString()))p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD6"));
					if(p.isOp())p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD7"));
					if(p.isOp())p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD8"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD9"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD10"));
					p.sendMessage(TranslationHandler.getText(p, "SKYBLOCK_CMD11"));
				}else if(args[0].equalsIgnoreCase("erstellen")||args[0].equalsIgnoreCase("create")){
					if(getInstance().getManager().haveIsland(p)){
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_HAVE_ISLAND"));
					}else{
						Island island = getInstance().getManager().addIsland(p);
						if(island!=null){
							p.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
							p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET,2));
							p.getInventory().addItem(new ItemStack(61,2));
							p.getInventory().addItem(new ItemStack(362,2));
							p.getInventory().addItem(new ItemStack(295,2));
							p.getInventory().addItem(new ItemStack(351,1,(byte)3));
							p.getInventory().addItem(new ItemStack(6,1));
							p.getInventory().addItem(new ItemStack(6,1,(byte)2));
							p.getInventory().addItem(new ItemStack(40,2));
							p.getInventory().addItem(new ItemStack(32,2));
							p.getInventory().addItem(new ItemStack(260,10));
							p.getInventory().addItem(new ItemStack(141,10));
							p.getInventory().addItem(new ItemStack(360,15));
							p.getInventory().addItem(new ItemStack(287,10));
							p.getInventory().addItem(new ItemStack(352,10));
							p.teleport(island.getHome());
						}else{
							System.out.println("[SkyBlock] WORLD == NULL");
						}
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_CREATE_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("kick")){
					if(args.length>=2){
						if(UtilPlayer.isOnline(args[1])){
							if(getInstance().getManager().haveIsland(p)){
								Island island = getInstance().getManager().getIsland(p);
								Player target=Bukkit.getPlayer(args[1]);
								
								if(island.contains(target.getLocation())){
									target.teleport(Bukkit.getWorld("world").getSpawnLocation());
									target.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PLAYER_KICKED",p.getName()));
									p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PLAYER_KICK",target.getName()));
								}else{
									p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PLAYER_NOT_ON_YOUR_ISLAND",target.getName()));
								}
							}else{
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND"));
							}
						}else{
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "PLAYER_IS_OFFLINE",args[1]));
						}
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§6/skyblock kick [Player]");
					}
				}else if(args[0].equalsIgnoreCase("entfernen")||args[0].equalsIgnoreCase("delete")||args[0].equalsIgnoreCase("remove")){
					if(getInstance().getManager().haveIsland(p)){
						p.teleport(Bukkit.getWorld("world").getSpawnLocation());
						Island island = getInstance().getManager().getIsland(p);
						if(island.delete())p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_REMOVE_ISLAND"));
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("home")){
					if(args.length==1){
						if(!getInstance().getAntiLogout().is(p)){
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausführen!");
							return false;
						}
						if(getInstance().getManager().haveIsland(p)){
							Island island = getInstance().getManager().getIsland(p);
							getInstance().getTeleport().getTeleport().add(new Teleporter(p, island.getHome(), 3));
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_TELEPORT_HOME"));
						}else{
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND"));
						}
					}else if(p.hasPermission(PermissionType.SKYBLOCK_HOME_OTHER.getPermissionToString())){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = UtilServer.getNickedPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								Island island = getInstance().getManager().getIsland(tp);
								getInstance().getTeleport().getTeleport().add(new Teleporter(p,tp, island.getHome(), 3));
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§aDu wurdest zur Insel teleportiert.");
							}else{
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "PLAYER_IS_OFFLINE",args[1]));
							int playerId = UtilPlayer.getPlayerId(args[1]);
							if(!getInstance().getManager().haveIsland(playerId)){
								for(SkyBlockWorld world : instance.getManager().getWorlds())world.loadIslandPlayer( playerId );
							}
							
							if(getInstance().getManager().haveIsland(playerId)){
								Island island = getInstance().getManager().getIsland(playerId);
								p.teleport(island.getHome());
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§aDu wurdest zur Insel teleportiert.");
							}else{
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+" Insel konnte nicht geladen werden.");
							}
						}
					}
				}else if(args[0].equalsIgnoreCase("fixhome")){
					if(getInstance().getManager().haveIsland(p)){
						if(!getInstance().getAntiLogout().is(p)){
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausf§hren!");
							return false;
						}
						Island island = getInstance().getManager().getIsland(p);
						getInstance().getTeleport().getTeleport().add(new Teleporter(p, island.getFixHome(), 3));
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_TELEPORT_HOME"));
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("invite")||args[0].equalsIgnoreCase("einladen")&&args.length==2){
					Island is = getInstance().getManager().getIsland(p);
					if(is.getMember().size()>=3)return false;
					
					if(p.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_4.getPermissionToString())){
						if(is.getMember().size()>=4){
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
							return false;
						}
					}else if(p.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_3.getPermissionToString())){
						if(is.getMember().size()>=3){
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
							return false;
						}
					}else if(p.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_2.getPermissionToString())){
						if(is.getMember().size()>=2){
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
							return false;
						}
					}else if(p.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_1.getPermissionToString())){
						if(is.getMember().size()>=1){
							p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
							return false;
						}
					}
					
					if(UtilPlayer.isOnline(args[1])){
						Player target = UtilServer.getNickedPlayer(args[1]);
						getInstance().getManager().getInvite().put(target.getName(), p.getName());
						TextComponent text = new TextComponent(TranslationHandler.getPrefixAndText(target, "SKYBLOCK_INVITE_GET",p.getName()));
						text.addExtra(UtilPlayer.createClickableText(" §a[ACCEPT]", "/is accept "+p.getName()));
						target.spigot().sendMessage(text);
						p.sendMessage(TranslationHandler.getPrefixAndText(p, "SKYBLOCK_INVITE_SEND",target.getName()));
					}else{
						p.sendMessage(TranslationHandler.getPrefixAndText(p, "PLAYER_IS_OFFLINE", args[1]));
					}
				}else if(args[0].equalsIgnoreCase("accept")&&args.length==2){
					if(getInstance().getManager().getInvite().containsKey(p.getName())){
						if(getInstance().getManager().getInvite().get(p.getName()).equalsIgnoreCase(args[1])){
							getInstance().getManager().getInvite().remove(p.getName());
							
							if(UtilPlayer.isOnline(args[1])){
								Player owner = Bukkit.getPlayer(args[1]);
								Island island = getInstance().getManager().getIsland(owner);
								if(island.getMember().size()>=3)return false;
								
								if(owner.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_4.getPermissionToString())){
									if(island.getMember().size()>=4){
										return false;
									}
								}else if(owner.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_3.getPermissionToString())){
									if(island.getMember().size()>=3){
										return false;
									}
								}else if(owner.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_2.getPermissionToString())){
									if(island.getMember().size()>=2){
										return false;
									}
								}else if(owner.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_1.getPermissionToString())){
									if(island.getMember().size()>=1){
										return false;
									}
								}
								
								int playerId = UtilPlayer.getPlayerId(p);
								if(p.hasPermission(PermissionType.SKYBLOCK_MEMBER_PLAYER_3.getPermissionToString())){
									if(getInstance().getManager().getPlayers().containsKey(playerId)){
										kPlayer kplayer = getInstance().getManager().getPlayers().get(playerId);
										
										if(kplayer.getMemberList().size() >= 3){
											p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
											return false;
										}
									}
								}else if(p.hasPermission(PermissionType.SKYBLOCK_MEMBER_PLAYER_2.getPermissionToString())){
									if(getInstance().getManager().getPlayers().containsKey(playerId)){
										kPlayer kplayer = getInstance().getManager().getPlayers().get(playerId);
										
										if(kplayer.getMemberList().size() >= 2){
											p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
											return false;
										}
									}
								}else if(p.hasPermission(PermissionType.SKYBLOCK_MEMBER_PLAYER_1.getPermissionToString())){
									if(getInstance().getManager().getPlayers().containsKey(playerId)){
										kPlayer kplayer = getInstance().getManager().getPlayers().get(playerId);
										
										if(kplayer.getMemberList().size() >= 1){
											p.sendMessage(TranslationHandler.getText(p,"PREFIX")+"§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
											return false;
										}
									}
								}
								
								if(island!=null && island.addMember(UtilPlayer.getPlayerId(p))){
									p.sendMessage(TranslationHandler.getPrefixAndText(p, "SKYBLOCK_INVITE_ACCEPT", owner.getName()));
									owner.sendMessage(TranslationHandler.getPrefixAndText(p, "SKYBLOCK_MEMBER_ENTER", p.getName()));
								}
							}
						}
					}
				}else if(args[0].equalsIgnoreCase("biome")){
					if(getInstance().getManager().haveIsland(p)){
						Island island = getInstance().getManager().getIsland(p);
						island.setBiome(Biome.JUNGLE);
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND",Biome.JUNGLE.name()));
					}else{
						p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_NO_ISLAND"));
					}
				}else if(args[0].equalsIgnoreCase("newisland")&&p.hasPermission(PermissionType.GILDE_NEWISLAND.getPermissionToString())){
					if(args.length==2){
						if(UtilPlayer.isOnline(args[1])){
							Player tp = Bukkit.getPlayer(args[1]);
							if(getInstance().getManager().haveIsland(tp)){
								Island island = getInstance().getManager().getIsland(tp);
								island.reset(true);
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§aDie Insel wurde erneuert.");
							}else{
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"Er hat keine Insel.");
							}
						}else{
							p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "PLAYER_IS_OFFLINE",args[1]));
							int playerId = UtilPlayer.getPlayerId(args[1]);
							if(!getInstance().getManager().haveIsland(playerId)){
								for(SkyBlockWorld world : instance.getManager().getWorlds())world.loadIslandPlayer( playerId );
							}
							
							if(getInstance().getManager().haveIsland(playerId)){
								Island island = getInstance().getManager().getIsland(playerId);
								island.reset(true);
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+"§aDie Insel wurde erneuert.");
							}else{
								p.sendMessage(TranslationHandler.getText(p, "PREFIX")+" Insel konnte nicht geladen werden.");
							}
						}
					}
				}
			}
		}
		return false;
	}
	
}

