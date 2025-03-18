package com.getinfo.gestaocontratual.config;

import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.RoleRepository;
import com.getinfo.gestaocontratual.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

@Configuration
public class AdminUsuarioConfig  implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UsuarioRepository usuarioRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUsuarioConfig(RoleRepository roleRepository, UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Role roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        if (roleAdmin == null) {
            roleAdmin = new Role();
            roleAdmin.setRoleID(1L);
            roleAdmin.setName(Role.Values.ADMIN.name());
            roleAdmin = roleRepository.save(roleAdmin);
        }

        Role roleBasic = roleRepository.findByName(Role.Values.BASIC.name());
        if (roleBasic == null) {
            roleBasic = new Role();
            roleBasic.setRoleID(2L);
            roleBasic.setName(Role.Values.BASIC.name());
            roleBasic = roleRepository.save(roleBasic);
        }

        Optional<Usuario> userAdmin = usuarioRepository.findByUsuario("admin");

        if (userAdmin.isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsuario("admin");
            usuario.setSenha(passwordEncoder.encode("admin"));
            usuario.setRoles(Set.of(roleAdmin));
            usuarioRepository.save(usuario);
            System.out.println("Usuário admin criado com sucesso!");
        } else {
            System.out.println("Usuário admin já existe.");
        }
    }
}
