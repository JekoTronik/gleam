/*
 * Copyright (c) 2001-2008 Guglielmo Nigri.  All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it would be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Further, this software is distributed without any warranty that it is
 * free of the rightful claim of any third person regarding infringement
 * or the like.  Any license provided herein, whether implied or
 * otherwise, applies only to this software file.  Patent licenses, if
 * any, provided herein do not apply to combinations of this program with
 * other software, or any other product whatsoever.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write the Free Software Foundation, Inc., 59
 * Temple Place - Suite 330, Boston MA 02111-1307, USA.
 *
 * Contact information: Guglielmo Nigri <guglielmonigri@yahoo.it>
 *
 */

package gleam.util;

import java.util.logging.ConsoleHandler;

/*
 * Log.java
 *
 * Based on the older Report.java, created on October 18, 2001, 1:02 AM
 */

/**
 * Logging utility class for Gleam.
 */
public class Logger {

    private static final int JUL_THRESHOLD_ALL = 1000;
    private static final int JUL_THRESHOLD_WARNING = 900;
    private static final int JUL_THRESHOLD_INFO = 800;
    private static final int JUL_THRESHOLD_CONFIG = 700;
    private static final int JUL_THRESHOLD_FINE = 500;

    private static final ThreadLocal<java.util.logging.Logger> loggerThreadLocal = new ThreadLocal<>();

    /**
     * Level<BR>
     * <code>
     * OFF, no output<BR>
     * ERROR, a serious failure<BR>
     * WARNING, a potential problem<BR>
     * INFO, significant messages<BR>
     * CONFIG, configuration messages<BR>
     * FINE, tracing information<BR>
     * ALL, all messages<BR>
     * </code>
     */
    public enum Level {
        OFF(6),
        ERROR(5),
        WARNING(4),
        INFO(3),
        CONFIG(2),
        FINE(1),
        ALL(0);

        private final int value;

        Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Can't instantiate this class
     */
    private Logger() {
    }

    /**
     * Sets current level.
     * Message with level lower than this level will be discarded.
     *
     * @param level level value
     * @see Level
     */
    public static void setLevel(int level) {
        getJulLogger().setLevel(getLoggingLevel(level));
    }

    /**
     * Sets current level.
     * Message with level lower than this level will be discarded.
     *
     * @param level numeric level value
     * @see Level
     */
    public static void setLevel(Level level) {
        getJulLogger().setLevel(getLoggingLevel(level.getValue()));
    }

    /**
     * @return the current level
     */
    public static int getLevelValue() {
        return getLevelValue(getJulLogger().getLevel());
    }

    /**
     * Obtains a java.util.logging Level from an integer level value
     *
     * @param level integer level value
     * @return the corresponding Level
     */
    private static java.util.logging.Level getLoggingLevel(int level) {
        java.util.logging.Level n;
        if (level < 0)
            level = 0;
        switch (level) {
            case 0:
                n = java.util.logging.Level.ALL;
                break;
            case 1:
                n = java.util.logging.Level.FINE;
                break;
            case 2:
                n = java.util.logging.Level.CONFIG;
                break;
            case 3:
                n = java.util.logging.Level.INFO;
                break;
            case 4:
                n = java.util.logging.Level.WARNING;
                break;
            case 5:
                n = java.util.logging.Level.SEVERE;
                break;
            default:
                n = java.util.logging.Level.OFF;
        }
        return n;
    }

    /**
     * Obtains a Level from an integer level value
     *
     * @param level the current level
     * @return the corresponding integer level value
     */
    private static int getLevelValue(java.util.logging.Level level) {
        final int n;

        if (level.intValue() == Integer.MAX_VALUE) {
            n = Level.OFF.getValue();
        } else if (level.intValue() >= JUL_THRESHOLD_ALL) {
            n = Level.ERROR.getValue();
        } else if (level.intValue() >= JUL_THRESHOLD_WARNING) {
            n = Level.WARNING.getValue();
        } else if (level.intValue() >= JUL_THRESHOLD_INFO) {
            n = Level.INFO.getValue();
        } else if (level.intValue() >= JUL_THRESHOLD_CONFIG) {
            n = Level.CONFIG.getValue();
        } else if (level.intValue() >= JUL_THRESHOLD_FINE) {
            n = Level.FINE.getValue();
        } else {
            n = Level.ALL.getValue();
        }
        return n;
    }

    /**
     * Logs a message, respecting current level.
     */
    public static void enter(int level, String message) {
        getJulLogger().log(getLoggingLevel(level), message);
    }

    /**
     * Logs a message, respecting current level.
     */
    public static void enter(Level level, String message) {
        enter(level.getValue(), message);
    }

    /**
     * Logs a message and an Entity, respecting current level.
     */
    public static void enter(int level, String message, gleam.lang.Entity obj) {
        getJulLogger().log(getLoggingLevel(level), message + " " + obj.toString());
    }

    /**
     * Logs a message and an Entity, respecting current level.
     */
    public static void enter(Level level, String message, gleam.lang.Entity obj) {
        enter(level.getValue(), message, obj);
    }

    /**
     * Logs a Throwable at WARNING level
     *
     * @param ex Throwable
     */
    public static void error(Throwable ex) {
        getJulLogger().log(java.util.logging.Level.WARNING, "", ex);
    }

    private static java.util.logging.Logger getJulLogger() {
        if (loggerThreadLocal.get() != null)
            return loggerThreadLocal.get();

        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("gleam");
        if (logger.getHandlers().length == 0) {
            logger.setUseParentHandlers(false);
            final ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(java.util.logging.Level.ALL);
            logger.addHandler(consoleHandler);
        }
        logger.setLevel(java.util.logging.Level.INFO);
        loggerThreadLocal.set(logger);
        return loggerThreadLocal.get();
    }
}
