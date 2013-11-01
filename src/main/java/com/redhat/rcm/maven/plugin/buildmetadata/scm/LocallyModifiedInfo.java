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

/**
 * Stores the information about locally modified files.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class LocallyModifiedInfo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The flag that shows whether the files are modified (<code>true</code>) or
   * not (<code>false</code>).
   */
  private final boolean locallyModified;

  /**
   * The list of files that where reported to be modified. This includes all
   * files that are not in-sync with the trunk of the repository.
   */
  private final String files;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param locallyModified the flag that shows whether the files are modified (
   *          <code>true</code>) or not (<code>false</code>).
   * @param files the list of files that where reported to be modified.
   */
  public LocallyModifiedInfo(final boolean locallyModified, final String files)
  {
    this.locallyModified = locallyModified;
    this.files = normalize(files);
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  private String normalize(final String files)
  {
    if (files == null)
    {
      return null;
    }
    return files.replace('\\', '/');
  }

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the flag that shows whether the files are modified (
   * <code>true</code>) or not (<code>false</code>).
   *
   * @return the flag that shows whether the files are modified (
   *         <code>true</code>) or not (<code>false</code>).
   */
  public boolean isLocallyModified()
  {
    return locallyModified;
  }

  /**
   * Returns the list of files that where reported to be modified. This includes
   * all files that are not in-sync with the trunk of the repository.
   *
   * @return the list of files that where reported to be modified.
   */
  public String getFiles()
  {
    return files;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
