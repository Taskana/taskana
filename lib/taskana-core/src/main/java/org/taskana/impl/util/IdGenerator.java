package org.taskana.impl.util;

import java.util.UUID;

public class IdGenerator {

	private static final String SEPERATOR = ":";

	public static String generateWithPrefix(String prefix) {
		return new StringBuilder().append(prefix).append(SEPERATOR).append(UUID.randomUUID().toString()).toString();
	}

}
