package eu.epicpvp.kSkyblock.World;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.ContainerBlock;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.Events.IslandAccessEvent;
import eu.epicpvp.kSkyblock.World.Events.IslandCreateEvent;
import eu.epicpvp.kSkyblock.World.Island.HomeAble;
import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kSkyblock.World.Island.IslandPermission;
import eu.epicpvp.kcore.Command.Commands.Events.PlayerDelHomeEvent;
import eu.epicpvp.kcore.Command.Commands.Events.PlayerHomeEvent;
import eu.epicpvp.kcore.Command.Commands.Events.PlayerSetHomeEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.MySQL.MySQLErr;
import eu.epicpvp.kcore.MySQL.Events.MySQLErrorEvent;
import eu.epicpvp.kcore.TeleportManager.Events.PlayerTeleportedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.kConfig.kConfig;
import eu.epicpvp.nbt.NBTCompressedStreamTools;
import eu.epicpvp.nbt.NBTTagCompound;
import lombok.Getter;
import lombok.Setter;

public class SkyBlockWorld extends kListener {

	@Getter
	private SkyBlockManager manager;
	@Getter
	private World minecraftWorld;
	@Getter
	private HashMap<Integer, Island> islands = new HashMap<>();
	@Getter
	private ArrayList<Island> empty_islands = new ArrayList<>();
	private int X = 0;
	private int Z = 0;
	@Getter
	private int radius;
	@Getter
	private int space;
	@Getter
	private String schematic;
	@Getter
	private EditSession session;
	@Getter
	@Setter
	private boolean async = false;
	private int creature_limit;

	public SkyBlockWorld(SkyBlockManager manager, String schematic, World minecraftWorld, int radius, int space,
			int anzahl, int creature_limit) {
		super(manager.getInstance(), "SkyBlockWorld:" + minecraftWorld.getName());
		this.manager = manager;
		this.minecraftWorld = minecraftWorld;
		this.creature_limit = creature_limit;
		this.schematic = schematic;
		this.radius = radius + space;
		this.space = space;
		this.session = new EditSession(new BukkitWorld(getMinecraftWorld()), 999999999);
		loadIslands();
		addIslands(anzahl);
	}

	@EventHandler
	public void teleported(PlayerTeleportedEvent ev) {
		if (ev.getTeleporter().getLoc_to().getWorld().getUID() == getMinecraftWorld().getUID() && ev.getTeleporter().getFrom() != null) {
			int playerId = UtilPlayer.getPlayerId(ev.getTeleporter().getFrom());
			if(this.islands.containsKey(playerId)){
				Island is = this.islands.get(playerId);
				
				if(is.contains(ev.getTeleporter().getLoc_to())){
					UtilPlayer.sendPacket(ev.getTeleporter().getFrom(), is.getIslandBorder());
					return;
				}
			}
			
			for(Island is : this.islands.values()){
				if(is.contains(ev.getTeleporter().getLoc_to())){
					if(is.getMember().containsKey(playerId)){
						UtilPlayer.sendPacket(ev.getTeleporter().getFrom(), is.getIslandBorder());
					}
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void home(PlayerHomeEvent ev){
		if(!ev.getPlayer().isOp() && ev.getHome().getWorld().getUID() == getMinecraftWorld().getUID()){
			if(ev.getConfig().isSet("homes."+ev.getName()+".ownerId")){
				int playerId = ev.getConfig().getInt("homes."+ev.getName()+".ownerId");
				
				if(!UtilPlayer.isOnline(playerId)){
					ev.setReason("Der Spieler der Insel ist nicht Online!");
					ev.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void delHome(PlayerDelHomeEvent ev){
		if(ev.getHome().getWorld().getUID() == getMinecraftWorld().getUID()){
			if(ev.getConfig().isSet("homes."+ev.getName()+".ownerId")){
				int playerId = ev.getConfig().getInt("homes."+ev.getName()+".ownerId");
				
				for(String home : ev.getConfig().getPathList("homes").keySet()){
					if(home.equals(ev.getName()))continue;
					
					if(ev.getConfig().isSet("homes."+home+".ownerId")){
						if(ev.getConfig().getInt("homes."+ev.getName()+".ownerId")==playerId){
							playerId=-1;
							break;
						}
					}
				}
				
				if(playerId!=-1){
					if(!islands.containsKey(playerId)){
						loadIslandPlayer(playerId);
					}
					
					Island is = getIsland(playerId);
					
					if(is!=null){
						is.removeHome(UtilPlayer.getPlayerId(ev.getPlayer()));
					}
				}
			}
		}
	}

	@EventHandler
	public void Home(PlayerSetHomeEvent ev) {
		if (ev.getHome().getWorld().getUID() == getMinecraftWorld().getUID()) {
			Island is = getIsland(ev.getHome());
			ev.setCancelled(true);
			
			if(is!=null){
				Player player = UtilPlayer.searchExact(is.getPlayerId());
				
				if(player!=null){
					if(is.getHomeAble()==HomeAble.QUESTION){
						if (getManager().getInstance().getHa().list.containsKey(player)) {
							getManager().getInstance().getHa().list.remove(player);
							getManager().getInstance().getHa().list_loc.remove(player);
							getManager().getInstance().getHa().list_name.remove(player);
						}
						getManager().getInstance().getHa().list.put(player, ev.getPlayer());
						getManager().getInstance().getHa().list_loc.put(player, ev.getHome());
						getManager().getInstance().getHa().list_name.put(player, ev.getName());
						player.sendMessage(TranslationHandler.getPrefixAndText(player, "HOME_QUESTION", ev.getPlayer().getName()));
						ev.setReason(TranslationHandler.getText(player, "HOME_ISLAND"));
					}else if(is.getHomeAble()==HomeAble.EVER){
						ev.setCancelled(false);
						kConfig config = UtilServer.getUserData().getConfig(ev.getPlayer());
						config.set("homes."+ev.getName()+".ownerId", is.getPlayerId());
						is.addHome(UtilPlayer.getPlayerId(ev.getPlayer()));
					}else{
						ev.setReason("Der Spieler möchte nicht das du ein Home auf seiner Insel setzt!");
					}
				}
			}

			if (ev.getReason() == null) {
				ev.setReason("Der Spieler der Insel ist nicht Online!");
			}
		}
	}

	@EventHandler
	public void Fall(PlayerMoveEvent ev) {
		if (ev.getPlayer().getWorld().getUID() == getMinecraftWorld().getUID() && !ev.getPlayer().isOnGround() && ev.getPlayer().getLocation().getBlockY() < 0) {
			ev.getPlayer().setHealth(((CraftPlayer) ev.getPlayer()).getMaxHealth());
			ev.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
			ev.getPlayer().setHealth(((CraftPlayer) ev.getPlayer()).getMaxHealth());
		}
	}

	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent ev) {
		if(ev.getLocation().getWorld().getUID() == getMinecraftWorld().getUID()){
			if(UtilServer.getLagMeter().getTicksPerSecond() < 17){
				ev.setCancelled(true);
				return;
			}
			
			if (ev.getSpawnReason() != SpawnReason.CUSTOM) {
				ev.setCancelled(true);
				Island is = getIsland(ev.getLocation());
				if(is!=null){
					if(is.isMobSpawn()){
						int a = 0;
						for (Entity e : getMinecraftWorld().getEntities()) {
							if (!(e instanceof Player) && !(e instanceof ItemFrame) && !(e instanceof ArmorStand)) {
								if (is.contains(e.getLocation())) a++;
							}
						}
						
						if (a < this.creature_limit)ev.setCancelled(false);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void access(IslandAccessEvent ev){
		if(ev.getWorld()==null){
			logMessage("ev.getWorld() is NULL!?"); 
			return;
		}
		
		if(ev.getWorld().getMinecraftWorld().getUID() == getMinecraftWorld().getUID()){
			int playerId = UtilPlayer.getPlayerId(ev.getPlayer());

			if(this.islands.containsKey(playerId)){
				if(this.islands.get(playerId).contains(ev.getLocation())){
					ev.setCancelled(false);
					return;
				}
			}
			
			for(Island is : this.islands.values()){
				if(is.contains(ev.getLocation())){
					if(is.getMember().containsKey(playerId)){
						
						if(ev.getEvent() instanceof InventoryOpenEvent){
							if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.USE_CHEST))ev.setCancelled(false);
						}else if(ev.getEvent() instanceof PlayerPickupItemEvent){
							if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.PICKUP_ITEMS))ev.setCancelled(false);
						}else if(ev.getEvent() instanceof EntityDamageByEntityEvent){
							if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.KILL_MOBS))ev.setCancelled(false);
						}else if(ev.getEvent() instanceof BlockBreakEvent){
							BlockBreakEvent breake = (BlockBreakEvent) ev.getEvent();
							if(breake.getBlock().getType()==Material.CHEST){
								if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.USE_CHEST))ev.setCancelled(false);
							}else{
								if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.BUILD))ev.setCancelled(false);
							}
						}else if(ev.getEvent() instanceof BlockPlaceEvent){
							if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.BUILD))ev.setCancelled(false);
						}else if(ev.getEvent() instanceof PlayerInteractEvent){
							PlayerInteractEvent e = (PlayerInteractEvent)ev.getEvent();
							
							if(UtilEvent.isAction(e, ActionType.RIGHT_BLOCK)){
								if(e.getClickedBlock().getState() instanceof ContainerBlock){
									if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.USE_CHEST))ev.setCancelled(false);
									break;
								}
							}else if(UtilEvent.isAction(e, ActionType.LEFT_BLOCK)){
								if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.BUILD))ev.setCancelled(false);
								break;
							}
							
							if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.INTERACT)){
								ev.setCancelled(false);
							}
						}else if(ev.getEvent() instanceof PlayerInteractEntityEvent){
							if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.INTERACT)){
								ev.setCancelled(false);
							}
						}else if(ev.getEvent() instanceof PlayerBucketFillEvent
								|| ev.getEvent() instanceof PlayerBucketEmptyEvent){
							if(is.getMember().get(playerId).getPermissions().contains(IslandPermission.INTERACT)){
								ev.setCancelled(false);
							}
						}
					}
					break;
				}
			}
		}
	}
	
	public Island getIsland(Player player){
		return getIsland(UtilPlayer.getPlayerId(player));
	}
	
	public Island getIsland(int playerId){
		return this.islands.get(playerId);
	}
	
	public Island getIsland(Location location){
		for(Island is : getIslands().values()){
			if(is.contains(location)){
				return is;
			}
		}
		return null;
	}

	public boolean haveIsland(Player player) {
		return haveIsland(UtilPlayer.getPlayerId(player));
	}

	public boolean haveIsland(int playerId) {
		return this.islands.containsKey(playerId);
	}

	public void loadIslandPlayer(Player player) {
		loadIslandPlayer(UtilPlayer.getPlayerId(player));
	}

	public void loadIslandPlayer(int playerId) {
		if (!this.islands.containsKey(playerId)) {
			try {
				ResultSet rs = getManager().getInstance().getMysql().Query("SELECT * FROM `list_skyblock_worlds` WHERE worldName='"+ getMinecraftWorld().getName().toLowerCase() + "' AND playerId='" + playerId + "'");
				while (rs.next()) {
					NBTTagCompound nbt = null;
					if(rs.getString("properties")==null||rs.getString("properties").isEmpty()){
						nbt=new NBTTagCompound();
					}else{
						nbt=NBTCompressedStreamTools.read(rs.getString("properties"));
					}
					
					islands.put(playerId, new Island(playerId, new Location(getMinecraftWorld(), rs.getInt("X"), 0, rs.getInt("Z")),nbt, this));
				}
				rs.close();
			} catch (Exception err) {
				Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getManager().getInstance().getMysql()));
			}
		}
	}

	public void addIslands(int anzahl) {
		int a = this.empty_islands.size();
		if (a < anzahl) {
			for (int i = 0; i < (anzahl - a); i++) {
				addIsland(-1, false);
			}
			logMessage((anzahl - a) + " Inseln wurden hinzugefügt!");
		}
	}

	public Island recycelnIsland() {
		return this.empty_islands.get(0);
	}

	public Island addIsland(Player player) {
		return addIsland(UtilPlayer.getPlayerId(player), true);
	}
	
	public Island addIsland(int playerId, boolean recyceln) {
		if (recyceln) {
			Island island = recycelnIsland();
			if (island != null) {
				island.setOwner(playerId);
				return island;
			}
		}

		if (Z == (radius * 100)) {
			X += radius;
			Z = 0;
		} else {
			Z += radius;
		}

		Location location = new Location(getMinecraftWorld(), X, 0, Z);
		Island is = new Island(playerId, location,new NBTTagCompound(), this);
		is.reset(false);

		if(playerId==-1){
			empty_islands.add(is);
		}else{
			islands.put(playerId, is);
		}
		try {
			getManager().getInstance().getMysql().Update(isAsync(), "INSERT INTO list_skyblock_worlds (playerId,worldName,X,Z,properties) VALUES ('" + playerId + "','" + getMinecraftWorld().getName() + "','" + X + "','" + Z + "','"+NBTCompressedStreamTools.toString(is.getNbt())+"');");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logMessage("Die Insel von den Spieler " + playerId + "(X:" + (X - (radius / 2)) + ",Z:" + (Z - (radius / 2)) + ") wurde erstellt.");
		Bukkit.getPluginManager().callEvent(new IslandCreateEvent(is));
		return is;
	}

	public int getCount() {
		try {
			ResultSet rs = getManager().getInstance().getMysql()
					.Query("SELECT COUNT(*) FROM list_skyblock_worlds WHERE worldName='"
							+ getMinecraftWorld().getName().toLowerCase() + "';");
			while (rs.next()) {
				return rs.getInt(1);
			}
			rs.close();
		} catch (Exception err) {
			Bukkit.getPluginManager()
					.callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getManager().getInstance().getMysql()));
		}
		return 0;
	}

	public void solveXandZ() {
		if (getCount() != 0) {
			try {
				ResultSet rs = getManager().getInstance().getMysql()
						.Query("SELECT `X` FROM `list_skyblock_worlds` WHERE worldName='"
								+ getMinecraftWorld().getName().toLowerCase() + "' ORDER BY X DESC LIMIT 1;");
				while (rs.next()) {
					X = rs.getInt(1);
				}
				rs.close();
			} catch (Exception err) {
				Bukkit.getPluginManager()
						.callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getManager().getInstance().getMysql()));
			}

			try {
				ResultSet rs = getManager().getInstance().getMysql()
						.Query("SELECT `Z` FROM `list_skyblock_worlds` WHERE X='" + X + "' AND worldName='"
								+ getMinecraftWorld().getName().toLowerCase() + "' ORDER BY Z DESC LIMIT 1;");
				while (rs.next()) {
					Z = rs.getInt(1);
				}
				rs.close();
			} catch (Exception err) {
				Bukkit.getPluginManager()
						.callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getManager().getInstance().getMysql()));
			}
			logMessage("X: " + X + " Z:" + Z);
		} else {
			X = radius;
			Z = 0;
			logMessage("X: " + X + " Z:" + Z);
		}
	}
	
	public void loadAllIslands() {
		try {
			int i = 0;
			ResultSet rs = getManager().getInstance().getMysql()
					.Query("SELECT * FROM `list_skyblock_worlds` WHERE worldName='"+ getMinecraftWorld().getName().toLowerCase() + "';");
			while (rs.next()) {
				NBTTagCompound nbt = null;
				if(rs.getString("properties")==null||rs.getString("properties").isEmpty()){
					nbt=new NBTTagCompound();
				}else{
					nbt=NBTCompressedStreamTools.read(rs.getString("properties"));
				}
				
				Island is = new Island(rs.getInt("playerId"), new Location(getMinecraftWorld(), rs.getInt("X"), 0, rs.getInt("Z")), nbt, this);
				this.islands.put(i++, is);
			}
			rs.close();
		} catch (Exception err) {
			Bukkit.getPluginManager()
					.callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getManager().getInstance().getMysql()));
		}
	}

	public void loadIslands() {
		try {
			ResultSet rs = getManager().getInstance().getMysql()
					.Query("SELECT * FROM `list_skyblock_worlds` WHERE worldName='"
							+ getMinecraftWorld().getName().toLowerCase() + "' AND playerId='-1';");
			while (rs.next()) {
				NBTTagCompound nbt = null;
				if(rs.getString("properties")==null||rs.getString("properties").isEmpty()){
					nbt=new NBTTagCompound();
				}else{
					nbt=NBTCompressedStreamTools.read(rs.getString("properties"));
				}
				this.empty_islands.add(new Island(-1, new Location(getMinecraftWorld(), rs.getInt("X"), 0, rs.getInt("Z")), nbt, this));
			}
			rs.close();
		} catch (Exception err) {
			Bukkit.getPluginManager()
					.callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getManager().getInstance().getMysql()));
		}
		
		if(UtilTime.nowDate().getHours()<4 && UtilTime.nowDate().getHours()>2){
			try {
				ResultSet rs = getManager().getInstance().getMysql()
						.Query("SELECT * FROM `list_skyblock_worlds` WHERE worldName='"
								+ getMinecraftWorld().getName().toLowerCase() + "' AND playerId='-2';");
				while (rs.next()) {
					NBTTagCompound nbt = null;
					if(rs.getString("properties")==null||rs.getString("properties").isEmpty()){
						nbt=new NBTTagCompound();
					}else{
						nbt=NBTCompressedStreamTools.read(rs.getString("properties"));
					}
					
					Island is = new Island(-2, new Location(getMinecraftWorld(), rs.getInt("X"), 0, rs.getInt("Z")), nbt, this);
					is.reset(true);
					this.empty_islands.add(is);
				}
				rs.close();
			} catch (Exception err) {
				Bukkit.getPluginManager()
						.callEvent(new MySQLErrorEvent(MySQLErr.QUERY, err, getManager().getInstance().getMysql()));
			}
		}
		
		logMessage(this.empty_islands.size() + " Inseln wurden Geladen!");
		solveXandZ();
	}
}
