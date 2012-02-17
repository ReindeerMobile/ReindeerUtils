package com.reindeermobile.reindeerutils.view;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
	private Matcher matcher;
	private Map<ValidatorType, Pattern> map;

	public enum ValidatorType {
		EMAIL, PHONE;
	}

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String PHONE_PATTER = "^(\\+){0,1}([0-9-]{9,})$";

	public Validator() {
		map = new EnumMap<Validator.ValidatorType, Pattern>(ValidatorType.class);
		map.put(ValidatorType.EMAIL, Pattern.compile(EMAIL_PATTERN));
		map.put(ValidatorType.PHONE, Pattern.compile(PHONE_PATTER));
	}

	public boolean validate(final String hex, ValidatorType type) {
		boolean result = false;
		if (map.get(type) != null) {
			matcher = map.get(type).matcher(hex);
			result = matcher.matches();
		}
		return result;
	}
}
