package eu.epicpvp.kSkyblock.Gilden;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.Util.UtilSchematic;
import eu.epicpvp.kcore.Gilden.GildenManager;
import eu.epicpvp.kcore.Gilden.Events.GildeLoadEvent;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.MySQL.MySQLErr;
import eu.epicpvp.kcore.MySQL.Events.MySQLErrorEvent;
import eu.epicpvp.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilBlock;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilWorld;

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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.InventoryHolder;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;

public class SkyBlockGildenWorld extends kListener{

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
	private GildenManager gilde;
	private EditSession session;
	@Getter
	@Setter
	private boolean async=false;
	
	public SkyBlockGildenWorld(SkyBlockManager manager,GildenManager gilde,World world,int radius,int anzahl,int creature_limit) {
		super(manager.getInstance(),"SkyBlockGildenWorld:"+world.getName());
		manager.getInstance().getMysql().Update(isAsync(),"CREATE TABLE IF NOT EXISTS list_gilden_Sky_world(gilde varchar(100),X int,Z int)");
		this.manager=manager;
		this.gilde=gilde;
		this.world=world;
		this.creature_limit=creature_limit;
		this.schematic="gilde";
		this.radius=radius;
		this.session=new EditSession(new BukkitWorld(getWorld()), 999999999);
		loadIslands();
		addIslands(anzahl);
	}
	
	public kPacketPlayOutWorldBorder getIslandBorder(Player player){
		if(player.hasPermission(PermissionType.SKYBLOCK_ISLAND_BORDER_BYPASS.getPermissionToString())){
			return null;
		}
		if(islands.containsKey(gilde.getPlayerGilde(player))){
			return UtilWorld.createWorldBorder(new Location(getWorld(), (islands.get(gilde.getPlayerGilde(player)).getX()-(radius/2)) ,30, (islands.get(gilde.getPlayerGilde(player)).getZ()-(radius/2)) ), radius, 25, 10);
		}else{
			return null;
		}
	}
	
	@EventHandler
	public void Load(GildeLoadEvent ev){
		loadIslandGilde(ev.getGilde());
	}
	
	@EventHandler
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getDamager().getWorld()==getWorld()){
			if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Player){
				ev.setCancelled(true);
			}else if(ev.getDamager() instanceof Projectile && ev.getEntity() instanceof Player){
				ev.setCancelled(true);
			}else if(ev.getDamager() instanceof Projectile && ev.getEntity() instanceof Creature && ((Projectile)ev.getDamager()).getShooter() instanceof Player){
				if(gilde.isPlayerInGilde(((Player)((Projectile)ev.getDamager()).getShooter()))&&islands.containsKey(gilde.getPlayerGilde(((Player)((Projectile)ev.getDamager()).getShooter())).toLowerCase())){
					if(isInIsland(((Player)((Projectile)ev.getDamager()).getShooter()), ev.getEntity().getLocation()))return;
				}
				ev.setCancelled(true);
			}else if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature){
				if(gilde.isPlayerInGilde(((Player)ev.getDamager()))&&islands.containsKey(gilde.getPlayerGilde(((Player)ev.getDamager())).toLowerCase())){
					if(isInIsland(((Player)ev.getDamager()), ev.getEntity().getLocation()))return;
				}
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void CreatureSpawn(CreatureSpawnEvent ev){
		if(ev.getSpawnReason() != SpawnReason.CUSTOM){
			for(String gilde : islands.keySet()){
				if(gilde.charAt(0)!='!'){
					if(isInIsland(gilde.toLowerCase(),ev.getLocation())){
						int a = 0;
						for(Entity e : world.getEntities()){
							if(!(e instanceof Player)){
								if(isInIsland(gilde,e.getLocation()))a++;
							}
						}
						if(a>=creature_limit)ev.setCancelled(true);
						break;
					}
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

			      if(gilde.isPlayerInGilde(player)&&islands.containsKey(gilde.getPlayerGilde(player).toLowerCase()))if(isInIsland(player,loc))return;
			      event.setCancelled(true);
		    }
	 }
		
		@EventHandler
		public void onPlayerBucketFill(PlayerBucketFillEvent ev) {
			if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
				if(gilde.isPlayerInGilde(ev.getPlayer())&&islands.containsKey(gilde.getPlayerGilde(ev.getPlayer()).toLowerCase())){
					if(isInIsland(ev.getPlayer(), ev.getBlockClicked().getLocation()))return;
				}
				ev.setCancelled(true);
			}
		}
		
		@EventHandler
		public void onPlayerBucketEmpty(PlayerBucketEmptyEvent ev) {
			if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
				if(gilde.isPlayerInGilde(ev.getPlayer())&&islands.containsKey(gilde.getPlayerGilde(ev.getPlayer()).toLowerCase())){
					if(isInIsland(ev.getPlayer(), ev.getBlockClicked().getLocation()))return;
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
		public void PotionSplash(PotionSplashEvent ev){
			if(ev.getPotion().getLocation().getWorld()==getWorld()&&!ev.isCancelled()){
				if(ev.getPotion().getShooter() instanceof Player){
					if(!((Player)ev.getPotion().getShooter()).isOp()){
						if(gilde.isPlayerInGilde(((Player)ev.getPotion().getShooter()))&&islands.containsKey(gilde.getPlayerGilde(((Player)ev.getPotion().getShooter())).toLowerCase())){
							if(isInIsland(((Player)ev.getPotion().getShooter()), ev.getPotion().getLocation()))return;
						}
						ev.setCancelled(true);
					}
				}
			}
		}
	
	@EventHandler
	public void PickUp(PlayerPickupItemEvent ev){
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(gilde.isPlayerInGilde(ev.getPlayer())&&islands.containsKey(gilde.getPlayerGilde(ev.getPlayer()).toLowerCase())){
				if(isInIsland(ev.getPlayer(), ev.getItem().getLocation()))return;
			}
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev){
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(ev.getBlock()==null)return;
			if(gilde.isPlayerInGilde(ev.getPlayer())&&islands.containsKey(gilde.getPlayerGilde(ev.getPlayer()).toLowerCase()))if(isInIsland(ev.getPlayer(), ev.getBlock().getLocation()))return;
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void interact(PlayerInteractEvent ev){
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()&&UtilEvent.isAction(ev, ActionType.BLOCK)){
			if(ev.getPlayer().getItemInHand()!=null&&UtilBlock.isBlock(ev.getPlayer().getItemInHand())){
				if(gilde.isPlayerInGilde(ev.getPlayer())&&islands.containsKey(gilde.getPlayerGilde(ev.getPlayer()).toLowerCase()))if(isInIsland(ev.getPlayer(), ev.getClickedBlock().getLocation()))return;
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev){
		if(ev.getPlayer().getWorld()==getWorld()&&!ev.isCancelled()&&!ev.getPlayer().isOp()){
			if(ev.getBlock()==null)return;
			if(gilde.isPlayerInGilde(ev.getPlayer())&&islands.containsKey(gilde.getPlayerGilde(ev.getPlayer()).toLowerCase()))if(isInIsland(ev.getPlayer(), ev.getBlock().getLocation()))return;
			ev.setCancelled(true);
		}
	}
	
	public void loadIslandGilde(String gilde){
		gilde=gilde.toLowerCase();
		if(!islands.containsKey(gilde)){
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X`,`Z` FROM `list_gilden_Sky_world` WHERE gilde='"+gilde+"';");
		      while (rs.next()) {
		    	islands.put(gilde, new Location(world,rs.getInt(1),0,rs.getInt(2)));
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
		}
	}
	
	public Location getIslandFixHome(String gilde){
		gilde=gilde.toLowerCase();
		Location loc = getIslandHome(gilde);
		loc.getBlock().setType(Material.AIR);
		loc.clone().add(0,-1,0).getBlock().setType(Material.AIR);
		loc.clone().add(0,-2,0).getBlock().setType(Material.GLASS);
		return loc;
	}
	
	public Location getIslandHome(String gilde){
		gilde=gilde.toLowerCase();
		if(islands.containsKey(gilde)){
			return new Location(getWorld(), (islands.get(gilde).getBlockX()-(radius/2)) ,32, (islands.get(gilde).getBlockZ()-(radius/2)) );
		}
		return null;
	}
	
	public boolean addIsland(Player player,String gilde){
		gilde=gilde.toLowerCase();
		if(player.hasPermission(PermissionType.SKYBLOCK_GILDEN_ISLAND.getPermissionToString())){
			return addIsland(player,gilde, schematic,true);
		}
		return false;
	}
	
	public boolean newIsland(String gilde){
		gilde=gilde.toLowerCase();
		if(islands.containsKey(gilde)){
			Location loc = islands.get(gilde);
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
			Location loc1 = new Location(getWorld(), (max_x-(radius/2)) ,30, (max_z-(radius/2)) );
			loc1.getWorld().loadChunk(loc1.getChunk());
			UtilSchematic.pastePlate(session,loc1, new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
			Log("Die Insel von der Gilde "+gilde+"(Entities:"+count+"/Bloecke:"+b_count+"/X:"+(max_x-(radius/2))+"/Z:"+(max_z-(radius/2))+") wurde erneuert.");
			return true;
		}
		return false;
	}
	
	public boolean removeIsland(Player player,String gilde){
		gilde=gilde.toLowerCase();
		if(islands.containsKey(gilde)){
			Location loc = islands.get(gilde);
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
			islands.remove(gilde);
			getManager().getInstance().getMysql().Update(isAsync(),"UPDATE list_gilden_Sky_world SET gilde='!"+gilde+"' WHERE X='"+loc.getBlockX()+"' AND Z='"+loc.getBlockZ()+"'");
			islands.put("!"+gilde, loc);
			Location loc1 = new Location(getWorld(), (max_x-(radius/2)) ,30, (max_z-(radius/2)) );
			loc1.getWorld().loadChunk(loc1.getChunk());
			UtilSchematic.pastePlate(session,loc1, new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
			Log("Die Insel von der Gilde "+gilde+"(Entities:"+count+"/Bloecke:"+b_count+"/X:"+(max_x-(radius/2))+"/Z:"+(max_z-(radius/2))+") wurde resetet.");
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
			if(player.startsWith("!")){
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
				addIsland(null,null,schematic,false);
			}
			Log((anzahl-a)+" Inseln wurden hinzugefügt!");
		}
	}
	
	public boolean addIsland(Player player,String gilde,String schematic,boolean recyceln){
		if(player!=null){
			if(getManager().getDelete().contains(player.getName().toLowerCase())){
				player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "SKYBLOCK_REMOVE_ISLAND_ONE"));
				return false;
			}
		}
		if(gilde!=null)gilde=gilde.toLowerCase();
		if(recyceln){
			String island = recycelnIsland();
			if(island!=null){
				int x = islands.get(island).getBlockX();
				int z = islands.get(island).getBlockZ();
				islands.remove(island);
				islands.put(gilde, new Location(world,x,0,z));
				getManager().getInstance().getMysql().Update(isAsync(),"UPDATE list_gilden_Sky_world SET gilde='"+gilde+"' WHERE gilde='"+island+"'");
				Log("Die Insel von der Gilde "+gilde+"(X:"+x+",Z:"+z+") wurde recycelt.");
				return true;
			}
		}
			
		if( Z == (radius*100)){
			X+=radius;
			Z=0;
		}else{
			Z+=radius;
		}
		Location loc = new Location(getWorld(), (X-(radius/2)) ,30, (Z-(radius/2)) );
		loc.getWorld().loadChunk(loc.getChunk());
		UtilSchematic.pastePlate(session,loc, new File("plugins/kSkyBlock/schematics/"+schematic+".schematic"));
		if(gilde==null){
			gilde="!"+UUID.randomUUID();
		}
		islands.put(gilde, new Location(world,X,0,Z));
		getManager().getInstance().getMysql().Update(isAsync(),"INSERT INTO list_gilden_Sky_world (gilde,X,Z) VALUES ('"+gilde+"','"+X+"','"+Z+"');");
		Log("Die Insel von der Gilde "+gilde+"(X:"+(X-(radius/2))+",Z:"+(Z-(radius/2))+") wurde erstellt.");
		return true;
	}
	
	public boolean isInIsland(Player player,Location loc){
		return isInIsland(this.gilde.getPlayerGilde(player).toLowerCase(),loc);
	}
	
	public boolean isInIsland(String gilde,Location loc){
		gilde=gilde.toLowerCase();
		if(islands.containsKey(gilde)){
			return isInIsland(islands.get(gilde),loc);
		}
		return false;
	}
	
	public boolean isInIsland(Location loc,Location loc1){
		return MinLoc(loc).getX() <= loc1.getX() && MinLoc(loc).getZ() <= loc1.getZ() && MaxLoc(loc).getBlockX() >= loc1.getBlockX() && MaxLoc(loc).getBlockZ() >= loc1.getBlockZ();
	}
	
	public Location MinLoc(Location loc){
		return new Location(loc.getWorld(),loc.getBlockX()-radius,0,loc.getBlockZ()-radius);
	}
	
	public Location MaxLoc(Location loc){
		return loc;
	}
	
	public void setBiome(String gilde){
		gilde=gilde.toLowerCase();
		if(islands.containsKey(gilde)){
			int min_x = islands.get(gilde).getBlockX()-radius;
			int max_x = islands.get(gilde).getBlockX();
			
			int min_z = islands.get(gilde).getBlockZ()-radius;
			int max_z = islands.get(gilde).getBlockZ();
			
			for(int x = min_x; x < max_x; x++){
				for(int z = min_z; z < max_z; z++){
					getWorld().setBiome(x, z, Biome.JUNGLE);
				}
			}
		}
	}
	
	public int getCount(){
		try
	    {
	      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT COUNT(*) FROM list_gilden_Sky_world");
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
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X` FROM `list_gilden_Sky_world` ORDER BY X DESC LIMIT 1;");
		      while (rs.next()) {
		    	  X=rs.getInt(1);
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
			
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `Z` FROM `list_gilden_Sky_world` WHERE X='"+X+"' ORDER BY Z DESC LIMIT 1;");
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
	      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `gilde`,`X`,`Z` FROM `list_gilden_Sky_world`");
	      while (rs.next()) {
	    	if(!rs.getString(1).startsWith("!"))continue;
	    	islands.put(rs.getString(1).toLowerCase(), new Location(world,rs.getInt(2),0,rs.getInt(3)));
	      }
	      rs.close();
	    } catch (Exception err) {
	    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
	    }
		Log(islands.size()+" Inseln wurden Geladen!");
		solveXandZ();
	}
	
}
