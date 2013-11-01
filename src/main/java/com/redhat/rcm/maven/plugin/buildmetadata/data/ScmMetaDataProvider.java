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
package com.redhat.rcm.maven.plugin.buildmetadata.data;

import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.provider.ScmProviderRepositoryWithHost;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.common.RevisionHelper;
import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmControl;
import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmCredentials;
import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmInfo;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmConnectionInfo;

/**
 * Extracts information from the Maven project, session, and runtime
 * information.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class ScmMetaDataProvider extends AbstractMetaDataProvider
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * Reference to the logger for this class.
   */
  private static final Log LOG = LogFactory.getLog(ScmMetaDataProvider.class);

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Constructor.
   *
   * @param project the Maven project.
   * @param scmInfo the value for scmInfo.
   * @see com.redhat.rcm.maven.plugin.buildmetadata.data.AbstractMetaDataProvider#AbstractMetaDataProvider()
   */
  public ScmMetaDataProvider(final MavenProject project, final ScmInfo scmInfo)
  {
    this.project = project;
    this.scmInfo = scmInfo;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Provides the SCM build information to the property sets if the URL to the
   * SCM is provided.
   *
   * @param buildMetaDataProperties the build meta data properties.
   */
  public final void provideBuildMetaData(final Properties buildMetaDataProperties)
  {
    final ScmControl scmControl = scmInfo.getScmControl();
    if (scmControl.isAddScmInfo() && !scmControl.isOffline()
        && project.getScm() != null)
    {
      try
      {
        final ScmConnectionInfo scmConnectionInfo = loadConnectionInfo();
        final ScmAccessInfo scmAccessInfo = createScmAccessInfo();
        final RevisionHelper helper =
            new RevisionHelper(scmInfo.getScmManager(), scmConnectionInfo,
                scmAccessInfo, scmInfo.getBuildDatePattern());
        helper.provideScmBuildInfo(buildMetaDataProperties, scmControl);
      }
      catch (final ScmRepositoryException e)
      {
        throw new IllegalStateException(
            "Cannot fetch SCM revision information.", e);
      }
      catch (final NoSuchScmProviderException e)
      {
        throw new IllegalStateException(
            "Cannot fetch SCM revision information.", e);
      }
    }
    else
    {
      LOG.debug("Skipping SCM data since addScmInfo="
                + scmControl.isAddScmInfo() + ", offline="
                + scmControl.isOffline() + ", scmInfoProvided="
                + (project.getScm() != null) + ".");
    }
  }

  /**
   * Load user name password from settings if user has not set them via JVM
   * properties.
   *
   * @return the connection information to connect to the SCM system.
   * @throws IllegalStateException if the connection string to the SCM cannot be
   *           fetched.
   * @throws ScmRepositoryException if the repository information is not
   *           sufficient to build the repository instance.
   * @throws NoSuchScmProviderException if there is no provider for the SCM
   *           connection URL.
   */
  private ScmConnectionInfo loadConnectionInfo() throws IllegalStateException,
    ScmRepositoryException, NoSuchScmProviderException
  {
    final String scmConnection = getConnection();
    final ScmCredentials credentials = scmInfo.getScmCrendentials();
    if (credentials.getUserName() == null || credentials.getPassword() == null)
    {
      final ScmRepository repository =
          scmInfo.getScmManager().makeScmRepository(scmConnection);
      if (repository.getProviderRepository() instanceof ScmProviderRepositoryWithHost)
      {
        final ScmProviderRepositoryWithHost repositoryWithHost =
            (ScmProviderRepositoryWithHost) repository.getProviderRepository();
        final String host = createHostName(repositoryWithHost);
        credentials.configureByServer(host);
      }
    }

    final ScmConnectionInfo info = new ScmConnectionInfo();
    info.setUserName(credentials.getUserName());
    info.setPassword(credentials.getPassword());
    info.setPrivateKey(credentials.getPrivateKey());
    info.setScmConnectionUrl(scmConnection);
    info.setTagBase(scmInfo.getTagBase());
    return info;
  }

  /**
   * Delegates call to {@link org.apache.maven.model.Scm#getConnection()}.
   *
   * @return the result of the call to
   *         {@link org.apache.maven.model.Scm#getConnection()}.
   * @throws IllegalStateException when there is insufficient information to
   *           return the SCM connection string.
   * @see org.apache.maven.model.Scm#getConnection()
   */
  protected final String getConnection() throws IllegalStateException
  {
    if (project.getScm() == null)
    {
      throw new IllegalStateException("SCM Connection is not set.");
    }

    final String scmConnection = project.getScm().getConnection();
    final String connectionType = scmInfo.getConnectionType();
    if (StringUtils.isNotEmpty(scmConnection)
        && "connection".equals(connectionType.toLowerCase(Locale.ENGLISH)))
    {
      return scmConnection;
    }

    final String scmDeveloper = project.getScm().getDeveloperConnection();
    if (StringUtils.isNotEmpty(scmDeveloper)
        && "developerconnection".equals(connectionType
            .toLowerCase(Locale.ENGLISH)))
    {
      return scmDeveloper;
    }

    throw new IllegalStateException("SCM Connection is not set.");
  }

  /**
   * Creates the host name by adding the port if present.
   *
   * @param repositoryWithHost the host information.
   * @return the host with port if present.
   */
  private String createHostName(
      final ScmProviderRepositoryWithHost repositoryWithHost)
  {
    final String host = repositoryWithHost.getHost();
    final int port = repositoryWithHost.getPort();
    if (port > 0)
    {
      return host + ":" + port;
    }
    return host;
  }

  /**
   * Creates the access information instance to retrieve the change logs from
   * the SCM.
   *
   * @return the SCM access instance.
   */
  private ScmAccessInfo createScmAccessInfo()
  {
    final ScmAccessInfo accessInfo = new ScmAccessInfo();
    accessInfo.setDateFormat(scmInfo.getScmDateFormat());
    accessInfo.setRootDirectory(scmInfo.getBasedir());
    accessInfo.setFailOnLocalModifications(scmInfo.getScmControl()
        .isFailOnLocalModifications());
    accessInfo.setIgnoreDotFilesInBaseDir(scmInfo.getScmControl()
        .isIgnoreDotFilesInBaseDir());
    accessInfo.setQueryRangeInDays(scmInfo.getQueryRangeInDays());
    return accessInfo;
  }

  // --- object basics --------------------------------------------------------

}
