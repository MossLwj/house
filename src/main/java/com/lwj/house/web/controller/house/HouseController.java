package com.lwj.house.web.controller.house;

import com.lwj.house.base.ApiResponse;
import com.lwj.house.base.RentValueBlock;
import com.lwj.house.service.ServiceMultiResult;
import com.lwj.house.service.ServiceResult;
import com.lwj.house.service.house.IAddressService;
import com.lwj.house.service.house.IHouseService;
import com.lwj.house.web.dto.HouseDTO;
import com.lwj.house.web.dto.SubwayDTO;
import com.lwj.house.web.dto.SubwayStationDTO;
import com.lwj.house.web.dto.SupportAddressDTO;
import com.lwj.house.web.form.RentSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwj
 */
@Controller
public class HouseController {

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    /**
     * 获取城市信息
     * @return
     */
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities(){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }


    /**
     * 根据选中的城市加载该城市下的区信息
     * @param cityName
     * @return
     */
    @RequestMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam("city_name") String cityName){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllRegionsByCityName(cityName);
        if (result.getResultSize() == 0) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 获取具体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
        List<SubwayDTO> subways = addressService.findAllSubwayByCity(cityEnName);
        if (subways.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return ApiResponse.ofSuccess(subways);
    }

    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") Integer subwayId) {
        List<SubwayStationDTO> stationDTOS = addressService.findAllStationBySubway(subwayId);
        if (stationDTOS.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return ApiResponse.ofSuccess(stationDTOS);
    }

    /**
     * 租房信息接口
     * @param rentSearch
     * @param model
     * @param httpSession
     * @param redirectAttributes
     * @return
     */
    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentSearch rentSearch, Model model,
                                HttpSession httpSession, RedirectAttributes redirectAttributes) {
        if (rentSearch.getCityEnName() == null) {
            String cityEnNameInSession = (String) httpSession.getAttribute("cityEnName");
            if (cityEnNameInSession == null) {
                redirectAttributes.addAttribute("msg", "must_chose_city");
                return "redirect:/index";
            } else {
                rentSearch.setCityEnName(cityEnNameInSession);
            }
        } else {
            httpSession.setAttribute("cityEnName", rentSearch.getCityEnName());
        }

        ServiceResult<SupportAddressDTO> city = addressService.findCity(rentSearch.getCityEnName());
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }
        model.addAttribute("currentCity", city.getResult());

        //  校验选择的城市是否是正确的
        ServiceMultiResult<SupportAddressDTO> allRegionsByCityName = addressService.findAllRegionsByCityName(rentSearch.getCityEnName());
        if (allRegionsByCityName.getResult() == null || allRegionsByCityName.getTotal() < 1) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }
        ServiceMultiResult<HouseDTO> serviceMultiResult = houseService.query(rentSearch);

        model.addAttribute("total", serviceMultiResult.getTotal());
        model.addAttribute("houses", serviceMultiResult.getResult());
        //  如果区域为null则默认为全部都选中
        if (rentSearch.getRegionEnName() == null) {
            rentSearch.setRegionEnName("*");
        }

        model.addAttribute("searchBody", rentSearch);
        model.addAttribute("regions", allRegionsByCityName.getResult());
        model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks", RentValueBlock.AREA_BLOCK);

        model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock", RentValueBlock.matchArea(rentSearch.getAreaBlock()));

        return "rent-list";
    }

}
