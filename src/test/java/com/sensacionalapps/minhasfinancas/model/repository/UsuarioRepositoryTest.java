package com.sensacionalapps.minhasfinancas.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.sensacionalapps.minhasfinancas.model.entity.Usuario;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarEmail() {
		//cenario
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com.br").build();
		entityManager.persist(usuario);

		//acao
		boolean existe = repository.existsByEmail("usuario@email.com.br");
		
		//verificacao
		Assertions.assertTrue(existe);

	}
	
	@Test
	public void deveVerificarEmailInexistente() {
		//cenario

		//acao
		boolean existe = repository.existsByEmail("usuario@email.com.br");
		
		//verificacao
		Assertions.assertFalse(existe);

	}
	
	@Test
	public void devePersistirUsuarioNaBase () {
		//cenario
		Usuario usuario = criarUsuario();
		
		//acao
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verificacao
		Assertions.assertNotNull(usuarioSalvo.getId());
		
	}

	@Test
	public void deveBuscarUsuarioPorEmail () {
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//acao
		Optional<Usuario> retorno = repository.findByEmail("usuario@email.com.br");
		
		//verificacao
		Assertions.assertTrue(retorno.isPresent());
		
	}
	
	@Test
	public void devBuscarUsuarioInexistentePorEmail () {
		//cenario
		
		//acao
		Optional<Usuario> retorno = repository.findByEmail("usuario@email.com.br");
		
		//verificacao
		Assertions.assertFalse(retorno.isPresent());
		
	}
	
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").email("usuario@email.com.br").senha("senha").build();
	}
}
