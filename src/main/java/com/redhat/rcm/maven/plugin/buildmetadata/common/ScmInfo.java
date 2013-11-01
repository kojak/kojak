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

import java.io.File;

import org.apache.maven.scm.manager.ScmManager;

/**
 * Bundles the SCM information to be passed to meta data providers.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class ScmInfo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The manager instance to access the SCM system. Provides access to the
   * repository and the provider information.
   */
  private final ScmManager scmManager;

  /**
   * Allows the user to choose which scm connection to use when connecting to
   * the scm. Can either be "connection" or "developerConnection".
   */
  private final String connectionType;

  /**
   * Used to specify the date format of the log entries that are retrieved from
   * your SCM system.
   */
  private final String scmDateFormat;

  /**
   * Input dir. Directory where the files under SCM control are located.
   */
  private final File basedir;

  /**
   * The authentication for the SCM server.
   */
  private final ScmCredentials scmCrendentials;

  /**
   * The url of tags base directory (used by svn protocol).
   */
  private final String tagBase;

  /**
   * The range of the query in days to fetch change log entries from the SCM. If
   * no change logs have been found, the range is incremented up to
   * {@value com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo#DEFAULT_RETRY_COUNT}
   * times. If no change log has been found after these
   * {@value com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo#DEFAULT_RETRY_COUNT}
   * additional queries, the revision number will not be set with a valid value.
   */
  private final int queryRangeInDays;

  /**
   * The date pattern to use to format the build and revision dates. Please
   * refer to the <a href =
   * "http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html"
   * >SimpleDateFormat</a> class for valid patterns.
   */
  private final String buildDatePattern;

  /**
   * The information to control the gathering of SCM meta data.
   */
  private final ScmControl scmControl;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // CHECKSTYLE:OFF
  /**
   * Default constructor.
   *
   * @param scmManager the manager instance to access the SCM system.
   * @param connectionType the value for connectionType.
   * @param scmDateFormat the value for scmDateFormat.
   * @param basedir the value for basedir.
   * @param scmCrendentials the authentication for the SCM server.
   * @param tagBase the url of tags base directory (used by svn protocol).
   * @param queryRangeInDays the range of the query in days to fetch change log
   *          entries from the SCM.
   * @param buildDatePattern the date pattern to use to format the build and
   *          revision dates.
   * @param scmControl the information to control the gathering of SCM meta
   *          data.
   * @note This argument list is quite long. The next time we touch this class,
   *       we should provide a builder.
   */
  public ScmInfo(final ScmManager scmManager, final String connectionType, // NOPMD
      final String scmDateFormat, final File basedir,
      final ScmCredentials scmCrendentials, final String tagBase,
      final int queryRangeInDays, final String buildDatePattern,
      final ScmControl scmControl)
  {
    this.scmManager = scmManager;
    this.connectionType = connectionType;
    this.scmDateFormat = scmDateFormat;
    this.basedir = basedir;
    this.scmCrendentials = scmCrendentials;
    this.tagBase = tagBase;
    this.queryRangeInDays = queryRangeInDays;
    this.buildDatePattern = buildDatePattern;
    this.scmControl = scmControl;
  }
  // CHECKSTYLE:ON

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the manager instance to access the SCM system. Provides access to
   * the repository and the provider information.
   *
   * @return the manager instance to access the SCM system.
   */
  public ScmManager getScmManager()
  {
    return scmManager;
  }

  /**
   * Returns the value for connectionType.
   * <p>
   * Allows the user to choose which scm connection to use when connecting to
   * the scm. Can either be "connection" or "developerConnection".
   *
   * @return the value for connectionType.
   */
  public String getConnectionType()
  {
    return connectionType;
  }

  /**
   * Returns the value for scmDateFormat.
   * <p>
   * Used to specify the date format of the log entries that are retrieved from
   * your SCM system.
   *
   * @return the value for scmDateFormat.
   */
  public String getScmDateFormat()
  {
    return scmDateFormat;
  }

  /**
   * Returns the value for basedir.
   * <p>
   * Input dir. Directory where the files under SCM control are located.
   *
   * @return the value for basedir.
   */
  public File getBasedir()
  {
    return basedir;
  }

  /**
   * Returns the authentication for the SCM server.
   *
   * @return the authentication for the SCM server.
   */
  public ScmCredentials getScmCrendentials()
  {
    return scmCrendentials;
  }

  /**
   * Returns the url of tags base directory (used by svn protocol).
   *
   * @return the url of tags base directory (used by svn protocol).
   */
  public String getTagBase()
  {
    return tagBase;
  }

  /**
   * Returns the range of the query in days to fetch change log entries from the
   * SCM. If no change logs have been found, the range is incremented up to
   * {@value com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo#DEFAULT_RETRY_COUNT}
   * times. If no change log has been found after these
   * {@value com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo#DEFAULT_RETRY_COUNT}
   * additional queries, the revision number will not be set with a valid value.
   *
   * @return the range of the query in days to fetch change log entries from the
   *         SCM.
   */
  public int getQueryRangeInDays()
  {
    return queryRangeInDays;
  }

  /**
   * Returns the date pattern to use to format the build and revision dates.
   * Please refer to the <a href =
   * "http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html"
   * >SimpleDateFormat</a> class for valid patterns.
   *
   * @return the date pattern to use to format the build and revision dates.
   */
  public String getBuildDatePattern()
  {
    return buildDatePattern;
  }

  /**
   * Returns the information to control the gathering of SCM meta data.
   *
   * @return the information to control the gathering of SCM meta data.
   */
  public ScmControl getScmControl()
  {
    return scmControl;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
