package com.getinfo.gestaocontratual.controller;


import com.getinfo.gestaocontratual.controller.dto.CreateUsuarioRequest;
import com.getinfo.gestaocontratual.controller.dto.LoginRequest;
import com.getinfo.gestaocontratual.controller.dto.LoginResponse;
import com.getinfo.gestaocontratual.entities.Role;
import com.getinfo.gestaocontratual.entities.Usuario;
import com.getinfo.gestaocontratual.repository.RoleRepository;
import com.getinfo.gestaocontratual.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class UsuarioController {

    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RoleRepository roleRepository;

    public UsuarioController(JwtEncoder jwtEncoder, UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

        var usuario = usuarioRepository.findByUsuario(loginRequest.usuario());

        if(usuario.isEmpty() || !usuario.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)){
             throw new BadCredentialsException("Usuário ou Senha inválidos");
        }

        var now = Instant.now();
        var expiresIn = 3600L;

        var scopes = usuario.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("gestaocontratos")
                .subject(usuario.get().getUserId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(now)
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse("Usuário Criado com Sucesso!", jwtValue, expiresIn));

    }

    @Transactional
    @PostMapping("/createUsuario")
    public ResponseEntity<String> createUsuario(@RequestBody CreateUsuarioRequest dto){
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        var userFromDb = usuarioRepository.findByUsuario(dto.usuario());

        if(userFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Usuário já existe!");
        }

        var usuario = new Usuario();
        usuario.setUsuario(dto.usuario());
        usuario.setSenha(bCryptPasswordEncoder.encode(dto.senha()));
        usuario.setRoles(Set.of(basicRole));

        usuarioRepository.save(usuario);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário criado com sucesso: " + usuario.getUsuario());
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<Usuario>> usuarios(){
        var usuarios = usuarioRepository.findAll();

        return ResponseEntity.ok(usuarios);

    }
}
