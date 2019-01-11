package com.lwj.house.web.controller.admin;

import com.google.gson.Gson;
import com.lwj.house.base.ApiDatatableResponse;
import com.lwj.house.base.ApiResponse;
import com.lwj.house.entity.SupportAddress;
import com.lwj.house.service.ServiceMultiResult;
import com.lwj.house.service.ServiceResult;
import com.lwj.house.service.house.IAddressService;
import com.lwj.house.service.house.IHouseService;
import com.lwj.house.service.house.IQiNiuService;
import com.lwj.house.web.dto.HouseDTO;
import com.lwj.house.web.dto.QiNiuPutRet;
import com.lwj.house.web.dto.SupportAddressDTO;
import com.lwj.house.web.form.DatatableSearch;
import com.lwj.house.web.form.HouseForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author lwj
 */
@Controller
public class AdminController {

    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private Gson gson;

    @GetMapping("admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    @GetMapping("admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    @GetMapping("admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @GetMapping("admin/add/house")
    public String add(){
        return "admin/house-add";
    }

    @PostMapping(value = "admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        //  上传七牛云
        try {
            InputStream inputStream = file.getInputStream();
            Response response = qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiNiuPutRet qiNiuPutRet = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(qiNiuPutRet);
            } else {
                return ApiResponse.ofMessage(response.statusCode, response.getInfo());
            }
        } catch (QiniuException e) {
            Response response = e.response;
            try {
                return ApiResponse.ofMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }

        //  上传本地默认目录
//        String fileName = file.getOriginalFilename();
//        File target = new File("E:/workSpace/house/tmp/" + fileName);
//        try {
//            file.transferTo(target);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
//        }
//        return ApiResponse.ofSuccess(null);
    }

    /**
     * 打开房源管理列表页面
     * @return
     */
    @GetMapping("admin/house/list")
    public String loadHousePage() {
        return "admin/house-list";
    }

    /**
     * 管理员角色房源查询接口
     * @param datatableSearch
     * @return
     */
    @RequestMapping("admin/houses")
    @ResponseBody
    public ApiDatatableResponse houses(@ModelAttribute DatatableSearch datatableSearch) {
        ServiceMultiResult<HouseDTO> result = houseService.adminQuery(datatableSearch);
        ApiDatatableResponse response = new ApiDatatableResponse(ApiResponse.Status.SUCCESS);
        response.setData(result.getResult());
        response.setRecordsFiltered(result.getTotal());
        response.setRecordsTotal(result.getTotal());
        response.setDraw(datatableSearch.getDraw());
        return response;
    }

    /**
     * 新增房源接口
     * @param houseForm
     * @param bindingResult
     * @return
     */
    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        //  如果参数校验出错，则返回错误结构对象
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }
        //  校验是否有上传图片，和封面
        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
        }
        //  校验城市是否正确
        Map<SupportAddress.Level, SupportAddressDTO> addressDTOMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (addressDTOMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        //  保存
        ServiceResult<HouseDTO> result = houseService.save(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(result.getResult());
        }
        return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
    }

}
