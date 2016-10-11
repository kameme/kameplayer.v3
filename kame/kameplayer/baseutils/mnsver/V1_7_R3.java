package kame.kameplayer.baseutils.mnsver;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.ChatComponentText;
import net.minecraft.server.v1_7_R3.CommandBlockListenerAbstract;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityTypes;
import net.minecraft.server.v1_7_R3.EnumClientCommand;
import net.minecraft.server.v1_7_R3.GroupDataEntity;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import net.minecraft.server.v1_7_R3.MojangsonParser;
import net.minecraft.server.v1_7_R3.NBTBase;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R3.TileEntity;
import net.minecraft.server.v1_7_R3.TileEntityCommand;
import net.minecraft.server.v1_7_R3.World;

public class V1_7_R3 extends Vx_x_Rx {
	public void sendPacket(Player player, String packet, float x, float y, float z,
			float x2, float y2, float z2, float speed, int amount){
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(packet, x, y, z, x2, y2, z2, speed, amount));
	}
	public List<String> getParticles() {
		List<String> list = new ArrayList<String>();
		for(Particle p : Particle.values())list.add(p.name());
		return list;
	}
	public void sendRespawn(Player player){
	((CraftPlayer)player).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
	}

	public void sendBarMessage(Player player, String message) {
	}
	public String getNBTTag(ItemStack bufferitem) {
		NBTTagCompound tag = CraftItemStack.asNMSCopy(bufferitem).tag;
		if(tag == null)return "";
		return tag.toString();
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
					text.a(new ChatComponentText(args[j]));
				}
				NBTBase base = MojangsonParser.parse(((IChatBaseComponent)text).c());
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
				if (tag && entity instanceof EntityInsentient){((EntityInsentient) entity).a((GroupDataEntity)null);}
				world.addEntity(entity);

				while ((entity != null) && (NBTTag.hasKeyOfType("Riding", 10))) {
					Entity tagentity = EntityTypes.a(NBTTag.getCompound("Riding"), world);
					if (tagentity != null) {
						tagentity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), tagentity.yaw, tagentity.pitch);
						world.addEntity(tagentity);
						entity.mount(tagentity);
					}
					entity = tagentity;
					NBTTag = NBTTag.getCompound("Riding");
				}
			}
			return entity.getBukkitEntity();

		}catch(Exception ex){
			System.err.println("コマンドの実行に失敗しましたデータタグを確認してください。");
			sender.sendMessage(ChatColor.RED + "コマンドの実行に失敗しましたデータタグを確認してください。");

		}
		return null;
	}

	public void copyBlockEntity(org.bukkit.World w, int x, int y, int z, int i, int j, int k) {
		World world = ((CraftWorld)w).getHandle();
		Block block = world.getType(x, y, z);
		world.setTypeAndData(i, j, k, block, world.getData(x, y, z), 3);
		if(block.isTileEntity()) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			TileEntity tileEntity2 = world.getTileEntity(i, j, k);
			if(tileEntity == null || tileEntity2 == null) {
				world.update(i, j, k, block);
				return;
			}
			world.getChunkAtWorldCoords(i/16, k/16);
			NBTTagCompound nbt = new NBTTagCompound();
			tileEntity.b(nbt);
			nbt.setInt("x", i);
			nbt.setInt("y", j);
			nbt.setInt("z", k);
			tileEntity2.a(nbt);
		}
		world.update(i, j, k, block);
	}
	
	public void copyBlockCommandNBT(org.bukkit.block.Block b, byte power) {
		World world = ((CraftWorld)b.getWorld()).getHandle();
		Block data = world.getType(b.getX(), b.getY(), b.getZ());
		TileEntity tile = world.getTileEntity(b.getX(), b.getY(), b.getZ());
		if(tile != null && tile instanceof TileEntityCommand) {
			CommandBlockListenerAbstract cb = ((TileEntityCommand) tile).a();
			NBTTagCompound nbt = new NBTTagCompound();
			cb.a(nbt);
			nbt.setByte("SuccessCount", power);
			cb.b(nbt);
			world.update(b.getX(), b.getY(), b.getZ(), data);
			world.notify(b.getX(), b.getY(), b.getZ());
		}
	}
	
	public void sendTexture(Player player, String string, String string2) {
	}
}
