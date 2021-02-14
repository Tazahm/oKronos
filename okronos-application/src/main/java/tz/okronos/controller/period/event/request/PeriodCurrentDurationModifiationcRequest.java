package tz.okronos.controller.period.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Getter @Setter
public class PeriodCurrentDurationModifiationcRequest extends PeriodRequest{
	private  int duration;
}
