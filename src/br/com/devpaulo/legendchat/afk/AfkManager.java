package br.com.devpaulo.legendchat.afk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import br.com.devpaulo.legendchat.api.Legendchat;

public class AfkManager {
	private static HashMap<Player,String> afk = new HashMap<Player,String>();
	private static HashMap<Player,Location> location = new HashMap<Player,Location>();
	private static HashMap<Player,Long> time = new HashMap<Player,Long>();
	private static HashMap<Player,String> name = new HashMap<Player,String>();
	private static List<Player> auto = new ArrayList<Player>();

	private static Integer autoTask = -1;
	private static Long autoTimeout = 0L;
	private static Boolean isEnabled = false;

	public AfkManager() {
	}

	public void enable() {
		if (!isEnabled && autoTimeout > 0 && autoTask < 0) {
			autoTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("Legendchat"), new Runnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (isAfk(p))
							continue;
						if (!(location.get(p) instanceof Location) || !location.get(p).equals(p.getLocation())) {
							time.put(p, System.currentTimeMillis());
							location.put(p, p.getLocation());
							continue;
						}
						if ((time.get(p) + autoTimeout) <= System.currentTimeMillis()) {
							setAutoAfk(p);
						}
					}
				}
			}, 20L, (autoTimeout/1000L) * 20L);
		}
		isEnabled = true;
	}

	public void disable() {
		if (isEnabled && autoTask > -1) {
			Bukkit.getScheduler().cancelTask(autoTask);
			autoTask = -1;
		}
		isEnabled = false;
	}

	public void setAutoTimeout(Integer seconds) {
		autoTimeout = seconds * 1000L;
		if (isEnabled) {
			disable();
			enable();
		}
	}

	public List<Player> getAllAfk() {
		List<Player> l = new ArrayList<Player>(afk.keySet());
		return l;
	}

	public void setAfk(Player p, String motivo) {
		removeAfk(p);
		if(motivo.equals(" ")||motivo.length()==0)
			motivo=null;
		afk.put(p, motivo);
		time.put(p, System.currentTimeMillis());
		setAfkPlayername(p);
		Bukkit.broadcastMessage(Legendchat.getMessageManager().getMessage((motivo==null) ? "pm_error2_1" : "pm_error2_2").replace("@player", p.getName()).replace("@motive", motivo));
	}

	public void setAutoAfk(Player p) {
		setAfk(p, Legendchat.getMessageManager().getMessage("automotive"));
		auto.add(p);
	}

	public boolean isAfk(Player p) {
		return afk.containsKey(p);
	}

	public boolean isAutoAfk(Player p) {
		return auto.contains(p);
	}

	private void setAfkPlayername(Player p) {
		if (!name.containsKey(p)) {
			name.put(p, p.getPlayerListName());
		}
		try {
			p.setPlayerListName(ChatColor.GRAY.toString() + ChatColor.stripColor(p.getPlayerListName()));
		} catch(IllegalArgumentException e) {
		}
	}

	private void restorePlayername(Player p) {
		if (!name.containsKey(p)) {
			return;
		}
		p.setPlayerListName(name.get(p));
	}

	public void removeAfk(Player p) {
		if (isAfk(p)) {
			if (isAutoAfk(p)) {
				removeAutoAfk(p);
			} else {
				afk.remove(p);
				time.remove(p);
				location.remove(p);
				restorePlayername(p);
			}
		}
	}

	public void removeAutoAfk(Player p) {
		if (isAutoAfk(p)) {
			auto.remove(p);
			removeAfk(p);
		}
	}

	public String getPlayerAfkMotive(Player p) {
		if (isAfk(p)) {
			return afk.get(p);
		}
		return null;
	}

	public void playerDisconnect(Player p) {
		removeAfk(p);
	}
}
