package org.edec.dao;

import org.hibernate.*;
import org.hibernate.cfg.AnnotationConfiguration;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DAO {
    private static final Logger log = Logger.getAnonymousLogger();
    private static final ThreadLocal<Session> session = new ThreadLocal<Session>();
    protected String lastMessage;

    @SuppressWarnings("deprecation")
    private static final SessionFactory sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();

    protected DAO () {
    }

    public static Session getSession () {
        Session session = DAO.session.get();
        if (session == null) {
            session = sessionFactory.openSession();
            DAO.session.set(session);
        }
        return session;
    }

    protected static void begin () {
        getSession().beginTransaction();
    }

    protected static void commit () {
        getSession().getTransaction().commit();
    }

    protected static void rollback () {
        try {
            getSession().getTransaction().rollback();
        } catch (HibernateException e) {
            log.log(Level.WARNING, "Невозможно откатить", e);
        } finally {
            Session session = DAO.session.get();
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        DAO.session.set(null);
    }

    public static void close () {
        Session session = DAO.session.get();
        if (session != null && session.isOpen()) {
            session.close();
        }
        DAO.session.set(null);
    }

    protected Session openSession () {
        return sessionFactory.openSession();
    }

    protected List<?> getList (Query q, Session session) {
        try {
            session.beginTransaction();
            List<?> list = q.list();
            session.getTransaction().commit();
            return list;
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    public boolean executeUpdate (Query q) {
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

    public static List<?> getStList (Query q) {
        try {
            DAO.begin();
            List<?> list = q.list();
            DAO.commit();
            return list;
        } catch (HibernateException e) {
            DAO.rollback();
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            close();
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

    public <T> T getUnique (Query q) {
        try {
            begin();
            T result = (T)q.uniqueResult();
            commit();
            return result;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return null;
        } finally {
            close();
        }
    }

    public Object create (Object object) {
        try {
            begin();
            object = getSession().save(object);
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

    public Object saveOrUpdate (Object object) {
        try {
            begin();
            getSession().saveOrUpdate(object);
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

    protected boolean callFunction (final String query) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.doWork(connection -> {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
            });
            tx.commit();
            return true;
        } catch (HibernateException e) {
            tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    protected String castListToStringArray (List list) {
        String result;
        if (list == null || list.size() == 0) {
            return "{}";
        }
        result = "{";
        for (Object obj : list) {
            result += String.valueOf(obj) + ",";
        }
        result = result.substring(0, result.length() - 1);
        result += "}";
        return result;
    }

    public String getLastMessage () {
        return lastMessage;
    }
}
