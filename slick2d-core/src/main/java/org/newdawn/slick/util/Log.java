package org.newdawn.slick.util;

import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author tyler
 */
public final class Log {
	public static final int TRACE = 0;
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARN = 3;
	public static final int ERROR = 4;

	private final String clazzName;

	public Log(Class clazz) {
		this.clazzName = clazz.getName();
	}

	//
	// 0 - ALL; 1 - DEBUG; 2 - INFO; 3 - WARN
	//
	private static int logLevel = 2; // Info Default Level

	public static void setLevel(int level) {
		logLevel = level;
	}

	public void setVerbose(boolean enabled) {
		if (enabled) {
			setLevel(1);
		} else {
			setLevel(2);
		}
	}

	public void trace(Object... args) {
		if (logLevel > TRACE) {
			return;
		}

		logSout("[TRACE]", args);
	}

	public void debug(Object... args) {
		if (logLevel > DEBUG) {
			return;
		}

		logSout("[DEBUG]", args);
	}

	public void info(Object... args) {
		if (logLevel > INFO) {
			return;
		}

		logSout("[INFO]", args);
	}

	public void warn(Object... args) {
		if (logLevel > WARN) {
			return;
		}

		logSout("[WARN]", args);
	}

	public void error(Object... args) {
		if (logLevel > ERROR) {
			return;
		}

		logErr("[ERROR]", args);
		for (Object o : args) {
			if (o instanceof Exception) {
				((Exception) o).printStackTrace();
			}
		}
	}

	private void logSout(String tag, Object... args) {
		log(tag, System.out, args);
	}

	private void logErr(String tag, Object... arg) {
		log(tag, System.err, arg);
	}

	private static int countMatches(String str) {
		return (str.split( Pattern.quote("{}"), -1).length) - 1;
	}

	private void log(String label, PrintStream printStream, Object... args) {
		StringBuilder format = new StringBuilder(args[0].toString());

		int count = countMatches(format.toString());
		int arguments = args.length - 1;

		if (count < arguments) {
			format.append(", {}".repeat(Math.max(0, arguments - count)));
		} else if (arguments < count) {
			System.err.println(getTag(label) + "Please supply positional arguments for all {}.");
			new Exception("Bad Arguments").printStackTrace();
			return;
		}

		format = new StringBuilder(format.toString().replace("{}", "%s"));

		List<Object> tmp  = Arrays.asList(args).subList(1, args.length);
		List<Exception> exceptions = new ArrayList<>();
		String[] pos = tmp.stream().map(obj -> {
			if (obj instanceof Exception) {
				exceptions.add((Exception) obj);
				return ((Exception) obj).getMessage();
			}
			return obj.toString();
		}).toArray(String[]::new);

		printStream.printf(getTag(label) + format + "\n", pos);
		exceptions.forEach(Exception::printStackTrace);
	}

	private String getTag(String label) {
		return label + " | "
				+ Instant.now()
				+ " | "
				+ clazzName
				+ " | ";
	}
}
