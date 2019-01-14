package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    Product produit1 = new Product(1, "TV 4K", 800, 500);
    Product produit2 = new Product(2, "Enceinte", 900, 600);
    Product produit3 = new Product(3, "Mobile", 450, 200);

    @MockBean
    ProductDao productDao;

    /*@Test
    public final void testListeProduits() throws Exception {
    }

    @Test
    public final void testAfficherUnProduit() {

    }

    @Test
    public final void testAjouterProduit() {

    }

    @Test
    public final void testSupprimerProduit() {

    }

    @Test
    public final void updateProduitTest() {

    }*/

    @Test
    public void calculerMargeProduitTest() throws Exception {
        List<Product> listeProduits = new ArrayList<>();
        listeProduits.add(produit1);
        listeProduits.add(produit2);

        given(productDao.findAll()).willReturn(listeProduits);
        mockMvc.perform(get("/AdminProduits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{<br />\"Product{id=1, nom='TV 4K', prix=800}\" : 300,<br />\"Product{id=2, nom='Enceinte', prix=900}\" : 300<br />}"));
    }

    /*@Test
    public final void trierProduitsParOrdreAlphabetiqueTest() {

    }

    @Test
    public final void testeDeRequetesTest() {

    }*/
}
