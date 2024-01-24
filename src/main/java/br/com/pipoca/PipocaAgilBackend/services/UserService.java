package br.com.pipoca.PipocaAgilBackend.services;


import br.com.pipoca.PipocaAgilBackend.communication.Communication;
import br.com.pipoca.PipocaAgilBackend.dtos.UserLoginDTO;
import br.com.pipoca.PipocaAgilBackend.dtos.UserRegisterDTO;
import br.com.pipoca.PipocaAgilBackend.entity.User;
import br.com.pipoca.PipocaAgilBackend.entity.validation.EntityValidationException;
import br.com.pipoca.PipocaAgilBackend.enums.MailTypeEnum;
import br.com.pipoca.PipocaAgilBackend.enums.UserTypeEnum;
import br.com.pipoca.PipocaAgilBackend.exceptions.ConflictException;
import br.com.pipoca.PipocaAgilBackend.exceptions.InternalErrorException;
import br.com.pipoca.PipocaAgilBackend.exceptions.UnauthorizedException;
import br.com.pipoca.PipocaAgilBackend.providers.jwt.JwtProvider;
import br.com.pipoca.PipocaAgilBackend.repository.UserDAO;
import br.com.pipoca.PipocaAgilBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository repository;

    @Autowired
    private final UserDAO userDAO;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtProvider jwtProvider;
    @Autowired
    private final Communication communication;

    public UserService(UserRepository repository, UserDAO userDAO, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, Communication communication) {
        this.repository = repository;
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.communication = communication;
    }

    public int  createUser(UserRegisterDTO userRegisterDTO) throws ConflictException, EntityValidationException {
        if(repository.findByEmail(userRegisterDTO.email) != null ){
            throw new ConflictException("Email já cadastrado!");
        }

        String passwordEncrypted = this.passwordEncoder.encode(userRegisterDTO.password);
        User user = new User(userRegisterDTO.fullName, userRegisterDTO.email, passwordEncrypted, userRegisterDTO.dateBirth, UserTypeEnum.REGISTERED);

        try {
            this.communication.MailServiceMessage(user.getFullName(), user.getEmail(), MailTypeEnum.WELCOME, null);
        } catch (InternalErrorException e) {
            throw new RuntimeException(e);
        }
        return userDAO.createUser(user);
    }

    public String authorizeUser(UserLoginDTO userLoginDTO) throws UnauthorizedException {

        Optional<User> optionalUser = Optional.ofNullable(repository.findByEmail(userLoginDTO.email));
        User user = optionalUser.orElseThrow(() -> new UnauthorizedException("Email ou Senha inválidos."));

        if (!passwordEncoder.matches(userLoginDTO.password, user.getPassword())) {
            throw new UnauthorizedException("Email ou Senha inválidos.");
        }
        String hashJwt = jwtProvider.createToken(userLoginDTO.email);
        user.setJwt(hashJwt);

        userDAO.updateUser(user);

        return hashJwt;
    }

    public Optional<User> deleteUserById(Long id) {
        repository.findById(id).ifPresent(user -> repository.deleteById(id));
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
