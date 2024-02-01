package br.com.pipoca.PipocaAgilBackend.controller;

import br.com.pipoca.PipocaAgilBackend.dtos.UserIdentityDTO;
import br.com.pipoca.PipocaAgilBackend.dtos.UserLoginDTO;
import br.com.pipoca.PipocaAgilBackend.dtos.UserRegisterDTO;
import br.com.pipoca.PipocaAgilBackend.entity.User;
import br.com.pipoca.PipocaAgilBackend.entity.validation.EntityValidationException;
import br.com.pipoca.PipocaAgilBackend.enums.UserTypeEnum;
import br.com.pipoca.PipocaAgilBackend.exceptions.BadRequestException;
import br.com.pipoca.PipocaAgilBackend.exceptions.ConflictException;
import br.com.pipoca.PipocaAgilBackend.exceptions.UnauthorizedException;
import br.com.pipoca.PipocaAgilBackend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"id\": 123, \"message\": \"Usuário criado com sucesso!\"}"
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "(Internal Validation) *"
                            )
                    )),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Email já cadastrado!"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Erro interno, tente novamente mais tarde."
                            )
                    ))
    })

    @PostMapping("/create")
    public ResponseEntity<Object> creatUser(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {

        try {
            int idOfCreatedUser = service.createUser(userRegisterDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("id", idOfCreatedUser);
            response.put("message", "Usuário criado com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno, tente novamente mais tarde.");
        }
    }

    @Operation(summary = "Authorize user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authorized",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"userType\": SUBSCRIBE, \"hash\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\"}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Email ou Senha inválidos."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Erro interno, tente novamente mais tarde."
                            )
                    )
            )
    })
    @PostMapping("/authorize")
    public ResponseEntity<Object> authorizeUser(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        try {
            Object authorizedUser = service.authorizeUser(userLoginDTO);
            return ResponseEntity.ok().body(authorizedUser);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno, tente novamente mais tarde.");
        }
    }

    @Operation(summary = "Activate Plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Activate Plan",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Plano assinante ativado com sucesso!"
                            )
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Erro ao recuperar usuário. Faça login novamente."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Erro interno, tente novamente mais tarde."
                            )
                    )
            )
    })
    @PostMapping("/activatePlan")
    public ResponseEntity<String> activatePlan(@RequestBody @Valid UserIdentityDTO identity) {
        try {
            service.activatePlan(identity.userHash);
            return ResponseEntity.ok().body("Plano assinante ativado com sucesso!");
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno, tente novamente mais tarde.");
        }
    }

    @Operation(summary = "Verify User Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verify User Type",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "REGISTERED"
                            )
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Erro ao recuperar usuário. Faça login novamente."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Erro interno, tente novamente mais tarde."
                            )
                    )
            )
    })
    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody @Valid UserIdentityDTO identity) {
        try {
            String userType = service.checkUserType(identity.userHash).toString();
            return ResponseEntity.ok().body(userType);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno, tente novamente mais tarde.");
        }
   }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        try {
          service.deleteUserById(id);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno, tente novamente mais tarde.");
        }


    }


    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = service.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}