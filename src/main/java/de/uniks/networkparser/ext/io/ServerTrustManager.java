/**
 * Copyright 2003-2005 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.uniks.networkparser.ext.io;

import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.X509TrustManager;

/**
 * Trust manager that checks all certificates presented by the server. This class is used during TLS
 * negotiation. It is possible to disable/enable some or all checkings by configuring the
 * {@link ConnectionConfiguration}. The truststore file that contains knows and trusted CA root
 * certificates can also be configure in {@link ConnectionConfiguration}.
 * 
 * @author Gaston Dombiak
 */
class ServerTrustManager implements X509TrustManager {

  private static Pattern cnPattern = Pattern.compile("(?i)(cn=)([^,]*)");

  /**
   * Holds the domain of the remote server we are trying to connect
   * 
   * @return a X509Certificate Certifacte
   */
  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[0];
  }

  public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

  public void checkServerTrusted(X509Certificate[] x509Certificates, String arg1) throws CertificateException {}

  /**
   * Returns the identity of the remote server as defined in the specified certificate. The identity
   * is defined in the subjectDN of the certificate and it can also be defined in the subjectAltName
   * extension of type "xmpp". When the extension is being used then the identity defined in the
   * extension in going to be returned. Otherwise, the value stored in the subjectDN is returned.
   *
   * @param x509Certificate the certificate the holds the identity of the remote server.
   * @return the identity of the remote server as defined in the specified certificate.
   */
  public static List<String> getPeerIdentity(X509Certificate x509Certificate) {
    /* Look the identity in the subjectAltName extension if available */
    List<String> names = getSubjectAlternativeNames(x509Certificate);
    if (names.isEmpty() && x509Certificate != null) {
      String name = x509Certificate.getSubjectDN().getName();
      Matcher matcher = cnPattern.matcher(name);
      if (matcher.find()) {
        name = matcher.group(2);
      }
      /* Create an array with the unique identity */
      names = new ArrayList<String>();
      names.add(name);
    }
    return names;
  }

  /**
   * Returns the JID representation of an XMPP entity contained as a SubjectAltName extension in the
   * certificate. If none was found then return <code>null</code>.
   *
   * @param certificate the certificate presented by the remote entity.
   * @return the JID representation of an XMPP entity contained as a SubjectAltName extension in the
   *         certificate. If none was found then return <code>null</code>.
   **/
  private static List<String> getSubjectAlternativeNames(X509Certificate certificate) {
    List<String> identities = new ArrayList<String>();
    if (certificate == null) {
      return identities;
    }
    try {
      Collection<List<?>> altNames = certificate.getSubjectAlternativeNames();
      /* Check that the certificate includes the SubjectAltName extension */
      if (altNames == null) {
        return Collections.emptyList();
      }
    } catch (CertificateParsingException e) {
      e.printStackTrace();
    }
    return identities;
  }
}
