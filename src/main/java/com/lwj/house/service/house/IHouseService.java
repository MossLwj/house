package com.lwj.house.service.house;

import com.lwj.house.service.ServiceResult;
import com.lwj.house.web.dto.HouseDTO;
import com.lwj.house.web.form.HouseForm;

/**
 * 房屋管理服务接口
 * @author lwj
 */
public interface IHouseService {

    /**
     * 房屋新增保存接口
     * @param houseForm
     * @return
     */
    ServiceResult<HouseDTO> save(HouseForm houseForm);

}
