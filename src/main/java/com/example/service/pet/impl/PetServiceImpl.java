package com.example.service.pet.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.exception.InvalidTokenException;
import com.example.dao.CacheDao;
import com.example.exception.UnavailablePetNameException;
import com.example.mapper.PetMapper;
import com.example.pojo.Pet;
import com.example.pojo.User;
import com.example.request.pet.PetRequestDTO;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.pet.PetRequestData;
import com.example.service.AvatarService;
import com.example.service.ServiceExceptionHandler;
import com.example.service.UniqueNameQueryService;
import com.example.service.user.TokenCheckService;
import com.example.service.pet.PetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet>
                            implements PetService, ServiceExceptionHandler<PetRequestData>,
        TokenCheckService, AvatarService, UniqueNameQueryService<Pet> {

    private static final String AVATAR_RESOURCE_PATH_PREFIX = "/pet_collar/image/avatar/pet/";

    private static final String IMAGE_SUFFIX = ".jpg";

    private static final String DEFAULT_IMAGE_NAME = "default.png";

    private static final String AVATAR_URL_PREFIX = "http://resource.petcollar.top:8082/image/avatar/pet/";

    private static final String DEFAULT_AVATAR_URL_PREFIX = "http://www.petcollar.top:8083/image/avatar/pet/";

    private static final String DEFAULT_PET_INTRODUCTION = "~这只宠物还没有介绍哦~";

    private final PetMapper petMapper;

    private final CacheDao cacheDao;

    public PetServiceImpl(PetMapper petMapper, CacheDao cacheDao) {
        this.petMapper = petMapper;
        this.cacheDao = cacheDao;
    }

    @Override
    public ReactiveResponse<PetRequestData> getUserPetsResponse(String token) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            refreshTokenTime(token, user);
            List<Pet> userPets = getUserPets(user.getUserId());
            configureSuccessData(response, null, userPets, null, null);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void configureSuccessData(ReactiveResponse<PetRequestData> response,
                                      PetRequestData.PetOwner petOwner, List<Pet> pets, Pet pet,
                                      String avatarUrl) {
        PetRequestData data = new PetRequestData();
        data.setPetOwner(petOwner);
        data.setPets(pets);
        data.setPet(pet);
        data.setPetPortraitPath(avatarUrl);
        response.setSuccess(data);
    }

    private List<Pet> getUserPets(int userId) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Pet> pets = petMapper.selectList(queryWrapper);
        return (null == pets) ? new ArrayList<>() : pets;
    }

    @Override
    public ReactiveResponse<PetRequestData> getAddPetResponse(String token, PetRequestDTO requestDTO) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();

        try {
            User user = getUserByToken(token);
            refreshTokenTime(token, user);
            Pet pet = createPetToAdd(requestDTO, user.getUserId());
            checkIfNameIsAvailable(pet.getPetName());
            save(pet);
            configureSuccessData(response, null, null, pet, null);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    @Override
    public void checkIfNameIsAvailable(String petName) {
        if (!Objects.isNull(getByUniqueName(petName))) {
            throw new UnavailablePetNameException("宠物名称为" + petName + " 的宠物已存在!");
        }
    }


    private Pet createPetToAdd(PetRequestDTO requestDTO, Integer userId) {
        Pet pet = new Pet();
        pet.setPetId(null);
        pet.setUserId(userId);
        pet.setPetPortraitPath(DEFAULT_AVATAR_URL_PREFIX + DEFAULT_IMAGE_NAME);
        pet.setOvert(requestDTO.getOvert());
        if (Strings.isEmpty(requestDTO.getPetIntroduction())) {
            pet.setPetIntroduction(DEFAULT_PET_INTRODUCTION);
        } else {
            pet.setPetIntroduction(requestDTO.getPetIntroduction());
        }
        return pet;
    }


    @Override
    public ReactiveResponse<PetRequestData> getModifyPetProfileResponse(String token,
                                                                        String petId,
                                                                        PetRequestDTO requestDTO) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            refreshTokenTime(token, user);
            Pet petToModify = getById(Integer.parseInt(petId));
            permissionCheck(petToModify, user.getUserId());
            modifyProfile(petToModify, requestDTO);
            configureSuccessData(response, null, null, petToModify, null);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void permissionCheck(Pet pet, Integer userId) throws AuthenticationException {
        if (Objects.isNull(pet) || !(pet.getUserId().equals(userId))) {
            throw new AuthenticationException("用户 id 为" + userId + "没有进行该操作的权限!");
        }
    }

    private void modifyProfile(Pet pet, PetRequestDTO requestDTO) {
        pet.setPetName(requestDTO.getPetName());
        pet.setPetSpecies(requestDTO.getPetSpecies());
        pet.setOvert(requestDTO.getOvert());
        pet.setPetIntroduction(requestDTO.getPetIntroduction());
        updateById(pet);
    }

    @Override
    public ReactiveResponse<PetRequestData> getRemovePetResponse(String token, String petId) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            refreshTokenTime(token, user);
            Pet pet = getById(Integer.parseInt(petId));
            permissionCheck(pet, user.getUserId());
            removeById(pet.getPetId());
            configureSuccessData(response, null, null, null, null);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    @Override
    public ReactiveResponse<PetRequestData> getSameBreedPets(String breed) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {
            List<Pet> pets = getByBreed(breed);
            configureSuccessData(response, null, pets, null, null);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private List<Pet> getByBreed(String breed) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_species", breed);
        List<Pet> pets = petMapper.selectList(queryWrapper);
        return Objects.isNull(pets) ? new ArrayList<>() : pets;
    }


    @Override
    public ReactiveResponse<PetRequestData> getUploadAvatarResponse(String token, String petId,
                                                                    MultipartFile image) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            refreshTokenTime(token, user);

            Pet pet = getById(Integer.parseInt(petId));
            permissionCheck(pet, user.getUserId());
            deleteOriginalAvatarIfExists(pet.getPetPortraitPath());
            String avatarUrl = uploadAvatar(image);
            pet.setPetPortraitPath(avatarUrl);
            updateById(pet);
            configureSuccessData(response, null, null, null, avatarUrl);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }


    @Override
    public void handle(Exception e, ReactiveResponse<PetRequestData> response) {
        log.info(e.getMessage());

        if (e instanceof InvalidTokenException) {
            response.setError(Status.INVALID_TOKEN);
        } else if (e instanceof AuthenticationException || e instanceof NumberFormatException) {
            response.setError(Status.UNAUTHORIZED);
        } else if (e instanceof UnavailablePetNameException || e instanceof DuplicateKeyException)  {
            response.setError(Status.PET_NAME_NOT_AVAILABLE);
        } else if (e instanceof IOException) {
            response.setError(Status.SERVER_ERROR, "修改头像服务暂时不可用, 请稍后再试");
        } else {
            log.error(e.getMessage());
            response.setError(Status.SERVER_ERROR);
        }
    }


    @Override
    public void putUserCache(String k, User v, Long timeOut, TimeUnit timeUnit) {
        cacheDao.set(k, v, timeOut, timeUnit);
    }

    @Override
    public void putStringCache(String k, String v, Long timeOut, TimeUnit timeUnit) {
        if (Objects.isNull(k)) {
            throw new NullPointerException("k 不能为 null !");
        }
        cacheDao.set(k, v, timeOut, timeUnit);
    }

    @Override
    public User getUserCache(String k) {
        return cacheDao.getUserCache(k);
    }

    @Override
    public String getAvatarUrlPrefix() {
        return AVATAR_URL_PREFIX;
    }

    @Override
    public String getAvatarResourcePathPrefix() {
        return AVATAR_RESOURCE_PATH_PREFIX;
    }

    @Override
    public String getAvatarSuffix() {
        return IMAGE_SUFFIX;
    }

    @Override
    public String getDefaultAvatarName() {
        return DEFAULT_IMAGE_NAME;
    }

    @Override
    public Pet getByUniqueName(String name) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_name", name);
        return getOne(queryWrapper);
    }
}
