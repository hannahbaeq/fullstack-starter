package com.starter.fullstack.dao;

import com.starter.fullstack.api.Inventory;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Test Inventory DAO.
 */
@DataMongoTest
@RunWith(SpringRunner.class)
public class InventoryDAOTest {
  @ClassRule
  public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

  @Resource
  private MongoTemplate mongoTemplate;
  private InventoryDAO inventoryDAO;
  private static final String NAME = "Amber";
  private static final String NAME1 = "item1";
  private static final String NAME2 = "item2";
  private static final String PRODUCT_TYPE = "hops";
  private static final String NEW_TYPE = "bops";
  private static final String ID = "123";

  @Before
  public void setup() {
    this.inventoryDAO = new InventoryDAO(this.mongoTemplate);
  }

  @After
  public void tearDown() {
    this.mongoTemplate.dropCollection(Inventory.class);
  }

  /**
   * Test Find All method.
   */
  @Test
  public void findAll() {
    Inventory inventory = new Inventory();
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);
    this.mongoTemplate.save(inventory);
    List<Inventory> actualInventory = this.inventoryDAO.findAll();
    Assert.assertFalse(actualInventory.isEmpty());
  }

  /**
   * Test Create method:
   * Check for same names
   * Check for right # of items
   * Check for different ID
   */
  @Test
  public void create() {
    Inventory item1 = new Inventory();
    Inventory item2 = new Inventory();
    item1.setName(NAME1);
    item2.setName(NAME2);
    item1.setId(ID);
    this.inventoryDAO.create(item1);
    this.inventoryDAO.create(item2);
    List<Inventory> currInventory = this.inventoryDAO.findAll();
    Assert.assertEquals(NAME1, currInventory.get(0).getName());
    Assert.assertEquals(NAME2, currInventory.get(1).getName());
    Assert.assertNotEquals(ID, currInventory.get(0).getId());
    Assert.assertEquals(2, this.inventoryDAO.findAll().size());
  }

  /**
   * Test Retrieve method:
   * Check if Optional Inventory object is equivalent
   */
  @Test
  public void retrieve() {
    Inventory item1 = new Inventory();
    item1.setName(NAME1);
    item1.setId(ID);
    this.inventoryDAO.create(item1);
    List<Inventory> currInventory = this.inventoryDAO.findAll();
    String id = currInventory.get(0).getId();
    Optional<Inventory> test = Optional.of(currInventory.get(0));
    Assert.assertEquals(test, this.inventoryDAO.retrieve(id));
  }

  /**
   * Test Update method:
   * Check if updated item has new values
   * Check if ID remained the same
   */
  @Test
  public void update() {
    Inventory item1 = new Inventory();
    Inventory item2 = new Inventory();
    item1.setName(NAME1);
    item2.setName(NAME2);
    item1.setProductType(PRODUCT_TYPE);
    this.inventoryDAO.create(item1);
    this.inventoryDAO.create(item2);
    List<Inventory> currInventory = this.inventoryDAO.findAll();
    String id = currInventory.get(0).getId();
    item1.setProductType(NEW_TYPE);
    Optional<Inventory> updatedInventory = this.inventoryDAO.update(id, item1);
    Assert.assertEquals(2, this.inventoryDAO.findAll().size());
    List<Inventory> newInventory = this.inventoryDAO.findAll();
    String newId = newInventory.get(0).getId();
    Assert.assertEquals(id, newId);
    Assert.assertEquals(NEW_TYPE, newInventory.get(0).getProductType());
  }

  /**
   * Test Delete method:
   * Checks # after deletion
   * Checks ID empty after deletion
   * Check that empty is given on non-existent ID
   */
  @Test
  public void delete() {
    Inventory item1 = new Inventory();
    Inventory item2 = new Inventory();
    item1.setName(NAME1);
    item2.setName(NAME2);
    item1.setId(ID);
    this.inventoryDAO.create(item1);
    this.inventoryDAO.create(item2);
    List<Inventory> currInventory = this.inventoryDAO.findAll();
    String id = currInventory.get(0).getId();
    this.inventoryDAO.delete(id);
    Assert.assertEquals(1, this.inventoryDAO.findAll().size());
    List<Inventory> newInventory = this.inventoryDAO.findAll();
    Assert.assertNotEquals(id, newInventory.get(0).getId());
    Assert.assertTrue(this.inventoryDAO.delete(ID).isEmpty());
  }
}
