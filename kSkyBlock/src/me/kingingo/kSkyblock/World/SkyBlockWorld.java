package me.kingingo.kSkyblock.World;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import me.kingingo.kSkyblock.SkyBlockManager;
import me.kingingo.kSkyblock.Util.UtilSchematic;
import me.kingingo.kcore.AntiLogout.Events.AntiLogoutAddPlayerEvent;
import me.kingingo.kcore.Command.Commands.Events.PlayerSetHomeEvent;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.MySQL.MySQLErr;
import me.kingingo.kcore.MySQL.Events.MySQLErrorEvent;
import me.kingingo.kcore.Scoreboard.PlayerScoreboard;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;

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
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scoreboard.DisplaySlot;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;

public class SkyBlockWorld extends kListener{

	@Getter
	private SkyBlockManager manager;
	@Getter
	private World world;
	@Getter
	private HashMap<String,Location> islands = new HashMap<>();
	private int X=0;
	private int Z=0;
	private int radius;
	@Getter
	private String schematic;
	private int creature_limit;
	@Getter
	private HashMap<Player,ArrayList<String>> partys = new HashMap<>();
	@Getter
	private HashMap<String,Location> party_island = new HashMap<>();
	@Getter
	private HashMap<Player,PlayerScoreboard> partys_board = new HashMap<>();
	@Getter
	private HashMap<String,Player> party_einladungen = new HashMap<>();
	private EditSession session;
	
	public SkyBlockWorld(SkyBlockManager manager,String schematic,World world,int radius,int anzahl,int creature_limit) {
		super(manager.getInstance(),"SkyBlockWorld:"+world.getName());
		this.manager=manager;
		this.world=world;
		this.creature_limit=creature_limit;
		this.schematic=schematic;
		this.radius=radius;
		this.session=new EditSession(new BukkitWorld(getWorld()), 999999999);
		loadIslands();
		addIslands(anzahl);
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
			sendChatParty(player, Text.SKYBLOCK_PARTY_SCHLIEßEN.getText());
			for(String p : getPartys().get(player)){
				getParty_island().remove(p.toLowerCase());
				if(UtilPlayer.isOnline(p)){
					Bukkit.getPlayer(p).teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
			}
			getPartys().get(player).clear();
			getPartys().remove(player);
			getPartys_board().get(player).resetScoreboard();
			getPartys_board().remove(player);
			return true;
		}else{
			boolean b = false;
			for(Player owner : getPartys().keySet()){
				if(getPartys().get(owner).contains(player.getName().toLowerCase())){
					b=true;
					getParty_island().remove(player.getName().toLowerCase());
					getPartys().get(owner).remove(player.getName().toLowerCase());
					getPartys_board().get(owner).resetScore(player.getName(), DisplaySlot.SIDEBAR);
					getPartys_board().get(owner).removePlayer(player);
					player.teleport(Bukkit.getWorld("world").getSpawnLocation());
					if(withMSG)player.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_VERLASSEN.getText());
					break;
				}
			}
			if(!b){
				if(withMSG)player.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
				return false;
			}
			return true;
		}
	}
	
	public boolean kickenParty(Player owner,String kicken){
		if(getPartys().containsKey(owner)){
			ArrayList<String> list = getPartys().get(owner);
			if(list.contains(kicken.toLowerCase())){
				sendChatParty(owner, Text.SKYBLOCK_PARTY_KICKEN.getText(kicken));
				list.remove(kicken.toLowerCase());
				getParty_island().remove(kicken.toLowerCase());
				if(UtilPlayer.isOnline(kicken)){
					getPartys_board().get(owner).resetScore(Bukkit.getPlayer(kicken).getName(), DisplaySlot.SIDEBAR);
					getPartys_board().get(owner).removePlayer(Bukkit.getPlayer(kicken));
					Bukkit.getPlayer(kicken).teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
				return true;
			}else{
				owner.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_PLAYER_NOT.getText());
				return false;
			}
		}else{
			owner.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO_OWNER.getText());
			return false;
		}
	}
	
	public void sendChatParty(Player owner,String msg){
		if(getPartys().containsKey(owner)){
			owner.sendMessage(Text.PREFIX.getText()+msg);
			for(String player : getPartys().get(owner)){
				if(UtilPlayer.isOnline(player)){
					Bukkit.getPlayer(player).sendMessage(Text.PREFIX.getText()+msg);
				}
			}
		}
	}
	
	public boolean homeParty(Player player){
		if(getPartys().containsKey(player)){
			player.teleport(getIslandHome(player));
			return true;
		}else{
			boolean b = false;
			for(Player owner : getPartys().keySet()){
				if(getPartys().get(owner).contains(player.getName().toLowerCase())){
					b=true;
					player.teleport(getIslandHome(owner));
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
					p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_VOLL.getText());
					return false;
				}else{
					getPartys().get(owner).add(p.getName().toLowerCase());
					getParty_island().put(p.getName().toLowerCase(), islands.get(UtilPlayer.getRealUUID(owner).toString()));
					getPartys_board().get(owner).setScore(p.getName(), DisplaySlot.SIDEBAR, -1);
					getPartys_board().get(owner).setBoard(p);
					sendChatParty(owner, Text.SKYBLOCK_PARTY_ENTER_BY.getText(p.getName()));
					return true;
				}
			}else{
				p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_IN.getText());
				return false;
			}
		}else{
			p.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_EINLADEN_NO.getText());
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
							owner.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_SIZE.getText(8));
							return false;
						}else{
							getParty_einladungen().remove(einladen.toLowerCase());
							getParty_einladungen().put(einladen.toLowerCase(), owner);
							owner.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_EINLADEN.getText(invite.getName()));
							invite.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_EINLADEN_INVITE.getText(owner.getName()));
							return true;
						}
					}else{
						owner.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_EINLADEN_IS_IN.getText(einladen));
						return false;
					}
				}else{
					owner.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_EINLADEN_IS_IN.getText());
					return false;
				}
			}else{
				owner.sendMessage(Text.PREFIX.getText()+Text.PLAYER_IS_OFFLINE.getText(einladen));
				return false;
			}
		}else{
			owner.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_PARTY_NO.getText());
			return false;
		}
	}
	
	public boolean createParty(Player player){
		if(!getPartys().containsKey(player)){
			getPartys().put(player, new ArrayList<String>());
			getPartys_board().put(player, new PlayerScoreboard(player));
			String scorename = C.cAqua+C.Bold+player.getName()+" "+C.cGray+"-"+C.cAqua+C.Bold+" Party";
			
			if(scorename.length()>=32 ){
				String name = player.getName();
				name = name.substring(0, 32-((C.cAqua+C.Bold+" "+C.cGray+"-"+C.cAqua+C.Bold+" Party").length()+2));
				scorename = C.cAqua+C.Bold+name+" "+C.cGray+"-"+C.cAqua+C.Bold+" Party";
			}
			
			getPartys_board().get(player).addBoard(DisplaySlot.SIDEBAR, scorename);
			getPartys_board().get(player).setScore(C.cGray+"Spieler: ", DisplaySlot.SIDEBAR, 0);
			getPartys_board().get(player).setScore(player.getName(), DisplaySlot.SIDEBAR, -1);
			getPartys_board().get(player).setBoard();
			return true;
		}else{
			return false;
		}
	}
	
	@EventHandler
	public void Home(PlayerSetHomeEvent ev){
		if(ev.getHome().getWorld()==getWorld()){
			for(String uuid : islands.keySet()){
				if(uuid.startsWith("-"))continue;
				if( (islands.get(uuid).getBlockX()-radius <= ev.getHome().getBlockX() && islands.get(uuid).getBlockX() >= ev.getHome().getBlockX()) && (islands.get(uuid).getBlockZ()-radius <= ev.getHome().getBlockZ() && islands.get(uuid).getBlockZ() >= ev.getHome().getBlockZ()) ){
					for(Player player : UtilServer.getPlayers()){
						if(!player.getName().equalsIgnoreCase(ev.getPlayer().getName())&&UtilPlayer.getRealUUID(player).toString().equalsIgnoreCase(uuid)){
							if(getManager().getInstance().getHa().list.containsKey(player)){
								getManager().getInstance().getHa().list.remove(player);
								getManager().getInstance().getHa().list_loc.remove(player);
								getManager().getInstance().getHa().list_name.remove(player);
							}
							getManager().getInstance().getHa().list.put( player , ev.getPlayer());
							getManager().getInstance().getHa().list_loc.put(player, ev.getHome());
							getManager().getInstance().getHa().list_name.put(player, ev.getName());
							player.sendMessage(Text.PREFIX.getText()+Text.HOME_QUESTION.getText(ev.getPlayer().getName()));
							ev.setReason(Text.HOME_ISLAND.getText());
							ev.setCancelled(true);
							break;
						}
					}
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void Fall(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC_3)return;
		for(Player player : getWorld().getPlayers()){
			if(!player.isOnGround()&&player.getLocation().getBlockY()<=12){
				player.teleport(Bukkit.getWorld("world").getSpawnLocation());
				player.setHealth( ((CraftPlayer)player).getMaxHealth() );
			}
		}
	}

	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent ev) {
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(islands.containsKey(UtilPlayer.getRealUUID(ev.getPlayer()).toString())&&isInIsland(UtilPlayer.getRealUUID(ev.getPlayer()), ev.getBlockClicked().getLocation())) {
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
			if(islands.containsKey(UtilPlayer.getRealUUID(ev.getPlayer()).toString())&&isInIsland(UtilPlayer.getRealUUID(ev.getPlayer()), ev.getBlockClicked().getLocation())) {
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
	
//	Player move;
//	World move_world;
//	boolean result;
//	Location newLoc;
//	@EventHandler(priority=EventPriority.HIGH)
//    public void onPlayerMove(PlayerMoveEvent event){
//      move = event.getPlayer();
//      
//      if(move.isOp()||move.hasPermission(kPermission.SKYBLOCK_ISLAND_BYPASS.getPermissionToString())){
//    	  return;
//      }
//      
//      world = move.getWorld();
//      if (move.getVehicle() != null||move_world!=getWorld()) {
//        return;
//      }
//      
//        if ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || 
//          (event.getFrom().getBlockY() != event.getTo().getBlockY()) || 
//          (event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
//          result = isInIsland(event.getPlayer(), event.getTo());
//          if (!result) {  
//        	 if(getParty_island().containsKey(event.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(event.getPlayer().getName().toLowerCase()), event.getTo())){
//  				return;
//  			 }
//        	  
//            newLoc = event.getFrom();
//            newLoc.setX(newLoc.getBlockX() + 0.5D);
//            newLoc.setY(newLoc.getBlockY());
//            newLoc.setZ(newLoc.getBlockZ() + 0.5D);
//            event.setTo(newLoc);
//          }
//      }
//    }
	
	@EventHandler
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getDamager().getWorld()==getWorld()){
			if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Player){
				ev.setCancelled(true);
			}else if(ev.getDamager() instanceof Projectile && ev.getEntity() instanceof Player){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void CreatureSpawn(CreatureSpawnEvent ev){
		for(String player : islands.keySet()){
			if(player.charAt(0)!='!'){
				if(isInIsland(player,ev.getLocation())){
					int a = 0;
					for(Entity e : world.getEntities()){
						if(!(e instanceof Player)){
							if(isInIsland(player,e.getLocation()))a++;
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
			      
			      if(islands.containsKey(UtilPlayer.getRealUUID(player).toString()) && isInIsland(UtilPlayer.getRealUUID(player), loc)){
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
			if(islands.containsKey(UtilPlayer.getRealUUID(ev.getPlayer()).toString())&&isInIsland(UtilPlayer.getRealUUID(ev.getPlayer()), ev.getItem().getLocation())) {
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
			if(islands.containsKey(UtilPlayer.getRealUUID(ev.getPlayer()).toString())&&isInIsland(UtilPlayer.getRealUUID(ev.getPlayer()), ev.getBlock().getLocation())) {
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
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()&&UtilEvent.isAction(ev, ActionType.BLOCK)){
			if(ev.getClickedBlock().getType()==Material.CACTUS||ev.getClickedBlock().getType()==Material.SUGAR_CANE){
				if(islands.containsKey(UtilPlayer.getRealUUID(ev.getPlayer()).toString())&&isInIsland(UtilPlayer.getRealUUID(ev.getPlayer()), ev.getClickedBlock().getLocation())) {
					return;
				}
				if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getClickedBlock().getLocation())){
					return;
				}
				
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev){
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(ev.getBlock()==null)return;
			if(islands.containsKey(UtilPlayer.getRealUUID(ev.getPlayer()).toString())&&isInIsland(UtilPlayer.getRealUUID(ev.getPlayer()), ev.getBlock().getLocation())) {
				return;
			}
			if(getParty_island().containsKey(ev.getPlayer().getName().toLowerCase())&&isInIsland(getParty_island().get(ev.getPlayer().getName().toLowerCase()), ev.getBlock().getLocation())){
				return;
			}
			
			ev.setCancelled(true);
		}
	}
	
	public void loadIslandPlayer(Player player){
		loadIslandPlayer(UtilPlayer.getRealUUID(player));
	}
	
	public boolean haveIsland(Player player){
		return haveIsland(UtilPlayer.getRealUUID(player));
	}
	
	public boolean haveIsland(UUID uuid){
		return islands.containsKey(uuid.toString());
	}
	
	public void loadIslandPlayer(UUID uuid){
		if(!islands.containsKey(uuid)){
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X`,`Z` FROM `list_skyblock_worlds` WHERE worldName='"+world.getName().toLowerCase()+"' AND uuid='"+uuid+"'");
		      while (rs.next()) {
		    	islands.put(uuid.toString(), new Location(world,rs.getInt(1),0,rs.getInt(2)));
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
		}
	}
	
	public Location getIslandFixHome(Player player){
		Location loc = getIslandHome(player);
		loc.getBlock().setType(Material.AIR);
		loc.clone().add(0,-1,0).getBlock().setType(Material.AIR);
		loc.clone().add(0,-2,0).getBlock().setType(Material.GLASS);
		return loc;
	}
	
	public Location getIslandHome(Player player){
		return getIslandHome(UtilPlayer.getRealUUID(player));
	}
	
	public Location getIslandHome(UUID uuid){
		if(islands.containsKey(uuid.toString())){
			return new Location(getWorld(), (islands.get(uuid.toString()).getBlockX()-(radius/2)) ,93, (islands.get(uuid.toString()).getBlockZ()-(radius/2)) );
		}
		return null;
	}
	
	public boolean addIsland(Player player){
		if(player.hasPermission("epicpvp.skyblock.schematic."+schematic)){
			addIsland(UtilPlayer.getRealUUID(player), schematic,true);
			return true;
		}
		return true;
	}
	
	public boolean newIsland(Player player){
		return newIsland(UtilPlayer.getRealUUID(player).toString());
	}

	public boolean newIsland(UUID uuid){
		return newIsland(uuid.toString());
	}
	
	public boolean newIsland(String uuid){
		if(islands.containsKey(uuid)){
			Location loc = islands.get(uuid);
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
			Log("Die Insel von den Spieler "+uuid+"(Entities:"+count+"/Bloecke:"+b_count+") wurde erneuert.");
			return true;
		}
		return false;
	}
	
	public boolean removeIsland(Player player){
		if(getManager().getDelete().contains(player.getName().toLowerCase())){
			player.sendMessage(Text.PREFIX.getText()+Text.SKYBLOCK_REMOVE_ISLAND_ONE.getText());
			return false;
		}
		UUID uuid = UtilPlayer.getRealUUID(player);
		if(islands.containsKey(uuid.toString())){
			Location loc = islands.get(uuid.toString());
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
			islands.remove(uuid.toString());
			Log("Die Insel von den Spieler "+player.getName()+"(Entities:"+count+"/Bloecke:"+b_count+") wurde resetet.");
			uuid = UUID.randomUUID();
			getManager().getInstance().getMysql().Update("UPDATE list_skyblock_worlds SET uuid='!"+uuid+"' WHERE X='"+loc.getBlockX()+"' AND Z='"+loc.getBlockZ()+"' AND worldName='"+world.getName()+"'");
			islands.put("!"+uuid.toString(), loc);
			Location loc1 = new Location(getWorld(), (max_x-(radius/2)) ,90, (max_z-(radius/2)) );
			loc1.getWorld().loadChunk(loc1.getChunk());
			UtilSchematic.pastePlate(session,loc1, new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
			getManager().getDelete().add(player.getName().toLowerCase());
			return true;
		}
		return false;
	}
	
	public int recycelnIslandAnzahl(){
		int count = 0;
		for(String player : islands.keySet()){
			if(player.charAt(0)=='!'){
				count++;
			}
		}
		return count;
	}
	
	public String recycelnIsland(){
		String island = null;
		for(String player : islands.keySet()){
			if(player.charAt(0)=='!'){
				island=player;
				break;
			}
		}
		return island;
	}
	
	public void addIslands(int anzahl){
		int a = recycelnIslandAnzahl();
		if(a<anzahl){
			for(int i = 0; i< (anzahl-a) ;i++){
				addIsland(null,schematic,false);
			}
			Log((anzahl-a)+" Inseln wurden hinzugefügt!");
		}
	}
	
	public void addIsland(UUID uuid,String schematic,boolean recyceln){
		if(recyceln){
			String island = recycelnIsland();
			if(island!=null){
				int x = islands.get(island).getBlockX();
				int z = islands.get(island).getBlockZ();
				//Log("Island: "+island+" X:"+x+" Z:"+z);
				islands.remove(island);
				islands.put(uuid.toString(), new Location(world,x,0,z));
				getManager().getInstance().getMysql().Update("UPDATE list_skyblock_worlds SET uuid='"+uuid+"' WHERE worldName='"+world.getName()+"' AND uuid='"+island+"' AND X='"+x+"' AND Z='"+z+"'");
				Log("Die Insel von den Spieler "+uuid+"(X:"+x+",Z:"+z+") wurde recycelt.");
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
		String u = "";
		if(uuid==null){
			u="!"+uuid.randomUUID();
		}else{
			u=uuid.toString();
		}
		islands.put(u, new Location(world,X,0,Z));
		setBiome(u, Biome.JUNGLE);
		getManager().getInstance().getMysql().Update("INSERT INTO list_skyblock_worlds (uuid,worldName,X,Z) VALUES ('"+u+"','"+getWorld().getName()+"','"+X+"','"+Z+"');");
		Log("Die Insel von den Spieler "+u+"(X:"+(X-(radius/2))+",Z:"+(Z-(radius/2))+") wurde erstellt.");
	}
	
	public boolean isInIsland(Player player,Location loc){
		return isInIsland(UtilPlayer.getRealUUID(player),loc);
	}
	
	public boolean isInIsland(String uuid,Location loc){
		if(islands.containsKey(uuid)){
			return isInIsland(islands.get(uuid),loc);
		}
		return false;
	}
	
	public boolean isInIsland(UUID uuid,Location loc){
		if(islands.containsKey(uuid.toString())){
			return isInIsland(islands.get(uuid.toString()),loc);
		}
		return false;
	}
	
	public boolean isInIsland(Location loc,Location loc1){
		return (loc.getX()-radius) <= loc1.getX() && (loc.getZ()-radius) <= loc1.getZ() && loc.getBlockX() >= loc1.getBlockX() && loc.getBlockZ() >= loc1.getBlockZ();
	}
	
	public void setBiome(String player,Biome biome){
		player=player.toLowerCase();
		if(islands.containsKey(player)){
			int min_x = islands.get(player).getBlockX()-radius;
			int max_x = islands.get(player).getBlockX();
			
			int min_z = islands.get(player).getBlockZ()-radius;
			int max_z = islands.get(player).getBlockZ();
			
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
			Log("X: "+X+" Z:"+Z);
		}else{
			X=radius;
			Z=0;
			Log("X: "+X+" Z:"+Z);
		}
		
		
	}
	
	public void loadIslands(){
		try
	    {
	      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `uuid`,`X`,`Z` FROM `list_skyblock_worlds` WHERE worldName='"+world.getName().toLowerCase()+"';");
	      while (rs.next()) {
	    	if(rs.getString(1).charAt(0)!='!')continue;
	    	islands.put(rs.getString(1), new Location(world,rs.getInt(2),0,rs.getInt(3)));
	      }
	      rs.close();
	    } catch (Exception err) {
	    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
	    }
		Log(islands.size()+" Inseln wurden Geladen!");
		solveXandZ();
	}
	
}
