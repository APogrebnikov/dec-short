package org.edec.commons.hibernate;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class PostgresSQLCabinetDialect extends PostgreSQL82Dialect {
    public PostgresSQLCabinetDialect() {
        super();
        registerHibernateType(Types.BIGINT, StandardBasicTypes.LONG.getName());
    }
}
