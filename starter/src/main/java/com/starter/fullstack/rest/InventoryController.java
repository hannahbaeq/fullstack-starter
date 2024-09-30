package com.starter.fullstack.rest;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.dao.InventoryDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * Inventory Controller.
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {
  private final InventoryDAO inventoryDAO;

  /**
   * Default Constructor.
   * @param inventoryDAO inventoryDAO.
   */
  public InventoryController(InventoryDAO inventoryDAO) {
    Assert.notNull(inventoryDAO, "Inventory DAO must not be null.");
    this.inventoryDAO = inventoryDAO;
  }

  /**
   * Find Products.
   * @return List of Product.
   */
  @GetMapping
  public List<Inventory> findInventories() {
    return this.inventoryDAO.findAll();
  }

  /**
   * Create Products.
   * @param inventory inventory
   * @return Created inventory
   */
  @PostMapping
  public Inventory createInventory(@Valid @RequestBody Inventory inventory) {
    return this.inventoryDAO.create(inventory);
  }

  /**
   * Retrieve Products.
   * @param id id
   * @return Optional Inventory
   */
  @GetMapping("/retrieve")
  public Optional<Inventory> retrieveInventory(@Valid @RequestBody String id) {
    return this.inventoryDAO.retrieve(id);
  }

  /**
   * Update Products.
   * @param id id
   * @param inventory inventory
   * @return Optional Inventory
   */
  @PostMapping("/update")
  public Optional<Inventory> updateInventory(@Valid @RequestParam String id, @RequestBody Inventory inventory) {
    Assert.notNull(id, "NOOOOOO");
    return this.inventoryDAO.update(id, inventory);
  }

  /**
   * Delete Products by IDs
   * @param ids the list of inventory IDs to be deletes
   * @return Response entity list string
   */
  @DeleteMapping
  public List<String> deleteInventory(@Valid @RequestBody List<String> ids) {
    List<String> deleted = new ArrayList<>();
    for (String id : ids) {
      Optional<Inventory> curr = this.inventoryDAO.delete(id);
      if (curr.isPresent()) {
        deleted.add(id);
      }
    }
    return deleted;
  }
}