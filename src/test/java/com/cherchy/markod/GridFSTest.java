package com.cherchy.markod;

import com.cherchy.markod.model.*;
import com.cherchy.markod.repository.CampaignRepository;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GridFSTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Test
    public void t0_setUp() throws FileNotFoundException
    {
        mongoTemplate.remove(new Query(), "products");
        mongoTemplate.remove(new Query(), "categories");
        mongoTemplate.remove(new Query(), "fs.files");
        mongoTemplate.remove(new Query(), "fs.chunks");

        Category category = categoryService.create(new Category("Meyve", null));
        Assert.assertNotNull(category);

        productService.create(new Product("Deveci Armut", "91222", category.getId()));
        List<Product> products = productService.findAll("Deveci Armu");
        Assert.assertNotNull(products);
        Assert.assertNotEquals(0, products.size());
        Product product = products.get(0);

        DBObject metaData = new BasicDBObject();
        metaData.put("productId", product.getId());

        InputStream inputStream = new FileInputStream("src/main/resources/test.png");
        GridFSFile file = gridFsTemplate.store(inputStream, "test.png", "image/png", metaData);
        Assert.assertNotNull(file);
        System.out.println(file.getFilename() + " " + file.getId());
        System.out.println(file.toString());

        Query query = new Query();
        query.addCriteria(Criteria.where("metadata.productId").is(product.getId()));
        List<GridFSDBFile> files = gridFsTemplate.find(query);
        Assert.assertNotNull(files);

        System.out.println(files.size());
        try {
            System.out.println(file.getFilename());
            System.out.println(file.getContentType());

            //save as another image
            files.get(0).writeTo("src/main/resources/test_fromdb.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
