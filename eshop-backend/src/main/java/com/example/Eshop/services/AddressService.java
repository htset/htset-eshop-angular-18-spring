package com.example.Eshop.services;

import com.example.Eshop.models.Address;
import com.example.Eshop.repositories.AddressRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

  private AddressRepository addressRepository;

  public AddressService(AddressRepository addressRepository){
    this.addressRepository = addressRepository;
  }

  public List<Address> getAddressesByUserId(Long userId) {
    return addressRepository.findByUserId(userId);
  }

  public Address saveAddress(Address address) {
    //Check if the address has an ID (indicating an update)
    if (address.getId() != null && address.getId() > 0) {
      //Get the existing address from the database
      Optional<Address> optionalAddress
          = addressRepository.findById(address.getId());

      if (optionalAddress.isPresent()) {
        //Update the existing address
        Address existingAddress = optionalAddress.get();
        existingAddress.setFirstName(address.getFirstName());
        existingAddress.setLastName(address.getLastName());
        existingAddress.setStreet(address.getStreet());
        existingAddress.setZip(address.getZip());
        existingAddress.setCity(address.getCity());
        existingAddress.setCountry(address.getCountry());

        //Save and return the updated address
        return addressRepository.save(existingAddress);
      } else {
        throw new RuntimeException("Address not found with id: "
            + address.getId());
      }
    } else {
      //If no ID is present, it's a new address, so save it
      return addressRepository.save(address);
    }
  }

  public void deleteAddress(Long id) {
    //Check if the address exists before attempting to delete
    if (addressRepository.existsById(id)) {
      addressRepository.deleteById(id);
    } else {
      throw new RuntimeException("Address not found with id: " + id);
    }
  }
}
