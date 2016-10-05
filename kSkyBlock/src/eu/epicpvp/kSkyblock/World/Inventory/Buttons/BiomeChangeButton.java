package eu.epicpvp.kSkyblock.World.Inventory.Buttons;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;

public class BiomeChangeButton extends ButtonBase{

	public BiomeChangeButton(Biome biome,SkyBlockManager manager, ItemStack item) {
		super(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
				
				if(is!=null){
					is.setBiome(biome);
					player.closeInventory();
				}
			}
			
		}, item);
	}

}
