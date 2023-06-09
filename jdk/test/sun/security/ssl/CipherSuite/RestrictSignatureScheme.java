/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8226374
 * @library /javax/net/ssl/templates
 * @summary Restrict signature algorithms and named groups
 * @run main/othervm RestrictSignatureScheme
 */
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.interfaces.*;
import java.util.Arrays;
import java.util.Base64;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLException;

public class RestrictSignatureScheme extends SSLSocketTemplate {

    RestrictSignatureScheme() throws Exception {
    }

    @Override
    protected SSLContext createClientSSLContext() throws Exception {
        return getSSLContext(trustedCertStr, null, null);
    }

    @Override
    protected SSLContext createServerSSLContext() throws Exception {
        return getSSLContext(null, targetCertStr,
                                            targetPrivateKey);
    }

    // Servers are configured before clients, increment test case after.
    @Override
    protected void configureClientSocket(SSLSocket socket) {
        socket.setEnabledCipherSuites(new String[] {
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"});
    }

    @Override
    protected void configureServerSocket(SSLServerSocket serverSocket) {
        serverSocket.setEnabledCipherSuites(new String[] {
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"});
    }

    /*
     * Run the test case.
     */
    public static void main(String[] args) throws Exception {
        Security.setProperty("jdk.certpath.disabledAlgorithms",
                "MD2, RSA keySize < 1024, rsa_md5");
        Security.setProperty("jdk.tls.disabledAlgorithms",
                "SSLv3, RC4, DH keySize < 768");
            try {
                (new RestrictSignatureScheme()).run();
                throw new Exception("The test case should be disabled");
            } catch (SSLException | IllegalStateException ssle) {
                // The named group should be restricted.
            }
    }


    /*
     * Where do we find the keystores?
     */
    // Certificate information:
    // Issuer: C=US, O=Example, CN=localhost
    // Validity
    //     Not Before: May 25 00:35:58 2009 GMT
    //     Not After : May  5 00:35:58 2030 GMT
    // Subject: C=US, O=Example, CN=localhost
    // X509v3 Subject Key Identifier:
    //     56:AB:FE:15:4C:9C:4A:70:90:DC:0B:9B:EB:BE:DC:03:CC:7F:CE:CF
    // X509v3 Authority Key Identifier:
    //     keyid:56:AB:FE:15:4C:9C:4A:70:90:DC:0B:9B:EB:BE:DC:03:CC:7F:CE:CF
    //     DirName:/C=US/O=Example/CN=localhost
    //     serial:00
    static String trustedCertStr =
        "-----BEGIN CERTIFICATE-----\n" +
        "MIICejCCAeOgAwIBAgIBADANBgkqhkiG9w0BAQQFADAzMQswCQYDVQQGEwJVUzEQ\n" +
        "MA4GA1UEChMHRXhhbXBsZTESMBAGA1UEAxMJbG9jYWxob3N0MB4XDTA5MDUyNTAw\n" +
        "MDQ0M1oXDTMwMDUwNTAwMDQ0M1owMzELMAkGA1UEBhMCVVMxEDAOBgNVBAoTB0V4\n" +
        "YW1wbGUxEjAQBgNVBAMTCWxvY2FsaG9zdDCBnzANBgkqhkiG9w0BAQEFAAOBjQAw\n" +
        "gYkCgYEA0Wvh3FHYGQ3vvw59yTjUxT6QuY0fzwCGQTM9evXr/V9+pjWmaTkNDW+7\n" +
        "S/LErlWz64gOWTgcMZN162sVgx4ct/q27brY+SlUO5eSud1fSac6SfefhOPBa965\n" +
        "Xc4mnpDt5sgQPMDCuFK7Le6A+/S9J42BO2WYmNcmvcwWWrv+ehcCAwEAAaOBnTCB\n" +
        "mjAdBgNVHQ4EFgQUq3q5fYEibdvLpab+JY4pmifj2vYwWwYDVR0jBFQwUoAUq3q5\n" +
        "fYEibdvLpab+JY4pmifj2vahN6Q1MDMxCzAJBgNVBAYTAlVTMRAwDgYDVQQKEwdF\n" +
        "eGFtcGxlMRIwEAYDVQQDEwlsb2NhbGhvc3SCAQAwDwYDVR0TAQH/BAUwAwEB/zAL\n" +
        "BgNVHQ8EBAMCAgQwDQYJKoZIhvcNAQEEBQADgYEAHL8BSwtX6s8WPPG2FbQBX+K8\n" +
        "GquAyQNtgfJNm60B4i+fVBkJiQJtLmE0emvHx/3sIaHmB0Gd0HKnk/cIQXY304vr\n" +
        "QpqwudKcIZuzmj+pa7807joV+WzRDVIlt4HpYg7tiUvEoyw+X8jwY2lgiGR7mWu6\n" +
        "jQU8PN/06+qgtvSGFpo=\n" +
        "-----END CERTIFICATE-----";

    // Certificate information:
    // Issuer: C=US, O=Example, CN=localhost
    // Validity
    //     Not Before: May 25 00:35:58 2009 GMT
    //     Not After : May  5 00:35:58 2030 GMT
    // Subject: C=US, O=Example, CN=localhost
    // X509v3 Subject Key Identifier:
    //     0D:30:76:22:D6:9D:75:EF:FD:83:50:31:18:08:83:CD:01:4E:6A:C4
    // X509v3 Authority Key Identifier:
    //     keyid:56:AB:FE:15:4C:9C:4A:70:90:DC:0B:9B:EB:BE:DC:03:CC:7F:CE:CF
    //     DirName:/C=US/O=Example/CN=localhost
    //     serial:00
    static String targetCertStr =
        "-----BEGIN CERTIFICATE-----\n" +
        "MIICaTCCAdKgAwIBAgIBAjANBgkqhkiG9w0BAQQFADAzMQswCQYDVQQGEwJVUzEQ\n" +
        "MA4GA1UEChMHRXhhbXBsZTESMBAGA1UEAxMJbG9jYWxob3N0MB4XDTA5MDUyNTAw\n" +
        "MDQ0M1oXDTI5MDIwOTAwMDQ0M1owMzELMAkGA1UEBhMCVVMxEDAOBgNVBAoTB0V4\n" +
        "YW1wbGUxEjAQBgNVBAMTCWxvY2FsaG9zdDCBnzANBgkqhkiG9w0BAQEFAAOBjQAw\n" +
        "gYkCgYEAzmPahrH9LTQv3HEWsua+hIpzyU1ACooSd5BtDjc7XnVzSdGW8QD9R8EA\n" +
        "xko7TvfJo6IH6wwgHBspySwsl+6xvHhbwQjgtWlT71ksrUbqcUzmvSvcycQYA8RC\n" +
        "yk9HK5pEJQgSxldpR3Kmy0V6CHC4dCm15trnJYWisTuezY3fjXECAwEAAaOBjDCB\n" +
        "iTAdBgNVHQ4EFgQUQkiWFRkjKsfwFo7UMQfGEzNNW60wWwYDVR0jBFQwUoAUq3q5\n" +
        "fYEibdvLpab+JY4pmifj2vahN6Q1MDMxCzAJBgNVBAYTAlVTMRAwDgYDVQQKEwdF\n" +
        "eGFtcGxlMRIwEAYDVQQDEwlsb2NhbGhvc3SCAQAwCwYDVR0PBAQDAgPoMA0GCSqG\n" +
        "SIb3DQEBBAUAA4GBAIMz7c1R+6KEO7FmH4rnv9XE62xkg03ff0vKXLZMjjs0CX2z\n" +
        "ybRttuTFafHA6/JS+Wz0G83FCRVeiw2WPU6BweMwwejzzIrQ/K6mbp6w6sRFcbNa\n" +
        "eLBtzkjEtI/htOSSq3/0mbKmWn5uVJckO4QiB8kUR4F7ngM9l1uuI46ZfUsk\n" +
        "-----END CERTIFICATE-----";

    // Private key in the format of PKCS#8
    static String targetPrivateKey =
        "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAM5j2oax/S00L9xx\n" +
        "FrLmvoSKc8lNQAqKEneQbQ43O151c0nRlvEA/UfBAMZKO073yaOiB+sMIBwbKcks\n" +
        "LJfusbx4W8EI4LVpU+9ZLK1G6nFM5r0r3MnEGAPEQspPRyuaRCUIEsZXaUdypstF\n" +
        "eghwuHQpteba5yWForE7ns2N341xAgMBAAECgYEAgZ8k98OBhopoJMLBxso0jXmH\n" +
        "Dr59oiDlSEJku7DkkIajSZFggyxj5lTI78BfT1FASozQ/EY5RG2q6LXdq+41oU/U\n" +
        "JVEQWhdIE1mQDwE0vgaYdjzMaVIsC3cZYOCOmCYvNxCiTt7e/z8yBMmAE5udqJMB\n" +
        "pim4WXDfpy0ssK81oCECQQDwMC4xu+kn0yD/Qyi9Zn26gIRDv4bjzDQoJfSvMhrY\n" +
        "a4duxLzh9u4gCDd0+wHxpPQvNxGCk0c1JUxBJ2rb4G3HAkEA2/oVRV6+xiRXUnoo\n" +
        "bdPEO27zEJmdpE42yU/JLIy6DPu2IUhEqY45fU2ZERmwMdhpiK/vsf/CZKJ2j/ZU\n" +
        "PdMLBwJBAJIYTFDWAqjFpCGAASzLRZiGiW0H941h7Suqgp159ZhEN5mps1Yis47q\n" +
        "UIkoEHOiKSD69vychsiNykcrKbVaWosCQQC1UrYX4Vo1r5z/EkyjAwzcxL68rzM/\n" +
        "TW1hkU/NVg7CRvXBB3X5oY+H1t/WNauD2tRa5FMbESwmkbhTQIP+FikfAkEA4goD\n" +
        "HCxUn0Z1OQq9QL6y1Yoof6sHxicUwABosuCLJnDJmA5vhpemvdXQTzFII8g1hyQf\n" +
        "z1yyDoxhddcleKlJvQ==";

    static char passphrase[] = "passphrase".toCharArray();

    private static final String keyCertStr = trustedCertStr;

    private static final String privateKey = targetPrivateKey;

    private static String tmAlgorithm = "PKIX";

    // get the ssl context
    private static SSLContext getSSLContext(String trusedCertStr,
            String keyCertStr, String keySpecStr) throws Exception {

        // generate certificate from cert string
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // create a key store
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);

        // import the trused cert
        Certificate trusedCert = null;
        ByteArrayInputStream is = null;
        if (trusedCertStr != null) {
            is = new ByteArrayInputStream(trusedCertStr.getBytes());
            trusedCert = cf.generateCertificate(is);
            is.close();

            ks.setCertificateEntry("RSA Export Signer", trusedCert);
        }

        if (keyCertStr != null) {
            // generate the private key.
            PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(
                                Base64.getMimeDecoder().decode(keySpecStr));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKey priKey =
                    (RSAPrivateKey)kf.generatePrivate(priKeySpec);

            // generate certificate chain
            is = new ByteArrayInputStream(keyCertStr.getBytes());
            Certificate keyCert = cf.generateCertificate(is);
            is.close();

            Certificate[] chain = null;
            if (trusedCert != null) {
                chain = new Certificate[2];
                chain[0] = keyCert;
                chain[1] = trusedCert;
            } else {
                chain = new Certificate[1];
                chain[0] = keyCert;
            }

            // import the key entry.
            ks.setKeyEntry("Whatever", priKey, passphrase, chain);
        }

        // create SSL context
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmAlgorithm);
        tmf.init(ks);

        SSLContext ctx = SSLContext.getInstance("TLS");
        if (keyCertStr != null && !keyCertStr.isEmpty()) {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("NewSunX509");
            kmf.init(ks, passphrase);

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            ks = null;
        } else {
            ctx.init(null, tmf.getTrustManagers(), null);
        }

        return ctx;
    }
}
