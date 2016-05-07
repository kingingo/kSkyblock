package eu.epicpvp.kSkyblock.World;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.Util.UtilSchematic;
import eu.epicpvp.kcore.AntiLogout.Events.AntiLogoutAddPlayerEvent;
import eu.epicpvp.kcore.Command.Commands.Events.PlayerSetHomeEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.MySQL.MySQLErr;
import eu.epicpvp.kcore.MySQL.Events.MySQLErrorEvent;
import eu.epicpvp.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.TeleportManager.Teleporter;
import eu.epicpvp.kcore.TeleportManager.Events.PlayerTeleportedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorld;
import lombok.Getter;
import lombok.Setter;

public class SkyBlockWorld extends kListener{

	@Getter
	private SkyBlockManager manager;
	@Getter
	private World world;
	@Getter
	private HashMap<Integer,Location> islands = new HashMap<>();
	@Getter
	private ArrayList<Location> empty_islands = new ArrayList<>();
	private int X=0;
	private int Z=0;
	private int radius;
	private int space;
	@Getter
	private String schematic;
	private int creature_limit;
	@Getter
	private HashMap<Player,ArrayList<String>> partys = new HashMap<>();
	@Getter
	private HashMap<String,Location> party_island = new HashMap<>();
	@Getter
	private HashMap<String,Player> party_einladungen = new HashMap<>();
	private EditSession session;
	@Getter
	@Setter
	private boolean async=false;
	
	public SkyBlockWorld(SkyBlockManager manager,String schematic,World world,int radius,int space,int anzahl,int creature_limit) {
		super(manager.getInstance(),"SkyBlockWorld:"+world.getName());
		this.manager=manager;
		this.world=world;
		this.creature_limit=creature_limit;
		this.schematic=schematic;
		this.radius=radius+space;
		this.space=space;
		this.session=new EditSession(new BukkitWorld(getWorld()), 999999999);
		loadIslands();
		addIslands(anzahl);
	}
	
	@EventHandler
	public void teleported(PlayerTeleportedEvent ev){
		if(ev.getTeleporter().getLoc_to().getWorld().getUID() == getWorld().getUID()&&ev.getTeleporter().getFrom()!=null){
			if(isInIsland(ev.getTeleporter().getFrom(), ev.getTeleporter().getLoc_to())){
				UtilPlayer.sendPacket(ev.getTeleporter().getFrom(), getIslandBorder(ev.getTeleporter().getFrom()));
			}else if(ev.getTeleporter().getTo()!=null&&isInIsland(ev.getTeleporter().getTo(), ev.getTeleporter().getLoc_to())){
				UtilPlayer.sendPacket(ev.getTeleporter().getFrom(), getIslandBorder(ev.getTeleporter().getTo()));
			}
		}
	}
	
	@EventHandler
	public void QuitParty(PlayerQuitEvent ev){
		verlassenParty(ev.getPlayer(), false);
	}
	
	@EventHandler
	public void FightParty(AntiLogoutAddPlayerEvent ev){
		verlassenParty(ev.getPlayer(), true);
	}
	
	public boolean isInParty(Player player){
		if(!getPartys().containsKey(player)){
			for(ArrayList<String> list : getPartys().values()){
				if(list.contains(player.getName().toLowerCase())){
					return true;
				}
			}
			return false;
		}else{
			return true;
		}
	}
	
	public boolean verlassenParty(Player player,boolean withMSG){
		if(getPartys().containsKey(player)){
			sendChatParty(player,"SKYBLOCK_PARTY_SCHLIEßEN");
			for(String p : getPartys().get(player)){
				getParty_island().remove(p.toLowerCase());
				if(UtilPlayer.isOnline(p)){
					getManager().getInstance().getPermissionManager().setTabList(Bukkit.getPlayer(p));
					Bukkit.getPlayer(p).teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
			}
			getPartys().get(player).clear();
			getPartys().remove(player);
			getManager().getInstance().getPermissionManager().setTabList(player);
			return true;
		}else{
			boolean b = false;
			for(Player owner : getPartys().keySet()){
				if(getPartys().get(owner).contains(player.getName().toLowerCase())){
					b=true;
					getParty_island().remove(player.getName().toLowerCase());
					getPartys().get(owner).remove(player.getName().toLowerCase());
					UtilScoreboard.resetScore(owner.getScoreboard(),player.getName(), DisplaySlot.SIDEBAR);
					getManager().getInstance().getPermissionManager().setTabList(player);
					player.teleport(Bukkit.getWorld("world").getSpawnLocation());
					if(withMSG)player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "SKYBLOCK_PARTY_VERLASSEN"));
					break;
				}
			}
			if(!b){
				if(withMSG)player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "SKYBLOCK_PARTY_NO"));
				return false;
			}
			return true;
		}
	}
	
	public boolean kickenParty(Player owner,String kicken){
		if(getPartys().containsKey(owner)){
			ArrayList<String> list = getPartys().get(owner);
			if(list.contains(kicken.toLowerCase())){
				sendChatParty(owner, "SKYBLOCK_PARTY_KICKEN",kicken);
				list.remove(kicken.toLowerCase());
				getParty_island().remove(kicken.toLowerCase());
				if(UtilPlayer.isOnline(kicken)){
					UtilScoreboard.resetScore(owner.getScoreboard(),Bukkit.getPlayer(kicken).getName(), DisplaySlot.SIDEBAR);
					getManager().getInstance().getPermissionManager().setTabList(Bukkit.getPlayer(kicken));
					Bukkit.getPlayer(kicken).teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
				return true;
			}else{
				owner.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(owner, "SKYBLOCK_PARTY_PLAYER_NOT"));
				return false;
			}
		}else{
			owner.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(owner, "SKYBLOCK_PARTY_NO_OWNER"));
			return false;
		}
	}
	
	public void sendChatParty(Player owner,String name){
		if(getPartys().containsKey(owner)){
			owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, name));
			for(String player : getPartys().get(owner)){
				if(UtilPlayer.isOnline(player)){
					Bukkit.getPlayer(player).sendMessage(TranslationHandler.getText(Bukkit.getPlayer(player), "PREFIX")+TranslationHandler.getText(Bukkit.getPlayer(player), name));
				}
			}
		}
	}
	
	public void sendChatParty(Player owner,String name,Object[] input){
		if(getPartys().containsKey(owner)){
			owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, name, input));
			for(String player : getPartys().get(owner)){
				if(UtilPlayer.isOnline(player)){
					Bukkit.getPlayer(player).sendMessage(TranslationHandler.getText(Bukkit.getPlayer(player), "PREFIX")+TranslationHandler.getText(Bukkit.getPlayer(player), name, input));
				}
			}
		}
	}
	
	public void sendChatParty(Player owner,String name,Object input){
		if(getPartys().containsKey(owner)){
			owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, name, input));
			for(String player : getPartys().get(owner)){
				if(UtilPlayer.isOnline(player)){
					Bukkit.getPlayer(player).sendMessage(TranslationHandler.getText(Bukkit.getPlayer(player), "PREFIX")+TranslationHandler.getText(Bukkit.getPlayer(player), name, input));
				}
			}
		}
	}
	
	public boolean homeParty(Player player){
		if(getPartys().containsKey(player)){
			getManager().getInstance().getTeleport().getTeleport().add(new Teleporter(player, getIslandHome(player),3));
			return true;
		}else{
			boolean b = false;
			for(Player owner : getPartys().keySet()){
				if(getPartys().get(owner).contains(player.getName().toLowerCase())){
					b=true;
					getManager().getInstance().getTeleport().getTeleport().add(new Teleporter(player, owner, getIslandHome(owner), 3));
					break;
				}
			}
			if(!b){
				return false;
			}
			return true;
		}
	}
	
	public boolean annehmenParty(Player p){
		if(getParty_einladungen().containsKey(p.getName().toLowerCase())){
			if(!isInParty(p)){
				Player owner = getParty_einladungen().get(p.getName().toLowerCase());
				getParty_einladungen().remove(p.getName().toLowerCase());
				if(!getPartys().containsKey(owner))return false;
					
				if(getPartys().get(owner).size()>=8){
					p.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "SKYBLOCK_PARTY_VOLL"));
					return false;
				}else{
					getPartys().get(owner).add(p.getName().toLowerCase());
					getParty_island().put(p.getName().toLowerCase(), islands.get(UtilPlayer.getPlayerId(owner)));
					UtilScoreboard.setScore(owner.getScoreboard(),p.getName(), DisplaySlot.SIDEBAR, -1);
					p.setScoreboard(owner.getScoreboard());
					sendChatParty(p, "SKYBLOCK_PARTY_ENTER_BY",p.getName());
					return true;
				}
			}else{
				p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_IN"));
				return false;
			}
		}else{
			p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "SKYBLOCK_PARTY_EINLADEN_NO"));
			return false;
		}
	}
	
	public boolean einladenParty(Player owner,String einladen){
		einladen=einladen.toLowerCase();
		if(getPartys().containsKey(owner)){
			if(UtilPlayer.isOnline(einladen)){
				Player invite = Bukkit.getPlayer(einladen);
				if(!isInParty(invite)){
					if(!getParty_einladungen().containsKey(einladen.toLowerCase())||getParty_einladungen().containsKey(einladen.toLowerCase())&&getParty_einladungen().get(einladen).getName().equalsIgnoreCase(owner.getName())){
						if(getPartys().get(owner).size()>=8){
							owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, "SKYBLOCK_PARTY_SIZE",8));
							return false;
						}else{
							getParty_einladungen().remove(einladen.toLowerCase());
							getParty_einladungen().put(einladen.toLowerCase(), owner);
							owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, "SKYBLOCK_PARTY_EINLADEN",invite.getName()));
							invite.sendMessage(TranslationHandler.getText(invite, "PREFIX")+TranslationHandler.getText(invite, "SKYBLOCK_PARTY_EINLADEN_INVITE",owner.getName()));
							return true;
						}
					}else{
						owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, "SKYBLOCK_PARTY_EINLADEN_IS_IN",einladen));
						return false;
					}
				}else{
					owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, "SKYBLOCK_PARTY_EINLADEN_IS_IN"));
					return false;
				}
			}else{
				owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, "PLAYER_IS_OFFLINE",einladen));
				return false;
			}
		}else{
			owner.sendMessage(TranslationHandler.getText(owner, "PREFIX")+TranslationHandler.getText(owner, "SKYBLOCK_PARTY_NO"));
			return false;
		}
	}
	
	public boolean createParty(Player player){
		if(!getPartys().containsKey(player)){
			
			boolean b = false;
			for(Player owner : getPartys().keySet()){
				if(getPartys().get(owner).contains(player.getName().toLowerCase())){
					b=true;
					break;
				}
			}
			if(b){
				return false;
			}
			
			getPartys().put(player, new ArrayList<String>());
			Scoreboard board = player.getScoreboard();
			String scorename = "§b"+player.getName()+" Party";
			
			UtilScoreboard.addBoard(board,DisplaySlot.SIDEBAR, scorename);
			UtilScoreboard.setScore(board,"§7"+"Spieler: ", DisplaySlot.SIDEBAR, 0);
			UtilScoreboard.setScore(board,player.getName(), DisplaySlot.SIDEBAR, -1);
			player.setScoreboard(board);
			return true;
		}else{
			return false;
		}
	}
	
	@EventHandler
	public void Home(PlayerSetHomeEvent ev){
		if(ev.getHome().getWorld()==getWorld()){
			if(islands.containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))
					&&isInIsland(ev.getPlayer(), ev.getHome()))return;
			
			for(Player player : UtilServer.getPlayers()){
				if(islands.containsKey(UtilPlayer.getPlayerId(player))){
					if( (islands.get(UtilPlayer.getPlayerId(player)).getBlockX()-radius <= ev.getHome().getBlockX() && islands.get(UtilPlayer.getPlayerId(player)).getBlockX() >= ev.getHome().getBlockX()) && (islands.get(UtilPlayer.getPlayerId(player)).getBlockZ()-radius <= ev.getHome().getBlockZ() && islands.get(UtilPlayer.getPlayerId(player)).getBlockZ() >= ev.getHome().getBlockZ()) ){
						if(!player.getName().equalsIgnoreCase(ev.getPlayer().getName())&&UtilPlayer.getPlayerId(player)==(UtilPlayer.getPlayerId(player))){
							if(getManager().getInstance().getHa().list.containsKey(player)){
								getManager().getInstance().getHa().list.remove(player);
								getManager().getInstance().getHa().list_loc.remove(player);
								getManager().getInstance().getHa().list_name.remove(player);
							}
							getManager().getInstance().getHa().list.put( player , ev.getPlayer());
							getManager().getInstance().getHa().list_loc.put(player, ev.getHome());
							getManager().getInstance().getHa().list_name.put(player, ev.getName());
							player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "HOME_QUESTION",ev.getPlayer().getName()));
							ev.setReason(TranslationHandler.getText(player, "HOME_ISLAND"));
							ev.setCancelled(true);
							break;
						}
					}
				}
			}
				
				
			if(ev.getReason()==null){
				ev.setCancelled(true);
				ev.setReason("Der Spieler der Insel ist nicht Online!");
			}
		}
	}
	
	@EventHandler
	public void Fall(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC_2)return;
		for(Player player : getWorld().getPlayers()){
			if(!player.isOnGround()&&player.getLocation().getBlockY()<=12){
				player.setHealth( ((CraftPlayer)player).getMaxHealth() );
				player.teleport(Bukkit.getWorld("world").getSpawnLocation());
				player.setHealth( ((CraftPlayer)player).getMaxHealth() );
			}
		}
	}

	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent ev) {
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(islands.containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))&&isInIsland(UtilPlayer.getPlayerId(ev.getPlayer()), ev.getBlockClicked().getLocation())) {
				return;
			}
			if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getBlockClicked().getLocation())){
				return;
			}
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent ev) {
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(islands.containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))&&isInIsland(UtilPlayer.getPlayerId(ev.getPlayer()), ev.getBlockClicked().getLocation())) {
				return;
			}
			if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getBlockClicked().getLocation())){
				return;
			}
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(ev.getEntity().getWorld()==getWorld()){
			if(ev.getEntity() instanceof Player && ev.getCause() == DamageCause.FALL&& ev.getCause() == DamageCause.LAVA){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getDamager().getWorld()==getWorld()){
			if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Player){
				ev.setCancelled(true);
			}else if(ev.getDamager() instanceof Projectile && ev.getEntity() instanceof Player){
				ev.setCancelled(true);
			}else if(ev.getDamager() instanceof Projectile && ev.getEntity() instanceof Creature && ((Projectile)ev.getDamager()).getShooter() instanceof Player){
				if(islands.containsKey(UtilPlayer.getPlayerId( ((Player)((Projectile)ev.getDamager()).getShooter()) ))&&isInIsland(UtilPlayer.getPlayerId(((Player)((Projectile)ev.getDamager()).getShooter())), ev.getEntity().getLocation())) {
					return;
				}
				if(getParty_island().containsKey(((Player)((Projectile)ev.getDamager()).getShooter()).getName().toLowerCase())&&isInIsland(getParty_island().get(((Player)((Projectile)ev.getDamager()).getShooter()).getName().toLowerCase()), ev.getEntity().getLocation())){
					return;
				}
				ev.setCancelled(true);
			}else if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature){
				if(islands.containsKey(UtilPlayer.getPlayerId(((Player)ev.getDamager())))&&isInIsland(UtilPlayer.getPlayerId(((Player)ev.getDamager())), ev.getEntity().getLocation())) {
					return;
				}
				if(getParty_island().containsKey(((Player)ev.getDamager()).getName().toLowerCase())&&isInIsland(getParty_island().get(((Player)ev.getDamager()).getName().toLowerCase()), ev.getEntity().getLocation())){
					return;
				}
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void CreatureSpawn(CreatureSpawnEvent ev){
		if(ev.getSpawnReason() != SpawnReason.CUSTOM){
			
			for(int playerId : islands.keySet()){
				if(isInIsland(playerId,ev.getLocation())){
					int a = 0;
					for(Entity e : world.getEntities()){
						if(!(e instanceof Player)){
							if(isInIsland(playerId,e.getLocation()))a++;
						}
					}
					if(a>=creature_limit)ev.setCancelled(true);
					break;
				}
			}
		}
	}
	
     Player player;
     Location loc;
	 @EventHandler(priority=EventPriority.LOWEST)
	 public void onInventoryOpenEvent(InventoryOpenEvent event){
		 if (event.getPlayer().getWorld()!=getWorld() || (event.isCancelled()) || (event.getInventory() == null) || (event.getInventory().getHolder() == null) || event.getPlayer().isOp())return;
		    
		    if (((event.getInventory().getHolder() instanceof Hopper)) ||((event.getInventory().getHolder() instanceof BrewingStand)) ||((event.getInventory().getHolder() instanceof Chest)) || ((event.getInventory().getHolder() instanceof DoubleChest)) || ((event.getInventory().getHolder() instanceof Furnace)) || ((event.getInventory().getHolder() instanceof Dispenser))) {
			    player = (Player)event.getPlayer();
			    loc = player.getLocation();
			    
			      if ((event.getInventory().getHolder() instanceof Chest)){
			        loc = ((Chest)event.getInventory().getHolder()).getLocation();
			      }
			      else if ((event.getInventory().getHolder() instanceof Furnace)){
			        loc = ((Furnace)event.getInventory().getHolder()).getLocation();
			      }
			      else if ((event.getInventory().getHolder() instanceof DoubleChest)){
			        loc = ((DoubleChest)event.getInventory().getHolder()).getLocation();
			      }
			      else if ((event.getInventory().getHolder() instanceof Dispenser)){
			        loc = ((Dispenser)event.getInventory().getHolder()).getLocation();
			      }
			      else if ((event.getInventory().getHolder() instanceof BrewingStand)){
				        loc = ((BrewingStand)event.getInventory().getHolder()).getLocation();
			      }
			      else if ((event.getInventory().getHolder() instanceof Hopper)){
				        loc = ((Hopper)event.getInventory().getHolder()).getLocation();
			      }
			      
			      if(islands.containsKey(UtilPlayer.getPlayerId(player)) && isInIsland(UtilPlayer.getPlayerId(player), loc)){
			    	  return;
			      }
			      if(getParty_island().containsKey(player.getName().toLowerCase()) && isInIsland(getParty_island().get(player.getName().toLowerCase()), loc)){
			    	  return;
			      }
				    
			      event.setCancelled(true);
		    }
	 }
	
	@EventHandler
	public void PickUp(PlayerPickupItemEvent ev){
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(islands.containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))&&isInIsland(UtilPlayer.getPlayerId(ev.getPlayer()), ev.getItem().getLocation())) {
				return;
			}
			if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getItem().getLocation())){
				return;
			}
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev){
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(ev.getBlock()==null)return;
			if(islands.containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))&&isInIsland(UtilPlayer.getPlayerId(ev.getPlayer()), ev.getBlock().getLocation())) {
				return;
			}
			if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getBlock().getLocation())){
				return;
			}
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void interact(PlayerInteractEvent ev){
		if(ev.getPlayer().getWorld().getUID()==getWorld().getUID()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(ev.getPlayer().getItemInHand()!=null){
				if(islands.containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))&&isInIsland(UtilPlayer.getPlayerId(ev.getPlayer()), ev.getClickedBlock().getLocation())) {
					return;
				}
				
				if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getClickedBlock().getLocation())){
					return;
				}

				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void PotionSplash(PotionSplashEvent ev){
		if(ev.getPotion().getLocation().getWorld()==getWorld()&&!ev.isCancelled()){
			if(ev.getPotion().getShooter() instanceof Player){
				if(!((Player)ev.getPotion().getShooter()).isOp()){
					if(islands.containsKey(UtilPlayer.getPlayerId(((Player)ev.getPotion().getShooter())))&&isInIsland(UtilPlayer.getPlayerId(((Player)ev.getPotion().getShooter())), ev.getPotion().getLocation())) {
						return;
					}
					ev.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev){
		if(ev.getPlayer().getWorld().getUID()==getWorld().getUID()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(ev.getBlock()==null)return;
			if(islands.containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))&&isInIsland(UtilPlayer.getPlayerId(ev.getPlayer()), ev.getBlock().getLocation())) {
				return;
			}
			if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getBlock().getLocation())){
				return;
			}
			
			ev.setCancelled(true);
		}
	}
	
	public boolean haveIsland(Player player){
		return haveIsland(UtilPlayer.getPlayerId(player));
	}
	
	public boolean haveIsland(int playerId){
		return islands.containsKey(playerId);
	}
	
	public void loadIslandPlayer(Player player){
		loadIslandPlayer(UtilPlayer.getPlayerId(player));
	}
	
	public void loadIslandPlayer(int playerId){
		if(!islands.containsKey(playerId)){
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X`,`Z` FROM `list_skyblock_worlds` WHERE worldName='"+world.getName().toLowerCase()+"' AND playerId='"+playerId+"'");
		      while (rs.next()) {
		    	  islands.put(playerId, new Location(world,rs.getInt(1),0,rs.getInt(2)));
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
		}
	}
	
	public kPacketPlayOutWorldBorder getIslandBorder(Player player){
		if(player.hasPermission(PermissionType.SKYBLOCK_ISLAND_BORDER_BYPASS.getPermissionToString())){
			return null;
		}
		return getIslandBorder(UtilPlayer.getPlayerId(player));
	}
	
	public kPacketPlayOutWorldBorder getIslandBorder(int playerId){
		if(islands.containsKey(playerId)){
			return UtilWorld.createWorldBorder(new Location(getWorld(), ((islands.get(playerId).getX())-((radius)/2)) ,90, ((islands.get(playerId).getZ())-((radius)/2)) ), radius-space, 25, 10);
		}else{
			return null;
		}
	}
	
	public Location getIslandFixHome(Player player){
		Location loc = getIslandHome(player);
		loc.getBlock().setType(Material.AIR);
		loc.clone().add(0,-1,0).getBlock().setType(Material.AIR);
		loc.clone().add(0,-2,0).getBlock().setType(Material.BEDROCK);
		return loc;
	}
	
	public Location getIslandHome(Player player){
		return getIslandHome(UtilPlayer.getPlayerId(player));
	}
	
	public Location getIslandHome(int playerId){
		if(islands.containsKey(playerId)){
			return new Location(getWorld(), (islands.get(playerId).getBlockX()-(radius/2)) ,93, (islands.get(playerId).getBlockZ()-(radius/2)) );
		}
		return null;
	}
	
	public boolean addIsland(Player player){
//		if(player.hasPermission("epicpvp.skyblock.schematic."+schematic)){
			addIsland(UtilPlayer.getPlayerId(player), schematic,true);
			return true;
//		}
//		return true;
	}
	
	public void newIsland(Player player){
		if(islands.containsKey(UtilPlayer.getPlayerId(player))) newIsland(islands.get(UtilPlayer.getPlayerId(player)));
	}
	
	public void newIsland(Location loc){
			int min_x = loc.getBlockX()-radius;
			int max_x = loc.getBlockX();
			
			int min_z = loc.getBlockZ()-radius;
			int max_z = loc.getBlockZ();
			
			int count=0;
			for(Entity e : world.getEntities()){
				if(!(e instanceof Player)){
					if(e.getLocation().getBlockX() > min_x&&e.getLocation().getBlockX()<max_x&&e.getLocation().getBlockZ()>min_z&&e.getLocation().getBlockZ()<max_z){
						e.remove();
						count++;
					}
				}
			}
			
			Block block;
			BlockState state;
			InventoryHolder invHolder;
			int b_count=0;
			for(int x = min_x; x < max_x; x++){
				for(int z = min_z; z < max_z; z++){
					for(int y = 0; y < 256; y++){
						block=world.getBlockAt(x,y,z);
						if(block!=null&&(!block.isEmpty())){
							block.getDrops().clear();
							state=block.getState();
							if(state instanceof InventoryHolder){
								invHolder=(InventoryHolder)state;
								invHolder.getInventory().clear();
							}
							block.setType(Material.AIR);
							b_count++;
						}
					}
				}
			}
			Location loc1 = new Location(getWorld(), (max_x-(radius/2)) ,90, (max_z-(radius/2)) );
			loc1.getWorld().loadChunk(loc1.getChunk());
			UtilSchematic.pastePlate(session,loc1, new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
			logMessage("Die Insel "+loc.getX()+"/"+loc.getZ()+" (Entities:"+count+"/Bloecke:"+b_count+") wurde erneuert.");
	}
	
	public boolean removeIsland(Player player){
		if(getManager().getDelete().contains(player.getName().toLowerCase())){
			player.sendMessage(TranslationHandler.getText(player, "PREFIX")+TranslationHandler.getText(player, "SKYBLOCK_REMOVE_ISLAND_ONE"));
			return false;
		}
		int playerId = UtilPlayer.getPlayerId(player);
		if(islands.containsKey(playerId)){
			Location loc = islands.get(playerId);
			int min_x = loc.getBlockX()-radius;
			int max_x = loc.getBlockX();
			
			int min_z = loc.getBlockZ()-radius;
			int max_z = loc.getBlockZ();
			
			int count=0;
			for(Entity e : world.getEntities()){
				if(!(e instanceof Player)){
					if(e.getLocation().getBlockX() > min_x&&e.getLocation().getBlockX()<max_x&&e.getLocation().getBlockZ()>min_z&&e.getLocation().getBlockZ()<max_z){
						e.remove();
						count++;
					}
				}
			}
			
			Block block;
			BlockState state;
			InventoryHolder invHolder;
			int b_count=0;
			for(int x = min_x; x < max_x; x++){
				for(int z = min_z; z < max_z; z++){
					for(int y = 0; y < 256; y++){
						block=world.getBlockAt(x,y,z);
						if(block!=null&&(!block.isEmpty())){
							block.getDrops().clear();
							state=block.getState();
							if(state instanceof InventoryHolder){
								invHolder=(InventoryHolder)state;
								invHolder.getInventory().clear();
							}
							block.setType(Material.AIR);
							b_count++;
						}
					}
				}
			}
			islands.remove(playerId);
			logMessage("Die Insel von den Spieler "+player.getName()+"(PlayerId:"+playerId+" / Entities:"+count+" / Bloecke:"+b_count+") wurde resetet.");
			getManager().getInstance().getMysql().Update(isAsync(),"UPDATE list_skyblock_worlds SET playerId='-1' WHERE X='"+loc.getBlockX()+"' AND Z='"+loc.getBlockZ()+"' AND worldName='"+world.getName()+"'");
			empty_islands.add(loc);
			Location loc1 = new Location(getWorld(), (max_x-(radius/2)) ,90, (max_z-(radius/2)) );
			loc1.getWorld().loadChunk(loc1.getChunk());
			UtilSchematic.pastePlate(session,loc1, new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
			getManager().getDelete().add(player.getName().toLowerCase());
			return true;
		}
		return false;
	}
	
	public int recycelnIslandAnzahl(){
		return empty_islands.size();
	}
	
	public Location recycelnIsland(){
		return empty_islands.get(0);
	}
	
	public void addIslands(int anzahl){
		int a = recycelnIslandAnzahl();
		if(a<anzahl){
			for(int i = 0; i< (anzahl-a) ;i++){
				addIsland(-1,schematic,false);
			}
			logMessage((anzahl-a)+" Inseln wurden hinzugef§gt!");
		}
	}
	
	public void addIsland(int playerId,String schematic,boolean recyceln){
		if(recyceln){
			Location loc = recycelnIsland();
			if(loc!=null){
				empty_islands.remove(loc);
				int x = loc.getBlockX();
				int z = loc.getBlockZ();
				islands.put(playerId, new Location(world,x,0,z));
				getManager().getInstance().getMysql().Update(isAsync(),"UPDATE list_skyblock_worlds SET playerId='"+playerId+"' WHERE worldName='"+world.getName()+"' AND X='"+x+"' AND Z='"+z+"'");
				logMessage("Die Insel von den Spieler "+playerId+"(X:"+x+",Z:"+z+") wurde recycelt.");
				return;
			}
		}
			
		if( Z == (radius*100)){
			X+=radius;
			Z=0;
		}else{
			Z+=radius;
		}
		
		Location loc = new Location(getWorld(), (X-(radius/2)) ,90, (Z-(radius/2)) );
		loc.getWorld().loadChunk(loc.getChunk());
		UtilSchematic.pastePlate(session,loc, new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
		
		islands.put(playerId, new Location(world,X,0,Z));
		setBiome(playerId, Biome.JUNGLE);
		getManager().getInstance().getMysql().Update(isAsync(),"INSERT INTO list_skyblock_worlds (playerId,worldName,X,Z) VALUES ('"+playerId+"','"+getWorld().getName()+"','"+X+"','"+Z+"');");
		logMessage("Die Insel von den Spieler "+playerId+"(X:"+(X-(radius/2))+",Z:"+(Z-(radius/2))+") wurde erstellt.");
	}
	
	public boolean isInIsland(Player player,Location loc){
		return isInIsland(UtilPlayer.getPlayerId(player),loc);
	}
	
	public boolean isInIsland(int playerId,Location loc){
		if(islands.containsKey(playerId)){
			return isInIsland(islands.get(playerId),loc);
		}
		return false;
	}
	
	public boolean isInIsland(Location loc,Location loc1){
//		int MinZ = ((loc.getBlockZ())-((radius)/2) - ((radius-space)/2));
//		int MaxZ = ((loc.getBlockZ()-(radius/2)) + ((radius-space)/2));
//		
//		int MinX = ((loc.getBlockX()-(radius/2)) - ((radius-space)/2));
//		int MaxX = ((loc.getBlockX()-(radius/2)) + ((radius-space)/2));
//		System.out.println("X: "+MaxX+" Z: "+MaxZ);
//			for (int z = MinZ; z < MaxZ; z++) {
//				new Location(loc.getWorld(), MaxX, 90, z).getBlock().setType(Material.BEDROCK);
//			}
//
//			for (int z = MaxZ; z > MinZ; z--) {
//				new Location(loc.getWorld(), MinX, 90, z).getChunk().load();
//				new Location(loc.getWorld(), MinX, 90, z).getBlock().setType(Material.BEDROCK);
//			}
//			
//			for (int x = MaxX; x > MinX; x--) {
//				new Location(loc.getWorld(), x, 90, MaxZ).getBlock().setType(Material.BEDROCK);
//			}
//
//			for (int x = MinX; x < MaxX; x++) {
//				new Location(loc.getWorld(), x, 90, MinZ).getBlock().setType(Material.BEDROCK);
//			}
		
		return ((loc.getBlockX()-(radius/2)) - ((radius-space)/2)) <= loc1.getX() && ((loc.getBlockZ()-(radius/2)) - ((radius-space)/2)) <= loc1.getZ() && ((loc.getBlockX()-(radius/2)) + ((radius-space)/2)) >= loc1.getX()&& ((loc.getBlockZ()-(radius/2)) + ((radius-space)/2)) >= loc1.getZ();
	}

	public void setBiome(Player player,Biome biome){
		setBiome(UtilPlayer.getPlayerId(player), biome);
	}
	
	public void setBiome(int playerId,Biome biome){
		if(islands.containsKey(playerId)){
			int min_x = islands.get(playerId).getBlockX()-radius;
			int max_x = islands.get(playerId).getBlockX();
			
			int min_z = islands.get(playerId).getBlockZ()-radius;
			int max_z = islands.get(playerId).getBlockZ();
			
			for(int x = min_x; x < max_x; x++){
				for(int z = min_z; z < max_z; z++){
					getWorld().setBiome(x, z, biome);
				}
			}
		}
	}
	
	public int getCount(){
		try
	    {
	      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT COUNT(*) FROM list_skyblock_worlds WHERE worldName='"+world.getName().toLowerCase()+"';");
	      while (rs.next()) {
	    	  return rs.getInt(1);
	      }
	      rs.close();
	    } catch (Exception err) {
	    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
	    }
		return 0;
	}
	
	public void solveXandZ(){
		if(getCount()!=0){
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X` FROM `list_skyblock_worlds` WHERE worldName='"+world.getName().toLowerCase()+"' ORDER BY X DESC LIMIT 1;");
		      while (rs.next()) {
		    	  X=rs.getInt(1);
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
			
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `Z` FROM `list_skyblock_worlds` WHERE X='"+X+"' AND worldName='"+world.getName().toLowerCase()+"' ORDER BY Z DESC LIMIT 1;");
		      while (rs.next()) {
		    	  Z=rs.getInt(1);
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
			logMessage("X: "+X+" Z:"+Z);
		}else{
			X=radius;
			Z=0;
			logMessage("X: "+X+" Z:"+Z);
		}
	}
	
	public void loadIslands(){
		try
	    {
	      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X`,`Z` FROM `list_skyblock_worlds` WHERE worldName='"+world.getName().toLowerCase()+"' AND playerId='-1';");
	      while (rs.next()) {
	    	  empty_islands.add(new Location(world,rs.getInt(1),0,rs.getInt(2)));
	      }
	      rs.close();
	    } catch (Exception err) {
	    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
	    }
		logMessage(empty_islands.size()+" Inseln wurden Geladen!");
		solveXandZ();
	}
}
