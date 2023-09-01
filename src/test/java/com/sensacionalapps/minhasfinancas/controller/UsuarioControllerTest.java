package com.sensacionalapps.minhasfinancas.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensacionalapps.minhasfinancas.dto.UsuarioDTO;
import com.sensacionalapps.minhasfinancas.exception.ErroAutenticacaoException;
import com.sensacionalapps.minhasfinancas.exception.RegraNegocioException;
import com.sensacionalapps.minhasfinancas.model.entity.Usuario;
import com.sensacionalapps.minhasfinancas.service.LancamentoService;
import com.sensacionalapps.minhasfinancas.service.UsuarioService;

@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

	public static final String API = "/api/usuarios/";
	MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	UsuarioService usuarioService;
	
	@MockBean
	LancamentoService lancamentoService;

	@Test
	void deveAutenticarUmUsuario() throws Exception {
		
		String email = "usuario@mail.com";
		String senha = "password";
		
		//cenario
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		Mockito.when(usuarioService.autenticar(email, senha)).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mockMvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			;
	}
	
	@Test
	void deveFalharAoAutenticarUmUsuario() throws Exception {
		
		String email = "usuario@mail.com";
		String senha = "password";
		
		//cenario
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(usuarioService.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mockMvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			;
	}
	
	
	@Test
	void deveCriarUmUsuario() throws Exception {
		
		String email = "usuario@mail.com";
		String senha = "password";
		
		//cenario
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/salvar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mockMvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			;
	}
	
	@Test
	void deveFalharAoCriarUmUsuario() throws Exception {
		
		String email = "usuario@mail.com";
		String senha = "password";
		
		//cenario
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/salvar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mockMvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			;
	}
}
