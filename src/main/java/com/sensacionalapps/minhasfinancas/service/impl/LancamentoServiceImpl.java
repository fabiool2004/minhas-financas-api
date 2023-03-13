package com.sensacionalapps.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sensacionalapps.minhasfinancas.exception.RegraNegocioException;
import com.sensacionalapps.minhasfinancas.model.entity.Lancamento;
import com.sensacionalapps.minhasfinancas.model.entity.StatusLancamento;
import com.sensacionalapps.minhasfinancas.model.repository.LancamentoRepository;
import com.sensacionalapps.minhasfinancas.service.LancamentoService;


@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	@Autowired
	LancamentoRepository lancamentoRepository;

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		lancamentoRepository.delete(lancamento);
	}

	@Override
	public List<Lancamento> buscar(Lancamento lancamento) {
		Example<Lancamento> example = Example.of(lancamento,
										ExampleMatcher.matching()
											.withIgnoreCase()
											.withStringMatcher(StringMatcher.CONTAINING));

		return lancamentoRepository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatusLancamento(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		if(lancamento.getDescricao() == null || lancamento.getDescricao().isBlank()) {
			throw new RegraNegocioException("Informe uma descrição válida.");
		}
		if(lancamento.getMes() == null || lancamento.getMes() < 1  || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um mês válido.");
		}
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um ano válido.");
		}
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um usuário válido.");
		}
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um valor válido.");
		}
		if(lancamento.getTipoLancamento() == null) {
			throw new RegraNegocioException("Informe um tipo de lançamento.");
		}
	}

}
