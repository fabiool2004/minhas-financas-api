package com.sensacionalapps.minhasfinancas.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sensacionalapps.minhasfinancas.exception.ErroAutenticacaoException;
import com.sensacionalapps.minhasfinancas.exception.RegraNegocioException;
import com.sensacionalapps.minhasfinancas.model.entity.Usuario;
import com.sensacionalapps.minhasfinancas.model.repository.UsuarioRepository;
import com.sensacionalapps.minhasfinancas.service.impl.UsuarioServiceImpl;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@MockBean
	UsuarioRepository repository;

	@SpyBean
	UsuarioServiceImpl service;
	
//	@BeforeEach
//	public void setUp() {
//		///repository = Mockito.mock(UsuarioRepository.class); substituiído pela anotação @MockBean
//		//service = Mockito.spy(UsuarioServiceImpl.class); substituiído pela anotação @SpyBean
//	}
	
	@Test
	public void deveValidarEmail() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao e verificação
		Assertions.assertDoesNotThrow(() -> service.validarEmail("usuario@email.com.br"));
		
	}
	
	@Test
	public void deveLancarExcecaoAoValidarEmail() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		//acao e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("usuario@email.com.br"));

	}
	
	@Test
	public void deveAutenticarUsuarioComSucesso () {
		//cenario
		String email = "usuario@email.com.br";
		String senha = "senha";
		Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//acao e verificação
		Assertions.assertDoesNotThrow(() -> service.autenticar(email, senha));
	}		
	
	@Test
	public void deveLancarExcecaoNaoExisteUsuarioAoAutenticarUsuario () {
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao e verificação
		assertThatThrownBy(() -> service.autenticar("usuario@email.com.br", "senha")).hasMessage("Usuário não encontrado");
	}		

	@Test
	public void deveLancarExcecaoSenhaInvalidaAoAutenticarUsuario () {
		//cenario
		String email = "usuario@email.com.br";
		String senha = "senha";
		Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//acao e verificação
		assertThatThrownBy(() -> service.autenticar(email, "senhaDiferente")).hasMessage("Senha inválida");
	}
	
	@Test
	public void deveSalvarUsuario () {
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString()); //Mockcando a execução do método validarEmail
		Usuario usuario = Usuario.builder().id(1L).email("usuario@email.com.br").senha("senha").nome("nome").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario); //Mockando a execução do mátodo save
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertNotNull(usuarioSalvo);
		Assertions.assertEquals(usuarioSalvo.getId(), 1L);
		Assertions.assertEquals(usuarioSalvo.getNome(), "nome");
		Assertions.assertEquals(usuarioSalvo.getEmail(), "usuario@email.com.br");
		Assertions.assertEquals(usuarioSalvo.getSenha(), "senha");
	}
	
	@Test
	public void naodeveSalvarUsuarioComEmailJaCadastrado () {
		//cenario
		Usuario usuario = Usuario.builder().id(1L).email("usuario@email.com.br").build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail("usuario@email.com.br"); //Mockando a execução do método validarEmail lançando uma exception
		
		//acao
		Assertions.assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}

}
