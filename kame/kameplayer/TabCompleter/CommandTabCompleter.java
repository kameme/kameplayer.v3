package kame.kameplayer.TabCompleter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.BlockIterator;

import com.google.common.collect.ImmutableList;

import kame.kameplayer.baseutils.AxeUtils;
import kame.kameplayer.baseutils.AxeUtils.Locs;
import kame.kameplayer.baseutils.Utils;

public class CommandTabCompleter implements TabCompleter {


	private String[] particles= Utils.getParticles().toArray(new String[0]);
	private List<String> entitytypes;
	ArrayList<String> materialList = new ArrayList<String>(), entityList = new ArrayList<String>();
	{
		for (EntityType entity: EntityType.values())entityList.add(StringUtils.capitalize(entity.name().toLowerCase()));
		Collections.sort(entityList);entitytypes = ImmutableList.copyOf(entityList);

	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{

		int l = args.length-1;
		ArrayList<String> completion = new ArrayList<String>();
		Player player = (Player)sender;
		if(!Utils.hasPermCommand(sender, cmd.getName()))return completion;
		if(cmd.getName().equals("mods")) {
			if(args.length == 1)completion.addAll(completer(args[l], Bukkit.getOnlinePlayers()));
			return completion;
		}
		if(cmd.getName().equals("vacuum")) {
			if(args.length == 1)completion.addAll(completer(args[l], new String[]{"on", "off", "add", "remove"}));
			if(args.length == 2) {
				if(args[0].equals("on"))completion.addAll(completer(args[l], new String[]{"5"}));
				if(args[0].equals("add") || args[0].equals("remove"))return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
			}
			return completion;
		}
		if(cmd.getName().equalsIgnoreCase("guidelines")) {
			if(args.length == 1)completion.addAll(completer(args[l], new String[]{"create", "put", "cancel"}));
			if(args.length == 2)completion.addAll(completer(args[l], new String[]{"@a", "@p", "@r"}));
			if(args.length >= 3)return completion;

		}
		if(cmd.getName().equalsIgnoreCase("trig")) {
			if(args.length == 1)completion.addAll(completer(args[l], Bukkit.getOnlinePlayers()));
			if(args.length == 2)completion.addAll(completer(args[l], new String[]{"1"}));
		}

		if(cmd.getName().equalsIgnoreCase("invclose")) {
			if(args.length == 1)completion.addAll(completer(args[l], Bukkit.getOnlinePlayers()));
		}
		if(cmd.getName().equalsIgnoreCase("regioncopy")) {
			if(args.length == 1) {
				Locs locs = AxeUtils.getAxePos(player);
				if(locs != null) {
					Location look = lockAt(player);
					String message = locs.getL().getBlockX() + " " + locs.getL().getBlockY() + " " + locs.getL().getBlockZ()
							 + " " + locs.getR().getBlockX() + " " + locs.getR().getBlockY() + " " + locs.getR().getBlockZ()
							 + " " + look.getBlockX() + " " + look.getBlockY() + " " + look.getBlockZ();
						completion.addAll(completer("", new String[]{message}));
				}
			}
			if(args.length > 9)return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);

		}
		
		if(cmd.getName().equals("pull")) {
			if(args.length == 1)completion.addAll(completer(args[l], Bukkit.getOnlinePlayers()));
			if(args.length == 2)return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
			if(args.length == 3)completion.addAll(completer(args[l], new String[]{"-", "amount", "0"}));
			if(args.length == 4)completion.addAll(completer(args[l], new String[]{"-", "data", "0"}));
			if(args[l].startsWith("-s"))completion.add("-stack");
		}
		if(cmd.getName().equals("time")) {
			if(args.length == 1)completion.addAll(completer(args[l], new String[]{"set", "add"}));
			if(args.length == 2)completion.addAll(completer(args[l], new String[]{"0"}));
			if(args.length == 3)completion.addAll(completer(args[l], new String[]{"-a"}));
		}
		if(cmd.getName().equals("itemfix")) {
			// itemfix player addlore|setlore(-line=fix)|removelore|addname|setname|removename| item data ~~~~
			if(args.length == 1)completion.addAll(completer(args[l], Bukkit.getOnlinePlayers()));
			if(args.length == 2)completion.addAll(completer(args[l], new String[]{"addlore","setlore","removelore","addname","setname","removename"}));
			if(args.length == 3)return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
			if(args.length == 4)completion.addAll(completer(args[l], new String[]{"-"}));


		}

		if(cmd.getName().equals("tellbar")) {
			if(args.length > 0) completion.addAll(completer(args[l], Bukkit.getOnlinePlayers()));
			return completion;
		}

		if(cmd.getName().equalsIgnoreCase("fill"))
		{
			//fill x y z dx dy dz block option block

			//replace destroy keep replacement
			if(args.length == 1) {
				Locs locs = AxeUtils.getAxePos(player);
				if(locs != null) {
					String message = locs.getL().getBlockX() + " " + locs.getL().getBlockY() + " " + locs.getL().getBlockZ()
							 + " " + locs.getR().getBlockX() + " " + locs.getR().getBlockY() + " " + locs.getR().getBlockZ();
					completion.addAll(completer("", new String[]{message}));
					return completion;
				}
			}
			if(args.length == 1)completion.addAll(completer(args[l], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 2)completion.addAll(completer(args[l], new String[]{player.getLocation().getBlockY()-1+""}));
			if(args.length == 3)completion.addAll(completer(args[l], new String[]{player.getLocation().getBlockZ()+""}));
			if(args.length > 3 && args.length < 7)
			{
				Location loc = lockAt(player);
				if(args.length == 4)completion.addAll(completer(args[l], new String[]{loc.getBlockX()+""}));
				if(args.length == 5)completion.addAll(completer(args[l], new String[]{loc.getBlockY()+""}));
				if(args.length == 6)completion.addAll(completer(args[l], new String[]{loc.getBlockZ()+""}));
			}
			if(args.length == 7 || args.length == 9)return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
			if(args.length == 8)completion.addAll(completer(args[l], new String[]{"destroy", "replace", "replacement", "keep"}));
		}
		if(cmd.getName().equalsIgnoreCase("attack"))
		{
			//attack @r x y z damage distance type
			if(args.length == 1)return null;
			if(args.length == 2)completion.addAll(completer(args[1], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 3)completion.addAll(completer(args[2], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 4)completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockZ()+""}));

			if(args.length == 5)completion.addAll(completer(args[4], new String[]{0+""}));
			if(args.length == 6)completion.addAll(completer(args[5], new String[]{0+""}));
			if(args.length == 7)for (String entity : entitytypes)if (entity.startsWith(args[6]))completion.add(entity);
		}
		if(cmd.getName().equalsIgnoreCase("armor"))
		{
			//armor <repair>
			if(args.length == 1)return null;
			if(args.length == 2)completion.add("repair");
		}

		if(cmd.getName().equalsIgnoreCase("itemrun"))
		{ //itemrun <player> item data amount {NBT} /command
			if(args.length == 1)return null;
			if(args.length == 2)return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
			if(args.length == 3)completion.addAll(completer(args[l], new String[]{"-1"}));
			if(args.length == 4)completion.addAll(completer(args[l], new String[]{"1"}));
			if(args.length == 5)completion.addAll(completer(args[l], new String[]{"consume","keep","consume-p","keep-p"}));
			if(args.length == 6)completion.addAll(completer(args[l], new String[]{Utils.getNBTTag(((Player)sender).getItemInHand())}));
			if(args.length == 7)completion.addAll(completer(args[l], Bukkit.getCommandAliases().keySet().toArray(new String[0])));
		}

		if(cmd.getName().equalsIgnoreCase("entityrun"))
		{ //entityrun <player> item data amount {NBT} /command
			if(args.length == 1)for(String type : entityList)if(type.startsWith(args[0]))completion.add(type);
			if(args.length == 2)completion.addAll(completer(args[1], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 3)completion.addAll(completer(args[2], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 4)completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockZ()+""}));
		}

		if(cmd.getName().equalsIgnoreCase("broadcast"))
		{
			//bloadcast <> <> <>
			if(args.length == 1)completion.addAll(completer(args[0], new String[]{"all","server","@a","@p"}));
			if(args.length > 1)
			{
				if(args[args.length-1].startsWith("score_"))
				{
					String str = args[args.length-1].replaceFirst("score_", "");
					for(Objective object : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives())if(object.getName().startsWith(str))completion.add("score_" + object.getName());
					return completion;
				}
				completion.addAll(completer(args[0], new String[]{"@p","[+]","score_"}));
			}
		}
		if(cmd.getName().equalsIgnoreCase("drop"))
		{
			//drop player minecraft:item id time
			if(args.length == 1)return null;
			if(args.length > 2)return completion;
			return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
		}
		if(cmd.getName().equalsIgnoreCase("entity"))
		{
			//entity remove
			if(args.length == 1)completion.add("remove");
			if(args.length == 2)return null;
			if(args.length == 3 && args[1].equals("loc"))completion.addAll(completer(args[2], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 4 && args[1].equals("loc"))completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 5 && args[1].equals("loc"))completion.addAll(completer(args[4], new String[]{player.getLocation().getBlockZ()+""}));
			if((args.length ==3 &&!args[1].equals("loc")) || (args.length == 6 && args[1].equals("loc")))completion.add("0");
			if((args.length ==4 &&!args[1].equals("loc")) || (args.length == 7 && args[1].equals("loc")))for (String entity : entitytypes)if (entity.startsWith(args[3]))completion.add(entity);
		}
		if(cmd.getName().equalsIgnoreCase("explode"))
		{
			//explode loc,player power  player, location
			if(args.length == 1){completion.add("player");completion.add("loc");}
			if(args.length == 2)completion.add("0");
			if(args.length == 3 && args[0].equals("player"))return null;
			if(args.length == 3 && args[0].equals("loc"))completion.addAll(completer(args[2], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 4 && args[0].equals("loc"))completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 5 && args[0].equals("loc"))completion.addAll(completer(args[4], new String[]{player.getLocation().getBlockZ()+""}));
		}
		if(cmd.getName().equalsIgnoreCase("firework"))
		{
			//firework 1~4
			if(args.length == 1)return null;
			if(args.length == 2)completion.add("1~4");
		}

		if(cmd.getName().equalsIgnoreCase("itemtp"))
		{
			if(args.length == 1)return null;
			if(args.length == 2)completion.addAll(completer(args[1], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 3)completion.addAll(completer(args[2], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 4)completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockZ()+""}));
			if(args.length == 5)return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
			if(args.length == 6)completion.addAll(completer(args[5], new String[]{"0","data"}));
			if(args.length == 7)completion.addAll(completer(args[5], new String[]{"amount"}));
			if(args.length == 8)completion.addAll(completer(args[5], new String[]{"flag"}));

		}
		if(cmd.getName().equalsIgnoreCase("particle"))
		{
			if(label.startsWith("c")) {
				if(args.length == 1)completion.addAll(completer(args[0] ,particles));
				if(args.length == 2) {
					Location loc = lockAt(player);
					completion.addAll(completer(args[5], new String[]{loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()}));
				}
				if(args.length == 3)completion.addAll(completer(args[5], new String[]{"0,0"}));
			}
			if(args.length == 1)return null;
			if(args.length == 2)for(String name : particles)if(name.startsWith(args[1]))completion.add(name);
			if(args.length == 3){for(String name : new String[]{"0","circle","line"})if(name.startsWith(args[2]))completion.add(name);return completion;}

			if(args.length == 4 && (args[2].matches("circle|line"))) {
				Locs locs = AxeUtils.getAxePos(player);
				if(locs != null) {
					Location L = locs.getL();
					Location R = locs.getR();
					StringBuilder builder = new StringBuilder().append((int)(L.distance(R)*10)).append(" 0 0 0 0 ")
							.append(L.getBlockX()).append(" ").append(L.getBlockY()).append(" ").append(L.getBlockZ()).append(" ");
					if(args[2].equals("line"))  builder.append(R.getBlockX()).append(" ")
													   .append(R.getBlockY()).append(" ")
													   .append(R.getBlockZ());
					if(args[2].equals("circle"))builder.append(Math.abs(L.getBlockX()-R.getBlockX()))
													   .append(" ").append(Math.abs(R.getBlockY()-L.getBlockY()))
													   .append(" ").append(Math.abs(L.getBlockZ()-R.getBlockZ()));

					completion.addAll(completer(args[l], new String[]{builder.toString()}));
					return completion;
				}
			}

			if(args.length == 4)completion.add("10");
			if(args.length == 5)completion.add("0");
			if(args.length == 6)completion.add("0");
			if(args.length == 7)completion.add("0");
			if(args.length == 8)completion.add("0");

			if(args.length == 9 )completion.addAll(completer(args[8], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 10)completion.addAll(completer(args[9], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 11)completion.addAll(completer(args[10], new String[]{player.getLocation().getBlockZ()+""}));

			if(args.length == 12 && (args[2].equals("circle") || args[2].equals("line")))completion.addAll(completer(args[11], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 13 && (args[2].equals("circle") || args[2].equals("line")))completion.addAll(completer(args[12], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 14 && (args[2].equals("circle") || args[2].equals("line")))completion.addAll(completer(args[13], new String[]{player.getLocation().getBlockZ()+""}));
			if(args.length == 15 && args[2].equals("circle"))completion.addAll(completer(args[14], new String[]{"from"}));
			if(args.length == 16 && args[2].equals("circle"))completion.addAll(completer(args[15], new String[]{"to"}));
		}
		if(cmd.getName().equalsIgnoreCase("respawn"))
		{
			if(args.length == 1)return null;
			if(args.length == 2)
			{
				if("set".startsWith(args[1]))completion.add("set");
				if("remove".startsWith(args[1]))completion.add("remove");
				for(World name : Bukkit.getWorlds())if(name.getName().startsWith(args[1]))completion.add(name.getName());
			}
			if(args.length > 2 && (args[1].equals("set") || args[1].equals("remove")))
			{
				if(args.length == 3)for(World name : Bukkit.getWorlds())if(name.getName().startsWith(args[2]))completion.add(name.getName());
				if(args.length == 4)completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockX()+""}));
				if(args.length == 5)completion.addAll(completer(args[4], new String[]{player.getLocation().getBlockY()+""}));
				if(args.length == 6)completion.addAll(completer(args[5], new String[]{player.getLocation().getBlockZ()+""}));
				if(args.length == 7)completion.addAll(completer(args[6], new String[]{player.getLocation().getYaw()+""}));
				if(args.length == 8)completion.addAll(completer(args[7], new String[]{player.getLocation().getPitch()+""}));
				if(args.length == 9 && "keep".startsWith(args[8]))completion.add("keep");
			}
			else
			{
				if(args.length == 3)completion.addAll(completer(args[2], new String[]{player.getLocation().getBlockX()+""}));
				if(args.length == 4)completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockY()+""}));
				if(args.length == 5)completion.addAll(completer(args[4], new String[]{player.getLocation().getBlockZ()+""}));
				if(args.length == 6)completion.addAll(completer(args[5], new String[]{player.getLocation().getYaw()+""}));
				if(args.length == 7)completion.addAll(completer(args[6], new String[]{player.getLocation().getPitch()+""}));
			}
		}
		if(cmd.getName().equalsIgnoreCase("tpto"))
		{
			if(args.length == 1)return null;
			if(args.length == 2)completion.addAll(completer(args[1], new String[]{player.getLocation().getBlockX()+""}));
			if(args.length == 3)completion.addAll(completer(args[2], new String[]{player.getLocation().getBlockY()+""}));
			if(args.length == 4)completion.addAll(completer(args[3], new String[]{player.getLocation().getBlockZ()+""}));
			if(args.length == 5)completion.addAll(completer(args[4], new String[]{player.getLocation().getYaw()+""}));
			if(args.length == 6)completion.addAll(completer(args[5], new String[]{player.getLocation().getPitch()+""}));
			if(args.length == 7)for(World name : Bukkit.getWorlds())if(name.getName().startsWith(args[6]))completion.add(name.getName());
		}
		if(cmd.getName().equalsIgnoreCase("Vec"))
		{
			if(args.length == 1)return null;
			if(args.length > 1 && args.length < 5)completion.add("1");
		}
		if(cmd.getName().equalsIgnoreCase("addlore"))
		{
			if(args.length == 1)return null;
			if(args.length == 2)return Bukkit.getUnsafe().tabCompleteInternalMaterialName(args[l], completion);
			if(args.length == 3)completion.add("-1");
		}
		return completion;
	}
	private Collection<? extends String> completer(String cmd, Collection<? extends Player> args) {
		List<String> completion = new ArrayList<String>();
		for(Player text : args)if(text.getName().startsWith(cmd))completion.add(text.getName());
		return completion;
	}
	private List<String> completer(String cmd , String[] args)
	{
		List<String> completion = new ArrayList<String>();
		for(String text : args)if(text.startsWith(cmd))completion.add(text);
		return completion;
	}
	private Location lockAt(Player player)
	{
		BlockIterator it = new BlockIterator(player, Bukkit.getViewDistance()*16);
		while (it.hasNext()){
			Block block = it.next();
			if (block.getType()!=Material.AIR)return block.getLocation();
		}
		return player.getLocation();
	}
}
