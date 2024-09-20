package com.starter.fullstack.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.fullstack.api.Inventory;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class InventoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  private Inventory inventory;

  private static final String NAME1 = "TEST";
  private static final String NAME2 = "ALSO TEST";
  private static final String ID1 = "ID";
  private static final String ID2 = "OTHER ID";
  private static final String TYPE = "TYPE";
  private static final String NEW_TYPE = "NEW TYPE";
  private static final String PARAM_ID = "id";

  @Before
  public void setup() throws Throwable {
    this.inventory = new Inventory();
    this.inventory.setId(ID1);
    this.inventory.setName(NAME1);
    // Sets the Mongo ID for us
    this.inventory = this.mongoTemplate.save(this.inventory);
  }

  @After
  public void teardown() {
    this.mongoTemplate.dropCollection(Inventory.class);
  }

  /**
   * Test findAll endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void findAll() throws Throwable {
    this.mockMvc.perform(get("/inventory")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().json("[" + this.objectMapper.writeValueAsString(inventory) + "]"));
  }

  /**
   * Test create endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void create() throws Throwable {
    this.inventory = new Inventory();
    this.inventory.setId(ID2);
    this.inventory.setName(NAME2);
    this.inventory.setProductType(TYPE);
    this.mockMvc.perform(post("/inventory")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(this.inventory)))
      .andExpect(status().isOk());
    
    Assert.assertEquals(2, this.mongoTemplate.findAll(Inventory.class).size());
  }

  /**
   * Test retrieve endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void retrieveTest() throws Throwable {
    List<Inventory> currInventory = this.mongoTemplate.findAll(Inventory.class);
    String id = currInventory.get(0).getId();

    Optional<Inventory> item = Optional.of(currInventory.get(0));
    this.mockMvc.perform(get("/inventory/retrieve")
            .accept(MediaType.APPLICATION_JSON)
            .content(id))
            .andExpect(status().isOk());
  }

  /**
   * Test update endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void updateTest() throws Throwable {
    List<Inventory> currInventory = this.mongoTemplate.findAll(Inventory.class);
    String id = currInventory.get(0).getId();
    currInventory.get(0).setProductType(NEW_TYPE);

    this.mockMvc.perform(post("/inventory/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param(PARAM_ID, id)
                    .content(this.objectMapper.writeValueAsString(currInventory.get(0))))
            .andExpect(status().isOk());

    List<Inventory> newInventory = this.mongoTemplate.findAll(Inventory.class);
    String newId = newInventory.get(0).getId();
    Assert.assertEquals(NEW_TYPE, newInventory.get(0).getProductType());
    Assert.assertEquals(id, newId);
  }

  /**
   * Test delete endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void deleteTest() throws Throwable {
    this.mockMvc.perform(delete("/inventory")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.inventory.getId()))
            .andExpect(status().isOk());

    Assert.assertEquals(0, this.mongoTemplate.findAll(Inventory.class).size());
  }
}
