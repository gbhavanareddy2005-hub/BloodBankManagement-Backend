package BloodBankManagement.controller;

import BloodBankManagement.entity.BloodRequest;
import BloodBankManagement.entity.BloodInventory;
import BloodBankManagement.repository.BloodRequestRepository;
import BloodBankManagement.repository.BloodInventoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin
public class BloodRequestController {

    private final BloodRequestRepository bloodRequestRepository;
    private final BloodInventoryRepository inventoryRepository;

    public BloodRequestController(
            BloodRequestRepository bloodRequestRepository,
            BloodInventoryRepository inventoryRepository) {

        this.bloodRequestRepository = bloodRequestRepository;
        this.inventoryRepository = inventoryRepository;
    }


    // Create a new blood request
    @PostMapping
    public BloodRequest addRequest(
            @RequestBody BloodRequest request) {

        request.setStatus("Pending");

        return bloodRequestRepository.save(request);
    }


    // Get all blood requests
    @GetMapping
    public List<BloodRequest> getAllRequests() {

        return bloodRequestRepository.findAll();
    }


    // Approve request and deduct blood from inventory
    @PutMapping("/{id}/approve")
    public BloodRequest approveRequest(
            @PathVariable Long id) {

        // Find the request
        BloodRequest request =
                bloodRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Request not found"
                                ));


        // Find matching blood group
        BloodInventory inventory =
                inventoryRepository
                        .findByBloodGroup(request.getBloodGroup())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Blood group not available"
                                ));


        // Check whether enough blood is available
        if (inventory.getUnits() < request.getRequiredUnits()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Not enough blood units available"
            );
        }


        // Deduct requested units
        inventory.setUnits(
                inventory.getUnits()
                        - request.getRequiredUnits()
        );

        inventoryRepository.save(inventory);


        // Change request status
        request.setStatus("Approved");

        return bloodRequestRepository.save(request);
    }
}