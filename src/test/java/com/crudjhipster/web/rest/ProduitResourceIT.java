package com.crudjhipster.web.rest;

import static com.crudjhipster.domain.ProduitAsserts.*;
import static com.crudjhipster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.crudjhipster.IntegrationTest;
import com.crudjhipster.domain.Produit;
import com.crudjhipster.repository.EntityManager;
import com.crudjhipster.repository.ProduitRepository;
import com.crudjhipster.service.dto.ProduitDTO;
import com.crudjhipster.service.mapper.ProduitMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProduitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProduitResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRIX = new BigDecimal("100.00");  // Valeur de prix par défaut
    private static final BigDecimal UPDATED_PRIX = new BigDecimal("200.00");  // Valeur de prix mise à jour


    private static final Integer DEFAULT_QUANTITE = 1;
    private static final Integer UPDATED_QUANTITE = 2;
    private static final Integer SMALLER_QUANTITE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ProduitMapper produitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Produit produit;

    private Produit insertedProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createEntity() {
        return new Produit().nom(DEFAULT_NOM).prix(DEFAULT_PRIX).quantite(DEFAULT_QUANTITE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createUpdatedEntity() {
        return new Produit().nom(UPDATED_NOM).prix(UPDATED_PRIX).quantite(UPDATED_QUANTITE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Produit.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        produit = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProduit != null) {
            produitRepository.delete(insertedProduit).block();
            insertedProduit = null;
        }
        deleteEntities(em);
    }
    @Test
    void createProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);
        var returnedProduitDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProduitDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Produit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduit = produitMapper.toEntity(returnedProduitDTO);
        assertProduitUpdatableFieldsEquals(returnedProduit, getPersistedProduit(returnedProduit));

        insertedProduit = returnedProduit;
    }

    @Test
    void createProduitWithExistingId() throws Exception {
        // Create the Produit with an existing ID
        produit.setId(1L);
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkPrixIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setPrix(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkQuantiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setQuantite(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProduits() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(produit.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].prix")
            .value(hasItem(DEFAULT_PRIX))
            .jsonPath("$.[*].quantite")
            .value(hasItem(DEFAULT_QUANTITE));
    }

    @Test
    void getProduit() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get the produit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(produit.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.prix")
            .value(is(DEFAULT_PRIX))
            .jsonPath("$.quantite")
            .value(is(DEFAULT_QUANTITE));
    }

    @Test
    void getProduitsByIdFiltering() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        Long id = produit.getId();

        defaultProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllProduitsByNomIsEqualToSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where nom equals to
        defaultProduitFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    void getAllProduitsByNomIsInShouldWork() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where nom in
        defaultProduitFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    void getAllProduitsByNomIsNullOrNotNull() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where nom is not null
        defaultProduitFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    void getAllProduitsByNomContainsSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where nom contains
        defaultProduitFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    void getAllProduitsByNomNotContainsSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where nom does not contain
        defaultProduitFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    void getAllProduitsByPrixIsEqualToSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where prix equals to
        defaultProduitFiltering("prix.equals=" + DEFAULT_PRIX, "prix.equals=" + UPDATED_PRIX);
    }

    @Test
    void getAllProduitsByPrixIsInShouldWork() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where prix in
        defaultProduitFiltering("prix.in=" + DEFAULT_PRIX + "," + UPDATED_PRIX, "prix.in=" + UPDATED_PRIX);
    }

    @Test
    void getAllProduitsByPrixIsNullOrNotNull() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where prix is not null
        defaultProduitFiltering("prix.specified=true", "prix.specified=false");
    }

    @Test
    void getAllProduitsByPrixContainsSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where prix contains
        defaultProduitFiltering("prix.contains=" + DEFAULT_PRIX, "prix.contains=" + UPDATED_PRIX);
    }

    @Test
    void getAllProduitsByPrixNotContainsSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where prix does not contain
        defaultProduitFiltering("prix.doesNotContain=" + UPDATED_PRIX, "prix.doesNotContain=" + DEFAULT_PRIX);
    }

    @Test
    void getAllProduitsByQuantiteIsEqualToSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where quantite equals to
        defaultProduitFiltering("quantite.equals=" + DEFAULT_QUANTITE, "quantite.equals=" + UPDATED_QUANTITE);
    }

    @Test
    void getAllProduitsByQuantiteIsInShouldWork() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where quantite in
        defaultProduitFiltering("quantite.in=" + DEFAULT_QUANTITE + "," + UPDATED_QUANTITE, "quantite.in=" + UPDATED_QUANTITE);
    }

    @Test
    void getAllProduitsByQuantiteIsNullOrNotNull() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where quantite is not null
        defaultProduitFiltering("quantite.specified=true", "quantite.specified=false");
    }

    @Test
    void getAllProduitsByQuantiteIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where quantite is greater than or equal to
        defaultProduitFiltering("quantite.greaterThanOrEqual=" + DEFAULT_QUANTITE, "quantite.greaterThanOrEqual=" + UPDATED_QUANTITE);
    }

    @Test
    void getAllProduitsByQuantiteIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where quantite is less than or equal to
        defaultProduitFiltering("quantite.lessThanOrEqual=" + DEFAULT_QUANTITE, "quantite.lessThanOrEqual=" + SMALLER_QUANTITE);
    }

    @Test
    void getAllProduitsByQuantiteIsLessThanSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where quantite is less than
        defaultProduitFiltering("quantite.lessThan=" + UPDATED_QUANTITE, "quantite.lessThan=" + DEFAULT_QUANTITE);
    }

    @Test
    void getAllProduitsByQuantiteIsGreaterThanSomething() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        // Get all the produitList where quantite is greater than
        defaultProduitFiltering("quantite.greaterThan=" + SMALLER_QUANTITE, "quantite.greaterThan=" + DEFAULT_QUANTITE);
    }

    private void defaultProduitFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultProduitShouldBeFound(shouldBeFound);
        defaultProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProduitShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(produit.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].prix")
            .value(hasItem(DEFAULT_PRIX))
            .jsonPath("$.[*].quantite")
            .value(hasItem(DEFAULT_QUANTITE));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProduitShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingProduit() {
        // Get the produit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProduit() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit
        Produit updatedProduit = produitRepository.findById(produit.getId()).block();
        updatedProduit.nom(UPDATED_NOM).prix(UPDATED_PRIX).quantite(UPDATED_QUANTITE);
        ProduitDTO produitDTO = produitMapper.toDto(updatedProduit);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, produitDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProduitToMatchAllProperties(updatedProduit);
    }

    @Test
    void putNonExistingProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, produitDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit.nom(UPDATED_NOM).quantite(UPDATED_QUANTITE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduitUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProduit, produit), getPersistedProduit(produit));
    }

    @Test
    void fullUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit.nom(UPDATED_NOM).prix(UPDATED_PRIX).quantite(UPDATED_QUANTITE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduitUpdatableFieldsEquals(partialUpdatedProduit, getPersistedProduit(partialUpdatedProduit));
    }

    @Test
    void patchNonExistingProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, produitDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(produitDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProduit() {
        // Initialize the database
        insertedProduit = produitRepository.save(produit).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the produit
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return produitRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Produit getPersistedProduit(Produit produit) {
        return produitRepository.findById(produit.getId()).block();
    }

    protected void assertPersistedProduitToMatchAllProperties(Produit expectedProduit) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProduitAllPropertiesEquals(expectedProduit, getPersistedProduit(expectedProduit));
        assertProduitUpdatableFieldsEquals(expectedProduit, getPersistedProduit(expectedProduit));
    }

    protected void assertPersistedProduitToMatchUpdatableProperties(Produit expectedProduit) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProduitAllUpdatablePropertiesEquals(expectedProduit, getPersistedProduit(expectedProduit));
        assertProduitUpdatableFieldsEquals(expectedProduit, getPersistedProduit(expectedProduit));
    }
}
