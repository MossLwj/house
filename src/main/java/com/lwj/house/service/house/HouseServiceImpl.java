package com.lwj.house.service.house;

import com.lwj.house.base.HouseSort;
import com.lwj.house.base.HouseStatus;
import com.lwj.house.base.LoginUserUtil;
import com.lwj.house.entity.*;
import com.lwj.house.repository.*;
import com.lwj.house.service.ServiceMultiResult;
import com.lwj.house.service.ServiceResult;
import com.lwj.house.service.search.ISearchService;
import com.lwj.house.web.dto.HouseDTO;
import com.lwj.house.web.dto.HouseDetailDTO;
import com.lwj.house.web.dto.HousePictureDTO;
import com.lwj.house.web.form.DatatableSearch;
import com.lwj.house.web.form.HouseForm;
import com.lwj.house.web.form.RentSearch;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

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
    private IQiNiuService qiNiuService;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;

    @Override
    @Transactional
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
        houseDTO.setPictures(housePictureDTOS);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());
        //      保存房屋标签信息
        List<String> tags = houseForm.getTags();
        if (null != tags && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            tags.forEach(tag-> houseTags.add(new HouseTag(house.getId(), tag)));
            houseTagRepository.saveAll(houseTags);
            houseDTO.setTags(tags);
        }
        return new ServiceResult<>(true, null, houseDTO);
    }

    @Override
    @Transactional
    public ServiceResult<HouseDTO> update(HouseForm houseForm) {
        House house = this.houseRepository.findById(houseForm.getId()).orElse(null);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail houseDetail = this.houseDetailRepository.findByHouseId(houseForm.getId());
        if (houseDetail == null) {
            return ServiceResult.notFound();
        }

        ServiceResult<HouseDTO> wrapperResult = wrapperDetailInfo(houseDetail, houseForm);
        if (wrapperResult != null) {
            return wrapperResult;
        }
        houseDetailRepository.save(houseDetail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.saveAll(pictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }
        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);

        if (house.getStatus() == HouseStatus.PASSES.getValue()) {
            searchService.index(house.getId());
        }

        return ServiceResult.success();
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch datatableSearch) {
        List<HouseDTO> houseDTOS = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.fromString(datatableSearch.getDirection()), datatableSearch.getOrderBy());
        int page = datatableSearch.getStart() / datatableSearch.getLength();
        Pageable pageable = PageRequest.of(page, datatableSearch.getLength(), sort);

        Specification<House> specification = (Specification<House>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));
            //  加入城市信息
            if (datatableSearch.getCity() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("cityEnName"), datatableSearch.getCity()));
            }
            //  状态
            if (datatableSearch.getStatus() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), datatableSearch.getStatus()));
            }
            //  创建时间
            if (datatableSearch.getCreateTimeMin() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), datatableSearch.getCreateTimeMin()));
            }
            if (datatableSearch.getCreateTimeMax() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), datatableSearch.getCreateTimeMax()));
            }
            //  标题
            if (datatableSearch.getTitle() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("title").as(String.class), "%" + datatableSearch.getTitle() + "%"));
            }
            return predicate;
        };

        Page<House> houses = houseRepository.findAll(specification, pageable);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);
        });
        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Integer houseId) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);

        List<HousePicture> pictures = housePictureRepository.findByHouseId(houseId);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        pictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));

        List<HouseTag> tags = houseTagRepository.findAllByHouseId(houseId);
        List<String> tagList = new ArrayList<>();
        tags.forEach(houseTag -> tagList.add(houseTag.getName()));

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setTags(tagList);

        return ServiceResult.of(houseDTO);
    }

    @Override
    public ServiceResult removePhoto(Integer id) {
        HousePicture housePicture = housePictureRepository.findById(id).orElse(null);
        if (housePicture == null) {
            return ServiceResult.notFound();
        }
        try {
            Response response = this.qiNiuService.delete(housePicture.getPath());
            if (response.isOK()) {
                housePictureRepository.delete(housePicture);
                return ServiceResult.success();
            } else {
                return new ServiceResult(false, response.error);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult updateCover(Integer coverId, Integer targetId) {
        HousePicture cover = housePictureRepository.findById(coverId).orElse(null);
        if (cover == null) {
            return ServiceResult.notFound();
        }
        houseRepository.updateCover(targetId, cover.getPath());
        return ServiceResult.success();
    }

    @Override
    public ServiceResult addTag(Integer houseId, String tag) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house == null) {
            return ServiceResult.notFound();
        }
        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag != null) {
            return new ServiceResult(false, "标签已存在");
        }
        houseTagRepository.save(new HouseTag(houseId, tag));
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeTag(Integer houseId, String tag) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house == null) {
            return ServiceResult.notFound();
        }
        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag == null) {
            return new ServiceResult(false, "标签不存在");
        }
        houseTagRepository.delete(houseTag);
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult updateStatus(Integer id, Integer status) {
        House house = houseRepository.findById(id).orElse(null);
        if (house == null) {
            return ServiceResult.notFound();
        }
        if (house.getStatus() == status) {
            return new ServiceResult(false, "状态未发生变化");
        }
        if (house.getStatus() == HouseStatus.RENTED.getValue()) {
            return new ServiceResult(false, "已出租的房源不允许修改状态");
        }
        if (house.getStatus() == HouseStatus.DELETED.getValue()) {
            return new ServiceResult(false, "已删除的房源不允许操作");
        }
        houseRepository.updateStatus(id, status);

        //  上架的时候更新索引，其他情况都要删除索引
        if (status == HouseStatus.PASSES.getValue()) {
            searchService.index(id);
        } else {
            searchService.remove(id);
        }
        return ServiceResult.success();
    }

    @Override
    public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {
        Sort sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
        int page = rentSearch.getStart() / rentSearch.getSize();
        Pageable pageable = PageRequest.of(page, rentSearch.getSize(), sort);
        Specification<House> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), HouseStatus.PASSES.getValue());
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("cityEnName"), rentSearch.getCityEnName()));
            if (HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(rentSearch.getOrderBy())) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY), -1));
            }
            return predicate;
        });
        Page<House> houses = houseRepository.findAll(specification, pageable);
        List<HouseDTO> houseDTOS = new ArrayList<>();

        List<Integer> houseIds = new ArrayList<>();
        Map<Integer, HouseDTO> idToHouseMap = new HashMap<>();
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);

            houseIds.add(house.getId());
            idToHouseMap.put(house.getId(), houseDTO);

        });
        wrapperHouseList(houseIds, idToHouseMap);
        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
    }

    /**
     * 渲染详细信息 及 标签
     *
     * @param houseIds
     * @param idToHouseMap
     */
    private void wrapperHouseList(List<Integer> houseIds, Map<Integer, HouseDTO> idToHouseMap) {
        List<HouseDetail> houseDetails = houseDetailRepository.findByHouseIdIn(houseIds);
        houseDetails.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseMap.get(houseDetail.getHouseId());
            houseDTO.setHouseDetail(modelMapper.map(houseDetail, HouseDetailDTO.class));
        });
        List<HouseTag> houseTags = houseTagRepository.findAllByHouseIdIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDTO houseDTO = idToHouseMap.get(houseTag.getHouseId());
            houseDTO.getTags().add(houseTag.getName());
        });
    }

    /**
     * 整理housePicture部分的数据
     * @param houseForm
     * @param houseId
     * @return
     */
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

    /**
     * 整理houseDetail部分的数据
     * @param houseDetail
     * @param houseForm
     * @return
     */
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
