package me.kingingo.kSkyblock.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;

public class UtilSchematic {

	@Getter
	@Setter
    EditSession editSession=null ;
	
	public UtilSchematic(){
		
	}
	
	public void removePlate(){
		editSession.undo(editSession);
	}
	
	public static List<Block> getScans(int radius, Location startloc) {
		List<Block> list = Lists.newArrayList();
		final Block block = startloc.getBlock();
		final int x = block.getX();
		final int y = block.getY();
		final int z = block.getZ();
		final int minX = x - radius;
		final int minY = y - radius;
		final int minZ = z - radius;
		final int maxX = x + radius;
		final int maxY = y + radius;
		final int maxZ = z + radius;
		for (int counterX = minX; counterX <= maxX; counterX++) {
			for (int counterY = minY; counterY <= maxY; counterY++) {
				for (int counterZ = minZ; counterZ <= maxZ; counterZ++) {
					final Block blockName = startloc.getWorld().getBlockAt(
							counterX, counterY, counterZ);
					list.add(blockName);
				}
			}
		}

		return list;
	}
	
	private void loadArea(World world, File file,Vector origin) throws DataException, IOException, MaxChangedBlocksException{
	    EditSession es = editSession;
	    CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
	    cc.paste(es, origin, false);
	    cc.copy(es);   
	}
	
	public void pastePlate(Location l,File file){
		if(editSession==null)editSession=new EditSession(new BukkitWorld(l.getWorld()), 999999999);
		Vector v = new Vector(l.getX(), l.getY(), l.getZ());
		try {
			loadArea(Bukkit.getWorld("ender"), file, v);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
