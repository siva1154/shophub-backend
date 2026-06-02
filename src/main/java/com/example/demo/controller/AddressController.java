package com.example.demo.controller;

import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.repo.AddressRepo;
import com.example.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private UserRepo userRepo;

    // ✅ Get all addresses of logged-in user
    @GetMapping
    public List<Address> getAddresses(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return addressRepo.findByUserId(user.getId());
    }

    // ✅ Add new address
    @PostMapping
    public Address addAddress(@RequestBody Address address, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUser(user);

        return addressRepo.save(address);
    }

    // ✅ Update address
    @PutMapping("/{id}")
    public Address updateAddress(@PathVariable Long id,
                                 @RequestBody Address updatedAddress,
                                 Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (address.getUser().getId()!=(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        address.setType(updatedAddress.getType());
        address.setName(updatedAddress.getName());
        address.setPhone(updatedAddress.getPhone());
        address.setAddressLine(updatedAddress.getAddressLine());

        return addressRepo.save(address);
    }

    // ✅ Delete address
    @DeleteMapping("/{id}")
    public String deleteAddress(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (address.getUser().getId()!=(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepo.delete(address);

        return "Address deleted successfully";
    }
}