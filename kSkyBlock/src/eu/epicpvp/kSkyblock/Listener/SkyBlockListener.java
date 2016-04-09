package eu.epicpvp.kSkyblock.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.kSkyblock.kSkyBlock;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.GemsShop.Events.PlayerGemsBuyEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Permission.Events.PlayerLoadPermissionEvent;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.SignShop.Events.SignShopUseEvent;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsChangeEvent;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Translation.TranslationManager;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.UserStores.Events.PlayerCreateUserStoreEvent;
import eu.epicpvp.kcore.Util.RestartScheduler;
import eu.epicpvp.kcore.Util.TabTitle;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import lombok.Getter;

public class SkyBlockListener extends kListener{

	@Getter
	private kSkyBlock manager;
	private HashMap<Player,Location> player_loc = new HashMap<>();
	private ArrayList<UUID> vote_list = new ArrayList<>();
	
	public SkyBlockListener(kSkyBlock manager) {
		super(manager.getAntiLogout().getInstance(), "Listener");
		this.manager=manager;
	}
	
	@EventHandler
	public void buygems(PlayerGemsBuyEvent ev){
		if(ev.getItem().hasItemMeta()&&ev.getItem().getItemMeta().hasDisplayName()&&ev.getItem().getItemMeta().getDisplayName().toLowerCase().contains("usershop")){
			if(UtilServer.getUserData()!=null){
				UtilServer.getUserData().getConfig(ev.getPlayer()).set("Stores", UtilServer.getUserData().getConfig(ev.getPlayer()).getInt("Stores")+1);
				UtilServer.getUserData().getConfig(ev.getPlayer()).save();
				
				UtilScoreboard.resetScore(ev.getPlayer().getScoreboard(), 2, DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), ""+UtilServer.getUserData().getConfig(ev.getPlayer()).getInt("Stores"), DisplaySlot.SIDEBAR, 2);
			}
		}
	}
	
	@EventHandler
	public void userstore(PlayerCreateUserStoreEvent ev){
		UtilScoreboard.resetScore(ev.getPlayer().getScoreboard(), 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), ""+UtilServer.getUserData().getConfig(ev.getPlayer()).getInt("Stores"), DisplaySlot.SIDEBAR, 2);
	}
	
	@EventHandler
	public void statsMONEY(PlayerStatsChangeEvent ev){
		if(ev.getManager().getType() != GameType.Money){
			if(ev.getStats() == StatsKey.MONEY){
				if(UtilPlayer.isOnline(ev.getPlayername())){
					UtilScoreboard.resetScore(Bukkit.getPlayer(ev.getPlayername()).getScoreboard(), 5, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(Bukkit.getPlayer(ev.getPlayername()).getScoreboard(),UtilMath.trim(2, getManager().getStatsManager().getDouble(Bukkit.getPlayer(ev.getPlayername()), StatsKey.MONEY))+"$", DisplaySlot.SIDEBAR, 5);
				}
			}
		}
	}
	
	@EventHandler
	public void AddBoard(PlayerSetScoreboardEvent ev){
		UtilPlayer.setSkyBlockScoreboard(ev.getPlayer(),UtilServer.getGemsShop().getGems(), getManager().getStatsManager(), UtilServer.getUserData());
	}
	
//	Player player;
//	@EventHandler
//	public void Receive(PacketReceiveEvent ev){
//		if(ev.getPacket() instanceof PLAYER_VOTE){
//			PLAYER_VOTE vote = (PLAYER_VOTE)ev.getPacket();
//			
//			if(UtilPlayer.isOnline(vote.getPlayer())){
//				if(UtilServer.getDeliveryPet()!=null){
//					 UtilServer.getDeliveryPet().deliveryUSE(Bukkit.getPlayer(vote.getPlayer()), "§aVote for EpicPvP", true);
//				 }
//				
//				player=Bukkit.getPlayer(vote.getPlayer());
//				manager.getStatsManager().setDouble(player, manager.getStatsManager().getDouble(Stats.MONEY, player)+200, Stats.MONEY);
//				player.getInventory().addItem(new ItemStack(Material.DIAMOND,2));
//				player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
//				player.getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
//				player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "VOTE_THX"));
//			}else{
//				vote_list.add(vote.getUuid());
//			}
//		}else if(ev.getPacket() instanceof TWITTER_PLAYER_FOLLOW){
//			TWITTER_PLAYER_FOLLOW tw = (TWITTER_PLAYER_FOLLOW)ev.getPacket();
//			
//			if(UtilPlayer.isOnline(tw.getPlayer())){
//				Player p = Bukkit.getPlayer(tw.getPlayer());
//				if(!tw.isFollow()){
//					getManager().getMysql().Update("DELETE FROM BG_TWITTER WHERE uuid='" + UtilPlayer.getRealUUID(p) + "'");
//					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_FOLLOW_N"));
//					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_REMOVE"));
//				}else{
//					UtilServer.getDeliveryPet().deliveryBlock(p, "§cTwitter Reward");
//					getManager().getStatsManager().addDouble(p, 300, Stats.MONEY);
//					p.setLevel(p.getLevel()+15);
//					p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","300"}));
//				}
//			}
//		}
//	}
	
	@EventHandler
	public void onClickinEnchant(EnchantItemEvent e){
		if(e.getItem().getAmount() > 1){
			e.setCancelled(true);
			e.getEnchanter().sendMessage("§cFEHLER: BuggUsing ist verboten!");
		}
	}
	
	@EventHandler
	public void wheather(WeatherChangeEvent ev){
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void Explosion(ExplosionPrimeEvent ev){
		ev.setCancelled(true);
	}
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event){
	    if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void onSign(SignShopUseEvent ev){
		if(!ev.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			ev.getSign().setLine(0, "N§!");
			ev.getSign().setLine(1, "N§!");
			ev.getSign().setLine(2, "N§!");
			ev.getSign().setLine(3, "N§!");
			ev.getSign().update(true);
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Pickup(PlayerPickupItemEvent ev){
		if(ev.getItem().getItemStack().getAmount()<0||ev.getItem().getItemStack().getAmount()>64){
			ev.getItem().remove();
	        ev.getPlayer().sendMessage("§cFEHLER: BuggUsing ist verboten!");
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Drop(InventoryClickEvent ev){
		if(ev.getWhoClicked() instanceof Player){
			if(ev.getInventory()!=null&&ev.getCurrentItem()!=null){
				
				if(ev.getCurrentItem().getAmount()<0||ev.getCurrentItem().getAmount()>64){
					ev.getCurrentItem().setAmount(1);
					ev.getCurrentItem().setType(Material.AIR);
					((Player)ev.getWhoClicked()).sendMessage("§cFEHLER: BuggUsing ist verboten!");
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Drop(PlayerDropItemEvent ev){
		if(ev.getItemDrop().getItemStack().getAmount()<0||ev.getItemDrop().getItemStack().getAmount()>64){
			ev.getItemDrop().remove();
	        ev.getPlayer().sendMessage("§cFEHLER: BuggUsing ist verboten!");
		}
	}
	
	@EventHandler
	public void onClickinAnvil(InventoryClickEvent e){
	    try{
	      if ((e.getInventory().getType() == InventoryType.ANVIL) && 
	        (e.getCurrentItem().getAmount() > 1)){
	        e.setCancelled(true);
	        Player ps = (Player)e.getWhoClicked();
	        ps.sendMessage("§cFEHLER: BuggUsing ist verboten!");
	      }
	    }
	    catch (Exception localException){}
	  }
	
	@EventHandler
	public void Sign(SignChangeEvent ev){
		if(ev.getPlayer().hasPermission(PermissionType.CHAT_FARBIG.getPermissionToString())){
			ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
			ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
			ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
			ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void Create(CreatureSpawnEvent ev){
		if(ev.getSpawnReason() == SpawnReason.CUSTOM || ev.getSpawnReason() == SpawnReason.DEFAULT){
			ev.setCancelled(false);
		}else if(ev.getLocation().getWorld().getName().equalsIgnoreCase("world")){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		getManager().getStatsManager().save(ev.getPlayer());
	}
	
	@EventHandler
	public void Kick(PlayerKickEvent ev){
		System.out.println("[EpicPvP] "+ev.getPlayer().getName()+" L:"+ev.getLeaveMessage()+" R:"+ev.getReason());
	}
	
	
	Player death;
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		ev.setDeathMessage(null);
		if(ev.getEntity() instanceof Player){
			death=(Player)ev.getEntity();;
			UtilPlayer.RespawnNow(death, manager);
			getManager().getStatsManager().add(death, StatsKey.DEATHS,1);
			if(ev.getEntity().getKiller() instanceof Player)getManager().getStatsManager().add(ev.getEntity().getKiller(),StatsKey.KILLS, 1);
		}
	}
	
	Player fall;
	@EventHandler
	public void Death(EntityDamageEvent ev){
		if(ev.getCause()==DamageCause.VOID&&ev.getEntity() instanceof Player&&ev.getEntity().getWorld().getName().equalsIgnoreCase("world")){
			if(!ev.getEntity().isOnGround()){
				fall=(Player)ev.getEntity();
				if(player_loc.containsKey(fall)){
					fall.teleport(player_loc.get(fall));
				}else{
					fall.teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
				UtilPlayer.damage(fall, 35.0);
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void loadPerm(PlayerLoadPermissionEvent ev){
		if(ev.getPlayer().getScoreboard()==null)ev.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
	@EventHandler
	public void loadedStats(PlayerStatsLoadedEvent ev){
		if(ev.getManager().getType() != GameType.Money){
			if(UtilPlayer.isOnline(ev.getPlayername())){
				if(vote_list.contains( UtilPlayer.getRealUUID(Bukkit.getPlayer(ev.getPlayername())) )){
					if(UtilServer.getDeliveryPet()!=null){
						 UtilServer.getDeliveryPet().deliveryUSE(Bukkit.getPlayer(ev.getPlayername()), "§aVote for EpicPvP", true);
					 }
					
					vote_list.remove(UtilPlayer.getRealUUID(Bukkit.getPlayer(ev.getPlayername())));
					manager.getStatsManager().add(Bukkit.getPlayer(ev.getPlayername()), StatsKey.MONEY,200);
					Bukkit.getPlayer(ev.getPlayername()).getInventory().addItem(new ItemStack(Material.DIAMOND,2));
					Bukkit.getPlayer(ev.getPlayername()).getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
					Bukkit.getPlayer(ev.getPlayername()).getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
					Bukkit.getPlayer(ev.getPlayername()).sendMessage(TranslationManager.getText(Bukkit.getPlayer(ev.getPlayername()), "PREFIX")+TranslationManager.getText(Bukkit.getPlayer(ev.getPlayername()), "VOTE_THX"));
				}
			}
		}
	}
	
	@EventHandler
	public void update(ServerStatusUpdateEvent ev){
		ev.getPacket().setPlayers(UtilServer.getPlayers().size());
		ev.getPacket().setTyp(GameType.SKYBLOCK);
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		getManager().getStatsManager().loadPlayer(ev.getPlayer());
		getManager().getMoney().loadPlayer(ev.getPlayer());
		getManager().getManager().getGilden_world().getGilde().loadPlayer(ev.getPlayer());
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEpicPvP§8.§eeu §8| §aSkyBlock Server", "§aTeamSpeak: §7ts.EpicPvP.eu §8| §eWebsite: §7EpicPvP.eu");
	}
	
	@EventHandler
	public void UpdaterFly(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_2){
			for(Player player : Bukkit.getWorld("world").getPlayers()){
				if(UtilWorldGuard.RegionFlag(player, DefaultFlag.PVP)){
					if(!player.hasPermission("epicpvp.kfly.pvp_allow")&&player.isFlying()){
						player.teleport(Bukkit.getWorld("world").getSpawnLocation());
						player.setAllowFlight(false);
						player.setFlying(false);
						player.sendMessage(TranslationManager.getText(player, "PREFIX")+TranslationManager.getText(player, "kFLY_PVP_FLAG"));
					}
					
					if(player.isOnGround()){
						player_loc.remove(player);
						player_loc.put(player, player.getLocation());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void potion(PotionSplashEvent ev){
		if(ev.getEntity().getItem().getData().getData()==16396||ev.getEntity().getItem().getData().getData()==16428){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Command(PlayerCommandPreprocessEvent ev){
		String cmd = "";
	    if (ev.getMessage().contains(" ")){
	      String[] parts = ev.getMessage().split(" ");
	      cmd = parts[0];
	    }else{
	      cmd = ev.getMessage();
	    }
	     
	    if(cmd.startsWith("/me")){
			ev.setCancelled(true);
			return;
		}else if(cmd.startsWith("/bukkit")){
			ev.setCancelled(true);
			return;
		}
	    
		if(ev.getPlayer().isOp()){
			if(cmd.equalsIgnoreCase("/reload")){
				ev.setCancelled(true);
				restart();
			}else if(cmd.equalsIgnoreCase("/restart")){
				ev.setCancelled(true);
				restart();
			}else if(cmd.equalsIgnoreCase("/stop")){
				ev.setCancelled(true);
				restart();
			}
		}else{
			if(!getManager().getAntiLogout().is(ev.getPlayer())){
				if(cmd.equalsIgnoreCase("/homes")||cmd.equalsIgnoreCase("/etpa")||cmd.equalsIgnoreCase("/fly")||cmd.equalsIgnoreCase("/kfly")||cmd.equalsIgnoreCase("/tpaccet")||cmd.equalsIgnoreCase("/tpyes")||cmd.equalsIgnoreCase("/tpask")||cmd.equalsIgnoreCase("/etpaccept")||cmd.equalsIgnoreCase("/ewarp")||cmd.equalsIgnoreCase("/tpa")||cmd.equalsIgnoreCase("/eback")||cmd.equalsIgnoreCase("/ehome")||cmd.equalsIgnoreCase("/tpaccept")||cmd.equalsIgnoreCase("/back")||cmd.equalsIgnoreCase("/home")||cmd.equalsIgnoreCase("/spawn")||cmd.equalsIgnoreCase("/espawn")||cmd.equalsIgnoreCase("/warp")){
					ev.getPlayer().sendMessage(TranslationManager.getText(ev.getPlayer(), "PREFIX")+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausf§hren!");
					ev.setCancelled(true);
				}
			}else{
				if(cmd.equalsIgnoreCase("/homes")||cmd.equalsIgnoreCase("/etpa")||cmd.equalsIgnoreCase("/tpask")||cmd.equalsIgnoreCase("/etpaccept")||cmd.equalsIgnoreCase("/ewarp")||cmd.equalsIgnoreCase("/eback")||cmd.equalsIgnoreCase("/ehome")||cmd.equalsIgnoreCase("/espawn")){
					ev.setCancelled(true);
				}
			}
		}
	}
	
	public void restart(){
		RestartScheduler restart = new RestartScheduler(getManager().getAntiLogout().getInstance());
		restart.setMoney(getManager().getMoney());
		restart.setAnti(getManager().getAntiLogout());
		restart.setStats(getManager().getStatsManager());
		restart.setUserData(getManager().getUserData());
		restart.start();
	}
	
}
