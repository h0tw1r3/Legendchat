package br.com.devpaulo.legendchat.commands;

import java.io.File;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import br.com.devpaulo.legendchat.Main;
import br.com.devpaulo.legendchat.api.Legendchat;

public class Afk implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName();
		if(cmdName.equalsIgnoreCase("afk")&&Legendchat.isAfkActive()) {
			if(sender==Bukkit.getConsoleSender())
				return false;
			if(sender.hasPermission("legendchat.block.afk")&&!sender.hasPermission("legendchat.admin")) {
				sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
				return true;
			}
			if(Legendchat.getAfkManager().isAfk((Player)sender)&&args.length==0) {
				Legendchat.getAfkManager().removeAfk((Player)sender);
			}
			else {
				String mot = "";
				if(args.length>0)
					for(int i=0;i<args.length;i++) {
						if(mot.length()==0)
							mot=args[i];
						else
							mot=" "+args[i];
					}
				if(mot.length()>0)
					if(sender.hasPermission("legendchat.block.afkmotive")&&!sender.hasPermission("legendchat.admin")) {
						sender.sendMessage(Legendchat.getMessageManager().getMessage("error6"));
						return true;
					}
				Legendchat.getAfkManager().setAfk((Player)sender,mot);
			}
			return true;
		}
		return false;
	}
}
