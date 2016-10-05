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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scoreboard.DisplaySlot;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.kSkyblock.kSkyBlock;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.GemsShop.Events.PlayerGemsBuyEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Permission.Events.PlayerLoadPermissionEvent;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.SignShop.Events.SignShopUseEvent;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsChangedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.UserStores.Events.PlayerCreateUserStoreEvent;
import eu.epicpvp.kcore.Util.RestartScheduler;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilInv;
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
	
	public SkyBlockListener(kSkyBlock manager)	 {
		super(manager.getAntiLogout().getInstance(), "Listener");
		this.manager=manager;
	}
	
	
	@EventHandler
	public void placeTNT(PlayerInteractEvent ev){
		if(ev.getAction() == Action.RIGHT_CLICK_BLOCK && ev.getPlayer().getItemInHand() != null && ev.getPlayer().getItemInHand().getType() == Material.EXPLOSIVE_MINECART){
			UtilInv.removeAll(ev.getPlayer(), Material.EXPLOSIVE_MINECART,(byte)0);
			ev.setCancelled(true);
		}
	}
	
	private ArrayList<String> duckduck_ips = new ArrayList<>();
	@EventHandler
	public void join(PlayerJoinEvent ev){
		if(ev.getPlayer().getName().toLowerCase().contains("DuckKali".toLowerCase()) || duckduck_ips.contains(ev.getPlayer().getAddress().getHostName())){
			int sec = (UtilMath.RandomInt(120, 20));
			System.err.println("[Duck-WatchDogs] Find a Duck Player '"+ev.getPlayer().getName()+"' ("+ev.getPlayer().getAddress().getHostName()+"). He will get a ban in "+sec+" seconds");
			duckduck_ips.add(ev.getPlayer().getAddress().getHostName());
			Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getMysql().getInstance(), new Runnable() {
				
				@Override
				public void run() {
					LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(ev.getPlayer().getName());
					loadedplayer.banPlayer(ev.getPlayer().getAddress().getHostName(), "CONSOLE", "CONSOLE", UUID.randomUUID(), 1, -1, "Banned");
				}
			}, TimeSpan.SECOND * sec);
		}
	}
	
	@EventHandler
	public void buygems(PlayerGemsBuyEvent ev){
		if(ev.getItem().hasItemMeta()&&ev.getItem().getItemMeta().hasDisplayName()&&ev.getItem().getItemMeta().getDisplayName().toLowerCase().contains("usershop")){
			if(UtilServer.getUserData()!=null){
				UtilServer.getUserData().getConfig(ev.getPlayer()).set("Stores", UtilServer.getUserData().getConfig(ev.getPlayer()).getInt("Stores")+1);
				UtilServer.getUserData().getConfig(ev.getPlayer()).save();
				
				UtilScoreboard.resetScore(ev.getPlayer().getScoreboard(), 2, DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§7"+UtilServer.getUserData().getConfig(ev.getPlayer()).getInt("Stores"), DisplaySlot.SIDEBAR, 2);
			}
		}
	}
	
	@EventHandler
	public void tntMinecartDamage(EntityDamageByEntityEvent event){
		if (event.getDamager().getType() == EntityType.MINECART_TNT && event.getEntityType() == EntityType.MINECART_TNT) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void tnt(EntityExplodeEvent ev){
		ev.setCancelled(true);
		ev.getEntity().remove();
	}
	
	@EventHandler
	 public void BlockBurn(BlockBurnEvent ev){
		 ev.setCancelled(true);
	 }
	
	@EventHandler
	public void userstore(PlayerCreateUserStoreEvent ev){
		UtilScoreboard.resetScore(ev.getPlayer().getScoreboard(), 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§7"+UtilServer.getUserData().getConfig(ev.getPlayer()).getInt("Stores"), DisplaySlot.SIDEBAR, 2);
	}
	
	@EventHandler
	public void statsMONEY(PlayerStatsChangedEvent ev){
		if(ev.getManager().getType() != GameType.Money){
			if(ev.getStats() == StatsKey.MONEY){
				if(UtilPlayer.isOnline(ev.getPlayerId())){
					Player player = UtilPlayer.searchExact(ev.getPlayerId());
					UtilScoreboard.resetScore(player.getScoreboard(), 5, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(player.getScoreboard(),"§7"+UtilMath.trim(2, getManager().getStatsManager().getDouble(player, StatsKey.MONEY))+"$", DisplaySlot.SIDEBAR, 5);
				}
			}
		}
	}
	
	@EventHandler
	public void AddBoard(PlayerSetScoreboardEvent ev){
		UtilPlayer.setSkyBlockScoreboard(ev.getPlayer(),UtilServer.getGemsShop().getGems(), getManager().getStatsManager(), UtilServer.getUserData());
	}
	
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
			ev.getSign().setLine(0, "Nö!");
			ev.getSign().setLine(1, "Nö!");
			ev.getSign().setLine(2, "Nö!");
			ev.getSign().setLine(3, "Nö!");
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
	}
	
	@EventHandler
	public void Kick(PlayerKickEvent ev){
		System.out.println("[ClashMC] "+ev.getPlayer().getName()+" L:"+ev.getLeaveMessage()+" R:"+ev.getReason());
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
	
//	@EventHandler
//	public void loadedStats(PlayerStatsLoadedEvent ev){
//		if(ev.getManager().getType() != GameType.Money){
//			if(UtilPlayer.isOnline(ev.getPlayername())){
//				if(vote_list.contains( UtilPlayer.getPlayerId(Bukkit.getPlayer(ev.getPlayername())) )){
//					if(UtilServer.getDeliveryPet()!=null){
//						 UtilServer.getDeliveryPet().deliveryUSE(Bukkit.getPlayer(ev.getPlayername()), "§aVote for ClashMC", true);
//					 }
//					
//					vote_list.remove(UtilPlayer.getPlayerId(ev.getPlayername()));
//					manager.getStatsManager().add(Bukkit.getPlayer(ev.getPlayername()), StatsKey.MONEY,200);
//					Bukkit.getPlayer(ev.getPlayername()).getInventory().addItem(new ItemStack(Material.DIAMOND,2));
//					Bukkit.getPlayer(ev.getPlayername()).getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
//					Bukkit.getPlayer(ev.getPlayername()).getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
//					Bukkit.getPlayer(ev.getPlayername()).sendMessage(TranslationManager.getText(Bukkit.getPlayer(ev.getPlayername()), "PREFIX")+TranslationManager.getText(Bukkit.getPlayer(ev.getPlayername()), "VOTE_THX"));
//				}
//			}
//		}
//	}
	
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
		UtilPlayer.setTab(ev.getPlayer(), "SkyBlock-Server");
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
						player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "kFLY_PVP_FLAG"));
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
					ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX")+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausführen!");
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
