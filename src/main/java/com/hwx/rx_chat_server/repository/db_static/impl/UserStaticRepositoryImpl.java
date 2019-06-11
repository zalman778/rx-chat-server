package com.hwx.rx_chat_server.repository.db_static.impl;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat_server.repository.db_static.UserStaticRepository;
import com.hwx.rx_chat_server.service.jwt.SpringUserAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@Primary
public class UserStaticRepositoryImpl implements UserStaticRepository, UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    SpringUserAssembler springUserAssembler;

    @Override
    public UserEntity findByUsernameAndPasswordHash(String username, String passwordHash) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (username != null)
            predicates.add(cb.like(cb.lower(root.get("mail").as(String.class)),"%"+username.toLowerCase()+"%"));

        if (passwordHash != null)
            predicates.add(cb.like(cb.lower(root.get("passwordHash").as(String.class)),"%"+passwordHash.toLowerCase()+"%"));

        query.where(predicates.toArray(new Predicate[]{}));
        return entityManager.createQuery(query.select(root)).getResultList().get(0);
    }

    @Override
    public UserEntity findByUsername(String username) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
            Root<UserEntity> root = query.from(UserEntity.class);

            List<Predicate> predicates = new ArrayList<Predicate>();

            if (username != null)
                predicates.add(cb.like(cb.lower(root.get("mail").as(String.class)), "%" + username.toLowerCase() + "%"));

            query.where(predicates.toArray(new Predicate[]{}));
            return entityManager.createQuery(query.select(root)).getResultList().get(0);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getLocalizedMessage());
        }
    }

    @Override
    public UserEntity add(UserEntity userEntity) {
        return entityManager.merge(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return springUserAssembler.buildUserFromUserEntity(findByUsername(username));
    }
}
