package br.com.pipoca.PipocaAgilBackend.repository;

import br.com.pipoca.PipocaAgilBackend.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDAO {
    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void updateUser(User updatedUser) {

        entityManager.merge(updatedUser);
    }
    @Transactional
    public int createUser(User user) {
        entityManager.persist(user);
        entityManager.flush();

        return user.getId();
    }
}
