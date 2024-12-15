package com.crudjhipster.web.rest;

import com.crudjhipster.domain.criteria.ProduitCriteria;
import com.crudjhipster.repository.ProduitRepository;
import com.crudjhipster.service.ProduitService;
import com.crudjhipster.service.dto.ProduitDTO;
import com.crudjhipster.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.crudjhipster.domain.Produit}.
 */
@RestController
@RequestMapping("/api/produits")
public class ProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProduitResource.class);

    private static final String ENTITY_NAME = "produit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProduitService produitService;

    private final ProduitRepository produitRepository;

    public ProduitResource(ProduitService produitService, ProduitRepository produitRepository) {
        this.produitService = produitService;
        this.produitRepository = produitRepository;
    }

    /**
     * {@code POST  /produits} : Create a new produit.
     *
     * @param produitDTO the produitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new produitDTO, or with status {@code 400 (Bad Request)} if the produit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ProduitDTO>> createProduit(@Valid @RequestBody ProduitDTO produitDTO) throws URISyntaxException {
        LOG.debug("REST request to save Produit : {}", produitDTO);
        if (produitDTO.getId() != null) {
            throw new BadRequestAlertException("A new produit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return produitService
            .save(produitDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/produits/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /produits/:id} : Updates an existing produit.
     *
     * @param id the id of the produitDTO to save.
     * @param produitDTO the produitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produitDTO,
     * or with status {@code 400 (Bad Request)} if the produitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the produitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProduitDTO>> updateProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProduitDTO produitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Produit : {}, {}", id, produitDTO);
        if (produitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return produitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return produitService
                    .update(produitDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /produits/:id} : Partial updates given fields of an existing produit, field will ignore if it is null
     *
     * @param id the id of the produitDTO to save.
     * @param produitDTO the produitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produitDTO,
     * or with status {@code 400 (Bad Request)} if the produitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the produitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the produitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProduitDTO>> partialUpdateProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProduitDTO produitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Produit partially : {}, {}", id, produitDTO);
        if (produitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return produitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProduitDTO> result = produitService.partialUpdate(produitDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /produits} : get all the produits.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of produits in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProduitDTO>>> getAllProduits(
        ProduitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Produits by criteria: {}", criteria);
        return produitService
            .countByCriteria(criteria)
            .zipWith(produitService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /produits/count} : count all the produits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countProduits(ProduitCriteria criteria) {
        LOG.debug("REST request to count Produits by criteria: {}", criteria);
        return produitService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /produits/:id} : get the "id" produit.
     *
     * @param id the id of the produitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the produitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProduitDTO>> getProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Produit : {}", id);
        Mono<ProduitDTO> produitDTO = produitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(produitDTO);
    }

    /**
     * {@code DELETE  /produits/:id} : delete the "id" produit.
     *
     * @param id the id of the produitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Produit : {}", id);
        return produitService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
