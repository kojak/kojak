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

/**
 * Information to control the gathering of SCM meta data.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class ScmControl
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * Flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   */
  private final boolean failOnLocalModifications;

  /**
   * The flag to ignore files and directories starting with a dot for checking
   * modified files. This implicates that any files or directories, starting
   * with a dot, are ignored when the check on changed files is run. If the
   * value is <code>true</code>, dot files are ignored, if it is set to
   * <code>false</code>, dot files are respected.
   */
  private final boolean ignoreDotFilesInBaseDir;

  /**
   * In offline mode the plugin will not generate revision information.
   */
  private final boolean offline;

  /**
   * Add SCM information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in SCM information, set this
   * to <code>false</code>.
   */
  private final boolean addScmInfo;

  /**
   * If it should be checked if the local files are up-to-date with the remote
   * files in the SCM repository. If the value is <code>true</code> the result
   * of the check, including the list of changed files, is added to the build
   * meta data.
   */
  private final boolean validateCheckout;

  /**
   * Fail if revision is requested to be retrieved, access to SCM is provided,
   * system is online, nothing should prevent the build from fetching the
   * information.
   * <p>
   * If set to <code>true</code> the build will fail, if revision cannot be
   * fetched, <code>false</code> will continue so that the meta data do not
   * contain the revision.
   * </p>
   */
  private final boolean failOnMissingRevision;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // CHECKSTYLE:OFF

  /**
   * Default constructor.
   *
   * @param failOnLocalModifications the value for failOnLocalModifications.
   * @param ignoreDotFilesInBaseDir the flag to ignore files and directories
   *          starting with a dot for checking modified files.
   * @param offline the value for offline.
   * @param addScmInfo the value for addScmInfo.
   * @param validateCheckout the value for validateCheckout.
   * @param failOnMissingRevision the value for failOnMissingRevision.
   */
  public ScmControl(// NOPMD
      final boolean failOnLocalModifications,
      final boolean ignoreDotFilesInBaseDir, final boolean offline,
      final boolean addScmInfo, final boolean validateCheckout,
      final boolean failOnMissingRevision)
  {
    this.failOnLocalModifications = failOnLocalModifications;
    this.ignoreDotFilesInBaseDir = ignoreDotFilesInBaseDir;
    this.offline = offline;
    this.addScmInfo = addScmInfo;
    this.validateCheckout = validateCheckout;
    this.failOnMissingRevision = failOnMissingRevision;
  }

  // CHECKSTYLE:ON

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the value for failOnLocalModifications.
   * <p>
   * Flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   * </p>
   *
   * @return the value for failOnLocalModifications.
   */
  public boolean isFailOnLocalModifications()
  {
    return failOnLocalModifications;
  }

  /**
   * Returns the flag to ignore files and directories starting with a dot for
   * checking modified files. This implicates that any files or directories,
   * starting with a dot, are ignored when the check on changed files is run. If
   * the value is <code>true</code>, dot files are ignored, if it is set to
   * <code>false</code>, dot files are respected.
   *
   * @return the flag to ignore files and directories starting with a dot for
   *         checking modified files.
   */
  public boolean isIgnoreDotFilesInBaseDir()
  {
    return ignoreDotFilesInBaseDir;
  }

  /**
   * Returns the value for offline.
   * <p>
   * In offline mode the plugin will not generate revision information.
   * </p>
   *
   * @return the value for offline.
   */
  public boolean isOffline()
  {
    return offline;
  }

  /**
   * Returns the value for addScmInfo.
   * <p>
   * Add SCM information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in SCM information, set this
   * to <code>false</code>.
   * </p>
   *
   * @return the value for addScmInfo.
   */
  public boolean isAddScmInfo()
  {
    return addScmInfo;
  }

  /**
   * Returns the value for validateCheckout.
   * <p>
   * If it should be checked if the local files are up-to-date with the remote
   * files in the SCM repository. If the value is <code>true</code> the result
   * of the check, including the list of changed files, is added to the build
   * meta data.
   * </p>
   *
   * @return the value for validateCheckout.
   */
  public boolean isValidateCheckout()
  {
    return validateCheckout;
  }

  /**
   * Returns the value for failOnMissingRevision.
   * <p>
   * Fail if revision is requested to be retrieved, access to SCM is provided,
   * system is online, nothing should prevent the build from fetching the
   * information.
   * </p>
   * <p>
   * If set to <code>true</code> the build will fail, if revision cannot be
   * fetched, <code>false</code> will continue so that the meta data do not
   * contain the revision.
   * </p>
   *
   * @return the value for failOnMissingRevision.
   */
  public boolean isFailOnMissingRevision()
  {
    return failOnMissingRevision;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
