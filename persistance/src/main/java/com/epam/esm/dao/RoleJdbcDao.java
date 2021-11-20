package com.epam.esm.dao;

import com.epam.esm.model.Role;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class RoleJdbcDao extends AbstractDao<Role> implements RoleDao {

    public RoleJdbcDao() {
        super(Role.class.getSimpleName());
    }

    @Override
    public List<Role> findPage(int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        Query<Role> query = session.createQuery("from Role", Role.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public Optional<Role> findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Role role = session.find(Role.class, id);
        return Optional.ofNullable(role);
    }

    @Override
    public Optional<Role> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query<Role> query = session.createQuery("from Role where name = ?1", Role.class);
        query.setParameter(1, name);
        return query.uniqueResultOptional();
    }
}
