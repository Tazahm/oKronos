package tz.okronos.core.property;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Converts from the system time (a long value that contains the number of seconds 
 * since 01/01/1970) to a time as hh:mm. */
public class SystemTimeToHourConverter implements OneWayConverter<Number, String> {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	
	@Override
	public String convert(Number source) {
		return dateFormat.format(new Date(source.longValue()));
	}

}
