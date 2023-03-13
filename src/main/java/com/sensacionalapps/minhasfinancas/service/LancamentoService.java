package com.sensacionalapps.minhasfinancas.service;

import java.util.List;

import com.sensacionalapps.minhasfinancas.model.entity.Lancamento;
import com.sensacionalapps.minhasfinancas.model.entity.StatusLancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);

	Lancamento atualizar(Lancamento lancamento);

	void deletar(Lancamento lancamento);

	List<Lancamento> buscar (Lancamento lancamento);

	void atualizarStatus (Lancamento lancamento, StatusLancamento status);

	void validar(Lancamento lancamento);
	
}
