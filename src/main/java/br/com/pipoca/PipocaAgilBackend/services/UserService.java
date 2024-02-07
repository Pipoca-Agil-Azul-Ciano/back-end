package br.com.pipoca.PipocaAgilBackend.services;


import br.com.pipoca.PipocaAgilBackend.communication.Communication;
import br.com.pipoca.PipocaAgilBackend.dtos.*;
import br.com.pipoca.PipocaAgilBackend.entity.User;
import br.com.pipoca.PipocaAgilBackend.entity.validation.EntityValidationException;
import br.com.pipoca.PipocaAgilBackend.enums.MailTypeEnum;
import br.com.pipoca.PipocaAgilBackend.enums.UserTypeEnum;
import br.com.pipoca.PipocaAgilBackend.exceptions.BadRequestException;
import br.com.pipoca.PipocaAgilBackend.exceptions.ConflictException;
import br.com.pipoca.PipocaAgilBackend.exceptions.InternalErrorException;
import br.com.pipoca.PipocaAgilBackend.exceptions.UnauthorizedException;
import br.com.pipoca.PipocaAgilBackend.providers.jwt.JwtProvider;
import br.com.pipoca.PipocaAgilBackend.providers.passwordGenerator.PasswordGenerator;
import br.com.pipoca.PipocaAgilBackend.repository.UserDAO;
import br.com.pipoca.PipocaAgilBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private final PasswordGenerator passwordGenerator;

    public UserService(UserRepository repository, UserDAO userDAO, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, Communication communication, PasswordGenerator passwordGenerator) {
        this.repository = repository;
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.communication = communication;
        this.passwordGenerator = passwordGenerator;
    }

    public int createUser(UserRegisterDTO userRegisterDTO) throws ConflictException, EntityValidationException {
        if (repository.findByEmail(userRegisterDTO.email) != null) {
            throw new ConflictException("Email já cadastrado!");
        }

        String passwordEncrypted = this.passwordEncoder.encode(userRegisterDTO.password);
        User user = new User(userRegisterDTO.fullName, userRegisterDTO.email, passwordEncrypted, userRegisterDTO.dateBirth, UserTypeEnum.REGISTERED);

        try {
            this.communication.mailServiceMessage(user.getFullName(), user.getEmail(), MailTypeEnum.WELCOME, null);
        } catch (InternalErrorException e) {
            throw new RuntimeException(e);
        }
        return userDAO.createUser(user);
    }

    public Map<String, String> authorizeUser(UserLoginDTO userLoginDTO) throws UnauthorizedException {

        Optional<User> optionalUser = Optional.ofNullable(repository.findByEmail(userLoginDTO.email));
        User user = optionalUser.orElseThrow(() -> new UnauthorizedException("Email ou Senha inválidos."));

        if (!passwordEncoder.matches(userLoginDTO.password, user.getPassword())) {
            throw new UnauthorizedException("Email ou Senha inválidos.");
        }
        String hashJwt = jwtProvider.createToken(userLoginDTO.email);
        user.setJwt(hashJwt);

        userDAO.updateUser(user);
        Map<String, String> result = new HashMap<>();
        result.put("hash", hashJwt);
        result.put("userType", user.getUserTypeEnum().toString());
        return result;
    }

    public void activateSubscription(String userHash) throws BadRequestException {
        Optional<User> optionalUser = Optional.ofNullable(repository.findByJwt(userHash));
        User user = optionalUser.orElseThrow(() -> new BadRequestException("Erro ao recuperar usuário. Faça login novamente."));


        if (user.getUserTypeEnum() != UserTypeEnum.ADMIN) {
            user.setUserTypeEnum(UserTypeEnum.SUBSCRIBER);
        }
        userDAO.updateUser(user);
    }

    public void disableSubscription(String userHash) throws BadRequestException {
        Optional<User> optionalUser = Optional.ofNullable(repository.findByJwt(userHash));
        User user = optionalUser.orElseThrow(() -> new BadRequestException("Erro ao recuperar usuário. Faça login novamente."));


        if (user.getUserTypeEnum() != UserTypeEnum.ADMIN) {
            user.setUserTypeEnum(UserTypeEnum.REGISTERED);
        }
        userDAO.updateUser(user);
    }

    public void recoveryPassword(String userEmail) throws BadRequestException {
        Optional<User> optionalUser = Optional.ofNullable(repository.findByEmail(userEmail));
        User user = optionalUser.orElseThrow(() -> new BadRequestException("Email incorreto."));

        String newPassword = passwordGenerator.generate();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userDAO.updateUser(user);
        try {
            this.communication.mailServiceMessage(user.getFullName(), user.getEmail(), MailTypeEnum.RECOVERYPASSWORD, newPassword);
        } catch (InternalErrorException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDTO updateUser(String userHash, UpdateUserDTO updateDTO) throws BadRequestException {
        Optional<User> optionalUser = Optional.ofNullable(repository.findByJwt(userHash));
        User user = optionalUser.orElseThrow(() -> new BadRequestException("Erro ao recuperar usuário. Faça login novamente."));

        if (updateDTO.password != null && !updateDTO.password.isEmpty()) {
            if (passwordEncoder.matches(updateDTO.password, user.getPassword())) {
                throw new BadRequestException("Nova senha não deve ser igual a senha anterior.");
            }
            String hashedPassword = passwordEncoder.encode(updateDTO.password);
            user.setPassword(hashedPassword);
        }

        user.setFullName(updateDTO.fullName != null && !updateDTO.fullName.isEmpty() ? updateDTO.fullName : user.getFullName());
        user.setEmail(updateDTO.email != null && !updateDTO.email.isEmpty() ? updateDTO.email : user.getEmail());
        user.setDateBirth(updateDTO.dateBirth != null ? updateDTO.dateBirth : user.getDateBirth());
        user.setUpdatedAt(LocalDate.now());

        userDAO.updateUser(user);

        return new UserDTO(user.getFullName(), user.getEmail(), user.getDateBirth(), user.getUserTypeEnum());
    }

    public UserDTO userInfos(String userHash) throws BadRequestException {
        Optional<User> optionalUser = Optional.ofNullable(repository.findByJwt(userHash));
        User user = optionalUser.orElseThrow(() -> new BadRequestException("Erro ao recuperar usuário. Faça login novamente."));

        return new UserDTO(user.getFullName(), user.getEmail(), user.getDateBirth(), user.getUserTypeEnum());

    }

    public Optional<User> deleteUserById(Long id) {
        repository.findById(id).ifPresent(user -> repository.deleteById(id));
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
