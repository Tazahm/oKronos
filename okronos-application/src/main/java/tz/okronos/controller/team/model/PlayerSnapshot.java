package tz.okronos.controller.team.model;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;
import tz.okronos.core.PlayPosition;


/**
 *  Records a player.
 */
@Setter @Getter
public class PlayerSnapshot  {
	private static AtomicLong generator = new AtomicLong(0);
	
	private long uid;
	private PlayPosition team;
	private String name;
	private String licence;
	private int shirt;
	private boolean goalkeeper;
	private boolean official;
	
	
	public static void copy(PlayerSnapshot src, PlayerSnapshot dst, boolean includeUid) {
		if (includeUid) dst.setUid(src.getUid());
		dst.team = src.team;
		dst.name = src.name;
		dst.licence = src.licence;
		dst.shirt = src.shirt;
		dst.goalkeeper = src.goalkeeper;
		dst.official = src.official;
	}
	
	public void copy(PlayerSnapshot input) {
		PlayerSnapshot.copy(input, this, false);
	}

	public static PlayerSnapshot of(PlayerSnapshot prototype) {
		PlayerSnapshot snapshot = new PlayerSnapshot();
		PlayerSnapshot.copy(prototype, snapshot, true);
		return snapshot;
	}

	
	public PlayerSnapshot() {
	}
	
	public void generateUID() {
		uid = generator.incrementAndGet();
	}
}
