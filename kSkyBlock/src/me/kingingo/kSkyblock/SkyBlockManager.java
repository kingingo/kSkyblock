package me.kingingo.kSkyblock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import lombok.Getter;
import me.kingingo.kSkyblock.Util.UtilSchematic;
import me.kingingo.kSkyblock.World.SkyBlockWorld;
import me.kingingo.kcore.ChunkGenerator.CleanroomChunkGenerator;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.WORLD_CHANGE_DATA;
import me.kingingo.kcore.Util.UtilFile;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.WorldUtil;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class SkyBlockManager extends kListener{
	
	@Getter
	private kSkyBlock instance;
	@Getter
	private ArrayList<SkyBlockWorld> worlds = new ArrayList<>();
	@Getter
	private UtilSchematic schematic;
	@Getter
	private ArrayList<String> schematics = new ArrayList<>();
	private boolean whitelist = false;
	
	public SkyBlockManager(kSkyBlock instance){
		super(instance,"[SkyBlockManager]");
		this.instance=instance;
		this.schematic=new UtilSchematic();
		loadSchematics();
		getInstance().getMysql().Update("CREATE TABLE IF NOT EXISTS list_skyblock_worlds(UUID varchar(100),worldName varchar(30),X int,Z int)");
		addWorld("normal", 100, 300);
	}
	
	public void loadSchematics(){
		File folder = new File("plugins/kSkyBlock/schematics");
		Log("Schamtics: ");
		if(!folder.exists())folder.mkdirs();
		for(File file : folder.listFiles()){
			if(file.isFile()){
				if(file.getName().contains(".schematic")){
					schematics.add(file.getName().replaceAll(".schematic", ""));
					Log(file.getName().replaceAll(".schematic", ""));
				}
			}
		}
	}
	
	public SkyBlockWorld addIsland(Player player){
		for(SkyBlockWorld world : worlds){
			if(player.hasPermission("epicpvp.skyblock.schematic."+world.getSchematic())){
				world.addIsland(player);
				return world;
			}
		}
		return null;
	}
	
	@EventHandler
	public void Login(PlayerLoginEvent ev){
		if(whitelist){
			ev.disallow(Result.KICK_WHITELIST, "§cDie Whitelist ist momentan Aktiv!");
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
	}
	
	@EventHandler
	public void PacketReceive(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof WORLD_CHANGE_DATA){
			WORLD_CHANGE_DATA packet = (WORLD_CHANGE_DATA)ev.getPacket();
			UtilPlayer.PermissionExChangeUUID(packet.getOld_uuid(), packet.getNew_uuid());
			for(World world : Bukkit.getWorlds())UtilPlayer.setWorldChangeUUID(world, packet.getOld_uuid(), packet.getNew_uuid());
		}
	}
	
	@EventHandler
	public void AsyncLogin(AsyncPlayerPreLoginEvent ev){
		for(SkyBlockWorld world : worlds)world.loadIslandPlayer(UtilPlayer.getRealUUID(ev.getName(),ev.getUniqueId()));
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.WHEREIS_TEXT.getText("SkyBlock"));
		ev.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
	}
	
	public SkyBlockWorld getIsland(Player player){
		return getIsland(UtilPlayer.getRealUUID(player));
	}
	
	public SkyBlockWorld getIsland(UUID uuid){
		for(SkyBlockWorld world : worlds){
			if(world.haveIsland(uuid)){
				return world;
			}
		}
		return null;
	}
	
	public boolean haveIsland(Player player){
		return haveIsland(UtilPlayer.getRealUUID(player));
	}
	
	public boolean haveIsland(UUID player){
		for(SkyBlockWorld world : worlds){
			if(world.haveIsland(player)){
				return true;
			}
		}
		return false;
	}
	
	public void loadWorld(String worldName){
		addWorld(worldName,getInstance().getFConfig().getInt("Config.World."+worldName+".Radius"),getInstance().getFConfig().getInt("Config.World."+worldName+".GenerateIsland"));
	}
	
	public void setConifg(String path,int paste){
		getInstance().getFConfig().set(path,paste);
		try {
			getInstance().getFConfig().save(new File("plugins/kSkyBlock/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setConifg(String path,String paste){
		getInstance().getFConfig().set(path,paste);
		try {
			getInstance().getFConfig().save(new File("plugins/kSkyBlock/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addWorld(String worldName,int radius,int generate){
		if(!UtilFile.existPath(new File(worldName))){
			setConifg("Config.World."+worldName+".Radius", radius);
			setConifg("Config.World."+worldName+".GenerateIsland", generate);
			setConifg("Config.World."+worldName+".CreatureLimit", 50);
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			worlds.add(new SkyBlockWorld(this,worldName,Bukkit.createWorld(wc),radius,generate,50));
		}else{
			WorldCreator wc = new WorldCreator(worldName);
			wc.generator(new CleanroomChunkGenerator(".0,AIR"));
			worlds.add(new SkyBlockWorld(this,worldName,WorldUtil.LoadWorld(wc),radius,generate,getInstance().getConfig().getInt("Config.World."+worldName+".CreatureLimit")));
		}
	}
	
}
