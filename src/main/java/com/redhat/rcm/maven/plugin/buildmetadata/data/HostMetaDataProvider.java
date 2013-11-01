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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;

import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;

/**
 * Provides information about the host running the build.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class HostMetaDataProvider implements MetaDataProvider
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @see com.redhat.rcm.maven.plugin.buildmetadata.data.MetaDataProvider#provideBuildMetaData(java.util.Properties)
   */
  public void provideBuildMetaData(final Properties buildMetaDataProperties)
    throws MojoExecutionException
  {
    try
    {
      final InetAddress address = InetAddress.getLocalHost();
      final String hostname = address.getHostName();
      buildMetaDataProperties.put(Constant.PROP_NAME_HOSTNAME, hostname);
    }
    catch (final UnknownHostException e)
    {
      throw new MojoExecutionException("Cannot determine host information.", e);
    }
  }

  // --- object basics --------------------------------------------------------

}
