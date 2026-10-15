package BloodBankManagement.controller;

import BloodBankManagement.entity.BloodInventory;
import BloodBankManagement.repository.BloodInventoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class BloodInventoryController {

    private final BloodInventoryRepository inventoryRepository;

    public BloodInventoryController(BloodInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping
    public BloodInventory addInventory(@RequestBody BloodInventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @GetMapping
    public List<BloodInventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
}