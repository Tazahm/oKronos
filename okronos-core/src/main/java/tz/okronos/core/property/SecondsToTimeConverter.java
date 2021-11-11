package tz.okronos.core.property;

import tz.okronos.core.KronoHelper;

/**
 * Converts from integer to string. The integer shall represent an amount 
 * of seconds. The minus infinite is handled as an empty value and printed as an empty string.
 */
public class SecondsToTimeConverter implements OneWayConverter<Number, String> {

	@Override
	public String convert(Number source) {
		if (source.intValue() == BindingHelper.NO_VALUE) return "";
		return KronoHelper.secondsToTime(source.intValue());
	}

}
