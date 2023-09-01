package com.sensacionalapps.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.sensacionalapps.minhasfinancas.model.entity.Lancamento;
import com.sensacionalapps.minhasfinancas.model.entity.StatusLancamento;
import com.sensacionalapps.minhasfinancas.model.entity.TipoLancamento;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository lancamentoRepository;
	
	@Autowired
	TestEntityManager testEntityManager;
	
	@Test
	void deveSalvarUmLancamento() {
		
		Lancamento lancamento = criarLancamento();
		lancamento = lancamentoRepository.save(lancamento);
				
		Assertions.assertNotNull(lancamento.getId());
		
	}
	
	@Test
	void deveApagarUmLancamento() {

		Lancamento lancamento = criarLancamento();
		testEntityManager.persist(lancamento);
				
		lancamento = testEntityManager.find(Lancamento.class, lancamento.getId());
		lancamentoRepository.delete(lancamento);
		
		Assertions.assertNull(testEntityManager.find(Lancamento.class, lancamento.getId()));
		
	}
	
	@Test
	void deveAtualizarUmLancamento() {

		Lancamento lancamento = criarLancamento();
		testEntityManager.persist(lancamento);
				
		lancamento.setAno(2022);
		lancamento.setDescricao("Teste para atualizar um lançamento");
		lancamento.setStatusLancamento(StatusLancamento.EFETIVADO);
		lancamentoRepository.save(lancamento);

		Lancamento lancamentoAtualizado = testEntityManager.find(Lancamento.class, lancamento.getId());
		Assertions.assertEquals(lancamento, lancamentoAtualizado);
		
	}
	
	@Test
	void deveBuscarUmLancamentoPorId() {

		Lancamento lancamento = criarLancamento();
		testEntityManager.persist(lancamento);
				
		Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());
		Assertions.assertTrue(lancamentoEncontrado.isPresent());
		
	}
	

	public static Lancamento criarLancamento() {
		Lancamento lancamento = Lancamento.builder()
									.ano(2023)
									.mes(7)
									.descricao("Teste para salvar um lançamento")
									.valor(BigDecimal.valueOf(150))
									.tipoLancamento(TipoLancamento.RECEITA)
									.statusLancamento(StatusLancamento.PENDENTE)
									.dataCadastro(LocalDate.now())
									.build();
		return lancamento;
	}

}
