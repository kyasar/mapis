package com.cherchy.markod.controller;

import com.cherchy.markod.model.Product;
import com.cherchy.markod.service.ProductService;
import com.sun.corba.se.spi.ior.ObjectKey;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpServletRequest request; // for debug

    @Autowired
    private ProductService productService;

    @RequestMapping(
            value = "/{_id}",
            method=GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Product> getProduct(
            @PathVariable(value="_id") String id)
    {
        Product p = productService.findOne(id);
        if (p != null) {
            return new ResponseEntity<>(p, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Returns the list of all products")
    @ApiResponses(value = {
            // @ApiResponse(code = 200, message = "Success", response = Product.class),
            @ApiResponse(code = 200, message = "Success", responseContainer = "List", response = Product.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    @RequestMapping(
            method=GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Collection<Product>> getProducts()
    {
        log.debug("GET " + request.getRequestURI());
        Collection<Product> products = productService.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @RequestMapping(
            method= POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Product> createProduct(@RequestBody Product p)
    {
        Product savedProduct = productService.create(p);
        if (savedProduct != null)
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @RequestMapping(
            value = "/{_id}",
            method= PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Product> updateProduct(
            @PathVariable(value="_id") String id,
            @RequestBody Product p)
    {
        Product present = productService.findOne(id);
        if (present == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        p.setId(present.getId());

        Product savedProduct = productService.update(p);
        if (savedProduct != null)
            return new ResponseEntity<>(savedProduct, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @RequestMapping(
            value = "/{_id}",
            method= DELETE)
    public ResponseEntity<Product> removeProduct(
            @PathVariable(value="_id") String id)
    {
        //log.debug("Delete role: " + request.isUserInRole("ADMIN"));
        Product present = productService.findOne(id);
        if (present == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
