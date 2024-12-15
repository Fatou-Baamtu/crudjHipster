package com.crudjhipster.service;

import com.crudjhipster.domain.criteria.ProduitCriteria;
import com.crudjhipster.service.dto.ProduitDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.crudjhipster.domain.Produit}.
 */
public interface ProduitService {
    /**
     * Save a produit.
     *
     * @param produitDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProduitDTO> save(ProduitDTO produitDTO);

    /**
     * Updates a produit.
     *
     * @param produitDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProduitDTO> update(ProduitDTO produitDTO);

    /**
     * Partially updates a produit.
     *
     * @param produitDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProduitDTO> partialUpdate(ProduitDTO produitDTO);
    /**
     * Find produits by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProduitDTO> findByCriteria(ProduitCriteria criteria, Pageable pageable);

    /**
     * Find the count of produits by criteria.
     * @param criteria filtering criteria
     * @return the count of produits
     */
    public Mono<Long> countByCriteria(ProduitCriteria criteria);

    /**
     * Returns the number of produits available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" produit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProduitDTO> findOne(Long id);

    /**
     * Delete the "id" produit.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
