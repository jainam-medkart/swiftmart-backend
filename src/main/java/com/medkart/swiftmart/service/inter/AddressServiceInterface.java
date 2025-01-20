package com.medkart.swiftmart.service.inter;

import com.medkart.swiftmart.dto.AddressDto;
import com.medkart.swiftmart.dto.Response;

public interface AddressServiceInterface {

    public Response saveAndUpdateAddress(AddressDto addressDto);
}
