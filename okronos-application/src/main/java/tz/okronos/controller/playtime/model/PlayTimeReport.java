package tz.okronos.controller.playtime.model;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class PlayTimeReport {
	private int forwardTime;
    private int backwardTime;
    private int cumulativeTime;
    private boolean playTimeRunning;
}