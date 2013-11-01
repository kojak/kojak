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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.project.MavenProject;

import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmInfo;

/**
 * Base implementation for {@link MetaDataProvider}s.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public abstract class AbstractMetaDataProvider implements MetaDataProvider
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The Maven project.
   *
   * @impl The implementation is required to cope with a <code>null</code>
   *       value.
   */
  protected MavenProject project;

  /**
   * The Maven session instance.
   *
   * @impl The implementation is required to cope with a <code>null</code>
   *       value.
   */
  protected MavenSession session;

  /**
   * The runtime information of the Maven instance being executed for the build.
   *
   * @impl The implementation is required to cope with a <code>null</code>
   *       value.
   */
  protected RuntimeInformation runtime;

  /**
   * Information for the SCM provided to the build plugin.
   */
  protected ScmInfo scmInfo;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  protected AbstractMetaDataProvider()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
