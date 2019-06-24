package com.hwx.rx_chat_server.repository.custom.impl;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat.common.response.UserDetailsResponse;
import com.hwx.rx_chat_server.repository.custom.UserEntityCustomRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.jwt.SpringUserAssembler;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
@Primary
public class UserEntityCustomRepositoryImpl implements UserEntityCustomRepository, UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SpringUserAssembler springUserAssembler;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;

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
    public UserDetailsResponse getUserEntityProfileInfo(String profileId) {
        Query query = entityManager.createNativeQuery(
                "select u.*\n" +
                        "    , (select max(1)\n" +
                        "       from friendship f\n" +
                        "       where (f.user_id = u.id\n" +
                        "                and f.requester_id = :userMe)\n" +
                        "          or (f.requester_id = u.id\n" +
                        "                and f.user_id = :userMe)" +
                        "          or (:userMe = :profileId)\n" +
                        "      ) bHasFriendRequest\n" +
                        "  from user u\n" +
                        " where u.id = :profileId");
        //fethching old info
        String mail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userEntityStaticRepository.findByMail(mail);

        query.setParameter("profileId", profileId);
        query.setParameter("userMe", userEntity.getId());

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        Map<String,Object> resultMap = (Map<String, Object>) query.getSingleResult();

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.setBio((String)resultMap.get("bio"));
        userDetailsResponse.setFirstname((String)resultMap.get("first_name"));
        userDetailsResponse.setId((String)resultMap.get("id"));
        userDetailsResponse.setLastname((String)resultMap.get("last_name"));
        userDetailsResponse.setImageUrl((String)resultMap.get("avatar_url"));
        userDetailsResponse.setUsername((String)resultMap.get("username"));
        userDetailsResponse.setAvailableSendFriendRequest(resultMap.get("bHasFriendRequest")==null);

        return userDetailsResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return springUserAssembler.buildUserFromUserEntity(findByUsername(username));
    }
}
