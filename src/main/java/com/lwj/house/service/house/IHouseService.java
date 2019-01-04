package com.lwj.house.service.house;

import com.lwj.house.service.ServiceResult;
import com.lwj.house.web.dto.HouseDTO;
import com.lwj.house.web.form.HouseForm;

/**
 * @author lwj
 */
public interface IHouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);

}
