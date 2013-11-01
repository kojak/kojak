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
package com.redhat.rcm.maven.plugin.buildmetadata.stub;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 * A stub to use with unit tests.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision: 13913 $
 */
public final class BuildMetaDataProjectStub extends MavenProjectStub
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The base directory of the test.
   */
  private File baseDir;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  public BuildMetaDataProjectStub()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the properties of the model.
   *
   * @return the properties of the model.
   */
  public Properties getProperties()
  {
    return getModel().getProperties();
  }

  /**
   * Returns the version of the model.
   * <p>
   * {@inheritDoc}
   *
   * @see org.apache.maven.plugin.testing.stubs.MavenProjectStub#getVersion()
   */
  public String getVersion()
  {
    return getModel().getVersion();
  }

  /**
   * Returns the build of the model.
   * <p>
   * {@inheritDoc}
   *
   * @see org.apache.maven.plugin.testing.stubs.MavenProjectStub#getBuild()
   */
  public Build getBuild()
  {
    return getModel().getBuild();
  }

  /**
   * Sets the base directory of the unit test build.
   *
   * @param dir the base directory of the unit test build.
   */
  public void setBasedir(final File dir)
  {
    this.baseDir = dir;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Returns the base directory of the unit test build.
   * </p>
   *
   * @see org.apache.maven.plugin.testing.stubs.MavenProjectStub#getBasedir()
   */
  public File getBasedir()
  {
    return baseDir;
  }

  // --- business -------------------------------------------------------------

  /**
   * Writes the model to the writer.
   *
   * @param writer the writer to write the model to.
   * @throws IOException on any problem writing to the writer.
   */
  public void writeModel(final Writer writer) throws IOException
  {
    final MavenXpp3Writer pomWriter = new MavenXpp3Writer();

    pomWriter.write(writer, getModel());
  }

  // --- object basics --------------------------------------------------------
}
