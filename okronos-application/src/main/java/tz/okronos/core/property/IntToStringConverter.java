package tz.okronos.core.property;

/**
 * Converts from integer to string.
 */
public class IntToStringConverter implements OneWayConverter<Number, String> {

	@Override
	public String convert(Number source) {
		if (source.intValue() == Integer.MIN_VALUE) return "";
		
		return Integer.toString(source.intValue());
	}

}
