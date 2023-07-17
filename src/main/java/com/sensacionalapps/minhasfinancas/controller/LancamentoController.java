package com.sensacionalapps.minhasfinancas.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sensacionalapps.minhasfinancas.dto.LancamentoDTO;
import com.sensacionalapps.minhasfinancas.dto.StatusDTO;
import com.sensacionalapps.minhasfinancas.exception.RegraNegocioException;
import com.sensacionalapps.minhasfinancas.mapper.LancamentoMapper;
import com.sensacionalapps.minhasfinancas.model.entity.Lancamento;
import com.sensacionalapps.minhasfinancas.model.entity.StatusLancamento;
import com.sensacionalapps.minhasfinancas.model.entity.TipoLancamento;
import com.sensacionalapps.minhasfinancas.model.entity.Usuario;
import com.sensacionalapps.minhasfinancas.service.LancamentoService;
import com.sensacionalapps.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<?> buscar (
			//Comando que recebe todos os parâmetros em um map. Sò pode ser usado caso todos os parametros sejam opcionais.
			//@RequestParam java.util.Map<String, String> params
			@RequestParam(value="descricao", required=false) String descricao,
			@RequestParam(value="mes", required=false) Integer mes,
			@RequestParam(value="ano", required=false) Integer ano,
			@RequestParam(value="tipo", required=false) String tipo,
			@RequestParam(value="usuario") Long idUsuario) {
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if(usuario.isPresent()) {
			Lancamento lancamentoFiltro = new Lancamento();
			lancamentoFiltro.setDescricao(descricao);
			lancamentoFiltro.setUsuario(usuario.get());
			lancamentoFiltro.setMes(mes);
			lancamentoFiltro.setAno(ano);
			
			if (tipo != null ) {
				lancamentoFiltro.setTipoLancamento(TipoLancamento.valueOf(tipo));
			}
			
			return ResponseEntity.ok(lancamentoService.buscar(lancamentoFiltro));
		} else {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
		}
	}

	@PostMapping
	public ResponseEntity<?> salvar (@RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamento = new LancamentoMapper(usuarioService).LancamentoFromDTO(dto);
			lancamento = lancamentoService.salvar(lancamento);
			return new ResponseEntity<Lancamento>(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> atualizar (@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return lancamentoService.obterPorId(id).map( entity -> {
			try {
				Lancamento lancamento = new LancamentoMapper(usuarioService).LancamentoFromDTO(dto);
				lancamento.setId(entity.getId());
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e){
				return ResponseEntity.badRequest().body(e.getMessage());
			}	
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST) );
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity<?> atualizarStatus(@PathVariable("id") Long id, @RequestBody StatusDTO dto) {
		return lancamentoService.obterPorId(id).map( entity -> {
				StatusLancamento statusNovo = StatusLancamento.valueOf(dto.getStatus());
				if (statusNovo == null) {
					return ResponseEntity.badRequest().body("Não foi possível atualizar status do lançamento. Envie um status válido");
				}
				try {
					entity.setStatusLancamento(statusNovo);
					lancamentoService.atualizar(entity);
					return ResponseEntity.ok(entity);
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST) );
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deletar (@PathVariable("id") Long id) {
		return lancamentoService.obterPorId(id).map(entidade -> {
			lancamentoService.deletar(entidade);
					return new ResponseEntity<Lancamento>(HttpStatus.NO_CONTENT);
				}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
	}
}
