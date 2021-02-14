package tz.okronos.core.property;

/**
 *  Converts from integer to string as a time.
 */
public class Int2DToStringConverter implements OneWayConverter<Number, String> {

	@Override
	public String convert(Number source) {
		return String.format("%02d", source.intValue());
	}

}
