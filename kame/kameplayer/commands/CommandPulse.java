package kame.kameplayer.commands;

import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class CommandPulse implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		//pulse cycle ontime kyoudo
		if(sender instanceof BlockCommandSender) {
			if(args.length == 0)return Utils.sendFailCommand(sender, Fail.LowParam, "/pulse on off power world");
			try {
				final Block loc = ((BlockCommandSender) sender).getBlock();
				int cycle = Integer.parseInt(args[0]);
				int on = Integer.parseInt(args[1]);
				byte power  = Byte.parseByte(args[2]);
				long time;
				if(args.length > 3)time = System.currentTimeMillis()/50 % cycle;
				else time = loc.getWorld().getTime() % cycle;
				if(time >= on)power = 0;
				sender.sendMessage(power + "を出力しました.");
				if(!args[args.length - 1].equals("-")) {
					final byte p = power;
					new BukkitRunnable() {
						@Override
						public void run() {
							Main.obj.copyBlockCommandNBT(loc, p);
						}
					}.runTask(Main.getPlugin());
				}
				return power != 0;
			}catch(Exception e) {
				return Utils.sendFailCommand(sender, Fail.NoNumber, "数値を確認してください /pulse on off power world");
			}
		}else {
			return Utils.sendFailCommand(sender, Fail.Other, "コマンドブロックより実行してください");
		}
	}
}
