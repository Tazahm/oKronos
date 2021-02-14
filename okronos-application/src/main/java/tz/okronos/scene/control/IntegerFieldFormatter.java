package tz.okronos.scene.control;

import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

/**
 *  A formatter to display an integer into a text field. 
 *  The length of the input is limited.
 *  Accepts only characters 0 to 9, eventually preceding by the minus sign.
 *  When the caret is before a digit, this digit is replaced by the input 
 *  (we are always in a replace mode).
 *  Where the caret is at the maximum length of the field, it loops back
 *  to the begin of the field (but after the sign, if any).
 */
public class IntegerFieldFormatter extends AbstractFieldFormatter<Integer> {
	
	public static class IntegerConverter extends StringConverter<Integer> {
		public final int maxlen;
		public final int modulus;
		public boolean allowNull;
		
		public IntegerConverter(int maxlen) {
			this.maxlen = maxlen;
			this.modulus = (int) Math.pow(10, maxlen);
		}

		@Override
		public String toString(Integer value) {
			// if (value == null) return allowNull ? null : "0";
			if (value == null) return "";
			return Integer.toString(value % modulus);
		}

		@Override
		public Integer fromString(String value) {
			// if (value == null) return allowNull ? null : 0;
			if (value == null) return null;
			if (value.isEmpty()) return null;
			if (value.equals("-")) return 0;
			int vint = Integer.parseInt(value);
			return vint % modulus;
		}

		public void setAllowNull(boolean allowNull) {
			this.allowNull = allowNull;
		}

	}
	
	public static class IntegerFilter implements UnaryOperator<TextFormatter.Change> {
		public final int maxlen;
		
		public IntegerFilter(int maxlen) {
			this.maxlen = maxlen;
		}

		@Override
		public Change apply(Change input) {
			// Allows deletion.
			if (input.getText().isEmpty()) {
				return input;
			}
	
			Change output = input.clone();
			output.setText("");
			output.setRange(input.getRangeStart(), input.getRangeStart());
			output.setAnchor(input.getRangeStart());
			output.setCaretPosition(input.getRangeStart());

			final boolean isPreviousNegative = input.getControlText().length() > 0 && 
					input.getControlText().charAt(0) == '-';
			final boolean isInputNegative = input.getText().length() > 0 && 
					input.getText().startsWith("-") && ! isPreviousNegative;
						
			final boolean isNegative = isPreviousNegative || isInputNegative;
			final int signPeviousLen = isPreviousNegative ? 1 : 0;
			final int signLen = isNegative ? 1 : 0;
					
			String text = input.getText().trim();
			text = text.replaceAll("[^0-9]", "");
			
			// Cannot replace the sign by some digit
			if (isPreviousNegative && text.length() > 0 && input.getRangeStart() == 0) {
				return output;
			}
			
			// Loop to the start or the number if the insertion begin at the end.
			int rangeStart = input.getRangeStart();
			if (rangeStart == maxlen) rangeStart = signPeviousLen;		
			   
			// Truncate, new string cannot exceed n characters.
			int trunc = maxlen + signLen - rangeStart;
			if (trunc < text.length()) {
				text = text.substring(0, trunc);
			}
			
			// The length of the insertion is the length of the new text, except
			// if the previous text was too small.
			int rangeEnd = rangeStart + text.length();
			int controlLimit = input.getControlText().length();
			if (rangeEnd > controlLimit) {
				rangeEnd = controlLimit;
			}
			
			if (isInputNegative) text = '-' + text;
			
			output.setText(text);
			output.setRange(rangeStart, rangeEnd);
			
			// Set the anchor at the end of the insertion.
			int anchor = rangeStart + text.length();
			output.setAnchor(anchor);
			output.setCaretPosition(anchor);
			return output;
		}
	}
	
	public IntegerFieldFormatter(final int maxlen) {
		super(new IntegerConverter(maxlen), Integer.valueOf(0), new IntegerFilter(maxlen));
	}
	
	public void setAllowNull(boolean value) {
		((IntegerConverter) getValueConverter()).setAllowNull(value);
	}

	@Override
	public void negateValue() {
		Integer value = getValue();
		if (value != null) {
		    setValue(- value);
		}
	}

	@Override
	public void clear() {
		setValue(null);
	}
}
