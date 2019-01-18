package com.lwj.house.service.house;


import com.lwj.house.entity.SupportAddress;
import com.lwj.house.service.ServiceMultiResult;
import com.lwj.house.service.ServiceResult;
import com.lwj.house.web.dto.SubwayDTO;
import com.lwj.house.web.dto.SubwayStationDTO;
import com.lwj.house.web.dto.SupportAddressDTO;

import java.util.List;
import java.util.Map;

/**
 * 地址服务接口
 * @author lwj
 */
public interface IAddressService {

    /**
     * 获取所有支持的城市列表
     * @return
     */
    ServiceMultiResult<SupportAddressDTO> findAllCities();

    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     * @param cityName
     * @return
     */
    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityName);

    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     * @param cityEnName
     * @return
     */
    List<SubwayDTO> findAllSubwayByCity(String cityEnName);

    /**
     * 根据地铁线id获取该地铁线的所有地铁站点
     * @param subwayId
     * @return
     */
    List<SubwayStationDTO> findAllStationBySubway(Integer subwayId);

    /**
     * 根据英文简写获取具体区域的信息
     * @param cityEnName
     * @param regionEnName
     * @return
     */
    Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    /**
     * 通过地铁线查询
     * @param subwayLineId
     * @return
     */
    ServiceResult<SubwayDTO> findSubway(Integer subwayLineId);

    /**
     * 根据id获取地铁站名
     * @param subwayStationId
     * @return
     */
    ServiceResult<SubwayStationDTO> findSubwayStation(Integer subwayStationId);

    /**
     * 根据城市英文简写获取城市详细信息
     * @param cityEnName
     * @return
     */
    ServiceResult<SupportAddressDTO> findCity(String cityEnName);
}
