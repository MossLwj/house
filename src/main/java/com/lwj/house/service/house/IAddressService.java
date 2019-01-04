package com.lwj.house.service.house;


import com.lwj.house.service.ServiceMultiResult;
import com.lwj.house.web.dto.SubwayDTO;
import com.lwj.house.web.dto.SubwayStationDTO;
import com.lwj.house.web.dto.SupportAddressDTO;

import java.util.List;

/**
 * 地址服务接口
 * @author lwj
 */
public interface IAddressService {

    ServiceMultiResult<SupportAddressDTO> findAllCities();

    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityName);

    List<SubwayDTO> findAllSubwayByCity(String cityEnName);

    List<SubwayStationDTO> findAllStationBySubway(Integer subwayId);
}
