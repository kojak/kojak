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
package com.redhat.rcm.maven.plugin.buildmetadata;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.common.Constant;
import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmControl;
import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmCredentials;
import com.redhat.rcm.maven.plugin.buildmetadata.common.ScmInfo;
import com.redhat.rcm.maven.plugin.buildmetadata.data.HostMetaDataProvider;
import com.redhat.rcm.maven.plugin.buildmetadata.data.MavenMetaDataProvider;
import com.redhat.rcm.maven.plugin.buildmetadata.data.MavenMetaDataSelection;
import com.redhat.rcm.maven.plugin.buildmetadata.data.ScmMetaDataProvider;
import com.redhat.rcm.maven.plugin.buildmetadata.io.BuildPropertiesFileHelper;
import com.redhat.rcm.maven.plugin.buildmetadata.io.BuildXmlFileHelper;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.ScmNoRevisionException;
import com.redhat.rcm.maven.plugin.buildmetadata.util.FilePathNormalizer;
import com.redhat.rcm.maven.plugin.buildmetadata.util.LoggingUtils;

/**
 * Provides the build properties. This information is also written to a
 * <code>build.properties</code> file.
 *
 * @goal provide-buildmetadata
 * @phase initialize
 * @requiresProject
 * @threadSafe
 * @since 1.0
 * @description Provides a build meta data to the build process.
 */
public final class BuildMetaDataMojo extends AbstractBuildMojo // NOPMD
{ // NOPMD
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ... Mojo infrastructure ..................................................

  /**
   * The user's settings.
   *
   * @parameter expression="${settings}"
   * @required
   * @readonly
   * @since 1.0
   */
  private Settings settings;

  /**
   * If set to <code>true</code>, build properties will be generate even if they
   * already exist in the target folder.
   *
   * @parameter default-value="false"
   * @since 1.0
   */
  private boolean forceNewProperties;

  /**
   * In offline mode the plugin will not generate revision information.
   *
   * @parameter default-value="${settings.offline}"
   * @required
   * @readonly
   * @since 1.0
   */
  private boolean offline;

  /**
   * Add SCM information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in SCM information, set this
   * to <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code>
   * property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addScmInfo}" default-value="true"
   * @since 1.0
   */
  private boolean addScmInfo;

  /**
   * Fail if revision is requested to be retrieved, access to SCM is provided,
   * system is online, nothing should prevent the build from fetching the
   * information.
   * <p>
   * If set to <code>true</code> the build will fail, if revision cannot be
   * fetched under the conditions mentioned above. If set to <code>false</code>
   * the build will continue silently so that the meta data do not contain the
   * revision.
   * </p>
   *
   * @parameter expression="${buildMetaData.failOnMissingRevision}"
   *            default-value="false"
   * @since 1.0
   */
  private boolean failOnMissingRevision;

  /**
   * Add host information if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in host information (e.g. for
   * security reasons), set this to <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code>
   * property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addHostInfo}" default-value="true"
   * @since 1.0
   */
  private boolean addHostInfo;

  /**
   * Add environment variables if set to <code>true</code>, skip it, if set to
   * <code>false</code>. If you are not interested in the environment variables
   * of the host (e.g. for security reasons), set this to <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code>
   * property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addEnvInfo}" default-value="false"
   * @since 1.0
   */
  private boolean addEnvInfo;

  /**
   * Add information about the Java runtime running the build if set to
   * <code>true</code>, skip it, if set to <code>false</code>.
   *
   * @parameter expression="${buildMetaData.addJavaRuntimeInfo}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addJavaRuntimeInfo;

  /**
   * Add information about the operating system the build is run in if set to
   * <code>true</code>, skip it, if set to <code>false</code>.
   *
   * @parameter expression="${buildMetaData.addOsInfo}" default-value="true"
   * @since 1.0
   */
  private boolean addOsInfo;

  /**
   * Add Maven execution information (all properties starting with
   * <code>build.maven.execution</code>, like command line, goals, profiles,
   * etc.) if set to <code>true</code>, skip it, if set to <code>false</code>.
   * If you are not interested in execution information, set this to
   * <code>false</code>.
   * <p>
   * For security reasons you may want to remove the properties file from the
   * META-INF folder. Please refer to <code>propertiesOutputFile</code>
   * property.
   * </p>
   *
   * @parameter expression="${buildMetaData.addMavenExecutionInfo}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addMavenExecutionInfo;

  /**
   * Add project information (homepage URL, categories, tags, etc.) if set to
   * <code>true</code>, skip it, if set to <code>false</code>. If you are not
   * interested in execution information, set this to <code>false</code>.
   *
   * @parameter expression="${buildMetaData.addProjectInfo}"
   *            default-value="false"
   * @since 1.1
   */
  private boolean addProjectInfo;

  /**
   * While the command line may be useful to refer to for a couple of reasons,
   * displaying it with the build properties is a security issue. Some plugins
   * allow to read passwords as properties from the command line and this
   * sensible data will be shown.
   * <p>
   * Therefore the command line is hidden by default (<code>true</code>). If you
   * want to include this information, use a value of <code>false</code>.
   * </p>
   *
   * @parameter expression="${buildMetaData.hideCommandLineInfo}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean hideCommandLineInfo;

  /**
   * While the <code>MAVEN_OPTS</code> may be useful to refer to for a couple of
   * reasons, displaying them with the build properties is a security issue.
   * Some plugins allow to read passwords as properties from the command line
   * and this sensible data will be shown.
   * <p>
   * Therefore the <code>MAVEN_OPTS</code> are hidden by default (
   * <code>true</code>). If you want to include this information, use a value of
   * <code>false</code>.
   * </p>
   * <p>
   * This exclusion does not prevent the property from being written as part of
   * <code>addEnvInfo</code>!
   * </p>
   *
   * @parameter expression="${buildMetaData.hideMavenOptsInfo}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean hideMavenOptsInfo;

  /**
   * While the <code>JAVA_OPTS</code> may be useful to refer to for a couple of
   * reasons, displaying them with the build properties is a security issue.
   * Some plugins allow to read passwords as properties from the command line
   * and this sensible data will be shown.
   * <p>
   * Therefore the <code>JAVA_OPTS</code> are hidden by default (
   * <code>true</code>). If you want to include this information, use a value of
   * <code>false</code>.
   * </p>
   * <p>
   * This exclusion does not prevent the property from being written as part of
   * <code>addEnvInfo</code>!
   * </p>
   *
   * @parameter expression="${buildMetaData.hideJavaOptsInfo}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean hideJavaOptsInfo;

  /**
   * A simple flag to skip the generation of the build information. If set on
   * the command line use <code>-DbuildMetaData.skip</code>.
   *
   * @parameter expression="${buildMetaData.skip}" default-value="false"
   * @since 1.0
   */
  private boolean skip;

  /**
   * If it should be checked if the local files are up-to-date with the remote
   * files in the SCM repository. If the value is <code>true</code> the result
   * of the check, including the list of changed files, is added to the build
   * meta data.
   *
   * @parameter expression="${buildMetaData.validateCheckout}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean validateCheckout;

  /**
   * Specifies the log level used for this plugin.
   * <p>
   * Allowed values are <code>SEVERE</code>, <code>WARNING</code>,
   * <code>INFO</code> and <code>FINEST</code>.
   * </p>
   *
   * @parameter expression="${buildMetaData.logLevel}"
   * @since 1.0
   */
  private String logLevel;

  /**
   * The manager instance to access the SCM system. Provides access to the
   * repository and the provider information.
   *
   * @component
   * @since 1.0
   */
  private ScmManager scmManager;

  /**
   * Allows the user to choose which scm connection to use when connecting to
   * the scm. Can either be "connection" or "developerConnection".
   *
   * @parameter default-value="connection"
   * @required
   * @since 1.0
   */
  private String connectionType;

  // ... core information .....................................................

  /**
   * The date pattern to use to format the build and revision dates. Please
   * refer to the <a href =
   * "http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html"
   * >SimpleDateFormat</a> class for valid patterns.
   *
   * @parameter expression="${buildMetaData.buildDate.pattern}"
   *            default-value="dd.MM.yyyy"
   * @since 1.0
   */
  protected String buildDatePattern = Constant.DEFAULT_DATE_PATTERN; // NOPMD

  /**
   * The property to query for the build user.
   *
   * @parameter default-value="username"
   * @since 1.0
   */
  private String buildUserPropertyName;

  // ... build information related ............................................

  /**
   * Flag to add the build date to the full version separated by a '-'. If
   * <code>true</code> the build date is added, if <code>false</code> it is not.
   *
   * @parameter expression="${buildMetaData.addBuildDateToFullVersion}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addBuildDateToFullVersion;

  // ... svn related ..........................................................

  /**
   * Used to specify the date format of the log entries that are retrieved from
   * your SCM system.
   *
   * @parameter expression="${changelog.dateFormat}"
   *            default-value="yyyy-MM-dd HH:mm:ss"
   * @required
   * @since 1.0
   */
  private String scmDateFormat;

  /**
   * Input dir. Directory where the files under SCM control are located.
   *
   * @parameter expression="${basedir}"
   * @required
   * @since 1.0
   */
  private File basedir;

  /**
   * The user name (used by svn and starteam protocol).
   *
   * @parameter expression="${username}"
   * @since 1.0
   */
  private String userName;

  /**
   * The user password (used by svn and starteam protocol).
   *
   * @parameter expression="${password}"
   * @since 1.0
   */
  private String password;

  /**
   * The private key (used by java svn).
   *
   * @parameter expression="${privateKey}"
   * @since 1.0
   */
  private String privateKey;

  /**
   * The passphrase (used by java svn).
   *
   * @parameter expression="${passphrase}"
   * @since 1.0
   */
  private String passphrase;

  /**
   * The url of tags base directory (used by svn protocol).
   *
   * @parameter expression="${tagBase}"
   * @since 1.0
   */
  private String tagBase;

  /**
   * Flag to add the revision number to the full version separated by an 'r'. If
   * <code>true</code> the revision number is added, if <code>false</code> it is
   * not.
   *
   * @parameter expression="${buildMetaData.addReleaseNumberToFullVersion}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addReleaseNumberToFullVersion;

  /**
   * Flag to add the tag <code>-locally-modified</code> to the full version
   * string to make visible that this artifact has been created with locally
   * modified sources. This is often the case while the artifact is built while
   * still working on an issue before it is committed to the SCM repository.
   *
   * @parameter expression="${buildMetaData.addLocallyModifiedTagToFullVersion}"
   *            default-value="true"
   * @since 1.0
   */
  private boolean addLocallyModifiedTagToFullVersion;

  /**
   * The range of the query in days to fetch change log entries from the SCM. If
   * no change logs have been found, the range is incremented up to {@value
   * com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo;#
   * DEFAULT_RETRY_COUNT} (5) times. If no change log has been found after these
   * {@value com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.ScmAccessInfo;#
   * DEFAULT_RETRY_COUNT} (5) additional queries, the revision number will not
   * be set with a valid value.
   *
   * @parameter expression="${buildMetaData.queryRangeInDays}"
   *            default-value="30"
   * @since 1.0
   */
  private int queryRangeInDays;

  /**
   * Flag to fail if local modifications have been found. The value is
   * <code>true</code> if the build should fail if there are modifications (any
   * files not in-sync with the remote repository), <code>false</code> if the
   * fact is only to be noted in the build properties.
   *
   * @parameter default-value="false"
   * @since 1.0
   */
  private boolean failOnLocalModifications;

  /**
   * The flag to ignore files and directories starting with a dot for checking
   * modified files. This implicates that any files or directories, starting
   * with a dot, are ignored when the check on changed files is run. If the
   * value is <code>true</code>, dot files are ignored, if it is set to
   * <code>false</code>, dot files are respected.
   *
   * @parameter default-value="true"
   * @since 1.0
   */
  private boolean ignoreDotFilesInBaseDir;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  public void execute() throws MojoExecutionException, MojoFailureException
  {
    if (!skip)
    {
      super.execute();

      final String baseDir = project.getBasedir().getAbsolutePath();
      final FilePathNormalizer filePathNormalizer =
          new FilePathNormalizer(baseDir);
      final BuildPropertiesFileHelper helper =
          new BuildPropertiesFileHelper(getLog(), propertiesOutputFile,
              filePathNormalizer);
      final Properties projectProperties = helper.getProjectProperties(project);
      if (!isBuildPropertiesAlreadySet(projectProperties))
      {
        LoggingUtils.configureLogger(getLog(), logLevel);
        final Properties buildMetaDataProperties = new Properties();
        if (isBuildPropertiesToBeRebuild())
        {
          createBuildProperties(helper, projectProperties,
              buildMetaDataProperties);
        }
        else
        {
          getLog().info("Reusing previously built metadata file.");
          helper.readBuildPropertiesFile(buildMetaDataProperties);
        }

        updateMavenEnvironment(buildMetaDataProperties, helper);
      }
    }
    else
    {
      getLog().info("Skipping buildmetadata collection since skip=true.");
    }
  }

  private void createBuildProperties(final BuildPropertiesFileHelper helper,
      final Properties projectProperties,
      final Properties buildMetaDataProperties) throws MojoExecutionException,
    MojoFailureException
  {
    final Date buildDate = session.getStartTime();

    provideBuildUser(projectProperties, buildMetaDataProperties);
    provideMavenMetaData(buildMetaDataProperties);
    provideHostMetaData(buildMetaDataProperties);
    final ScmInfo scmInfo = provideScmMetaData(buildMetaDataProperties);
    provideBuildDateMetaData(buildMetaDataProperties, buildDate);

    // The custom providers are required to be run at the end.
    // This allows these providers to access the information generated
    // by the built-in providers.
    provideBuildMetaData(buildMetaDataProperties, scmInfo, providers,
        false);

    writeBuildMetaData(helper, buildMetaDataProperties);
  }

  private void writeBuildMetaData(final BuildPropertiesFileHelper helper,
      final Properties buildMetaDataProperties) throws MojoExecutionException
  {
    helper.writePropertiesFile(buildMetaDataProperties);
    if (createXmlReport)
    {
      final String projectRootPath = project.getBasedir().getAbsolutePath();
      final BuildXmlFileHelper xmlHelper =
          new BuildXmlFileHelper(projectRootPath, getLog(), xmlOutputFile,
              properties);
      xmlHelper.writeXmlFile(buildMetaDataProperties);
    }
  }

  private void provideMavenMetaData(final Properties buildMetaDataProperties)
  {
    final MavenMetaDataSelection selection = new MavenMetaDataSelection();
    selection.setAddMavenExecutionInfo(addMavenExecutionInfo);
    selection.setAddEnvInfo(addEnvInfo);
    selection.setAddJavaRuntimeInfo(addJavaRuntimeInfo);
    selection.setAddOsInfo(addOsInfo);
    selection.setAddProjectInfo(addProjectInfo);
    selection.setHideCommandLineInfo(hideCommandLineInfo);
    selection.setHideJavaOptsInfo(hideJavaOptsInfo);
    selection.setHideMavenOptsInfo(hideMavenOptsInfo);
    selection.setSelectedSystemProperties(properties);

    final MavenMetaDataProvider mavenMetaDataProvider =
        new MavenMetaDataProvider(project, session, runtime, selection);
    mavenMetaDataProvider.provideBuildMetaData(buildMetaDataProperties);
  }

  private ScmInfo provideScmMetaData(final Properties buildMetaDataProperties)
    throws MojoFailureException
  {
    try
    {
      final ScmInfo scmInfo = createScmInfo();
      final ScmMetaDataProvider scmMetaDataProvider =
          new ScmMetaDataProvider(project, scmInfo);
      scmMetaDataProvider.provideBuildMetaData(buildMetaDataProperties);
      return scmInfo;
    }
    catch (final ScmNoRevisionException e)
    {
      throw new MojoFailureException(e.getMessage()); // NOPMD
    }
  }

  private void provideHostMetaData(final Properties buildMetaDataProperties)
    throws MojoExecutionException
  {
    if (addHostInfo)
    {
      final HostMetaDataProvider hostMetaData = new HostMetaDataProvider();
      hostMetaData.provideBuildMetaData(buildMetaDataProperties);
    }
  }

  private void provideBuildDateMetaData(
      final Properties buildMetaDataProperties, final Date buildDate)
  {
    final String buildDateString =
        createBuildDate(buildMetaDataProperties, buildDate);
    createYears(buildMetaDataProperties, buildDate);
    createBuildVersion(buildMetaDataProperties, buildDate, buildDateString);
  }

  private ScmInfo createScmInfo()
  {
    final ScmCredentials scmCredentials =
        new ScmCredentials(settings, userName, password, privateKey, passphrase);
    final ScmControl scmControl =
        new ScmControl(failOnLocalModifications, ignoreDotFilesInBaseDir,
            offline, addScmInfo, validateCheckout, failOnMissingRevision);
    final ScmInfo scmInfo =
        new ScmInfo(scmManager, connectionType, scmDateFormat, basedir,
            scmCredentials, tagBase, queryRangeInDays, buildDatePattern,
            scmControl);
    return scmInfo;
  }

  private boolean isBuildPropertiesToBeRebuild()
  {
    return forceNewProperties || !propertiesOutputFile.exists();
  }

  private boolean isBuildPropertiesAlreadySet(final Properties projectProperties)
  {
    return projectProperties.getProperty(Constant.PROP_NAME_FULL_VERSION) != null;
  }

  /**
   * Provides the name of the user running the build. The value is either
   * specified in the project properties or is taken from the Java system
   * properties (<code>user.name</code>).
   *
   * @param projectProperties the project properties.
   * @param buildMetaDataProperties the build meta data properties.
   */
  private void provideBuildUser(final Properties projectProperties,
      final Properties buildMetaDataProperties)
  {
    String userNameValue = System.getProperty("user.name");
    if ((buildUserPropertyName != null))
    {
      final String value = projectProperties.getProperty(buildUserPropertyName);
      if (!StringUtils.isBlank(value))
      {
        userNameValue = value;
      }
    }

    if (userNameValue != null)
    {
      buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_USER,
          userNameValue);
    }
  }

  /**
   * Creates and adds the build date information.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param buildDate the date of the build.
   * @return the formatted build date.
   */
  private String createBuildDate(final Properties buildMetaDataProperties,
      final Date buildDate)
  {
    final DateFormat format =
        new SimpleDateFormat(buildDatePattern, Locale.ENGLISH);
    final String buildDateString = format.format(buildDate);
    final String timestamp = String.valueOf(buildDate.getTime());

    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_DATE,
        buildDateString);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_DATE_PATTERN,
        this.buildDatePattern);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_TIMESTAMP,
        timestamp);

    return buildDateString;
  }

  /**
   * Adds the build and copyright year information.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param buildDate the build date to create the build year information.
   */
  private void createYears(final Properties buildMetaDataProperties,
      final Date buildDate)
  {
    final DateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    final String buildYearString = yearFormat.format(buildDate);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_YEAR,
        buildYearString);
    final String inceptionYearString = project.getInceptionYear();
    final String copyrightYearString =
        (buildYearString.equals(inceptionYearString) ? inceptionYearString
            : inceptionYearString + '-' + buildYearString);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_COPYRIGHT_YEAR,
        copyrightYearString);
  }

  /**
   * Adds the version information of the artifact.
   *
   * @param buildMetaDataProperties the build meta data properties.
   * @param buildDate the date of the build.
   * @param buildDateString the formatted date string.
   */
  private void createBuildVersion(final Properties buildMetaDataProperties,
      final Date buildDate, final String buildDateString)
  {
    final String version = project.getVersion();
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_VERSION, version);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_GROUP_ID,
        project.getGroupId());
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_ARTIFACT_ID,
        project.getArtifactId());
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_BUILD_DATE,
        buildDateString);

    final String fullVersion =
        createFullVersion(buildMetaDataProperties, buildDate);
    buildMetaDataProperties.setProperty(Constant.PROP_NAME_FULL_VERSION,
        fullVersion);
  }

  /**
   * Creates the full version string which may include the date, the build, and
   * the revision.
   *
   * @param buildMetaDataProperties the generated build meta data properties.
   * @param buildDate the date of the current build.
   * @return the full version string.
   */
  private String createFullVersion(final Properties buildMetaDataProperties,
      final Date buildDate)
  {
    final String version = project.getVersion();

    final DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    final String datePart = format.format(buildDate);
    final String revisionId =
        buildMetaDataProperties.getProperty(Constant.PROP_NAME_SCM_REVISION_ID);

    final String versionPrefix, versionSuffix;
    if (version.endsWith("-SNAPSHOT"))
    {
      versionPrefix = version.substring(0, version.lastIndexOf('-'));
      versionSuffix = "-SNAPSHOT";
    }
    else
    {
      versionPrefix = version;
      versionSuffix = "";
    }

    final String modified;
    if (addLocallyModifiedTagToFullVersion
        && "true".equals(buildMetaDataProperties
            .getProperty(Constant.PROP_NAME_SCM_LOCALLY_MODIFIED)))
    {
      modified = "-locally-modified";
    }
    else
    {
      modified = "";
    }

    final String fullVersion =
        versionPrefix
            + (addBuildDateToFullVersion ? '-' + datePart : "")
            + (addReleaseNumberToFullVersion
               && StringUtils.isNotBlank(revisionId) ? "r" + revisionId : "")
            + modified + versionSuffix;

    return fullVersion;
  }

  // --- object basics --------------------------------------------------------

}
