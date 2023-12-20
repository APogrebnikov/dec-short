package org.edec.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class MineDAO {
    private static final Logger log = Logger.getAnonymousLogger();
    private static final ThreadLocal<Session> session = new ThreadLocal<Session>();
    private static final SessionFactory sessionFactory = new Configuration().configure("mine.cfg.xml").buildSessionFactory();

    protected MineDAO () {
    }

    public static Session getSession () {
        Session session = MineDAO.session.get();
        if (session == null || !session.isOpen() || !session.isConnected()) {
            session = sessionFactory.openSession();
            MineDAO.session.set(session);
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
            Session session = MineDAO.session.get();
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        MineDAO.session.set(null);
    }

    public static void close () {
        Session session = MineDAO.session.get();
        if (session != null && session.isOpen()) {
            session.close();
            MineDAO.session.set(null);
        }
    }

    public List<?> getList (Query q) {
        try {
            begin();
            List<?> list = q.list();
            commit();
            return list;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            close();
        }
    }

    public boolean saveOrUpdate (Object object) {
        try {
            begin();
            getSession().saveOrUpdate(object);
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public boolean execute (Query q) {
        try {
            begin();
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public Object create (Object object) {
        try {
            begin();
            getSession().save(object);
            commit();
            return object;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    public void delete (Object object) {
        try {
            begin();
            getSession().delete(object);
            commit();
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public Object update (Object object) {
        try {
            begin();
            getSession().update(object);
            commit();
            return object;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }
}
