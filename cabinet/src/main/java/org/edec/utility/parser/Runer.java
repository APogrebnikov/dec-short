package org.edec.utility.parser;

import org.apache.commons.lang3.StringUtils;
import org.edec.dao.DAO;
import org.edec.main.auth.LDAPvalid;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.List;

public class Runer extends DAO {

    public static void main(String[] args) throws NamingException {
        /*
        SearchResult sr = LDAPvalid.getOneStaff("APogrebnikov");

        Attributes attrs = sr.getAttributes();
        if (attrs.get("cn") != null) {
            System.out.println( attrs.get("cn").get().toString() + "<<");
        }
        */
 /*
        ClusterXML clusterXML = new ClusterXML();

        clusterXML.getClusterInfo("C://temp//motiv//main2.xls",
                "C://temp//motiv//2clust.xls",
                "C://temp//motiv//main2mod.xls",
                5,
                6,
                42);

        clusterXML.getClusterInfo("C://temp//motiv//main2mod.xls",
                "C://temp//motiv//3clust.xls",
                "C://temp//motiv//main2mod.xls",
                5,
                6,
                43);

        clusterXML.getClusterInfo("C://temp//motiv//main2mod.xls",
                "C://temp//motiv//logit.xls",
                "C://temp//motiv//main2mod.xls",
                0,
                1,
                44);
                */

        ParentXML parentXML = new ParentXML();
        parentXML.getParentInfo("C://temp//parent2.xls", "C://temp//parent2_user.xls");


        /*

        List<Long> list = new ArrayList<>();
        list.add((long) 5364);
        list.add((long) 3890);

        String listOfReg = StringUtils.join(list, ',');

        String sql = "SELECT re.id_register AS id FROM \n" +
                "register re \n" +
                "WHERE id_register IN (:list)";

        Session session = getSession();
        Query query =  session.createSQLQuery(sql);
        query.setParameterList("list", list);
        List<Long> res = query.list();
        System.out.println(res);
        */
    }
}
