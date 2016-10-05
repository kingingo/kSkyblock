package eu.epicpvp.kSkyblock.World.Island;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

@Getter
public class Member {
	private kPlayer kplayer;
	private Island island;
	private ArrayList<IslandPermission> permissions;
	
	public Member(int playerId,Island island){
		this.permissions=new ArrayList<>();
		this.island=island;
		
		if(!island.getWorld().getManager().getPlayers().containsKey(playerId)){
			this.kplayer=island.getWorld().getManager().getPlayers().put(playerId, new kPlayer(playerId));
		}
		this.kplayer=island.getWorld().getManager().getPlayers().get(playerId);
		this.kplayer.getMemberList().put(island.getPlayerId(),this);
	}
	
	public int getPlayerId(){
		return kplayer.getPlayerId();
	}
	
	public void delete(){
		if(UtilPlayer.isOnline(kplayer.getPlayerId())){
			Player player = UtilPlayer.searchExact(kplayer.getPlayerId());
			
			if(island.contains(player.getLocation())){
				player.teleport(Bukkit.getWorld("world").getSpawnLocation());
			}
		}
		this.kplayer.getMemberList().remove(island.getPlayerId());
		this.island.getMember().remove(kplayer.getPlayerId());
		this.permissions.clear();
		UtilServer.getMysql().Update("DELETE FROM list_skyblock_worlds_friends WHERE playerId='"+kplayer.getPlayerId()+"' AND ownerId='"+island.getPlayerId()+"' AND worldName='"+island.getLocation().getWorld().getName()+"';");
	}
	
	public void remove(IslandPermission perm){
		if(permissions.contains(perm)){
			permissions.remove(perm);
			UtilServer.getMysql().Update("DELETE FROM list_skyblock_worlds_friends WHERE playerId='"+kplayer.getPlayerId()+"' AND ownerId='"+island.getPlayerId()+"' AND worldName='"+island.getLocation().getWorld().getName()+"' AND permission='"+perm.getPermission()+"'");
		}
	}
	
	public void add(IslandPermission perm){
		if(!permissions.contains(perm)){
			permissions.add(perm);
			UtilServer.getMysql().Update("INSERT INTO list_skyblock_worlds_friends (playerId,ownerId, worldName, permission) VALUES ('" + kplayer.getPlayerId() + "','" + island.getPlayerId() + "','" + island.getLocation().getWorld().getName() + "','"+perm.getPermission()+"');");
		}
	}
}
