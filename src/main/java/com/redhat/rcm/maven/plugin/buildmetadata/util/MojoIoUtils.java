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

import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Utilities for Mojos working with IO.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class MojoIoUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  private MojoIoUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

  /**
   * Writes the DOM document to the given stream in pretty print format.
   *
   * @param document the document to write.
   * @param out the stream to write to.
   * @throws TransformerException on any problem writing to the stream.
   */
  public static void serialize(final Document document, final OutputStream out)
    throws TransformerException
  {
    serialize(document, out, true);
  }

  /**
   * Writes the DOM document to the given stream.
   *
   * @param document the document to write.
   * @param out the stream to write to.
   * @param prettyPrint the flag controls indentation. If set to
   *          <code>true</code>, indent is set to <code>2</code>.
   * @throws TransformerException on any problem writing to the stream.
   */
  public static void serialize(final Document document, final OutputStream out,
      final boolean prettyPrint) throws TransformerException
  {
    final TransformerFactory factory = TransformerFactory.newInstance();
    final Transformer serializer = factory.newTransformer();
    if (prettyPrint)
    {
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
          "2");
    }

    serializer.transform(new DOMSource(document), new StreamResult(out));
  }

  // --- object basics --------------------------------------------------------

}
