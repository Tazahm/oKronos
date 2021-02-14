package tz.okronos.controller.score.model;


import tz.okronos.core.PlayPosition;

/**
 *  This interface shall be implemented by all class that registers a goal.
 */
public interface ScoreContract {
	public long getUid();
	public void setUid(long uid);
	public PlayPosition getTeam();
	public void setTeam(PlayPosition team);
	public int getTime();
	public void setSystemTime(long time);
	public long getSystemTime();
	public void setTime(int time);
	public int getPeriod();
	public void setPeriod(int period);
	public int getScorer();
	public void setScorer(int scorer);
	public int getAssist1();
	public void setAssist1(int assist1);
	public int getAssist2();
	public void setAssist2(int assist2);

	public static void copy(ScoreContract src, ScoreContract dst, boolean includeUid) {
		if (includeUid) dst.setUid(src.getUid());
		dst.setTeam(src.getTeam());
		dst.setSystemTime(src.getSystemTime());
		dst.setTime(src.getTime());
		dst.setPeriod(src.getPeriod());
		dst.setScorer(src.getScorer());
		dst.setAssist1(src.getAssist1());
		dst.setAssist2(src.getAssist2());
	}

}
