package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.springframework.ui.Model;

import javax.validation.Valid;
import java.net.URI;
import java.util.Iterator;
import java.util.List;


@Api( description="API pour es opérations CRUD sur les produits.")

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;


    /*@RequestMapping("/")
    public String index(Model model) {
        return "/index";
    }*/


    //Récupérer la liste des produits
    @ApiOperation(value = "Récupère la liste des produits et retourne un objet de type MappingJacksonValue.")
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)

    public MappingJacksonValue listeProduits() {

        Iterable<Product> produits = productDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }


    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")

    public Product afficherUnProduit(@PathVariable int id) {

        Product produit = productDao.findById(id);

        if(produit == null)
            throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");

        return produit;
    }


    //Ajouter un produit
    @ApiOperation(value = "Ajoute un produit au catalogue de produits à condition que le produit soit différent de null et que son prix soit supérieur à 0.")
    @PostMapping(value = "/Produits")

    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {

        if (product.getPrix() == 0)
            throw new ProduitGratuitException("Le produit " + product.getNom() + " ne peut pas être vendu à 0,00€ ! Rien est gratuit dans la vie... ");

        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    //Supprimer un produit (par son id)
    @ApiOperation(value = "Supprime un produit du catalogue de produits grâce à son ID.")
    @DeleteMapping (value = "/Produits/{id}")

    public void supprimerProduit(@PathVariable int id) {

        productDao.delete(id);
    }

    //Mettre à jour un produit (par son id)
    @ApiOperation(value = "Met à jour un produit du catalogue de produits.")
    @PutMapping (value = "/Produits")
    public void updateProduit(@RequestBody Product product) {

        productDao.save(product);
    }


    //Calculer la marge des produits
    @ApiOperation(value = "Calcule la marge des produits du catalogue.")
    @GetMapping(value = "/AdminProduits")
    public String calculerMargeProduits() {

        Iterable<Product> produits = productDao.findAll();
        //String newLine = System.getProperty("line.separator");
        String newLine = "<br />";      //saut de ligne pour affichage dans une page d'un navigateur internet
        String liste = "{" ;

        for (Iterator<Product> iterator = produits.iterator(); iterator.hasNext();) {
            Product prod = iterator.next();
            int marge = prod.getPrix() - prod.getPrixAchat() ;

            liste = liste + newLine + "\"" + prod.toString() + "\" : " + marge;
            if (iterator.hasNext()){
                liste = liste + ",";
            }
        }

        liste = liste + newLine + "}";
        return liste;
    }


    //Recuperer et trier la liste des produits par ordre alphabetique
    @ApiOperation(value = "Récupère la liste des produits et la retourne triée par ordre alphabétique.")
    @GetMapping(value = "/ProduitsTriAlphabetique")

    public MappingJacksonValue trierProduitsParOrdreAlphabetique() {

        Iterable<Product> produits = productDao.findAllByOrderByNomAsc();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("id");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }


    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    public List<Product>  testeDeRequetes(@PathVariable int prix) {

        return productDao.chercherUnProduitCher(400);
    }



}
