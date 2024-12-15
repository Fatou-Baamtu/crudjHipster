package com.crudjhipster.service.mapper;

import com.crudjhipster.domain.Produit;
import com.crudjhipster.service.dto.ProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Produit} and its DTO {@link ProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProduitMapper extends EntityMapper<ProduitDTO, Produit> {}
