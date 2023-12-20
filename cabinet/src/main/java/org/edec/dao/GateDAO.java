package org.edec.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class GateDAO {
    private static final Logger log = Logger.getAnonymousLogger();
    private static final ThreadLocal<Session> session = new ThreadLocal<Session>();
    private static final SessionFactory sessionFactory = new Configuration().configure("gate.cfg.xml").buildSessionFactory();

    public GateDAO () {
    }

    public static Session getSession () {
        Session session = GateDAO.session.get();
        if (session == null) {
            session = sessionFactory.openSession();
            GateDAO.session.set(session);
        }
        return session;
    }

    protected void begin () {
        getSession().beginTransaction();
    }

    protected void commit () {
        getSession().getTransaction().commit();
    }

    protected void rollback () {
        try {
            getSession().getTransaction().rollback();
        } catch (HibernateException e) {
            log.log(Level.WARNING, "Невозможно откатить", e);
        } finally {
            Session session = GateDAO.session.get();
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        GateDAO.session.set(null);
    }

    public static void close () {
        Session session = GateDAO.session.get();
        if (session != null && session.isOpen()) {
            session.close();
            GateDAO.session.set(null);
        }
    }
}
