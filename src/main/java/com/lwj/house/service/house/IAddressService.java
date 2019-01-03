package com.lwj.house.service.house;

import com.lwj.house.service.ServiceMultiResult;
import com.lwj.house.web.controller.house.SupportAddressDTO;

/**
 * 地址服务接口
 * @author lwj
 */
public interface IAddressService {

    ServiceMultiResult<SupportAddressDTO> findAllCities();
}
