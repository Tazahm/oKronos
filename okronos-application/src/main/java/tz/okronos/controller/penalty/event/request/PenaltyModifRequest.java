package tz.okronos.controller.penalty.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter @Setter
public class PenaltyModifRequest extends PenaltyChangeRequest {
	public enum ModifMode {
		LIVE,
		HISTORY;
	};
	
	private ModifMode modifMode;
	private boolean timeModification;
	
}
