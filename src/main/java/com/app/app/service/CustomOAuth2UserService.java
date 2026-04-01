package com.app.app.service;

import com.app.app.model.Usuario;
import com.app.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Extraer información del usuario de Google
        String email = oauth2User.getAttribute("email");
        String nombre = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");

        // Buscar o crear usuario
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        Usuario usuario;

        if (usuarioExistente.isPresent()) {
            usuario = usuarioExistente.get();
            // Actualizar googleId si no existe
            if (usuario.getGoogleId() == null) {
                usuario.setGoogleId(googleId);
                usuario.setProvider("GOOGLE");
                usuarioRepository.save(usuario);
            }
        } else {
            // Crear nuevo usuario
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            usuario.setGoogleId(googleId);
            usuario.setProvider("GOOGLE");
            usuario.setRol("USER");
            usuario.setActivo(true);
            usuario.setPassword(null); // No necesita contraseña para OAuth2
            usuarioRepository.save(usuario);
        }

        return oauth2User;
    }
}
