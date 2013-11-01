/*
 * Copyright 2006-2013 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.rcm.maven.plugin.buildmetadata.util;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.apache.maven.plugin.logging.Log;

/**
 * Utility class for configuring loggers in Maven.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class LoggingUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Utility class pattern.
   */
  private LoggingUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Configures the Java logging system.
   *
   * @param log the Maven logger.
   * @param logLevel the log4j level to log.
   * @throws NullPointerException if <code>log</code> is <code>null</code>.
   */
  public static void configureLogger(final Log log, final String logLevel)
    throws NullPointerException
  {
    final String newLevel;
    if (!isBlank(logLevel))
    {
      newLevel = logLevel;
      if (log.isDebugEnabled())
      {
        log.debug("Using configured level " + newLevel);
      }
    }
    else
    {
      newLevel = determineLevel(log);
      if (log.isDebugEnabled())
      {
        log.debug("Using Maven level " + newLevel);
      }
    }
    configure(log, newLevel);
  }

  private static void configure(final Log log, final String newLevel)
  {
    if (isAvailable("org.apache.log4j.Logger"))
    {
      runLog4jConfigure(log, newLevel);
    }

    new SunLoggerConfigurator().configure(log, newLevel);
  }

  private static void runLog4jConfigure(final Log log, final String newLevel)
  {
    try
    {
      final Class<?> clazz =
          Class.forName("com.redhat.rcm.maven.util.log4j.Log4jConfigurator");
      final Object instance = clazz.newInstance();
      final Method method =
          clazz.getMethod("configure", Log.class, String.class);
      method.invoke(instance, log, newLevel);
    }
    catch (final Exception e)
    {
      if (log.isWarnEnabled())
      {
        log.warn("Cannot configure log4j logger.", e);
      }
    }
  }

  private static boolean isAvailable(final String className)
  {
    try
    {
      Class.forName(className);
      return true;
    }
    catch (final Exception e)
    {
      return false;
    }
  }

  /**
   * Checks if the given string is blank or not.
   * <p>
   * Implemented to not require to include commons-lang.
   * </p>
   *
   * @param value the value to check.
   * @return <code>true</code> if the <code>value</code> is <code>null</code>,
   *         empty or contains only whitespaces.
   */
  private static boolean isBlank(final String logLevel)
  {
    return (logLevel == null || "".equals(logLevel.trim()));
  }

  /**
   * Determines the level of the Maven logger.
   *
   * @param log the Maven logger to request the log level from.
   * @return the log level for Java logging as {@link String}.
   */
  private static String determineLevel(final Log log)
  {
    if (log.isDebugEnabled())
    {
      return Level.FINEST.toString();
    }
    else if (log.isInfoEnabled())
    {
      return Level.INFO.toString();
    }
    else if (log.isWarnEnabled())
    {
      return Level.WARNING.toString();
    }
    else
    {
      return Level.SEVERE.toString();
    }
  }

  // --- object basics --------------------------------------------------------

}
