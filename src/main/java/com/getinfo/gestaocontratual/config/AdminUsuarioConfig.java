package com.getinfo.gestaocontratual.config;

import com.getinfo.gestaocontratual.entities.*;
import com.getinfo.gestaocontratual.repository.RoleRepository;
import com.getinfo.gestaocontratual.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    public void run(String... args) throws Exception{
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        var roleBasic = roleRepository.findByName(Role.Values.BASIC.name());

        var userAdmin = usuarioRepository.findByUsuario("admin");


        if (roleAdmin == null) {
            Role role = new Role();
            role.setRoleID(1L);
            role.setName(Role.Values.ADMIN.getName());
            roleRepository.save(role);
        }

        if (roleBasic == null) {
            Role role2 = new Role();
            role2.setRoleID(2L);
            role2.setName(Role.Values.BASIC.getName());
            roleRepository.save(role2);
        }

        userAdmin.ifPresentOrElse(
                usuario -> {
                 System.out.println("admin existe");
                },
                ()-> {
                    var usuario = new Usuario();
                    usuario.setUsuario("admin");
                    usuario.setSenha(passwordEncoder.encode("admin"));
                    usuario.setRoles(Set.of(roleAdmin));
                    usuarioRepository.save(usuario);
                }

        );
    }
}
