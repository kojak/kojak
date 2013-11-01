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
package com.redhat.rcm.maven.plugin.buildmetadata.common;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Utilities for this Mojo.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class MojoUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Utilities pattern.
   */
  private MojoUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Logs and creates the given exception.
   *
   * @param log the logger to use.
   * @param e the original exception to throw.
   * @param message the message to log and add to the mojo exception.
   * @return the exception that wraps the given exception.
   */
  public static MojoExecutionException createException(
      final Log log,
      final Throwable e,
      final String message)
  {
    if (log.isWarnEnabled())
    {
      log.warn(message, e);
    }

    return new MojoExecutionException(message, e);
  }

  /**
   * Turns the list to its string representation, removing the starting and trailing brackets.
   *
   * @param list the list whose string representation is requested.
   * @return the string representation of the list.
   */
  public static String toPrettyString(final List<?> list)
  {
    if (list != null)
    {
      final String string = String.valueOf(list);
      final int end = string.length() - 1;
      if (string.charAt(0) == '[' && string.charAt(end) == ']')
      {
        return string.substring(1, end);
      }
      else
      {
        return string;
      }
    }
    return null;
  }

  // --- object basics --------------------------------------------------------

}
