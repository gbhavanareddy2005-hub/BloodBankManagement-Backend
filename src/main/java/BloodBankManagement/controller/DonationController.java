package BloodBankManagement.controller;

import BloodBankManagement.entity.Donation;
import BloodBankManagement.entity.BloodInventory;
import BloodBankManagement.repository.DonationRepository;
import BloodBankManagement.repository.BloodInventoryRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin
public class DonationController {

    private final DonationRepository donationRepository;
    private final BloodInventoryRepository inventoryRepository;

    public DonationController(
            DonationRepository donationRepository,
            BloodInventoryRepository inventoryRepository) {

        this.donationRepository = donationRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping
    public Donation addDonation(@RequestBody Donation donation) {

        // Save donation
        Donation savedDonation = donationRepository.save(donation);

        // Find the blood group directly
        Optional<BloodInventory> inventory =
                inventoryRepository.findByBloodGroup(
                        donation.getBloodGroup()
                );

        if (inventory.isPresent()) {

            BloodInventory existingInventory =
                    inventory.get();

            existingInventory.setUnits(
                    existingInventory.getUnits()
                    + donation.getUnitsDonated()
            );

            inventoryRepository.save(existingInventory);

        } else {

            BloodInventory newInventory =
                    new BloodInventory();

            newInventory.setBloodGroup(
                    donation.getBloodGroup()
            );

            newInventory.setUnits(
                    donation.getUnitsDonated()
            );

            inventoryRepository.save(newInventory);
        }

        return savedDonation;
    }

    @GetMapping
    public List<Donation> getAllDonations() {

        return donationRepository.findAll();
    }
}