package org.edec.main.auth;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class LDAPvalid {

    private static List<String> LDAP_SERVERS = Arrays.asList(
            "auth.sfu-kras.ru",
            "10.2.3.100",
            "10.3.3.72"
    );

    private static final String LDAP_BIND_DN = "cn=ikitProxyUser,ou=web,ou=services,o=sfu";
    private static final String LDAP_BIND_PASSWORD = "eu8queeG7Loh";
    private static final String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String AUTHENTICATION = "simple";
    private static final String REFERRAL = "follow";

    public LDAPvalid() {
    }

    static SearchResult getOneStaffAuth(String userName, String userPassword) {

        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);

        env.put(Context.SECURITY_AUTHENTICATION, AUTHENTICATION);
        env.put(Context.SECURITY_PRINCIPAL, LDAP_BIND_DN);
        env.put(Context.SECURITY_CREDENTIALS, LDAP_BIND_PASSWORD);
        env.put(Context.REFERRAL, REFERRAL);
        String selectedServer = "???";

        DirContext ctx = null;

        for (String server : LDAP_SERVERS) {

            try {
                env.put(Context.PROVIDER_URL, "ldap://" + server);
                ctx = new InitialDirContext(env);
                selectedServer = server;
                break;
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }

        if (ctx == null) {
            System.err.println("Ldap: all server off");
            return null;
        }

        System.out.println("ldap: connect complite from " + selectedServer);
        NamingEnumeration results = null;

        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(2);
            controls.setCountLimit(1L);
            String searchString = "(&(objectClass=inetOrgPerson)(CN=" + userName + "))";
            results = ctx.search("", searchString, controls);
            if (results.hasMore()) {
                SearchResult result = (SearchResult) results.next();
                String dn = result.getNameInNamespace();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                System.out.println("("+dateFormat.format(date)+") Auth user: " + dn);
                env.put("java.naming.security.principal", dn);
                env.put("java.naming.security.credentials", userPassword);
                new InitialDirContext(env);
                return result;
            }
        } catch (AuthenticationException var37) {
            System.out.println("Autentification error: " + var37);
            return null;
        } catch (NameNotFoundException var38) {
            System.out.println("The base context was not found.");
            return null;
        } catch (SizeLimitExceededException var39) {
            throw new RuntimeException("LDAP Query Limit Exceeded, adjust the query to bring back less records", var39);
        } catch (NamingException var40) {
            throw new RuntimeException(var40);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception ignored) {
                }
            }

            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignored) {
                }
            }

        }

        return null;
    }
}
