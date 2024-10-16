package com.example.Eshop.controllers;

import com.example.Eshop.models.Address;
import com.example.Eshop.services.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

  private AddressService addressService;
  public AddressController(AddressService addressService){
    this.addressService = addressService;
  }

  @GetMapping("/{userId}")
  public ResponseEntity<List<Address>> getAddressesByUserId(@PathVariable Long userId) {
    List<Address> addresses = addressService.getAddressesByUserId(userId);
    return ResponseEntity.ok(addresses);
  }

  @PostMapping
  public ResponseEntity<Address> saveAddress(@RequestBody Address address) {
    try {
      Address savedAddress;
      if (address.getId() > 0) {
        savedAddress = addressService.saveAddress(address);
        //Return 200 OK if it's an update
        return ResponseEntity.ok(savedAddress);
      } else {
        address.setId(null);
        savedAddress = addressService.saveAddress(address);
        //Return 201 Created if it's new
        return ResponseEntity.status(201).body(savedAddress);
      }
    } catch (RuntimeException e) {
      //Return 404 if address to update is not found
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
    try {
      addressService.deleteAddress(id);
      //Return 204 No Content on successful delete
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      //Return 404 Not Found if address does not exist
      return ResponseEntity.notFound().build();
    }
  }
}
