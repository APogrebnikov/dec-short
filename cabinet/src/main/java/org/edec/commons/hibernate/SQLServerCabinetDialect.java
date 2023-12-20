package org.edec.commons.hibernate;

import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class SQLServerCabinetDialect extends SQLServer2008Dialect {
    public SQLServerCabinetDialect() {
        super();
        registerHibernateType(Types.BIGINT, StandardBasicTypes.LONG.getName());
    }
}
