package com.example.bank_backend.controller;

import com.example.bank_backend.entity.Transfer;
import com.example.bank_backend.entity.User;
import com.example.bank_backend.repository.TransferRepository;
import com.example.bank_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class BankController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransferRepository transferRepository;

    // Create a new user
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Add validation or other business logic if needed
        if (userRepository.existsByAccountNumber(user.getAccountNumber())) {
            return ResponseEntity.badRequest().body(null); // Return 400 if the account number already exists
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // Get user details by account number
    @GetMapping("/user/{accountNumber}")
    public ResponseEntity<User> getUser(@PathVariable String accountNumber) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get transfer history for a user
    @GetMapping("/user/{accountNumber}/transfers")
    public ResponseEntity<List<Transfer>> getTransferHistory(@PathVariable String accountNumber) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);
        if (user.isPresent()) {
            List<Transfer> transfers = transferRepository.findByUserId(user.get().getId());
            return ResponseEntity.ok(transfers);
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if user not found
        }
    }

    // Transfer money
    @PostMapping("/transfer")
    public ResponseEntity<Transfer> transferMoney(@RequestBody Transfer transferRequest) {
        String accountNumber = transferRequest.getUser().getAccountNumber(); // Ensure this is correctly passed
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        if (user.isPresent()) {
            User sender = user.get();

            // Check for sufficient balance
            if (sender.getBalance() < transferRequest.getAmount()) {
                return ResponseEntity.badRequest().body(null); // Return 400 for insufficient funds
            }

            // Deduct balance and save
            sender.setBalance(sender.getBalance() - transferRequest.getAmount());
            userRepository.save(sender);

            // Set the sender in the transfer record
            transferRequest.setUser(sender); // Link the transfer to the user
            transferRequest.setDate(LocalDateTime.now()); // Set current date and time for transfer
            Transfer savedTransfer = transferRepository.save(transferRequest);

            return ResponseEntity.ok(savedTransfer);
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if user not found
        }
    }
}
