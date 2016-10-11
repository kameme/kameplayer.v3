package kame.kameplayer.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.ChunkUtils;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class Command_kameplayer implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!Utils.hasPermCommand(sender, "admin"))return Utils.sendFailParm(sender);
		if(args.length > 0) {
			switch(args[0]) {
			case "chunkloader":
				if(sender instanceof Player) {
					((Player) sender).getWorld().dropItem(((Player) sender).getEyeLocation(), ChunkUtils.kames);
				}
				return true;
			case "debug":
				Main.setDebug();
				sender.sendMessage("§b[kame.] DebugMode = " + Main.isDebug());
				return false;
			case "help":
				help(sender, args);
				return false;
			case "reload":
				Main.loadConfig(sender);
				return false;
			case "difficulty":
				World w = null;
				if(sender instanceof Player)w = ((Player) sender).getWorld();
				if(sender instanceof BlockCommandSender) w = ((BlockCommandSender) sender).getBlock().getWorld();
				if(w == null)Utils.sendFailCommand(sender, Fail.NoNumber, "ワールドから送信してください");
				try {
					Difficulty dif = Difficulty.valueOf(args[1].toUpperCase());
					w.setDifficulty(dif);
					sender.sendMessage("§b[kame.] " + dif + "に変更しました");
				}catch(Exception e) {
					Utils.sendFailCommand(sender, Fail.Other, "難易度を選択してください");
					Utils.sendFailCommand(sender, Fail.Other, "現在の難易度" + w.getDifficulty());
					return false;
				}
			}
		}

		return false;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		int last = args.length-1;
		List<String> list = new ArrayList<String>();
		if(args.length == 1)list.addAll(Utils.completer(args[last], "help", "reload", "difficulty","chunkloader"));
		if(args.length == 2 && args[0].equals("difficulty"))list.addAll(Utils.completer(args[last], "peaceful", "easy", "normal", "hard"));
		
		return list;
	}
	/**
	 * HelpCommand
	 */
	String[] help = {
			"-------kameplayer------",
			"ver" + Main.ver,
			"[kame.] 各コマンドはTabキーのコンプリート機能がついています \n[kame.] 詳細な使用法はコマンドを実行してください",
			"Commands:",
			" §e1.7.x~  §maddlore:§r        <非推奨> アイテムの説明文の最後に指定文字追加",
			" §a1.7.x~  armor:§r          自動装備、修復コマンド",
			" §a1.7.x~  attack:§r         コマンドでのMOB攻撃",
			" §a1.7.x~  broadcast:§r      サーバーチャット系",
			" §a1.7.x~  commandblock:§r   コマンドブロック書き込み",
			" §a1.7.x~  copy:§r           cloneコマンドっぽいもの",
			" §a1.7.x~  drop:§r           対象のアイテムを手持ちから落とす",
			" §a1.7.x~  entity:§r         エンティティ系のコマンド",
			" §a1.7.x~  entityrun:§r      キル時コマンド実行するMOBの召喚",
			" §a1.7.x~  explode:§r        対象地点に爆発を起こす",
			" §a1.7.x~  firework:§r       コマンドでのランダム花火",
			" §a1.7.x~  guidelines:§r     パーティクルのガイドライン作成、表示",
			" §a1.7.x~  invclose:§r       インベントリを閉じさせるコマンド",
			" §a1.7.x~  itemfix:§r        対象プレイヤーのアイテムの名前説明文変更",
			" §a1.7.x~  itemrun:§r        アイテムを指定数持っていた時にコマンド実行",
			" §e1.7.x~  §mitemtp:§r         <非推奨> アイテムを持っていた時に座標へTP",
			" §a1.7.x~  particle:§r       パーティクル描画",
			" §a1.7.x~  respawn:§r        自動リスポーン、一時スポーン地点変更コマンド",
			" §b1.8.3~  tellbar:§r        アクションバーへ文字表示",
			" §a1.7.x~  tpto:§r           指定座標へ向きも合わせてTP",
			" §a1.7.x~  trig:§r           スコアトリガーscript用",
			" §a1.7.x~  vacuum:§r         アイテム吸い寄せコマンド",
			" §a1.7.x~  vec:§r            プレイヤーの移動方向変更コマンド",
	};
	private void help(CommandSender sender, String[] args) {
		for(String line : help)sender.sendMessage(line);
	}

}
