package com.example.service.pet.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.authc.InvalidTokenException;
import com.example.dao.CacheDao;
import com.example.mapper.PetMapper;
import com.example.pojo.Pet;
import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.pet.PetRequestData;
import com.example.service.AvatarService;
import com.example.service.ServiceExceptionHandler;
import com.example.service.TokenCheckService;
import com.example.service.pet.PetService;
import lombok.extern.slf4j.Slf4j;
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
        implements PetService, ServiceExceptionHandler<PetRequestData>, TokenCheckService, AvatarService {

    private static final String AVATAR_RESOURCE_PATH_PREFIX = "/pet_collar/image/avatar/pet/";

    private static final String IMAGE_SUFFIX = ".jpg";

    private static final String DEFAULT_IMAGE_NAME = "default.png";

    private static final String AVATAR_URL_PREFIX = "http://resource.petcollar.top:8082/image/avatar/pet/";
    private static final String DEFAULT_AVATAR_URL_PREFIX = "http://www.petcollar.top:8083/image/avatar/pet/";

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
            doGetUserPets(user.getUserId(), response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void doGetUserPets(Integer userId, ReactiveResponse<PetRequestData> response) {
        List<Pet> pets = getUserPets(userId);
        PetRequestData data = new PetRequestData();
        data.setPets(pets);
        response.setSuccess(data);
    }


    private List<Pet> getUserPets(int userId) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Pet> pets = petMapper.selectList(queryWrapper);
        return (null == pets) ? new ArrayList<>() : pets;
    }

    @Override
    public ReactiveResponse<PetRequestData> getAddPetResponse(String token, Pet petInfo) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            refreshTokenTime(token, user);
            doAddPet(petInfo, user.getUserId(), response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void doAddPet(Pet petInfo, int userId, ReactiveResponse<PetRequestData> response) {
        petInfo.setUserId(userId);
        petInfo.setPetPortraitPath(DEFAULT_AVATAR_URL_PREFIX + DEFAULT_IMAGE_NAME);
        petInfo.setPetIntroduction(Objects.requireNonNullElse(petInfo.getPetIntroduction(), "~这只宠物还没有介绍哦~"));
        save(petInfo);
        PetRequestData data = new PetRequestData();
        data.setPet(petInfo);
        response.setSuccess(data);
    }


    @Override
    public ReactiveResponse<PetRequestData> getModifyPetProfileResponse(String token,
                                                                        String petId,
                                                                        Pet newPetProfile) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {

            User user = getUserByToken(token);
            refreshTokenTime(token, user);
            Pet pet = getById(Integer.parseInt(petId));
            permissionCheck(pet, user.getUserId());
            doModifyProfile(pet, newPetProfile, response);
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

    private void doModifyProfile(Pet pet, Pet newPetProfile,
                                 ReactiveResponse<PetRequestData> response) {
        newPetProfile.setPetId(pet.getPetId());
        newPetProfile.setUserId(pet.getUserId());
        updateById(pet);
        response.setSuccess(null);
    }

    @Override
    public ReactiveResponse<PetRequestData> getRemovePetResponse(String token, String petId) {
        ReactiveResponse<PetRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            refreshTokenTime(token, user);
            Pet pet = getById(Integer.parseInt(petId));
            permissionCheck(pet, user.getUserId());
            doRemovePet(pet, response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void doRemovePet(Pet pet, ReactiveResponse<PetRequestData> response) {
        removeById(pet.getPetId());
        response.setSuccess(null);
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
            doUploadAvatar(pet, image, response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void doUploadAvatar(Pet pet, MultipartFile image, ReactiveResponse<PetRequestData> response) throws IOException {
        String originalPath = pet.getPetPortraitPath();
        deleteOriginalAvatarIfExists(originalPath);
        String avatarUrl = createAvatarFile(image);
        pet.setPetPortraitPath(avatarUrl);
        updateById(pet);
        Pet petData = new Pet();
        petData.setPetPortraitPath(avatarUrl);
        PetRequestData data = new PetRequestData();
        data.setPet(pet);
        response.setSuccess(data);
    }


    @Override
    public void handle(Exception e, ReactiveResponse<PetRequestData> response) {
        log.info(e.getMessage());

        if (e instanceof InvalidTokenException) {
            response.setError(Status.INVALID_TOKEN);
        } else if (e instanceof AuthenticationException || e instanceof NumberFormatException) {
            response.setError(Status.UNAUTHORIZED);
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
    public String getImageSuffix() {
        return IMAGE_SUFFIX;
    }

    @Override
    public String getDefaultImageName() {
        return DEFAULT_IMAGE_NAME;
    }
}
