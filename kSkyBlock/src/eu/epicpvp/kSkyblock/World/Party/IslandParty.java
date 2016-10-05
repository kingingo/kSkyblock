package eu.epicpvp.kSkyblock.World.Party;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class IslandParty{

	@Getter
	private Player owner;
	@Getter
	private Island island;
	@Getter
	private Scoreboard board;
	@Getter
	private ArrayList<Player> players;
	
	public IslandParty(Island island){
		this.owner=UtilPlayer.searchExact(island.getPlayerId());
		this.island=island;
		this.players=new ArrayList<>();
		
		if(owner==null)
			throw new NullPointerException("[IslandParty] der Owner der Party ist nicht online!?");
		
		createScoreboard();
	}
	
	public void broadcast(String name,Object... objects){
		this.owner.sendMessage(TranslationHandler.getPrefixAndText(this.owner, name,objects));
		for(Player player : this.players){
			player.sendMessage(TranslationHandler.getPrefixAndText(player, name,objects));
		}
	}
	
	public Location getHome(){
		return this.island.getHome();
	}
	
	public boolean containsPlayer(Player player){
		return this.players.contains(player) || player.getUniqueId() == this.owner.getUniqueId();
	}
	
	public void removePlayer(Player player){
		if(player.getUniqueId() == owner.getUniqueId()){
			for(Player p : new ArrayList<>(getPlayers())){
				removePlayer(p);
			}
			
			UtilScoreboard.resetScore(this.board, player.getName(), DisplaySlot.SIDEBAR);
			player.setScoreboard(UtilServer.getPermissionManager().getScoreboard());
			Bukkit.getPluginManager().callEvent(new PlayerSetScoreboardEvent(player));
		}else if(this.players.contains(player)){
			this.players.remove(player);
			UtilScoreboard.resetScore(this.board, player.getName(), DisplaySlot.SIDEBAR);
			player.setScoreboard(UtilServer.getPermissionManager().getScoreboard());
			Bukkit.getPluginManager().callEvent(new PlayerSetScoreboardEvent(player)); 
			
			if(this.island.contains(player.getLocation())){
				player.teleport(Bukkit.getWorld("world").getSpawnLocation());
			}
		}
	}
	
	public void addPlayer(Player player){
		if(!this.players.contains(player)){
			this.players.add(player);
			UtilScoreboard.setScore(this.board,player.getName(), DisplaySlot.SIDEBAR, -1);
			player.setScoreboard(this.board);
		}
	}
	
	private void createScoreboard(){
		this.board=this.owner.getScoreboard();
		
		String scorename = "§b"+this.owner.getName()+" Party";
		
		this.board.getObjective(DisplaySlot.SIDEBAR).setDisplayName(scorename);
		UtilScoreboard.clearScores(this.board, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(this.board,"§7"+"Spieler: ", DisplaySlot.SIDEBAR, 0);
		UtilScoreboard.setScore(this.board,this.owner.getName(), DisplaySlot.SIDEBAR, -1);
		this.owner.setScoreboard(this.board);
	}
}
