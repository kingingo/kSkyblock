package me.kingingo.kSkyblock.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import me.kingingo.kSkyblock.kSkyBlock;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.PLAYER_VOTE;
import me.kingingo.kcore.Packet.Packets.TWITTER_PLAYER_FOLLOW;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Permission.Event.PlayerLoadPermissionEvent;
import me.kingingo.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import me.kingingo.kcore.SignShop.Events.SignShopUseEvent;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.RestartScheduler;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilWorldGuard;

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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
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

import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class kSkyBlockListener extends kListener{

	@Getter
	private kSkyBlock manager;
	private HashMap<Player,Location> player_loc = new HashMap<>();
	private ArrayList<UUID> vote_list = new ArrayList<>();
	
	public kSkyBlockListener(kSkyBlock manager) {
		super(manager.getAntiLogout().getInstance(), "Listener");
		this.manager=manager;
	}
	
	@EventHandler
	public void AddBoard(PlayerSetScoreboardEvent ev){
		UtilPlayer.setScoreboard(ev.getPlayer(), 
				UtilServer.getGemsShop().getGems());
	}
	
	Player player;
	@EventHandler
	public void Receive(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof PLAYER_VOTE){
			PLAYER_VOTE vote = (PLAYER_VOTE)ev.getPacket();
			
			if(UtilPlayer.isOnline(vote.getPlayer())){
				if(UtilServer.getDeliveryPet()!=null){
					 UtilServer.getDeliveryPet().deliveryUSE(Bukkit.getPlayer(vote.getPlayer()), "§aVote for EpicPvP", true);
				 }
				
				player=Bukkit.getPlayer(vote.getPlayer());
				manager.getStatsManager().setDouble(player, manager.getStatsManager().getDouble(Stats.MONEY, player)+200, Stats.MONEY);
				player.getInventory().addItem(new ItemStack(Material.DIAMOND,2));
				player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
				player.getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
				player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "VOTE_THX"));
			}else{
				vote_list.add(vote.getUuid());
			}
		}else if(ev.getPacket() instanceof TWITTER_PLAYER_FOLLOW){
			TWITTER_PLAYER_FOLLOW tw = (TWITTER_PLAYER_FOLLOW)ev.getPacket();
			
			if(UtilPlayer.isOnline(tw.getPlayer())){
				Player p = Bukkit.getPlayer(tw.getPlayer());
				if(!tw.isFollow()){
					getManager().getMysql().Update("DELETE FROM BG_TWITTER WHERE uuid='" + UtilPlayer.getRealUUID(p) + "'");
					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_FOLLOW_N"));
					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_REMOVE"));
				}else{
					UtilServer.getDeliveryPet().deliveryBlock(p, "§cTwitter Reward");
					getManager().getStatsManager().addDouble(p, 300, Stats.MONEY);
					p.setLevel(p.getLevel()+15);
					p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","300"}));
				}
			}
		}
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
		if(ev.getPlayer().hasPermission(kPermission.CHAT_FARBIG.getPermissionToString())){
			ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
			ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
			ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
			ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Create(CreatureSpawnEvent ev){
		if(ev.getSpawnReason() == SpawnReason.CUSTOM){
			ev.setCancelled(false);
		}else if(ev.getLocation().getWorld().getName().equalsIgnoreCase("world")&&!manager.getPetManager().isPet(ev.getEntity())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		getManager().getStatsManager().SaveAllPlayerData(ev.getPlayer());
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
			getManager().getStatsManager().setInt(death, getManager().getStatsManager().getInt(Stats.DEATHS, death)+1, Stats.DEATHS);
			if(ev.getEntity().getKiller() instanceof Player)getManager().getStatsManager().setInt(ev.getEntity().getKiller(), getManager().getStatsManager().getInt(Stats.KILLS, ev.getEntity().getKiller())+1, Stats.KILLS);
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
	public void Join(PlayerJoinEvent ev){
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEpicPvP§8.§eeu §8| §aSkyBlock Server", "§aTeamSpeak: §7ts.EpicPvP.eu §8| §eWebsite: §7EpicPvP.eu");
		
		if(vote_list.contains( UtilPlayer.getRealUUID(ev.getPlayer()) )){
			if(UtilServer.getDeliveryPet()!=null){
				 UtilServer.getDeliveryPet().deliveryUSE(ev.getPlayer(), "§aVote for EpicPvP", true);
			 }
			
			vote_list.remove(UtilPlayer.getRealUUID(ev.getPlayer()));
			manager.getStatsManager().setDouble(ev.getPlayer(), manager.getStatsManager().getDouble(Stats.MONEY, ev.getPlayer())+200, Stats.MONEY);
			ev.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND,2));
			ev.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT,2));
			ev.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
			ev.getPlayer().sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "VOTE_THX"));
		}
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
						player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "kFLY_PVP_FLAG"));
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
					ev.getPlayer().sendMessage(Language.getText(player, "PREFIX")+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausführen!");
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
		restart.setGems(UtilServer.getGemsShop().getGems());
		restart.setAnti(getManager().getAntiLogout());
		restart.setStats(getManager().getStatsManager());
		restart.setUserData(getManager().getUserData());
		restart.start();
	}
	
}
