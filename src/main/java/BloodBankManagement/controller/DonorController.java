package BloodBankManagement.controller;

import BloodBankManagement.entity.Donor;
import BloodBankManagement.repository.DonorRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
@CrossOrigin(
    origins = "http://localhost:8082",
    allowCredentials = "true"
)
public class DonorController {

    private final DonorRepository donorRepository;

    public DonorController(DonorRepository donorRepository) {

        this.donorRepository = donorRepository;
    }

    // =========================
    // ADD DONOR
    // =========================

    @PostMapping
    public Donor addDonor(@RequestBody Donor donor) {

        return donorRepository.save(donor);
    }

    // =========================
    // GET ALL DONORS
    // =========================

    @GetMapping
    public List<Donor> getAllDonors() {

        return donorRepository.findAll();
    }
}