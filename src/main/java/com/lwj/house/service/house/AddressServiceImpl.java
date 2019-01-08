package com.lwj.house.service.house;

import com.lwj.house.entity.Subway;
import com.lwj.house.entity.SubwayStation;
import com.lwj.house.entity.SupportAddress;
import com.lwj.house.repository.SubwayRepository;
import com.lwj.house.repository.SubwayStationRepository;
import com.lwj.house.repository.SupportAddressRepository;
import com.lwj.house.service.ServiceMultiResult;

import com.lwj.house.web.dto.SubwayDTO;
import com.lwj.house.web.dto.SubwayStationDTO;
import com.lwj.house.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwj
 */
@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

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

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityName) {
        if (cityName == null) {
            return new ServiceMultiResult<>(0, null);
        }

        List<SupportAddressDTO> result = new ArrayList<>();
        List<SupportAddress> regions = supportAddressRepository.findAllByLevelAndBelongTo(SupportAddress.Level.REGION.getValue(), cityName);
        regions.forEach(supportAddress -> result.add(modelMapper.map(supportAddress, SupportAddressDTO.class)));
        return new ServiceMultiResult<>(regions.size(), result);
    }

    @Override
    public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
        List<SubwayDTO> result = new ArrayList<>();
        List<Subway> subways = subwayRepository.findAllByCityEnName(cityEnName);
        if (subways.isEmpty()) {
            return result;
        }

        subways.forEach(subway -> result.add(modelMapper.map(subway, SubwayDTO.class)));
        return result;
    }

    @Override
    public List<SubwayStationDTO> findAllStationBySubway(Integer subwayId) {
        List<SubwayStationDTO> result = new ArrayList<>();
        List<SubwayStation> stations = subwayStationRepository.findAllBySubwayId(subwayId);
        if (stations.isEmpty()) {
            return result;
        }

        stations.forEach(station -> result.add(modelMapper.map(station, SubwayStationDTO.class)));
        return result;
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDTO> result = new HashMap<>();
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        SupportAddress region = supportAddressRepository.findByEnNameAndLevel(regionEnName, SupportAddress.Level.REGION.getValue());
        result.put(SupportAddress.Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
        result.put(SupportAddress.Level.REGION, modelMapper.map(region, SupportAddressDTO.class));
        return result;
    }
}
