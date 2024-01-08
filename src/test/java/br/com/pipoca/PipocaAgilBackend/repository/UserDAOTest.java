package br.com.pipoca.PipocaAgilBackend.repository;

import br.com.pipoca.PipocaAgilBackend.entity.User;
import br.com.pipoca.PipocaAgilBackend.entity.validation.EntityValidationException;
import br.com.pipoca.PipocaAgilBackend.enums.UserTypeEnum;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserDAOTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserDAO userDAO;
    private final LocalDate mockDateOfBirth = LocalDate.parse("01/01/1990", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Transactional
    public void testUpdateUser() {
        User updatedUser = new User(); // Crie um usuário atualizado para teste

        userDAO.updateUser(updatedUser);
        verify(entityManager, times(1)).merge(updatedUser);
    }

    @Test
    @Transactional
    public void testCreateUser() throws EntityValidationException {
        User user = new User("John Doe", "john@example.com", "P!assword123", this.mockDateOfBirth, UserTypeEnum.REGISTERED);
        user.setId(1);

        // Simula o método persist do EntityManager
        doNothing().when(entityManager).persist(any(User.class));
        doAnswer(invocation -> null).when(entityManager).flush();

        int userId = userDAO.createUser(user);

        // Verifica se o método persist do EntityManager foi chamado uma vez com o usuário
        verify(entityManager, times(1)).persist(user);
        // Verifica se o método flush do EntityManager foi chamado uma vez
        verify(entityManager, times(1)).flush();
        // Verifica se o ID retornado é o mesmo do usuário simulado
        assertEquals(1, userId);
    }
}
