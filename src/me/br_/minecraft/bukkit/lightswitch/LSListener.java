package me.br_.minecraft.bukkit.lightswitch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

public class LSListener extends BlockListener {
	private List<Location> levers; // list of all the light switches on the
									// server

	public LSListener() {
		levers = new ArrayList<Location>(); // initialize levers
		// TODO: read levers from file
	}

	public void save() {
		// TODO: save levers to file
	}

	@Override
	public void onBlockRedstoneChange(BlockRedstoneEvent event) { // when
																	// redstone
																	// changes
		Block block = event.getBlock(); // the block that changed
		World world = event.getBlock().getWorld(); // the world that contains
													// the block that changed
		if (levers.contains(block.getLocation())) { // if the block that changed
													// is a light switch
			if (world.getTime() > 13000) { // if it's day
				world.setTime(0); // make it night
				for (Location l : levers) {
					Block lever = l.getBlock();
					lever.setData((byte) (lever.getData() % 8)); // update the
																	// levers
				}
			} else { // if it's night
				world.setTime(13000); // make it day
				for (Location l : levers) {
					Block lever = l.getBlock();
					lever.setData((byte) (8 + (lever.getData() % 8))); // update
																		// the
																		// levers
				}
			}
		} else if (block.getType() == Material.LEVER) { // if it's a lever but
														// not a light switch
			BlockFace[] faces = { BlockFace.NORTH, BlockFace.SOUTH,
					BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN,
					BlockFace.DOWN }; // all the faces a lever can be attached
										// to
			for (int i = 1; i <= 6; i++) {
				if (block.getFace(faces[i - 1]).getType() == Material.DIAMOND_BLOCK
						&& (block.getData() % 8) == i) { // if the lever is
															// attached to a
															// diamond block
					levers.add(block.getLocation()); // make it a light switch
					onBlockRedstoneChange(new BlockRedstoneEvent(block, 1, 0)); // change
																				// the
																				// time
				}
			}
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) { // when blocks are created
		Block block = event.getBlock(); // block placed
		if (block.getType() != Material.LEVER
				|| event.getBlockAgainst().getType() != Material.DIAMOND_BLOCK) {
			return; // if it isn't a light switch, stop thinking about it
		}
		if (block.getWorld().getTime() > 13000) { // if it's day
			block.setData((byte) (8 + (block.getData() % 8))); // turn switch up
		} else { // if it's night
			block.setData((byte) (block.getData() % 8)); // turn switch down
		}
		levers.add(block.getLocation());
		event.getPlayer().sendMessage("Light Switch created.");
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) { // when blocks are broken
		Block block = event.getBlock(); // block broken
		if (levers.remove(block.getLocation())) { // if it was a lever, remove
													// it from memory
			event.getPlayer().sendMessage("Light Switch destroyed.");
		} else if (block.getType() == Material.DIAMOND_BLOCK) { // if it was a
																// diamond block
			BlockFace[] faces = { BlockFace.NORTH, BlockFace.WEST,
					BlockFace.EAST, BlockFace.SOUTH, BlockFace.UP }; // faces a
																		// switch
																		// can
																		// be on
			int broken = 0;
			for (BlockFace face : faces) { // for each face,
				if (levers.remove(block.getFace(face).getLocation())) {
					broken++; // if there's a light switch there, remove it  
				}
			}
			if (broken > 1) {
				event.getPlayer().sendMessage(
						String.format("%d Light Switches destroyed.", broken));
			} else if (broken == 1) {
				event.getPlayer().sendMessage("Light Switch destroyed.");
			}
		}
	}
}
