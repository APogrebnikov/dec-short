package org.edec.main.auth;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.util.HashMap;

/**

 */
public class AuthLDAP {
    private HashMap<String, String> ldapGroup = new HashMap<String, String>();

    public AuthLDAP () {
        //Учебно организационный отдел ГИ
        ldapGroup.put("GI/UOO", "ou=UOO,ou=GI");
        //Учебно организационный отдел ИКИТ
        ldapGroup.put("IKIT/UOO", "ou=UOO,ou=IKIT");
        //НУЛ САУ ИКИТ (для разработчика)
        ldapGroup.put("IKIT/TEST", "ou=NULSAU,ou=KI,ou=IKIT");
        //ИКИТ кафедра прикладной математики и компьютерной безопасности (Сомова)
        ldapGroup.put("IKIT/KPMKB", "ou=KPMKB,ou=IKIT");
        //ИКИТ Кафедра систем автоматики, автоматизированного управления и проектирования (Кузьмина)
        ldapGroup.put("IKIT/KSAAUP", "ou=KSAAUP,ou=IKIT");
        //Деканат факультета искуствоведения и культурологии
        ldapGroup.put("GI/DECANAT", "ou=DEKANAT,ou=FIK,ou=GI");
        //ТЕСТ
        ldapGroup.put("IKIT/KSII", "ou=KSII,ou=IKIT");
    }

    public String getCN (String login, String pass) throws NamingException {
        SearchResult searchResult = validAndGetUser(login, pass);
        if (searchResult != null) {
            Attributes attrs = searchResult.getAttributes();
            if (attrs.get("cn") != null) {
                return attrs.get("cn").get().toString();
            }
            return null;
        }
        return null;
    }

    public SearchResult validAndGetUser (String login, String pass) {
        return LDAPvalid.getOneStaffAuth(login, pass);
    }
}
