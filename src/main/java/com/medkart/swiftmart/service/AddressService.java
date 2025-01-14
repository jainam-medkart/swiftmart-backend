package com.medkart.swiftmart.service;

import com.medkart.swiftmart.dto.AddressDto;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.entity.Address;
import com.medkart.swiftmart.entity.User;
import com.medkart.swiftmart.repository.AddressRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepo addressRepo;
    private final UserService userService;

    public Response saveAndUpdateAddress(AddressDto addressDto) {
        User user = userService.getLoginUser();
        Address address = user.getAddress();

        if(address == null){
            address = new Address();
            address.setUser(user);
        }

        if(addressDto.getStreet() != null)
            address.setStreet(addressDto.getStreet());
        if(addressDto.getCity() != null)
            address.setCity(addressDto.getCity());
        if(addressDto.getState() != null)
            address.setState(addressDto.getState());
        if(address.getZipCode() != null)
            address.setZipCode(addressDto.getZipCode());
        if(address.getCountry() != null)
            address.setCountry(addressDto.getCountry());

        address = addressRepo.save(address);

        String message = (user.getAddress() == null) ? "Address Saved Successfully" : "Address Updated Successfully";
        return Response.builder()
                .status(200)
                .message("Address Updated Successfully")
                .build();
    }

}
