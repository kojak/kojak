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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.LocaleUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.site.decoration.Body;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.StringUtils;

import com.redhat.rcm.maven.plugin.buildmetadata.util.LoggingUtils;
import com.redhat.rcm.maven.plugin.buildmetadata.util.ReportUtils;

/**
 * The abstract base implementation for reports.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public abstract class AbstractReportMojo extends AbstractMavenReport
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ... plugin infrastructure ................................................

  /**
   * The Maven project.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   * @since 1.0
   */
  protected MavenProject project;

  /**
   * The Doxia site renderer.
   *
   * @component
   * @required
   * @readonly
   * @since 1.0
   */
  protected Renderer siteRenderer;

  /**
   * Local Repository.
   *
   * @parameter expression="${localRepository}"
   * @required
   * @readonly
   * @since 1.0
   */
  protected ArtifactRepository localRepository;

  /**
   * The resolver for resolving artifacts.
   *
   * @component
   * @required
   * @readonly
   * @since 1.0
   */
  protected ArtifactResolver resolver;

  /**
   * The factory to create dependent artifacts.
   *
   * @component
   * @required
   * @readonly
   * @since 1.0
   */
  protected ArtifactFactory factory;

  // ... report configuration parameters ......................................

  /**
   * Specifies the directory where the report will written to. This information
   * is only used if the report is not part of the site generation process.
   *
   * @parameter expression="${project.reporting.outputDirectory}"
   * @readonly
   * @since 1.0
   */
  protected File outputDirectory;

  /**
   * Specifies the log level <code>buildmetadata.logLevel</code> used for this
   * plugin.
   * <p>
   * Allowed values are <code>SEVERE</code>, <code>WARNING</code>,
   * <code>INFO</code> and <code>FINEST</code>.
   * </p>
   *
   * @parameter expression="${buildmetadata.logLevel}"
   * @since 1.0
   */
  protected String logLevel;

  /**
   * The locale to use regardless of the report. This should be set to the
   * locale the Javadoc comment is written in. If not set, the Maven provided
   * locale is used.
   *
   * @parameter expression="${buildmetadata.locale}"
   * @since 1.0
   */
  protected String locale;

  /**
   * A simple flag to skip the generation of the reports. If set on the command
   * line use <code>-Dbuildmetadata.skip</code>.
   *
   * @parameter expression="${buildmetadata.skip}" default-value="false"
   * @since 1.0
   */
  protected boolean skip;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // ... plugin infrastructure ................................................

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
   */
  @Override
  protected final MavenProject getProject()
  {
    return project;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
   */
  @Override
  protected final Renderer getSiteRenderer()
  {
    return siteRenderer;
  }

  // ... report configuration parameters ......................................

  // ... basics

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
   */
  @Override
  protected final String getOutputDirectory()
  {
    return outputDirectory.getAbsolutePath();
  }

  // --- business -------------------------------------------------------------

  // CHECKSTYLE:OFF
  /**
   * Runs the report generation.
   *
   * @throws MojoExecutionException on any problem encountered.
   */
  public void execute() throws MojoExecutionException // CHECKSTYLE:ON
  {
    final Log log = getLog();
    if (!canGenerateReport())
    {
      if (log.isInfoEnabled())
      {
        log.info("Report '" + getName(Locale.getDefault())
                 + "' skipped due to offline mode.");
      }
      return;
    }

    LoggingUtils.configureLogger(log, logLevel);

    provideSink();
  }

  // CHECKSTYLE:OFF
  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
   */
  @Override
  public boolean canGenerateReport() // CHECKSTYLE:ON
  {
    return super.canGenerateReport() && !skip;
  }

  // CHECKSTYLE:OFF
  /**
   * {@inheritDoc}
   * <p>
   * Configures the plugin logger.
   * </p>
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
   */
  @Override
  protected void executeReport(final Locale locale) throws MavenReportException // CHECKSTYLE:ON
  {
    final Log log = getLog();
    LoggingUtils.configureLogger(log, logLevel);
  }

  /**
   * Ensures that a writeable sink is provided.
   * <p>
   * Stolen from the changes plugin.
   * </p>
   *
   * @throws MojoExecutionException if the sink cannot be created.
   */
  protected final void provideSink() throws MojoExecutionException
  {
    final Locale reportLocale = determineLocale();

    try
    {
      final DecorationModel model = new DecorationModel();
      model.setBody(new Body());
      final Map<String, String> attributes = new HashMap<String, String>();
      attributes.put("outputEncoding", "UTF-8"); // TODO correct???
      final SiteRenderingContext siteContext =
          siteRenderer.createContextForSkin(ReportUtils.getSkinArtifactFile(
              project, localRepository, resolver, factory), attributes, model,
              getName(reportLocale), reportLocale);

      final RenderingContext context =
          new RenderingContext(outputDirectory, getOutputName() + ".html");

      final SiteRendererSink sink = new SiteRendererSink(context);
      generate(sink, reportLocale);

      provideDir();

      // The writer will be closed by the renderer
      // http://maven.apache.org/doxia/doxia-sitetools/doxia-site-renderer/xref/index.html
      final Writer writer =
          new FileWriter(new File(outputDirectory, getOutputName() + ".html"));
      siteRenderer.generateDocument(writer, sink, siteContext);

      siteRenderer.copyResources(siteContext, new File(project.getBasedir(),
          "src/site/resources"), outputDirectory);
    }
    catch (final RendererException e)
    {
      throw new MojoExecutionException(createErrorMessage(reportLocale), e);
    }
    catch (final IOException e)
    {
      throw new MojoExecutionException(createErrorMessage(reportLocale), e);
    }
    catch (final MavenReportException e)
    {
      throw new MojoExecutionException(createErrorMessage(reportLocale), e);
    }
  }

  private void provideDir() throws IOException
  {
    if (!outputDirectory.exists())
    {
      if (!outputDirectory.mkdirs()) // NOPMD
      {
        throw new IOException("Cannot generate directories '"
                              + outputDirectory.getPath() + "'");
      }
    }
  }

  /**
   * Creates an error message signaling a problem with the report generation.
   *
   * @param reportLocale the locale to select the report name.
   * @return the error message for failed report generation.
   */
  private String createErrorMessage(final Locale reportLocale)
  {
    return "An error has occurred in " + getName(reportLocale)
           + " report generation.";
  }

  /**
   * Determines the locale to use. The plugin allows the user to override the
   * locale provided by Maven.
   *
   * @return the locale to use for this report.
   */
  private Locale determineLocale()
  {
    return StringUtils.isNotBlank(this.locale) ? LocaleUtils
        .toLocale(this.locale) : Locale.getDefault();
  }

  /**
   * Returns the resource bundle for the given locale.
   *
   * @param locale the locale for which the resource bundle is requested.
   * @return the bundle for the given locale.
   */
  protected final ResourceBundle getBundle(final Locale locale)
  {
    return ResourceBundle.getBundle(
        "com.redhat.rcm.maven.buildmetadata.BuildReport", locale);
  }

  // --- object basics --------------------------------------------------------

}
