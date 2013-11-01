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
package com.redhat.rcm.maven.plugin.buildmetadata.scm;

import org.apache.maven.scm.ScmFileSet;

/**
 * Provides means to fetch the revision number.
 * <p>
 * The implementation requires to provide access to the SCM.
 * </p>
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public interface RevisionNumberFetcher
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Returns the latest revision number from the SCM.
   *
   * @return the latest revision number from the SCM, <code>null</code> if none
   *         can be found.
   * @throws ScmException if the revision number cannot be fetched.
   */
  Revision fetchLatestRevisionNumber() throws ScmException;

  /**
   * Checks if the local source files are in-sync with the repository or not.
   *
   * @param fileSet the file set to check.
   * @return the status of the files.
   * @throws ScmException on any problem checking the up-to-date status.
   */
  LocallyModifiedInfo containsModifications(ScmFileSet fileSet)
      throws ScmException;

  // --- object basics --------------------------------------------------------

}
