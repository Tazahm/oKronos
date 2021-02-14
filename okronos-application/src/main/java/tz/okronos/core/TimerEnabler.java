package tz.okronos.core;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;

/**
 *  Extends java timer to allow a more easy usage.
 */
public class TimerEnabler extends Timer {
	
	/**
	 * Schedules the Runnable.
	 * See {@link Timer.scheduleAtFixedRate(TimerTask, long, long)}
	 */
	public TimerTask scheduleAtFixedRate(Runnable runnable, long delay, long period) {
		TimerTask task = new TimerTask() {
			public void run() {
				runnable.run();
			}
		};
		scheduleAtFixedRate(task, delay, period);
		return task;
	}
	
	/**
	 * Schedules the Runnable inside the fx main loop .
	 * See {@link Timer.scheduleAtFixedRate(TimerTask, long, long)}
	 */
	public TimerTask fxScheduleAtFixedRate(Runnable runnable, long delay, long period) {
		TimerTask task = new TimerTask() {
			public void run() {
				Platform.runLater(()-> runnable.run());
			}
		};
		scheduleAtFixedRate(task, delay, period);
		return task;
	}
	
	/**
	 * Schedules the Runnable.
	 * See {@link Timer.schedule(TimerTask, long, long)}
	 */
	public TimerTask schedule(Runnable runnable, long delay, long period) {
		TimerTask task = new TimerTask() {
			public void run() {
				runnable.run();
			}
		};
		schedule(task, delay, period);
		return task;
	}
	
	/**
	 * Schedules the Runnable inside the fx main loop.
	 * See {@link Timer.schedule(TimerTask, long, long)}
	 */
	public TimerTask fxSchedule(Runnable runnable, long delay, long period) {
		TimerTask task = new TimerTask() {
			public void run() {
				Platform.runLater(()-> runnable.run());
			}
		};
		schedule(task, delay, period);
		return task;
	}
	
	/**
	 * Schedules the Runnable.
	 * See {@link Timer.schedule(TimerTask, long)}
	 */
	 public TimerTask schedule(Runnable runnable, long delay) {
		 TimerTask task = new TimerTask() {
				public void run() {
					runnable.run();
				}
			};
			schedule(task, delay);
			return task;
	 }
	 
		/**
		 * Schedules the Runnable inside the fx main loop.
		 * See {@link Timer.schedule(TimerTask, long)}
		 */
	 public TimerTask fxSchedule(Runnable runnable, long delay) {
		 TimerTask task = new TimerTask() {
				public void run() {
					Platform.runLater(()-> runnable.run());
				}
			};
			schedule(task, delay);
			return task;
	 }
}
