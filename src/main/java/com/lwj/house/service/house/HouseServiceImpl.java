package com.lwj.house.service.house;

import com.lwj.house.base.LoginUserUtil;
import com.lwj.house.entity.*;
import com.lwj.house.repository.*;
import com.lwj.house.service.ServiceResult;
import com.lwj.house.web.dto.HouseDTO;
import com.lwj.house.web.dto.HouseDetailDTO;
import com.lwj.house.web.dto.HousePictureDTO;
import com.lwj.house.web.form.HouseForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 房屋管理服务实现类
 * @author lwj
 */
@Service
public class HouseServiceImpl implements IHouseService {

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;

    @Override
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        //  1.开始保存房屋主体信息
        House house = new House();
        modelMapper.map(houseForm, house);
        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        houseRepository.save(house);
        //  2.开始保存房屋详细信息
        HouseDetail houseDetail = new HouseDetail();
        modelMapper.map(houseForm, houseDetail);
        houseDetail.setHouseId(house.getId());
        //      对houseDetail中的其余参数进行校验并赋值
        ServiceResult<HouseDTO> subWayValidtionResult = wrapperDetailInfo(houseDetail, houseForm);
        houseDetail = houseDetailRepository.save(houseDetail);
        //      保存房屋照片
        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        List<HousePicture> housePictures = housePictureRepository.saveAll(pictures);
        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);

        List<HousePictureDTO> housePictureDTOS = new ArrayList<>();
        housePictures.forEach(housePicture -> housePictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));

        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());
        //      保存房屋标签信息
        List<String> tags = houseForm.getTags();
        if (tags != null || !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            tags.forEach(tag-> houseTags.add(new HouseTag(house.getId(), tag)));
            houseTagRepository.saveAll(houseTags);
            houseDTO.setTags(tags);
        }
        return new ServiceResult<HouseDTO>(true, null, houseDTO);
    }

    private List<HousePicture> generatePictures(HouseForm houseForm, Integer houseId) {
        List<HousePicture> pictures = new ArrayList<>();
        if (houseForm.getPhotos() == null || houseForm.getPhotos().isEmpty()) {
            return pictures;
        }

        houseForm.getPhotos().forEach(photoForm -> {
            HousePicture housePicture = new HousePicture();
            housePicture.setHouseId(houseId);
            housePicture.setCdnPrefix(cdnPrefix);
            housePicture.setPath(photoForm.getPath());
            housePicture.setWidth(photoForm.getWidth());
            housePicture.setHeight(photoForm.getHeight());
            pictures.add(housePicture);
        });
        return pictures;
    }

    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {
        //  校验是否存在地铁线
        Subway subway = subwayRepository.findById(houseForm.getSubwayLineId()).orElse(null);
        if (subway == null) {
            return new ServiceResult<>(false, "Not valid subway line!");
        }
        //  校验是否存在该地铁站
        SubwayStation subwayStation = subwayStationRepository.findById(houseForm.getSubwayStationId()).orElse(null);
        if (subwayStation == null || !subway.getId().equals(subwayStation.getSubwayId())) {
            return new ServiceResult<>(false, "Not valid subway station!");
        }
        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());

        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        return null;
    }
}
