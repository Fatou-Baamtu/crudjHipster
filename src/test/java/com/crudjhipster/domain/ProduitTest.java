package com.crudjhipster.domain;

import static com.crudjhipster.domain.ProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crudjhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Produit.class);
        Produit produit1 = getProduitSample1();
        Produit produit2 = new Produit();
        assertThat(produit1).isNotEqualTo(produit2);

        produit2.setId(produit1.getId());
        assertThat(produit1).isEqualTo(produit2);

        produit2 = getProduitSample2();
        assertThat(produit1).isNotEqualTo(produit2);
    }
}
