package com.sensacionalapps.minhasfinancas.mapper;

import com.sensacionalapps.minhasfinancas.dto.LancamentoDTO;
import com.sensacionalapps.minhasfinancas.exception.RegraNegocioException;
import com.sensacionalapps.minhasfinancas.model.entity.Lancamento;
import com.sensacionalapps.minhasfinancas.model.entity.StatusLancamento;
import com.sensacionalapps.minhasfinancas.model.entity.TipoLancamento;
import com.sensacionalapps.minhasfinancas.model.entity.Usuario;
import com.sensacionalapps.minhasfinancas.service.UsuarioService;

public class LancamentoMapper {
	
	private final UsuarioService usuarioService;
	
	public LancamentoMapper (UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	
	public Lancamento LancamentoFromDTO (LancamentoDTO dto) {

		Usuario usuario = usuarioService
				.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado."));

		Lancamento lancamento = Lancamento.builder()
									.id(dto.getId())
									.descricao(dto.getDescricao())
									.ano(dto.getAno())
									.mes(dto.getMes())
									.valor(dto.getValor())
									.usuario(usuario)
									.build();

		if (dto.getTipo() != null) {
			lancamento.setTipoLancamento(TipoLancamento.valueOf(dto.getTipo()));
		}

		if (dto.getStatus() != null) {
			lancamento.setStatusLancamento(StatusLancamento.valueOf(dto.getStatus()));
		}
			
		return lancamento;
		
	}

}
