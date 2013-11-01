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
package com.redhat.rcm.maven.plugin.buildmetadata.scm.maven;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.scm.ScmException;

/**
 * Provides access information to retrieve revision information from the SCM.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class ScmAccessInfo implements Serializable
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The class version identifier.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final long serialVersionUID = 1L;

  /**
   * The number of retries to fetch the change log if the first attempt failed
   * to return a non empty set.
   * <p>
   * The value of this constant is {@value}.
   */
  public static final int DEFAULT_RETRY_COUNT = 5;

  // --- members --------------------------------------------------------------

  /**
   * The format of the dates understood by the SCM system.
   */
  private String dateFormat;

  /**
   * The root directory that contains the files under SCM control.
   */
  private File rootDirectory;

  /**
   * The range of the query in days to fetch change log entries from the SCM. If
   * no change logs have been found, the range is incremented up to
   * {@value #DEFAULT_RETRY_COUNT} times. If no change log has been found after
   * these {@value #DEFAULT_RETRY_COUNT} additional queries, the revision number
   * will not be set with a valid value.
   */
  private int queryRangeInDays;

  /**
   * The flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   */
  private boolean failOnLocalModifications;

  /**
   * The flag to ignore files and directories starting with a dot for checking
   * modified files. This implicates that any files or directories, starting
   * with a dot, are ignored when the check on changed files is run. If the
   * value is <code>true</code>, dot files are ignored, if it is set to
   * <code>false</code>, dot files are respected.
   */
  private boolean ignoreDotFilesInBaseDir;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  public ScmAccessInfo()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the format of the dates understood by the SCM system.
   *
   * @return the format of the dates understood by the SCM system.
   */
  public String getDateFormat()
  {
    return dateFormat;
  }

  /**
   * Sets the format of the dates understood by the SCM system.
   *
   * @param dateFormat the format of the dates understood by the SCM system.
   */
  public void setDateFormat(final String dateFormat)
  {
    this.dateFormat = dateFormat;
  }

  /**
   * Returns the root directory that contains the files under SCM control.
   *
   * @return the root directory that contains the files under SCM control.
   */
  public File getRootDirectory()
  {
    return rootDirectory;
  }

  /**
   * Sets the root directory that contains the files under SCM control.
   *
   * @param rootDirectory the root directory that contains the files under SCM
   *          control.
   */
  public void setRootDirectory(final File rootDirectory)
  {
    this.rootDirectory = rootDirectory;
  }

  /**
   * Returns the range of the query in days to fetch change log entries from the
   * SCM. If no change logs have been found, the range is incremented up to
   * {@value #DEFAULT_RETRY_COUNT} times. If no change log has been found after
   * these {@value #DEFAULT_RETRY_COUNT} additional queries, the revision number
   * will not be set with a valid value.
   *
   * @return the range of the query in days to fetch change log entries from the
   *         SCM.
   */
  public int getQueryRangeInDays()
  {
    return queryRangeInDays;
  }

  /**
   * Sets the range of the query in days to fetch change log entries from the
   * SCM. If no change logs have been found, the range is incremented up to
   * {@value #DEFAULT_RETRY_COUNT} times. If no change log has been found after
   * these {@value #DEFAULT_RETRY_COUNT} additional queries, the revision number
   * will not be set with a valid value.
   *
   * @param queryRangeInDays the range of the query in days to fetch change log
   *          entries from the SCM.
   */
  public void setQueryRangeInDays(final int queryRangeInDays)
  {
    this.queryRangeInDays = queryRangeInDays;
  }

  /**
   * Returns the flag to fail if local modifications have been found. The value
   * is <code>true</code> if the build should fail if there are modifications
   * (any files not in-sync with the remote repository), <code>false</code> if
   * the fact is only to be noted in the build properties.
   *
   * @return the flag to fail if local modifications have been found.
   */
  public boolean isFailOnLocalModifications()
  {
    return failOnLocalModifications;
  }

  /**
   * Sets the flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   *
   * @param failOnLocalModifications the flag to fail if local modifications
   *          have been found.
   */
  public void setFailOnLocalModifications(final boolean failOnLocalModifications)
  {
    this.failOnLocalModifications = failOnLocalModifications;
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
   * Sets the flag to ignore files and directories starting with a dot for
   * checking modified files. This implicates that any files or directories,
   * starting with a dot, are ignored when the check on changed files is run. If
   * the value is <code>true</code>, dot files are ignored, if it is set to
   * <code>false</code>, dot files are respected.
   *
   * @param ignoreDotFilesInBaseDir the flag to ignore files and directories
   *          starting with a dot for checking modified files.
   */
  public void setIgnoreDotFilesInBaseDir(final boolean ignoreDotFilesInBaseDir)
  {
    this.ignoreDotFilesInBaseDir = ignoreDotFilesInBaseDir;
  }

  // --- business -------------------------------------------------------------

  /**
   * Returns the result of the change log query.
   *
   * @param repository the repository to fetch the change log information from.
   * @param provider the provider to use to access the repository.
   * @return the change log entries that match the query, <code>null</code> if
   *         none have been found.
   * @throws ScmException if the change log cannot be fetched.
   */
  public ChangeLogScmResult fetchChangeLog(final ScmRepository repository,
      final ScmProvider provider) throws ScmException
  {
    try
    {
      ChangeLogScmResult result = null;
      int currentRange = queryRangeInDays;
      for (int i = 0; i <= DEFAULT_RETRY_COUNT; i++)
      {
        result =
            provider.changeLog(repository, createFileSet(), null, null,
                currentRange, (ScmBranch) null, dateFormat);
        if (!isEmpty(result))
        {
          return result;
        }
        currentRange += queryRangeInDays;
      }
      return result;
    }
    catch (final org.apache.maven.scm.ScmException e)
    {
      throw new ScmException("Cannot fetch change log from repository.", e);
    }
  }

  /**
   * Checks if the given result contains change logs or not.
   * <p>
   * Calls
   * {@link com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo#isEmpty(org.apache.maven.scm.command.changelog.ChangeLogSet)}
   * with argument list (&lt;changeLogSet&gt;).
   *
   * @param result result the result to be checked.
   * @return <code>true</code> if change logs have been found,<code>false</code>
   *         if any reference up the path to the change logs is
   *         <code>null</code> or the set is empty.
   * @see com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo#isEmpty(org.apache.maven.scm.command.changelog.ChangeLogSet)
   */
  private boolean isEmpty(final ChangeLogScmResult result)
  {
    if (result != null)
    {
      final ChangeLogSet changeLogSet = result.getChangeLog();
      if (changeLogSet != null)
      {
        return isEmpty(changeLogSet);
      }
    }
    return false;
  }

  private boolean isEmpty(final ChangeLogSet changeLogSet)
  {
    final List<?> changeLogSets = changeLogSet.getChangeSets();
    if (changeLogSets != null)
    {
      return changeLogSets.isEmpty();
    }

    return false;
  }

  /**
   * Creates the file set on the root directory of the checked out project.
   *
   * @return the file set on the root directory of the checked out project.
   */
  protected ScmFileSet createFileSet()
  {
    return new ScmFileSet(rootDirectory);
  }

  /**
   * Checks whether the SCM configuration calls for a failure due to changed
   * files.
   *
   * @return <code>true</code> if a fail is indicated (i.e. there are locally
   *         modified files that are to be considered for a check),
   *         <code>false</code> if there are none.
   */
  public boolean isFailIndicated()
  {
    return isFailOnLocalModifications() && !isIgnoreDotFilesInBaseDir();
  }

  // --- object basics --------------------------------------------------------

  /**
   * Delegates call to {@link java.lang.StringBuilder#toString()}.
   *
   * @return the result of the call to
   *         {@link java.lang.StringBuilder#toString()}.
   * @see java.lang.StringBuilder#toString()
   */
  @Override
  public String toString()
  {
    final StringBuilder buffer = new StringBuilder();

    buffer.append("SCM access info: rootDirectory=").append(rootDirectory);
    appendIfExists(buffer, "dateFormat", dateFormat);
    appendIfExists(buffer, "queryRangeInDays", String.valueOf(queryRangeInDays));
    appendIfExists(buffer, "failOnLocalModifications",
        String.valueOf(failOnLocalModifications));
    appendIfExists(buffer, "ignoreDotFilesInBaseDir",
        String.valueOf(ignoreDotFilesInBaseDir));

    return buffer.toString();
  }

  private static void appendIfExists(final StringBuilder buffer,
      final String label, final String value)
  {
    if (StringUtils.isNotBlank(value))
    {
      buffer.append(", ").append(label).append('=').append(value);
    }
  }

}
