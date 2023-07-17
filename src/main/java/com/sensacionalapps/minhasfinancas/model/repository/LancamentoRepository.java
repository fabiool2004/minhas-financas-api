package com.sensacionalapps.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sensacionalapps.minhasfinancas.model.entity.Lancamento;
import com.sensacionalapps.minhasfinancas.model.entity.TipoLancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{
	
	@Query(value = "select sum(l.valor)"
			+ "      from Lancamento l"
			+ "      join l.usuario u"
			+ "     where u.id = :idParam "
			+ "       and l.tipoLancamento = :tipoParam"
			+ "     group by u" )
	BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idParam") Long idUsuario, @Param("tipoParam") TipoLancamento tipoLancamento);

}