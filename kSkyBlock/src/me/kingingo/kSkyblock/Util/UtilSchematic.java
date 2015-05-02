package me.kingingo.kSkyblock.Util;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;

public class UtilSchematic {
	
	public static void pastePlate(EditSession editSession,Location l,File file){
		l.getChunk().load();
		try {
		    CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
		    cc.paste(editSession, new Vector(l.getX(), l.getY(), l.getZ()), false);
		    cc.copy(editSession);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (com.sk89q.worldedit.world.DataException e) {
			e.printStackTrace();
		}
	}
}
