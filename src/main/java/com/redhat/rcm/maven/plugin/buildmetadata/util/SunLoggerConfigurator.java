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

import java.io.InputStream;
import java.util.logging.LogManager;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

/**
 * Configures the log4j framework with the logging information.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class SunLoggerConfigurator
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  public SunLoggerConfigurator()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Reads a pre-configured configuration file.
   *
   * @param log the Maven logger to use for logging.
   * @param level the new level to set the Java logging system to.
   */
  public void configure(final Log log, final String level)
  {
    try
    {
      final String file = "logging-" + level + ".properties";
      if (log.isDebugEnabled())
      {
        log.debug("Reading config file '" + file + "'...");
      }
      final LogManager logManager = LogManager.getLogManager();
      final InputStream input = LoggingUtils.class.getResourceAsStream(file);
      if (input != null)
      {
        try
        {
          logManager.readConfiguration(input);
        }
        finally
        {
          IOUtil.close(input);
        }
      }
    }
    catch (final Exception e)
    {
      if (log.isWarnEnabled())
      {
        log.warn("Cannot configure logger.", e);
      }
    }
  }

  // --- object basics --------------------------------------------------------

}
