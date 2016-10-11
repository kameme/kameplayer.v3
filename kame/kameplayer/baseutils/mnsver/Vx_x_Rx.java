package kame.kameplayer.baseutils.mnsver;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Vx_x_Rx {
	public abstract void sendPacket(Player player, String packet, float x, float y, float z,float x2, float y2, float z2, float speed, int amount);

	public abstract void sendRespawn(Player player);

	public abstract void sendBarMessage(Player player, String message);

	public abstract String getNBTTag(ItemStack bufferitem);

	public abstract Entity getNBTEntity(CommandSender sender, Location loc, String[] args);

	public abstract void copyBlockEntity(World world, int x, int y, int z, int i, int j, int k);

	public abstract void copyBlockCommandNBT(org.bukkit.block.Block b, byte power);

	public abstract List<String> getParticles();

	public abstract void sendTexture(Player player, String string, String string2);

}
