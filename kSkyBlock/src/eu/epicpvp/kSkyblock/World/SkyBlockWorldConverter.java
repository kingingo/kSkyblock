package eu.epicpvp.kSkyblock.World;

import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.base.Charsets;

import dev.wolveringer.client.LoadedPlayer;
import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kcore.MySQL.MySQLErr;
import eu.epicpvp.kcore.MySQL.Events.MySQLErrorEvent;
import eu.epicpvp.kcore.Util.UtilServer;

public class SkyBlockWorldConverter extends SkyBlockWorld{

	public SkyBlockWorldConverter(SkyBlockManager manager, String schematic, World world, int radius, int space,
			int anzahl, int creature_limit) {
		super(manager, schematic, world, radius, space, anzahl, creature_limit);
	}
	
	public void loadIslandPlayer(String playerName, UUID uuid){
		LoadedPlayer loadedplayer = UtilServer.getClient().getPlayer(playerName);
		
		if(!getIslands().containsKey(loadedplayer.getPlayerId())){
			try
		    {
		      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X`,`Z` FROM `list_skyblock_worlds` WHERE worldName='"+getWorld().getName().toLowerCase()+"' AND playerId='"+loadedplayer.getPlayerId()+"'");
		      while (rs.next()) {
		    	  getIslands().put(loadedplayer.getPlayerId(), new Location(getWorld(),rs.getInt(1),0,rs.getInt(2)));
		      }
		      rs.close();
		    } catch (Exception err) {
		    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
		    }
			
			if(!getIslands().containsKey(loadedplayer.getPlayerId())){
				try
			    {
			      ResultSet rs = getManager().getInstance().getMysql().Query("SELECT `X`,`Z` FROM `list_skyblock_worlds_old` WHERE worldName='"+getWorld().getName().toLowerCase()+"' AND uuid='"+getRealUUID(playerName,uuid)+"'");
			      while (rs.next()) {
			    	  getIslands().put(loadedplayer.getPlayerId(), new Location(getWorld(),rs.getInt(1),0,rs.getInt(2)));
			      }
			      rs.close();
			    } catch (Exception err) {
			    	Bukkit.getPluginManager().callEvent(new MySQLErrorEvent(MySQLErr.QUERY,err,getManager().getInstance().getMysql()));
			    }
				
				if(getIslands().containsKey(loadedplayer.getPlayerId())){
					getManager().getInstance().getMysql().Update("DELETE FROM list_skyblock_worlds_old WHERE uuid='"+getRealUUID(playerName,uuid)+"';");
					getManager().getInstance().getMysql().Update(isAsync(),"INSERT INTO list_skyblock_worlds (playerId,worldName,X,Z) VALUES ('"+loadedplayer.getPlayerId()+"','"+getWorld().getName()+"','"+getIslands().get(loadedplayer.getPlayerId()).getBlockX()+"','"+getIslands().get(loadedplayer.getPlayerId()).getBlockZ()+"');");
					logMessage(playerName+"("+loadedplayer.getPlayerId()+"/"+getRealUUID(playerName,uuid)+") Island converting to a NEW!");
				}
			}
		}
	}
	
	public static UUID getRealUUID(String playerName, UUID uuid){
		if(UUID.nameUUIDFromBytes(new StringBuilder().append("OfflinePlayer:").append(playerName).toString().getBytes(Charsets.UTF_8)).equals(uuid)){
			uuid=getOfflineUUID(playerName.toLowerCase());
		}
		return uuid;
	}
	
	public static UUID getRealUUID(Player player){
		UUID uuid = player.getUniqueId();
		if(UUID.nameUUIDFromBytes(new StringBuilder().append("OfflinePlayer:").append(player.getName()).toString().getBytes(Charsets.UTF_8)).equals(uuid)){
			uuid=getOfflineUUID(player.getName().toLowerCase());
		}
		return uuid;
	}
	
	public static UUID getOfflineUUID(String player){
		return UUID.nameUUIDFromBytes(new StringBuilder().append("OfflinePlayer:").append(player.toLowerCase()).toString().getBytes(Charsets.UTF_8));
	}
}
