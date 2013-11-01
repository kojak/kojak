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
package com.redhat.rcm.maven.plugin.buildmetadata.util;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.doxia.site.decoration.Skin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Utilities for Maven for generating reports.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class ReportUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Utility class pattern.
   */
  private ReportUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Returns a file reference to the default skin useful for rendering
   * standalone run reports.
   * <p>
   * Stolen from the changes plugin.
   * </p>
   *
   * @param project the project of the plugin that calls this method.
   * @param localRepository a reference to the local repository to reference to.
   * @param resolver to resolve the skin artifact.
   * @param factory to resolve dependencies.
   * @return a file reference to the default skin.
   * @throws MojoExecutionException if the skin artifact cannot be resolved.
   */
  public static File getSkinArtifactFile(final MavenProject project,
      final ArtifactRepository localRepository,
      final ArtifactResolver resolver, final ArtifactFactory factory)
    throws MojoExecutionException
  {
    final Skin skin = Skin.getDefaultSkin();
    final String version = determineVersion(skin);
    try
    {
      final VersionRange versionSpec =
          VersionRange.createFromVersionSpec(version);
      final Artifact artifact =
          factory.createDependencyArtifact(skin.getGroupId(),
              skin.getArtifactId(), versionSpec, "jar", null, null);
      resolver.resolve(artifact, project.getRemoteArtifactRepositories(),
          localRepository);

      return artifact.getFile();
    }
    catch (final InvalidVersionSpecificationException e)
    {
      throw new MojoExecutionException("The skin version '" + version
                                       + "' is not valid: " + e.getMessage(), e);
    }
    catch (final ArtifactResolutionException e)
    {
      throw new MojoExecutionException("Unable to find skin", e);
    }
    catch (final ArtifactNotFoundException e)
    {
      throw new MojoExecutionException("The skin does not exist: "
                                       + e.getMessage(), e);
    }
  }

  /**
   * Determines the version of the given skin. If the version is not set in the
   * skin, the {@link Artifact#RELEASE_VERSION} is returned.
   *
   * @param skin the skin whose version is requested.
   * @return the version of the skin or {@link Artifact#RELEASE_VERSION} as
   *         default.
   */
  private static String determineVersion(final Skin skin)
  {
    String version = skin.getVersion();
    if (version == null)
    {
      version = Artifact.RELEASE_VERSION;
    }
    return version;
  }

  // --- object basics --------------------------------------------------------

}
