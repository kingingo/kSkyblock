package me.kingingo.kSkyblock;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.RestartScheduler;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilWorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class kSkyBlockListener extends kListener{

	@Getter
	private kSkyBlock manager;
	private HashMap<Player,Location> player_loc = new HashMap<>();
	
	public kSkyBlockListener(kSkyBlock manager) {
		super(manager.getAntiLogout().getInstance(), "Listener");
		this.manager=manager;
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
	public void Sign(SignChangeEvent ev){
		ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
		ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
		ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
		ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));
	}
	
	@EventHandler
	public void Create(CreatureSpawnEvent ev){
		if(ev.getLocation().getWorld().getName().equalsIgnoreCase("world"))ev.setCancelled(true);
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
	
	
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		ev.setDeathMessage(null);
		if(ev.getEntity() instanceof Player){
			UtilPlayer.RespawnNow(((Player)ev.getEntity()), manager);
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
	public void UpdaterFly(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC_2){
			for(Player player : Bukkit.getWorld("world").getPlayers()){
				if(UtilWorldGuard.RegionFlag(player, DefaultFlag.PVP)){
					if(!player.hasPermission("epicpvp.kfly.pvp_allow")&&player.isFlying()){
						player.teleport(Bukkit.getWorld("world").getSpawnLocation());
						player.setAllowFlight(false);
						player.setFlying(false);
						player.sendMessage(Text.PREFIX.getText()+Text.kFLY_PVP_FLAG.getText());
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
				if(cmd.equalsIgnoreCase("/etpa")||cmd.equalsIgnoreCase("/fly")||cmd.equalsIgnoreCase("/kfly")||cmd.equalsIgnoreCase("/tpaccet")||cmd.equalsIgnoreCase("/tpyes")||cmd.equalsIgnoreCase("/tpask")||cmd.equalsIgnoreCase("/etpaccept")||cmd.equalsIgnoreCase("/ewarp")||cmd.equalsIgnoreCase("/tpa")||cmd.equalsIgnoreCase("/eback")||cmd.equalsIgnoreCase("/ehome")||cmd.equalsIgnoreCase("/tpaccept")||cmd.equalsIgnoreCase("/back")||cmd.equalsIgnoreCase("/home")||cmd.equalsIgnoreCase("/spawn")||cmd.equalsIgnoreCase("/espawn")||cmd.equalsIgnoreCase("/warp")){
					ev.getPlayer().sendMessage(Text.PREFIX.getText()+"§cDu kannst den Befehl §b"+cmd+"§c nicht in Kampf ausführen!");
					ev.setCancelled(true);
				}
			}else{
				if(cmd.equalsIgnoreCase("/etpa")||cmd.equalsIgnoreCase("/tpask")||cmd.equalsIgnoreCase("/etpaccept")||cmd.equalsIgnoreCase("/ewarp")||cmd.equalsIgnoreCase("/eback")||cmd.equalsIgnoreCase("/ehome")||cmd.equalsIgnoreCase("/espawn")){
					ev.setCancelled(true);
				}
			}
		}
	}
	
	public void restart(){
		RestartScheduler restart = new RestartScheduler(getManager().getAntiLogout().getInstance());
		restart.setAnti(getManager().getAntiLogout());
		restart.setStats(getManager().getStatsManager());
		restart.start();
	}
	
}
