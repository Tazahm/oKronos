package tz.okronos.scene.control;

import java.util.Scanner;
import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import tz.okronos.core.KronoHelper;

/**
 *  A formatter to display an time into a text field. 
 *  The format is [-]hh:mm. "hh:mm" are always printed (completed by zeros if necessary).
 *  Accepts only characters 0 to 9, eventually preceding by the minus sign.
 *  When the caret is before a digit, this digit is replaced by the input 
 *  (we are always in a replace mode).
 *  Where the caret is at the maximum length of the field, it loops back
 *  to the begin of the field (but after the sign, if any).
 */public class TimeFieldFormatter extends AbstractFieldFormatter<Integer> {
	
	public static class TimeConverter extends StringConverter<Integer> {
		
		public boolean allowNull;
		
		public TimeConverter() {
		}

		@Override
		public String toString(Integer value) {
			 if (value == null) return "00:00";
			return KronoHelper.secondsToTime(value);
		}

		@Override
		public Integer fromString(String value) {
			int intValue = updateValue(value);
			return intValue;
		}
		
		private int updateValue(final String txt) {
			
			if (txt == null) return 0;
			String value = txt.trim();
			if (value.equals(":")) return 0;

			int sign = 1;
			if (value.startsWith("-")) {
				sign = -1;
				value = value.substring(1);
			}
			int minutes = 0, seconds = 0;
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(value).useDelimiter(":");
			if (scanner.hasNextInt()) minutes = scanner.nextInt();
			if (scanner.hasNextInt()) seconds = scanner.nextInt();
			
			return sign * (minutes * 60 + seconds);
		}

		public void setAllowNull(boolean allowNull) {
			this.allowNull = allowNull;
		}
		
	}
	
	
	public static class TimeFilter implements UnaryOperator<TextFormatter.Change> {
		
		public TimeFilter() {
		}

		@Override
		public Change apply(Change input) {
			Change output = input.clone();
			output.setText("");
			output.setRange(input.getRangeStart(), input.getRangeStart());
			output.setAnchor(input.getRangeStart());
			output.setCaretPosition(input.getRangeStart());

			final boolean isPreviousNegative = input.getControlText().length() > 0 && 
					input.getControlText().charAt(0) == '-';

			// Do not delete as we shall always have 5 characters,
			// except the sign.
			if (input.getText().isEmpty()) {
				if (isPreviousNegative && input.getRangeStart() == 0 && input.getRangeEnd() > 0) {
					output.setRange(0, 1);
					output.setAnchor(0);
					output.setCaretPosition(0);
				}
				return output;
			}
			
			final boolean isInputNegative = input.getText().length() > 0 && 
					input.getText().startsWith("-") && ! isPreviousNegative;
						
			final boolean isNegative = isPreviousNegative || isInputNegative;
			final int signInputLen = isInputNegative ? 1 : 0;
//			final int signPreviousLen = isInputNegative ? 1 : 0;
			final int signLen = isNegative ? 1 : 0;
					
			String text = input.getText().trim();
			text = text.replaceAll("[^0-9]", "");
			//if (text.isEmpty()) return output;
			
			// Cannot replace the sign by some digit
			if (isPreviousNegative && text.length() > 0 && input.getRangeStart() == 0) {
				return output;
			}

			// Insert colon if needed.
			int colonInsertion = 2 + signLen - input.getRangeStart();
			if (colonInsertion >= 0 && colonInsertion <= text.length()) {
				text = text.substring(0, colonInsertion) + ":" 
					+ text.substring(colonInsertion);
			}
			
			// Check that the character 4 of the new text (example : "4" in "12:45") 
			// is between 0 and 5
			int pos4 = 3 + signLen - input.getRangeStart();
			if (pos4 >= 0 && pos4 < text.length()) {
				char char4 = text.charAt(pos4);
				if (char4 > '5') {
					return output;
				}
			}
			   
			// Truncate, new string cannot exceed 5 characters.
			int trunc = 5 + signLen - input.getRangeStart();
			if (trunc < text.length()) {
				text = text.substring(0, trunc);
			}
			
			// The insertion length is the length of the new text, except
			// if the previous text was too small.
			int rangeEnd = input.getRangeStart() + text.length();
			int controlLimit = input.getControlText().length();
			if (rangeEnd > controlLimit) {
				rangeEnd = controlLimit;
			}
			
			if (isInputNegative) text = '-' + text;
			
			output.setText(text);
			output.setRange(input.getRangeStart(), rangeEnd);
			
			// Set the anchor at the end of the insertion, but loop to the
			// begin of the text if the insertion occurs at the end.
			int anchor = rangeEnd + signInputLen;
			if (anchor == 5 + signLen) anchor = signLen;
			output.setAnchor(anchor);
			output.setCaretPosition(anchor);
			return output;
		}
	}

	
	public TimeFieldFormatter() {
		super(new TimeConverter(), Integer.valueOf(0), new TimeFilter());
	}


	public void setAllowNull(boolean value) {
		((TimeConverter) getValueConverter()).setAllowNull(value);
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
		setValue(0);
	}

}
