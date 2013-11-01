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

import java.io.Serializable;

import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.ScmProviderRepositoryWithHost;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.scm.ScmException;

/**
 * Provides the information required to connect to a SCM system.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class ScmConnectionInfo implements Serializable
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

  // --- members --------------------------------------------------------------

  /**
   * The URL to connect to the SCM system.
   */
  private String connectionUrl;

  /**
   * The user name to authenticate against the SCM system.
   */
  private String userName;

  /**
   * The password to authenticate against the SCM system.
   */
  private String password;

  /**
   * The private key to authenticate against the SCM system.
   */
  private String privateKey;

  /**
   * The pass phrase to authenticate against the SCM system.
   */
  private String passPhrase;

  /**
   * The url of tags base directory (used by svn protocol).
   */
  private String tagBase;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the URL to connect to the SCM system.
   *
   * @return the URL to connect to the SCM system.
   */
  public String getConnectionUrl()
  {
    return connectionUrl;
  }

  /**
   * Sets the URL to connect to the SCM system.
   *
   * @param connectionUrl the URL to connect to the SCM system.
   */
  public void setScmConnectionUrl(final String connectionUrl)
  {
    this.connectionUrl = connectionUrl;
  }

  /**
   * Returns the user name to authenticate against the SCM system.
   *
   * @return the user name to authenticate against the SCM system.
   */
  public String getUserName()
  {
    return userName;
  }

  /**
   * Sets the user name to authenticate against the SCM system.
   *
   * @param userName the user name to authenticate against the SCM system.
   */
  public void setUserName(final String userName)
  {
    this.userName = userName;
  }

  /**
   * Returns the password to authenticate against the SCM system.
   *
   * @return the password to authenticate against the SCM system.
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Sets the password to authenticate against the SCM system.
   *
   * @param password the password to authenticate against the SCM system.
   */
  public void setPassword(final String password)
  {
    this.password = password;
  }

  /**
   * Returns the private key to authenticate against the SCM system.
   *
   * @return the private key to authenticate against the SCM system.
   */
  public String getPrivateKey()
  {
    return privateKey;
  }

  /**
   * Sets the private key to authenticate against the SCM system.
   *
   * @param privateKey the private key to authenticate against the SCM system.
   */
  public void setPrivateKey(final String privateKey)
  {
    this.privateKey = privateKey;
  }

  /**
   * Returns the pass phrase to authenticate against the SCM system.
   *
   * @return the pass phrase to authenticate against the SCM system.
   */
  public String getPassPhrase()
  {
    return passPhrase;
  }

  /**
   * Sets the pass phrase to authenticate against the SCM system.
   *
   * @param passPhrase the pass phrase to authenticate against the SCM system.
   */
  public void setPassPhrase(final String passPhrase)
  {
    this.passPhrase = passPhrase;
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
   * Sets the url of tags base directory (used by svn protocol).
   *
   * @param tagBase the url of tags base directory (used by svn protocol).
   */
  public void setTagBase(final String tagBase)
  {
    this.tagBase = tagBase;
  }

  // --- business -------------------------------------------------------------

  /**
   * Creates and configures the SCM repository.
   *
   * @param scmManager the manager to create the repository dependent on the
   *          {@link #getConnectionUrl() connection URL}.
   * @return the repository implementation to connect to the SCM system.
   * @throws ScmException if the repository implementation cannot be created or
   *           configured. This happens especially if no provider exists for the
   *           given connection URL.
   */
  public ScmRepository createRepository(final ScmManager scmManager)
    throws ScmException
  {
    try
    {
      final ScmRepository repository =
          scmManager.makeScmRepository(connectionUrl);

      final ScmProviderRepository providerRepository =
          repository.getProviderRepository();
      configure(providerRepository);

      if (repository.getProviderRepository() instanceof ScmProviderRepositoryWithHost)
      {
        final ScmProviderRepositoryWithHost providerRepositoryWithHost =
            (ScmProviderRepositoryWithHost) repository.getProviderRepository();
        configure(providerRepositoryWithHost);
      }

      if (!StringUtils.isEmpty(tagBase)
          && repository.getProvider().equals("svn"))
      {
        final SvnScmProviderRepository svnRepository =
            (SvnScmProviderRepository) repository.getProviderRepository();
        configure(svnRepository);
      }
      return repository;
    }
    catch (final Exception e)
    {
      throw new ScmException("The SCM provider cannot be created.", e);
    }
  }

  /**
   * Configures the repository with authentication information.
   *
   * @param repository the repository instance to configure.
   */
  protected void configure(final ScmProviderRepository repository)
  {
    if (!StringUtils.isEmpty(userName))
    {
      repository.setUser(userName);
    }

    if (!StringUtils.isEmpty(password))
    {
      repository.setPassword(password);
    }
  }

  /**
   * Configures the repository with private key and password information.
   *
   * @param repository the repository instance to configure.
   */
  protected void configure(final ScmProviderRepositoryWithHost repository)
  {
    if (!StringUtils.isEmpty(privateKey))
    {
      repository.setPrivateKey(privateKey);
    }

    if (!StringUtils.isEmpty(passPhrase))
    {
      repository.setPassphrase(passPhrase);
    }
  }

  /**
   * Configures the repository with the tag base information.
   *
   * @param repository the repository instance to configure.
   */
  protected void configure(final SvnScmProviderRepository repository)
  {
    repository.setTagBase(tagBase);
  }

  // --- object basics --------------------------------------------------------

  /**
   * Returns the string representation of the object.
   * Sensitive information is masked.
   *
   * @return the string representation of the object.
   */
  @Override
  public String toString()
  {
    final StringBuilder buffer = new StringBuilder();

    buffer.append("SCM connection info: url=").append(connectionUrl);
    appendIfExists(buffer, "user", userName);
    appendSensibleDataIfExists(buffer, "password", password);
    appendSensibleDataIfExists(buffer, "privateKey", privateKey);
    appendSensibleDataIfExists(buffer, "passPhrase", passPhrase);
    appendIfExists(buffer, "tagBase", tagBase);

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

  private static void appendSensibleDataIfExists(final StringBuilder buffer,
      final String label, final String value)
  {
    if (StringUtils.isNotBlank(value))
    {
      buffer.append(", ").append(label).append('=').append(mask(value));
    }
  }

  private static String mask(final String input)
  {
    return StringUtils.repeat("*", input.length());
  }
}
