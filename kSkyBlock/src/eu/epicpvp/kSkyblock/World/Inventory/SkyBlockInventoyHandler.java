package eu.epicpvp.kSkyblock.World.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.kSkyblock.SkyBlockManager;
import eu.epicpvp.kSkyblock.World.Inventory.Buttons.BiomeChangeButton;
import eu.epicpvp.kSkyblock.World.Inventory.Buttons.InviteButton;
import eu.epicpvp.kSkyblock.World.Inventory.Buttons.MemberButton;
import eu.epicpvp.kSkyblock.World.Island.HomeAble;
import eu.epicpvp.kSkyblock.World.Island.Island;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryYesNo;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventoryCopy;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.AnvilGUI;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilClickEvent;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilClickEventHandler;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilSlot;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.InventorySplit;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilNumber;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;

public class SkyBlockInventoyHandler {

	@Getter
	private SkyBlockManager manager;
	private InventoryPageBase createIsland;
	private InventoryPageBase menueIsland;
	private InventoryCopy memberPage;
	private InventoryCopy invitePage;
	private InventoryPageBase biomeChange;
	private InventoryCopy home;
	private InventoryCopy options;

	public SkyBlockInventoyHandler(SkyBlockManager manager) {
		this.manager = manager;
		createInv();
	}

	public void createInv() {
		this.createIsland = new InventoryPageBase(InventorySize._27, "Island Menue");
		this.menueIsland = new InventoryPageBase(InventorySize._45, "Island Menue");
		this.memberPage = new InventoryCopy(InventorySize._45, "Member Page");
		this.invitePage = new InventoryCopy(InventorySize._54, "Islands");
		this.biomeChange = new InventoryPageBase(InventorySize._54, "Biome ändern");
		this.options = new InventoryCopy(InventorySize._54, "Einstellungen");
		this.home = new InventoryCopy(InventorySize._54, "Homes");

		// Home Start
		this.home.setCreate_new_inv(true);
		this.home.setFor_with_copy_page(false);
		this.home.setItem(4, UtilItem.Item(new ItemStack(Material.BED), new String[] { "", "§7Hier kannst du Homes auf deiner", "§7Insel verwalten", "" }, "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §6Homes"));
		this.home.addButton(12, new ButtonCopy(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object inv) {
				Island is = manager.getIsland(player);
				if (is != null) {
					int[] array = is.getHomesArray();
					int index = 0;
					if (array != null && array.length != 0) {
						int page = 2;
						ItemStack item = ((InventoryPageBase) inv).getItem(50);
						if (item != null && !item.getItemMeta().getDisplayName().equalsIgnoreCase(" ")) {
							page = UtilNumber.toInt(item.getItemMeta().getDisplayName().split("c")[1]) + 1;
							index = (page == 2 ? 0 : (page - 2) * 7);
						}

						if (((InventoryPageBase) inv).getButton(50) == null) {
							((InventoryPageBase) inv).addButton(50, new ButtonBase(new Click() {

								@Override
								public void onClick(Player player, ActionType type, Object object) {
									if (((ItemStack) object).getType() != Material.ARROW)
										return;
									((ButtonCopy) ((InventoryPageBase) inv).getButton(12)).set.onClick(player, type, inv);
								}

							}, UtilItem.RenameItem(new ItemStack(Material.ARROW, 1), "§7§l" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §c" + page)));
						}

						if (array.length < (index + 7)) {
							((InventoryPageBase) inv).setItem(50, UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14), " "));
						} else {
							((InventoryPageBase) inv).setItem(50, UtilItem.RenameItem(new ItemStack(Material.ARROW), "§7§l" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §c" + page));
						}

						item = ((InventoryPageBase) inv).getItem(48);
						if (((InventoryPageBase) inv).getButton(48) == null) {
							((InventoryPageBase) inv).addButton(48, new ButtonBase(new Click() {

								@Override
								public void onClick(Player player, ActionType type, Object object) {
									if (((ItemStack) object).getType() != Material.ARROW)
										return;
									int npage = 0;
									if (((InventoryPageBase) inv).getItem(50).getType() == Material.STAINED_GLASS_PANE) {
										npage = UtilNumber.toInt(((InventoryPageBase) inv).getItem(48).getItemMeta().getDisplayName().split(" ")[0].substring(2));
									} else {
										npage = UtilNumber.toInt(((InventoryPageBase) inv).getItem(50).getItemMeta().getDisplayName().split("c")[1]) - 2;
									}
									((InventoryPageBase) inv).setItem(50, UtilItem.RenameItem(new ItemStack(Material.ARROW, 1, (byte) 14), "§7§l" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §c" + npage));
									((ButtonCopy) ((InventoryPageBase) inv).getButton(12)).set.onClick(player, type, inv);
								}

							}, UtilItem.RenameItem(new ItemStack(Material.ARROW, 1), "§c" + (page - 2) + " §7§l" + Zeichen.DOUBLE_ARROWS_l.getIcon())));

						}

						if (index == 0) {
							((InventoryPageBase) inv).setItem(48, UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14), " "));
						} else {
							((InventoryPageBase) inv).setItem(48, UtilItem.RenameItem(new ItemStack(Material.ARROW), "§c" + (page - 2) + " §7§l" + Zeichen.DOUBLE_ARROWS_l.getIcon()));
						}

						for (int slot = 28; slot <= 34; slot++) {
							((InventoryPageBase) inv).delButton(slot + 9);
							if (array.length > index) {
								LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(array[index]);
								((InventoryPageBase) inv).setItem(slot, UtilItem.RenameItem(UtilItem.Head(loadedplayer.getName()), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 " + loadedplayer.getName()));
								((InventoryPageBase) inv).addButton(slot + 9, new ButtonBase(new Click() {

									@Override
									public void onClick(Player player, ActionType type, Object object) {
										is.removeHome(loadedplayer.getPlayerId());
										options.open(player, UtilInv.getBase());
									}

								}, UtilItem.RenameItem(new ItemStack(351, 1, (byte) 1), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§c Alle Homes löschen.")));
								index++;
							} else {
								((InventoryPageBase) inv).setItem(slot, new ItemStack(Material.AIR));
								((InventoryPageBase) inv).setItem(slot + 9, new ItemStack(Material.AIR));
							}
						}
					}

					if (is.getHomeAble() == HomeAble.NOTHING) {
						((InventoryPageBase) inv).setItem(12, UtilItem.addEnchantmentGlow(((InventoryPageBase) inv).getItem(12)));
					}
				}
			}

		}, new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
				if (is != null) {
					if (is.getHomeAble() != HomeAble.NOTHING) {
						is.setHomeAble(HomeAble.NOTHING);
					}
				}
			}

		}, UtilItem.RenameItem(new ItemStack(351, 1, (byte) 8), "§cnicht setzbar")));
		this.home.addButton(13, new ButtonCopy(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
				if (is != null) {
					if (is.getHomeAble() == HomeAble.QUESTION) {
						((InventoryPageBase) object).setItem(13, UtilItem.addEnchantmentGlow(((InventoryPageBase) object).getItem(13)));
					}
				}
			}

		}, new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
				if (is != null) {
					if (is.getHomeAble() != HomeAble.QUESTION) {
						is.setHomeAble(HomeAble.QUESTION);
					}
				}
			}

		}, UtilItem.RenameItem(new ItemStack(351, 1, (byte) 9), "§dNur auf Anfrage")));
		this.home.addButton(14, new ButtonCopy(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
				if (is != null) {
					if (is.getHomeAble() == HomeAble.EVER) {
						((InventoryPageBase) object).setItem(14, UtilItem.addEnchantmentGlow(((InventoryPageBase) object).getItem(14)));
					}
				}
			}

		}, new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
				if (is != null) {
					if (is.getHomeAble() != HomeAble.EVER) {
						is.setHomeAble(HomeAble.EVER);
					}
				}
			}

		}, UtilItem.RenameItem(new ItemStack(351, 1, (byte) 10), "§dOhne Anfrage")));
		this.home.addButton(0, new ButtonOpenInventoryCopy(this.options, UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));

		UtilInv.getBase().addPage(home);
		// Home End

		// Biome start
		this.biomeChange.addButton(0, new ButtonOpenInventoryCopy(this.options, UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
		this.biomeChange.addButton(20, new BiomeChangeButton(Biome.FOREST, manager, UtilItem.RenameItem(new ItemStack(Material.SAPLING), "§6Wald")));
		this.biomeChange.addButton(21, new BiomeChangeButton(Biome.TAIGA, manager, UtilItem.RenameItem(new ItemStack(Material.SAPLING, 1, (byte) 1), "§6Taiga")));
		this.biomeChange.addButton(22, new BiomeChangeButton(Biome.DESERT, manager, UtilItem.RenameItem(new ItemStack(32), "§6Wüste")));
		this.biomeChange.addButton(23, new BiomeChangeButton(Biome.SWAMPLAND, manager, UtilItem.RenameItem(new ItemStack(111), "§6Sumpf")));
		this.biomeChange.addButton(24, new BiomeChangeButton(Biome.JUNGLE, manager, UtilItem.RenameItem(new ItemStack(106), "§6Dschungel")));

		this.biomeChange.addButton(29, new BiomeChangeButton(Biome.BEACH, manager, UtilItem.RenameItem(new ItemStack(Material.SAND), "§6Strand")));
		this.biomeChange.addButton(30, new BiomeChangeButton(Biome.MESA, manager, UtilItem.RenameItem(new ItemStack(Material.RED_SANDSTONE), "§6Mesa")));
		this.biomeChange.addButton(31, new BiomeChangeButton(Biome.SKY, manager, UtilItem.RenameItem(new ItemStack(Material.ENDER_STONE), "§6End")));
		this.biomeChange.addButton(32, new BiomeChangeButton(Biome.PLAINS, manager, UtilItem.RenameItem(new ItemStack(Material.GRASS), "§6Graslandschaft")));
		this.biomeChange.addButton(33, new BiomeChangeButton(Biome.SAVANNA, manager, UtilItem.RenameItem(new ItemStack(6, 1, (byte) 4), "§6Savanne")));
		UtilInv.getBase().addPage(biomeChange);
		// Biome end

		// OPTIONS START
		this.options.setCreate_new_inv(true);
		this.options.addButton(0, new ButtonOpenInventory(this.menueIsland, UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
		this.options.addButton(11, new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = getManager().getIsland(player);
				if (is != null && is.contains(player.getLocation())) {
					is.setHome(player.getLocation());
				}
				player.closeInventory();
			}

		}, UtilItem.RenameItem(new ItemStack(Material.BED), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §6 Homepunkt setzen")));
		this.options.addButton(13, new ButtonOpenInventoryCopy(this.home, UtilInv.getBase(), UtilItem.Item(new ItemStack(Material.BOOK), new String[] { " ", "§7Hier kannst du sehen, wer", "§7auf deiner Insel ein Home", "§7gesetzt hat und du kannst einstellen,", "§7ob Spieler auf deiner Insel ein Home", "§7setzen können.", " " }, "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §6 Home Optionen")));
		this.options.addButton(15, new ButtonOpenInventory(this.biomeChange, UtilItem.Item(new ItemStack(Material.GRASS), new String[] { " ", "§7Wechsel das Biom", "§7deiner Insel.", " " }, "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §6 Inselbiome ändern")));
		this.options.setItem(InventorySplit._36.getMiddle(), UtilItem.Item(new ItemStack(Material.EYE_OF_ENDER), new String[] { "", "§7Klicke, um Mob-Spawn zu", "§7aktivieren oder zu deaktivieren.", "" }, "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 Mob-Spawn"));
		this.options.addButton(InventorySplit._45.getMiddle(), new ButtonCopy(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = getManager().getIsland(player);
				if (is != null) {
					if (is.isMobSpawn()) {
						((InventoryPageBase) object).setItem(InventorySplit._45.getMiddle(), UtilItem.RenameItem(new ItemStack(351, 1, (byte) 10), "§aOn"));
					} else {
						((InventoryPageBase) object).setItem(InventorySplit._45.getMiddle(), UtilItem.RenameItem(new ItemStack(351, 1, (byte) 8), "§cOff"));
					}
				}
			}

		}, new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = getManager().getIsland(player);
				if (is != null) {
					if (is.isMobSpawn()) {
						is.setMobSpawn(false);
						((ItemStack) object).setDurability((short) 8);
						ItemMeta im = ((ItemStack) object).getItemMeta();
						im.setDisplayName("§cOff");
						((ItemStack) object).setItemMeta(im);
					} else {
						is.setMobSpawn(true);
						((ItemStack) object).setDurability((short) 10);
						ItemMeta im = ((ItemStack) object).getItemMeta();
						im.setDisplayName("§aOn");
						((ItemStack) object).setItemMeta(im);
					}
				}
			}

		}, new ItemStack(351, 1, (byte) 1)));
		UtilInv.getBase().addPage(this.options);
		// OPTIONS END

		// CREATE START
		this.createIsland.addButton(0, new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				player.closeInventory();
			}

		}, UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cSchließen")));

		this.createIsland.addButton(InventorySplit._18.getMiddle(), new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				player.closeInventory();
				Bukkit.dispatchCommand(player, "is create");
			}

		}, UtilItem.RenameItem(new ItemStack(Material.GRASS), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 Erstelle deine §eSkyBlock§6 Insel!")));
		UtilInv.getBase().addPage(this.createIsland);
		// CREATE END

		// Member Page Start
		this.memberPage.setCreate_new_inv(true);
		UtilInv.getBase().addPage(this.memberPage);
		this.memberPage.addButton(0, new ButtonOpenInventory(this.menueIsland, UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
		this.memberPage.addButton(8, new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Island is = manager.getIsland(player);
				if (is.getMember().size() >= 3)
					return;

				if (player.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_4.getPermissionToString())) {
					if (is.getMember().size() >= 4) {
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
						return;
					}
				} else if (player.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_3.getPermissionToString())) {
					if (is.getMember().size() >= 3) {
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
						return;
					}
				} else if (player.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_2.getPermissionToString())) {
					if (is.getMember().size() >= 2) {
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
						return;
					}
				} else if (player.hasPermission(PermissionType.SKYBLOCK_INVITE_PLAYER_1.getPermissionToString())) {
					if (is.getMember().size() >= 1) {
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDu benötigst mindestens §bMVP§c um auf mehr als zwei Insel eingeladen zu werden oder mehr als zwei Spieler auf deine Insel einzuladen.");
						return;
					}
				}

				AnvilGUI gui = new AnvilGUI(player, getManager().getInstance(), new AnvilClickEventHandler() {

					@Override
					public void onAnvilClick(AnvilClickEvent event) {
						if (event.getSlot() == AnvilSlot.OUTPUT) {

							if (UtilPlayer.isOnline(event.getName())) {
								Player target = (Player) Bukkit.getPlayer(event.getName());
								getManager().getInvite().put(target.getName(), player.getName());
								TextComponent text = new TextComponent(TranslationHandler.getPrefixAndText(target, "SKYBLOCK_INVITE_GET", player.getName()));
								text.addExtra(UtilPlayer.createClickableText(" §a[ACCEPT]", "/is accept " + "{player_"+player.getName()+"}"));
								target.spigot().sendMessage(text);
								player.sendMessage(TranslationHandler.getPrefixAndText(player, "SKYBLOCK_INVITE_SEND", target.getName()));
							} else {
								player.sendMessage(TranslationHandler.getPrefixAndText(player, "PLAYER_IS_OFFLINE", event.getName()));
							}
							player.closeInventory();
						}
					}
				});
				gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "Name"));
				gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aFertig"));
				gui.open();
			}

		}, UtilItem.Item(new ItemStack(Material.BOOK_AND_QUILL), new String[] { "§a/is einladen [Player]" }, "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + " §6Mitglieder§e hinzufügen§6.")));
		int s = 19;
		for (int i = 0; i < 4; i++) {
			this.memberPage.addButton(s, new MemberButton(s, i, memberPage, getManager()));
			s += 2;
		}
		// Member Page End

		// Menue START
		this.menueIsland.addButton(0, new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				player.closeInventory();
			}

		}, UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cSchließen")));

		this.menueIsland.addButton(11, new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				player.closeInventory();
				Bukkit.dispatchCommand(player, "is home");
			}

		}, UtilItem.RenameItem(new ItemStack(Material.BED), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 Teleportiere dich zu deiner §eInsel§6.")));
		this.menueIsland.addButton(13, new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				player.closeInventory();
				if (!getManager().getInstance().getAntiLogout().is(player))
					return;
				Bukkit.dispatchCommand(player, "spawn");
			}

		}, UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 Teleportiere dich zum §eSpawn§6.")));
		this.menueIsland.addButton(15, new ButtonOpenInventoryCopy(UtilServer.getAchievementsHandler().getInventory(), UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.BOOK), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 Erfolge")));
		this.menueIsland.addButton(28, new ButtonOpenInventoryCopy(options, UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(356), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 Einstellungen")));
		this.menueIsland.addButton(30, new ButtonOpenInventoryCopy(memberPage, UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§e Mitglieder §6Verwaltung.")));
		this.menueIsland.addButton(32, new ButtonOpenInventoryCopy(invitePage, UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.EMERALD), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§6 Insel auf denen du §eMitlgied§6 bist.")));
		this.menueIsland.addButton(34, new ButtonBase(new Click() {

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				InventoryYesNo question = new InventoryYesNo("Delete Island", new Click() {

					@Override
					public void onClick(Player player, ActionType type, Object object) {
						player.closeInventory();
						Bukkit.dispatchCommand(player, "is delete");
					}

				}, new Click() {

					@Override
					public void onClick(Player player, ActionType type, Object object) {
						player.closeInventory();
					}

				});

				UtilInv.getBase().addAnother(question);
				player.openInventory(question);
			}

		}, UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY, 1, (byte) 14), "§7" + Zeichen.DOUBLE_ARROWS_R.getIcon() + "§c Insel löschen.")));
		UtilInv.getBase().addPage(this.menueIsland);
		// Menue END

		// invitePage Start
		this.invitePage.setCreate_new_inv(true);
		this.invitePage.addButton(0, new ButtonOpenInventory(this.menueIsland, UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));

		int slot = 11;
		for (int i = 0; i < 3; i++) {
			this.invitePage.addButton(new InviteButton(invitePage, slot, i, manager));
			slot += 2;
		}

		UtilInv.getBase().addPage(this.invitePage);

		this.createIsland.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.menueIsland.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.memberPage.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.invitePage.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.options.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.biomeChange.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.home.fillBorder(Material.STAINED_GLASS_PANE, 7);
	}

	public void openInv(Player player) {
		if (getManager().haveIsland(player)) {
			player.openInventory(this.menueIsland);
		} else {
			player.openInventory(this.createIsland);
		}
	}
}
