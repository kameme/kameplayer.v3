package kame.kameplayer.baseutils.mnsver;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import kame.kameplayer.baseutils.Utils;
import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.ChatComponentText;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityTypes;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.MojangsonParser;
import net.minecraft.server.v1_9_R1.NBTBase;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_9_R1.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import net.minecraft.server.v1_9_R1.PacketPlayOutResourcePackSend;
import net.minecraft.server.v1_9_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_9_R1.TileEntity;
import net.minecraft.server.v1_9_R1.TileEntityCommand;
import net.minecraft.server.v1_9_R1.World;

public class V1_9_R1 extends Vx_x_Rx {
	public void sendPacket(Player player, String packet, float x, float y, float z,
			float x2, float y2, float z2, float speed, int amount) {
		String[] str = packet.split("_");
		EnumParticle particle = get(str[0]);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(
				new PacketPlayOutWorldParticles(particle, true, x, y, z, x2, y2, z2, speed, amount, a(str)));
		
	}
	private EnumParticle get(String name) {
		for(EnumParticle particle : EnumParticle.values()) {
			if(particle.b().replaceFirst("_", "").equalsIgnoreCase(name))return particle;
		}
		return EnumParticle.BARRIER;
	}
	
	private int[] a(String... str) {
		List<Integer> list = new ArrayList<Integer>();
		for(String l : str)if(l.matches("[0-9]+"))list.add(Utils.parse(l, 0));
		int[] m = new int[list.size()];
		for(int i = 0; i < list.size(); i++)m[i] = list.get(i);
		return m;
		
	}
	public List<String> getParticles() {
		List<String> list = new ArrayList<String>();
		for(EnumParticle p : EnumParticle.values())list.add(p.b().toLowerCase());
		return list;
	}
	
	public String getNBTTag(ItemStack bufferitem) {
		NBTTagCompound tag = CraftItemStack.asNMSCopy(bufferitem).getTag();
		if(tag == null)return "";
		return tag.toString();
	}
	
	public void sendRespawn(Player player) {
	((CraftPlayer)player).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
	}
	
	public void sendBarMessage(Player player, String message) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(message), (byte) 2));
	}
	
	public org.bukkit.entity.Entity getNBTEntity(CommandSender sender, Location loc, String[] args) {

		World world = ((CraftWorld)loc.getWorld()).getHandle();
		NBTTagCompound NBTTag = new NBTTagCompound();
		boolean tag = true;
		try{
			if (args.length >= 5 && args[4].startsWith("{")) {
				int i;
				for(i = 4; args.length > i && !args[i].endsWith("}"); i++);

				ChatComponentText text = new ChatComponentText("");
				for (int j = 4; j < i+1; j++) {
					text.a(" ");
					text.addSibling(new ChatComponentText(args[j]));
				}
				NBTBase base = MojangsonParser.parse(((IChatBaseComponent)text).toPlainText());
				if ((base instanceof NBTTagCompound)) {
					NBTTag = (NBTTagCompound) base;
					tag = false;
				} else {
					sender.sendMessage(ChatColor.RED + "BadTag");
					return null;
				}
			}
			NBTTag.setString("id", args[0]);

			Entity entity = EntityTypes.a(NBTTag, world );
			if (entity != null) {
				(entity).setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), entity.yaw, entity.pitch);
				if (tag && entity instanceof EntityInsentient){((EntityInsentient) entity).prepare(world.D(new BlockPosition(entity)), null);}
				world.addEntity(entity);

				while ((entity != null) && (NBTTag.hasKeyOfType("Riding", 10))) {
					Entity tagentity = EntityTypes.a(NBTTag.getCompound("Riding"), world);
					if (tagentity != null) {
						tagentity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), tagentity.yaw, tagentity.pitch);
						world.addEntity(tagentity);
						entity.startRiding(tagentity);
					}
					entity = tagentity;
					NBTTag = NBTTag.getCompound("Riding");
				}
			}
			return entity.getBukkitEntity();

		}catch(Exception ex) {
			System.err.println("コマンドの実行に失敗しましたデータタグを確認してください。");
			sender.sendMessage(ChatColor.RED + "コマンドの実行に失敗しましたデータタグを確認してください。");

		}
		return null;
	}
	public void copyBlockEntity(org.bukkit.World w, int x, int y, int z, int i, int j, int k) {
		World world = ((CraftWorld)w).getHandle();
		BlockPosition pos1 = new BlockPosition(x, y, z);
		BlockPosition pos2 = new BlockPosition(i, j, k);
		IBlockData data = world.getType(pos1);
		Block block = data.getBlock();
		world.setTypeAndData(pos2, data, 3);
		if(block.isTileEntity()) {
			TileEntity tileEntity = world.getTileEntity(pos1);
			TileEntity tileEntity2 = world.getTileEntity(pos2);
			if(tileEntity == null || tileEntity2 == null) {
				world.update(pos2, block);
				return;
			}
			world.getChunkAtWorldCoords(pos2);
			NBTTagCompound nbt = new NBTTagCompound();
			tileEntity.save(nbt);
			nbt.setInt("x", i);
			nbt.setInt("y", j);
			nbt.setInt("z", k);
			world.getTileEntity(pos2).a(nbt);
		}
		world.update(pos2, block);
	}
	
	public void copyBlockCommandNBT(org.bukkit.block.Block b, byte power) {
		World world = ((CraftWorld)b.getWorld()).getHandle();
		BlockPosition pos = new BlockPosition(b.getX(), b.getY(), b.getZ());
		((TileEntityCommand) world.getTileEntity(pos)).getCommandBlock().a(power);
		IBlockData data = world.getChunkAtWorldCoords(pos).getBlockData(pos);
		world.notify(pos, data, data, 3);
		world.update(pos, data.getBlock());
	}
	
	public void sendTexture(Player player, String string, String string2) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutResourcePackSend(string, string2));
	}
}