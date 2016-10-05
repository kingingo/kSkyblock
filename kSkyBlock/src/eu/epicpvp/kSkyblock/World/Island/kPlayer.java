package eu.epicpvp.kSkyblock.World.Island;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.SkyBlockWorld;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class kPlayer {

	@Getter
	private int playerId;
	@Getter
	private HashMap<Integer,Member> memberList;
	
	public kPlayer(int playerId){
		this.playerId=playerId;
		this.memberList=new HashMap<>();
	}
	
	public void load(){
		UtilServer.getMysql().asyncQuery("SELECT * FROM `list_skyblock_worlds_friends` WHERE playerId='"+playerId+"' AND permission='none';", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rs,Throwable ex) {
				try {
					if(!getMemberList().containsKey(rs.getInt("ownerId"))){
						for(SkyBlockWorld world : SkyBlockManager.getManager().getWorlds())
							world.loadIslandPlayer(rs.getInt("ownerId"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
