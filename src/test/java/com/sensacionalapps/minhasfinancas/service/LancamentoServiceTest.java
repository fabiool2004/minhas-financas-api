package com.sensacionalapps.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sensacionalapps.minhasfinancas.exception.RegraNegocioException;
import com.sensacionalapps.minhasfinancas.model.entity.Lancamento;
import com.sensacionalapps.minhasfinancas.model.entity.StatusLancamento;
import com.sensacionalapps.minhasfinancas.model.entity.Usuario;
import com.sensacionalapps.minhasfinancas.model.repository.LancamentoRepository;
import com.sensacionalapps.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.sensacionalapps.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LancamentoServiceTest {
	
	@MockBean
	LancamentoRepository lancamentoRepository;

	@SpyBean
	LancamentoServiceImpl lancamentoService;

	@Test
	void deveSalvarUmLancamento () {
		
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(lancamentoService).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatusLancamento(StatusLancamento.PENDENTE);
		Mockito.when(lancamentoRepository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		//execucao
		Lancamento lancamento = lancamentoService.salvar(lancamentoASalvar);
		
		//verificação
		Assertions.assertEquals(lancamentoSalvo.getId(), lancamento.getId());
		Assertions.assertEquals(StatusLancamento.PENDENTE, lancamento.getStatusLancamento());
		
	}
	
	@Test
	void naoDeveSalvarUmLancamentoQuandoFalhaValidacao () {
		
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoASalvar);
		
		//execução e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.salvar(lancamentoASalvar));
		Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoASalvar);	
		
	}
	
	@Test
	void deveAtualizarUmLancamento () {
		
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatusLancamento(StatusLancamento.PENDENTE);
		Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvo);
		Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		//execucao
		lancamentoService.atualizar(lancamentoSalvo);
		
		//verificação
		Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);
		
	}
	
	
	@Test
	void deveLancarErroAoAtualizarUmLancamentoNaoSalvo () {
		
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		//execução e verificação
		Assertions.assertThrows(NullPointerException.class, () -> lancamentoService.atualizar(lancamentoASalvar));
		Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoASalvar);	
		
	}
	
	@Test
	void deveApagarUmLancamento() {
		
		//cenário
		Lancamento lancamentoAApagar = LancamentoRepositoryTest.criarLancamento();
		lancamentoAApagar.setId(1L);
		
		//execução
		lancamentoService.deletar(lancamentoAApagar);
		
		//verificacao
		Mockito.verify(lancamentoRepository).delete(lancamentoAApagar);
		
	}
	
	@Test
	void deveLancarErroAoApagarUmLancamento() {
		
		//cenário
		Lancamento lancamentoAApagar = LancamentoRepositoryTest.criarLancamento();
		
		//execução e verificacao
		Assertions.assertThrows(NullPointerException.class, () -> lancamentoService.deletar(lancamentoAApagar));
		Mockito.verify(lancamentoRepository, Mockito.never()).delete(lancamentoAApagar);
		
	}
	
	@Test
	void deveBuscarLancamento() {
		
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		List<Lancamento> listaABuscar = Arrays.asList(lancamento);
		Mockito.when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(listaABuscar);

		//execucao
		List<Lancamento> listaBuscada = lancamentoService.buscar(lancamento);
		
		//verificacao
		Assertions.assertEquals(listaABuscar, listaBuscada);
		
	}
	
	
	@Test
	void deveAtualizarStatusLancamento() {

		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);
		
		//execução
		lancamentoService.atualizarStatus(lancamento, novoStatus);
		
		
		//verificação
		Assertions.assertEquals(lancamento.getStatusLancamento(), novoStatus);
		Mockito.verify(lancamentoService).atualizar(lancamento);
		
	}
	
	@Test
	void deveObterUmLancamentoPorId() {

		//cenário
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execução
		Optional<Lancamento> resultado = lancamentoService.obterPorId(id);
		
		//verificação
		Assertions.assertTrue(resultado.isPresent());
	}
	
	@Test
	void deveObterVazioPorId() {

		//cenário
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());
		
		//execução
		Optional<Lancamento> resultado = lancamentoService.obterPorId(id);
		
		//verificação
		Assertions.assertFalse(resultado.isPresent());
	}

	@Test
	void deveInvalidarLancamento() {
		//cenário
		Lancamento lancamento = new Lancamento();
		
		//execução e verificacao
		RegraNegocioException excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe uma descrição válida.", excecao.getMessage());

		lancamento.setDescricao("");
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe uma descrição válida.", excecao.getMessage());
		
		lancamento.setDescricao("Salário");
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um mês válido.", excecao.getMessage());

		lancamento.setMes(0);
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um mês válido.", excecao.getMessage());

		lancamento.setMes(13);
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um mês válido.", excecao.getMessage());

		lancamento.setMes(9);
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um ano válido.", excecao.getMessage());

		lancamento.setAno(23);
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um ano válido.", excecao.getMessage());

		lancamento.setAno(2023);
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um usuário válido.", excecao.getMessage());

		lancamento.setUsuario(new Usuario());
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um usuário válido.", excecao.getMessage());

		lancamento.getUsuario().setId(1L);
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um valor válido.", excecao.getMessage());

		lancamento.setValor(BigDecimal.ZERO);
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um valor válido.", excecao.getMessage());

		lancamento.setValor(new BigDecimal(12000));
		excecao = Assertions.assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
		Assertions.assertEquals("Informe um tipo de lançamento.", excecao.getMessage());

	}
}
