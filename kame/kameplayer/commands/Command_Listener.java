package kame.kameplayer.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import kame.kameplayer.TabCompleter.CommandTabCompleter;

public class Command_Listener {
	JavaPlugin pl;
	CommandTabCompleter Tab= new CommandTabCompleter();
	public Command_Listener(JavaPlugin pl) {
		this.pl = pl;
		pl.getCommand("commandblock").setTabCompleter(new CommandCommandBlock());
		pl.getCommand("commandblock").setExecutor(new CommandCommandBlock());
		pl.getCommand("kameplayer").setExecutor(new Command_kameplayer());
		pl.getCommand("kameplayer").setTabCompleter(new Command_kameplayer());

		getCommand("armor",		new CommandArmor());
		getCommand("broadcast",	new CommandBroadcast());
		getCommand("tpto",		new CommandTpto());
		getCommand("addlore",	new CommandAddLore());
		getCommand("vec",		new CommandVec());
		getCommand("drop",		new CommandDrop());
		getCommand("entity",	new CommandEntity());
		getCommand("itemtp",	new CommandItemTp());
		getCommand("explode",	new CommandExplode());
		getCommand("firework",	new CommandFirework());
		getCommand("respawn",	new CommandRespawn());
		getCommand("attack",	new CommandAtack());
		getCommand("itemrun",	new CommandItemRun());
		getCommand("entityrun",	new CommandEntityRun());
		getCommand("guidelines",new CommandGuideLines());
		getCommand("vacuum",	new CommandVacume());
		getCommand("regioncopy",new CommandCopy());
		getCommand("itemfix",	new CommandItemFix());
		getCommand("tellbar",	new CommandTellBar());
		getCommand("invclose",	new CommandInvClose());
		getCommand("trig",		new CommandTrig());
		getCommand("pull",		new CommandPull());
		getCommand("pulse",		new CommandPulse());
		getCommand("mods",		new CommandMods());
	}
	private void getCommand(String name, CommandExecutor obj) {
		pl.getCommand(name).setExecutor(obj);
		pl.getCommand(name).setTabCompleter(Tab);
	}
}
