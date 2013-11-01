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
package test.com.redhat.rcm.maven.plugin.buildmetadata;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.redhat.rcm.maven.plugin.buildmetadata.BuildMetaDataMojo;
import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;
import com.redhat.rcm.maven.plugin.buildmetadata.stub.BuildMetaDataProjectStub;

/**
 * Tests {@link BuildMetaDataMojo}.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision: 15989 $
 */
public class BuildMetaDataMojoTest extends AbstractMojoTestCase
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The test identifier to identify the POM to use and the target directory to
   * write to.
   * <p>
   * The value of this constant is {@value}.
   */
  protected static final String TEST_ID = "no-properties-pom";

  /**
   * The relative (according to the basedir) path to the test input dir.
   * <p>
   * The value of this constant is {@value}.
   */
  protected static final String ROOT_DIR_SUFFIX = "target/test-" + TEST_ID;

  /**
   * The relative (according to the basedir) path to the test input dir. Since
   * the POM is altered we will copy the POM to this location.
   * <p>
   * The value of this constant is {@value}.
   */
  protected static final String INPUT_DIR_SUFFIX = ROOT_DIR_SUFFIX + "/basedir";

  /**
   * The relative (according to the basedir) path to the test output dir.
   * <p>
   * The value of this constant is {@value}.
   */
  protected static final String OUTPUT_DIR_SUFFIX = ROOT_DIR_SUFFIX + "/target";

  // --- members --------------------------------------------------------------

  /**
   * The unit under test.
   */
  private BuildMetaDataMojo uut;

  /**
   * The base directory for the test.
   */
  private File baseDir;

  /**
   * The directory to direct the test output to.
   */
  private File targetDir;

  /**
   * The name of the test POM file to set to the project.
   */
  private File testPomFile;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- prepare --------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @throws Exception {@inheritDoc}
   */
  @Before
  protected void setUp() throws Exception
  {
    super.setUp();

    targetDir = createDir(OUTPUT_DIR_SUFFIX);
    baseDir = createDir(INPUT_DIR_SUFFIX);

    testPomFile = new File(baseDir, TEST_ID + ".xml");

    // uut = (BuildMetaDataMojo) lookupMojo("provide-buildmetadata",
    // testPomFile);
    uut = new BuildMetaDataMojo();
    uut.setProject(createProject());
  }

  /**
   * {@inheritDoc}
   *
   * @throws Exception {@inheritDoc}
   */
  @After
  protected void tearDown() throws Exception
  {
    final File rootDir = new File(getBasedir(), ROOT_DIR_SUFFIX);
    if (rootDir.exists())
    {
      FileUtils.deleteDirectory(rootDir);
    }

    targetDir = null;
    baseDir = null;
    uut = null;

    super.tearDown();
  }

  // --- helper ---------------------------------------------------------------

  /**
   * Creates the maven project.
   *
   * @return the created project.
   * @throws IOException never.
   */
  protected MavenProject createProject() throws IOException
  {
    final BuildMetaDataProjectStub project = new BuildMetaDataProjectStub();
    final Model model = createModel();
    project.setFile(testPomFile);
    project.setBasedir(targetDir.getParentFile());
    project.setModel(model);
    project.setGroupId(model.getGroupId());
    project.setArtifactId(model.getArtifactId());
    final Build build = new Build();
    build.setDirectory(targetDir.getAbsolutePath());
    build.setOutputDirectory(new File(targetDir, "classes").getAbsolutePath());
    model.setBuild(build);
    Writer writer = null;
    try
    {
      writer = new BufferedWriter(new FileWriter(testPomFile));
      project.writeModel(writer);
    }
    finally
    {
      IOUtil.close(writer);
    }
    return project;
  }

  /**
   * Creates the model used in this test.
   *
   * @return the model used in this test.
   */
  protected Model createModel()
  {
    final Model model = new Model();
    final Properties parentProperties = new Properties();
    parentProperties.setProperty("PARENT", "parentValue");
    model.setProperties(new Properties(parentProperties));
    model.setGroupId("test.group");
    model.setArtifactId("test.artifact");
    model.setVersion("1.0.0");
    return model;
  }

  /**
   * Provides the given directory within the base director. If the directoy
   * exists, it will be removed.
   *
   * @param fileSuffix the suffix to append to the base directory.
   * @return reference to the created directory.
   * @throws IOException on any problem generating the directory.
   */
  protected File createDir(final String fileSuffix) throws IOException
  {
    final File dir = new File(getBasedir(), fileSuffix);
    if (dir.exists())
    {
      FileUtils.deleteDirectory(dir);
    }
    final boolean dirCreated = dir.mkdirs();
    if (!dirCreated)
    {
      throw new IOException("Cannot create directory '" + dir.getAbsolutePath()
                            + "'.");
    }
    return dir;
  }

  /**
   * Loads the properties from the file. Check that the file exists with an
   * assert.
   *
   * @return the read properties.
   * @throws IOException in any problem reading the properties.
   */
  final Properties loadProperties() throws IOException
  {
    final File buildPropertiesFile =
        new File(targetDir, "META-INF/buildmetadata.properties");
    assertTrue("Build properties does not exists.",
        buildPropertiesFile.exists());

    final Properties buildProperties = new Properties();
    InputStream in = null;
    try
    {
      in = new BufferedInputStream(new FileInputStream(buildPropertiesFile));
      buildProperties.load(in);
      return buildProperties;
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  // --- tests ----------------------------------------------------------------

  /**
   * Simple test on the build.
   *
   * @throws Exception never.
   */
  @Test
  public void testBuild() throws Exception
  {
    uut.setCreatePropertiesReport(true);
    uut.setPropertiesOutputFile(new File(targetDir,
        "META-INF/buildmetadata.properties"));
    final MavenSession session =
        new MavenSession(null, null, null, null, null, null, null, null,
            new Date());
    uut.setSession(session);
    uut.execute();
    final Properties buildProperties = loadProperties();

    final String version =
        buildProperties.getProperty(Constant.PROP_NAME_VERSION);
    assertEquals("Version check.", "1.0.0", version);

    final String buildDate =
        buildProperties.getProperty(Constant.PROP_NAME_BUILD_DATE);
    assertNotNull("Build date check.", buildDate);

    final String fullVersion =
        buildProperties.getProperty(Constant.PROP_NAME_FULL_VERSION);
    assertNotNull("Full version check.", fullVersion);
  }

}
