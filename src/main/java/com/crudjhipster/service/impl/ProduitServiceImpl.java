package com.crudjhipster.service.impl;

import com.crudjhipster.domain.criteria.ProduitCriteria;
import com.crudjhipster.repository.ProduitRepository;
import com.crudjhipster.service.ProduitService;
import com.crudjhipster.service.dto.ProduitDTO;
import com.crudjhipster.service.mapper.ProduitMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.crudjhipster.domain.Produit}.
 */
@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(ProduitServiceImpl.class);

    private final ProduitRepository produitRepository;

    private final ProduitMapper produitMapper;

    public ProduitServiceImpl(ProduitRepository produitRepository, ProduitMapper produitMapper) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
    }

    @Override
    public Mono<ProduitDTO> save(ProduitDTO produitDTO) {
        LOG.debug("Request to save Produit : {}", produitDTO);
        return produitRepository.save(produitMapper.toEntity(produitDTO)).map(produitMapper::toDto);
    }

    @Override
    public Mono<ProduitDTO> update(ProduitDTO produitDTO) {
        LOG.debug("Request to update Produit : {}", produitDTO);
        return produitRepository.save(produitMapper.toEntity(produitDTO)).map(produitMapper::toDto);
    }

    @Override
    public Mono<ProduitDTO> partialUpdate(ProduitDTO produitDTO) {
        LOG.debug("Request to partially update Produit : {}", produitDTO);

        return produitRepository
            .findById(produitDTO.getId())
            .map(existingProduit -> {
                produitMapper.partialUpdate(existingProduit, produitDTO);

                return existingProduit;
            })
            .flatMap(produitRepository::save)
            .map(produitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProduitDTO> findByCriteria(ProduitCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Produits by Criteria");
        return produitRepository.findByCriteria(criteria, pageable).map(produitMapper::toDto);
    }

    /**
     * Find the count of produits by criteria.
     * @param criteria filtering criteria
     * @return the count of produits
     */
    public Mono<Long> countByCriteria(ProduitCriteria criteria) {
        LOG.debug("Request to get the count of all Produits by Criteria");
        return produitRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return produitRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProduitDTO> findOne(Long id) {
        LOG.debug("Request to get Produit : {}", id);
        return produitRepository.findById(id).map(produitMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Produit : {}", id);
        return produitRepository.deleteById(id);
    }
}
