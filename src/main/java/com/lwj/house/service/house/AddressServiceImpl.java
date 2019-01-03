package com.lwj.house.service.house;

import com.lwj.house.repository.SupportAddressRepository;
import com.lwj.house.service.ServiceMultiResult;
import com.lwj.house.web.controller.house.SupportAddressDTO;
import com.lwj.house.web.dto.SupportAddress;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lwj
 */
@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> supportAddresses = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDTO> supportAddressDTOS = new ArrayList<>();
        supportAddresses.forEach(supportAddress -> {
            SupportAddressDTO target = modelMapper.map(supportAddress, SupportAddressDTO.class);
            supportAddressDTOS.add(target);
        });
        return new ServiceMultiResult<>(supportAddressDTOS.size(), supportAddressDTOS);
    }
}
