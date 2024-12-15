package com.crudjhipster.domain;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Produit getProduitSample1() {
        return new Produit()
            .id(1L)
            .nom("nom1")
            .prix(new BigDecimal("199.99"))
            .quantite(1);
    }

    public static Produit getProduitSample2() {
        return new Produit()
            .id(2L)
            .nom("nom2")
            .prix(new BigDecimal("299.99"))
            .quantite(2);
    }

    public static Produit getProduitRandomSampleGenerator() {
        return new Produit()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .prix(new BigDecimal(random.nextInt(1000) + ".99"))
            .quantite(intCount.incrementAndGet());
    }
}
