package kame.kameplayer.commands;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBroadcast implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args)
	{
		if(!Utils.hasPermCommand(sender, "broadcast"))return Utils.sendFailParm(sender);
		if(args.length > 1)
		{
			boolean skip = false;
			StringBuilder message = new StringBuilder();
			for(int i = 1 ; i < args.length ; i++)
			{
				if(!skip)
				{
					String str = args[i];
					if(str.startsWith("score_") && args.length - 1 > i)
					{
						if(parse(Main.ver.replaceAll("[a-zA-Z_]", "")) <= 172)
							message.append(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(str.replaceFirst("score_", "")).getScore(Bukkit.getOfflinePlayer(args[i+1])).getScore());
						else 
							message.append(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(str.replaceFirst("score_", "")).getScore(args[i+1]).getScore());
						skip = true;
					}
					else if(str.equals("[+]"))
					{
						message.append(" ");
					}
					else if(str.equals("/n"))
					{
						if(args[0].equals("all"))for(Player player : Bukkit.getOnlinePlayers())player.sendMessage(message.toString());
						else if(args[0].equals("server"))Bukkit.broadcastMessage(message.toString());
						else {Player player = Bukkit.getPlayer(args[0]);if(player != null)player.sendMessage(message.toString());}
						message = new StringBuilder();
					}
					else if(str.equals("<player>")) {
						message.append(Bukkit.getPlayer(str));
					}
					else
					{
						Player p = Bukkit.getPlayerExact(str);
						if(p == null)message.append(str);
						else message.append(p.getDisplayName());
					}
				}
				else skip = false;
			}
			switch(args[0]) {
			case "all":
				for(Player player : Bukkit.getOnlinePlayers())player.sendMessage(message.toString());
				break;
			case "server":
				Bukkit.broadcastMessage(message.toString());
				break;
			default:
				Player player = Bukkit.getPlayer(args[0]);
				if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
					player.sendMessage(message.toString());
			}
		}
		return false;
	}
	private int parse(String i){
		return Integer.parseInt(i);
	}

}
