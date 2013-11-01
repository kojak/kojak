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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.manager.ScmManager;

import com.redhat.rcm.maven.plugin.buildmetadata.scm.LocallyModifiedInfo;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.Revision;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.RevisionNumberFetcher;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.ScmException;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.ScmNoRevisionException;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.MavenScmRevisionNumberFetcher;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmConnectionInfo;

/**
 * Helper to access the revision information.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class RevisionHelper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * Reference to the logger for this class.
   */
  private static final Log LOG = LogFactory.getLog(RevisionHelper.class);

  // --- members --------------------------------------------------------------

  /**
   * The manager instance to access the SCM system. Provides access to the
   * repository and the provider information.
   */
  private final ScmManager scmManager;

  /**
   * The information to connect to the SCM.
   */
  private final ScmConnectionInfo scmConnectionInfo;

  /**
   * The information to query the SCM.
   */
  private final ScmAccessInfo scmAccessInfo;

  /**
   * The date pattern to use to format revision dates.
   */
  private final String buildDatePattern;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param scmManager the manager instance to access the SCM system.
   * @param scmConnectionInfo the information to connect to the SCM.
   * @param scmAccessInfo the information to query the SCM.
   * @param buildDatePattern the date pattern to use to format revision dates.
   */
  public RevisionHelper(final ScmManager scmManager,
      final ScmConnectionInfo scmConnectionInfo,
      final ScmAccessInfo scmAccessInfo, final String buildDatePattern)
  {
    this.scmManager = scmManager;
    this.scmConnectionInfo = scmConnectionInfo;
    this.scmAccessInfo = scmAccessInfo;
    this.buildDatePattern = buildDatePattern;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Fetches the revision information and adds it to the property sets.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param scmControl the properties to control the gathering of SCM info.
   * @throws ScmException if the creation of the SCM information failed.
   */
  public void provideScmBuildInfo(final Properties buildMetaDataProperties,
      final ScmControl scmControl) throws ScmException
  {
    final boolean failOnMissingRevision = scmControl.isFailOnMissingRevision();

    final RevisionNumberFetcher revisionFetcher =
        new MavenScmRevisionNumberFetcher(scmManager, scmConnectionInfo,
            scmAccessInfo);
    final Revision revision = revisionFetcher.fetchLatestRevisionNumber();
    if (revision != null)
    {
      buildMetaDataProperties.setProperty(Constant.PROP_NAME_SCM_URL,
          scmConnectionInfo.getConnectionUrl());
      final String revisionId = revision.getId();
      buildMetaDataProperties.setProperty(Constant.PROP_NAME_SCM_REVISION_ID,
          revisionId);
      final Date revisionDate = revision.getDate();
      final DateFormat format =
          new SimpleDateFormat(buildDatePattern, Locale.ENGLISH);
      final String revisionDateString = format.format(revisionDate);
      buildMetaDataProperties.setProperty(Constant.PROP_NAME_SCM_REVISION_DATE,
          revisionDateString);

      final boolean validateCheckout = scmControl.isValidateCheckout();
      if (validateCheckout)
      {
        provideLocallyModifiedInfo(buildMetaDataProperties, revisionFetcher);
      }
    }
    else if (failOnMissingRevision)
    {
      throw new ScmNoRevisionException("Cannot fetch SCM revision. "
                                       + scmConnectionInfo);
    }
  }

  // --- object basics --------------------------------------------------------

  /**
   * Provides the information of locally modified files to the build properties.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param revisionFetcher the fetcher to use.
   * @throws ScmException if the creation of the modification information
   *           failed.
   */
  private void provideLocallyModifiedInfo(
      final Properties buildMetaDataProperties,
      final RevisionNumberFetcher revisionFetcher) throws ScmException
  {
    try
    {
      final ScmFileSet fileSet =
          new ScmFileSet(scmAccessInfo.getRootDirectory(), "**/*", null);
      final LocallyModifiedInfo info =
          revisionFetcher.containsModifications(fileSet);
      buildMetaDataProperties.setProperty(
          Constant.PROP_NAME_SCM_LOCALLY_MODIFIED,
          String.valueOf(info.isLocallyModified()));
      if (info.isLocallyModified())
      {
        buildMetaDataProperties.setProperty(
            Constant.PROP_NAME_SCM_LOCALLY_MODIFIED_FILES, info.getFiles());
        if (scmAccessInfo.isFailIndicated())
        {
          throw new ScmException("Local Modifications detected ("
                                 + info.getFiles() + ").");
        }
      }
    }
    catch (final Exception e)
    {
      if (scmAccessInfo.isFailIndicated())
      {
        throw new ScmException(e);
      }
      else
      {
        buildMetaDataProperties.setProperty(
            Constant.PROP_NAME_SCM_LOCALLY_MODIFIED, "unknown");
        if (LOG.isInfoEnabled())
        {
          LOG.info("Failed to check modification status.");
        }
      }
    }
  }
}
