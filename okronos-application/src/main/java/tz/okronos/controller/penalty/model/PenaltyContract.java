package tz.okronos.controller.penalty.model;

import tz.okronos.core.PlayPosition;

/**
 *  This interface shall be implemented by all classes that describes a penalty.
 */
public interface PenaltyContract {
	public long getUid();
	public void setUid(long uid);
	public void setSystemTime(long time);
	public long getSystemTime();
	public int getPenaltyTime();
	public void setPenaltyTime(int time);
	public int getStartTime();
	public void setStartTime(int time);
	public int getStopTime();
    public void setStopTime(int time);
	public PlayPosition getTeam();
	public void setTeam(PlayPosition team);
	public int getRemainder();
	public void setRemainder(int remainder);
	public int getPlayer();
	public void setPlayer(int player);
	public int getDuration();
	public void setDuration(int duration);
	public boolean isOnStoppage();
	public void setOnStoppage(boolean onStoppage);
	public boolean isPending();
	public void setPending(boolean pending);
	public boolean isValidated();
	public void setValidated(boolean validated);
	public int getPeriod();
	public void setPeriod(int period);
	public String getCode();
	public void setCode(String code);

	public static void copy(PenaltyContract src, PenaltyContract dst, boolean includeUid) {
		if (includeUid) dst.setUid(src.getUid());
		dst.setSystemTime(src.getSystemTime());
		dst.setPenaltyTime(src.getPenaltyTime());
		dst.setStartTime(src.getStartTime());
		dst.setStopTime(src.getStopTime());
		dst.setRemainder(src.getRemainder());
		dst.setPlayer(src.getPlayer());
		dst.setDuration(src.getDuration());
		dst.setOnStoppage(src.isOnStoppage());
		dst.setPending(src.isPending());
		dst.setTeam(src.getTeam());
		dst.setValidated(src.isValidated());
		dst.setPeriod(src.getPeriod());
		dst.setCode(src.getCode());
	}
	
	public default void copy(PenaltyContract input) {
		PenaltyContract.copy(input, this, false);
	}
}
