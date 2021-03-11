package com.example.controller.pet;

import com.example.request.pet.PetRequestDTO;
import com.example.request.pet.PetRequestDTO.*;
import com.example.response.ReactiveResponse;
import com.example.response.data.pet.PetRequestData;
import com.example.service.pet.PetService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 实践 REST URL
 * @author Lexin Huang
 */
@Api(tags = "宠物模块")
@RestController
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/pets/view")
    @ApiOperation("获取用户所有宠物信息接口")
    @ApiImplicitParam(name = "token", value = "用户令牌", required = true,
                      dataTypeClass = String.class, paramType = "header")
    public ReactiveResponse<PetRequestData> getUserPets(@RequestHeader(value = "token") String token) {
        return petService.getUserPetsResponse(token);
    }

    @GetMapping("/pets")
    @ApiOperation(("查询同品种的宠物"))
    @ApiImplicitParam(name = "breed", value = "宠物品种", required = true,
                    dataTypeClass = String.class, paramType = "query")
    public ReactiveResponse<PetRequestData> getPetsOfTheSameBreed(@RequestParam("breed") String breed) {
        return petService.getSameBreedPets(breed);
    }


    @PostMapping("/pets")
    @ApiOperation("添加宠物接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header")
    })
    public ReactiveResponse<PetRequestData> addPet(@RequestHeader("token") String token,
                                                   @Validated(value = AddPetGroup.class) @RequestBody PetRequestDTO pet) {
        return petService.getAddPetResponse(token, pet);
    }


    @PatchMapping("/pets/{petId}/profile")
    @ApiOperation("修改宠物信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header"),
            @ApiImplicitParam(name = "petId", value = "宠物id", required = true,
                    dataTypeClass = String.class, paramType = "path"),
    })
    public ReactiveResponse<PetRequestData> modifyPetProfile(@RequestHeader("token") String token,
                                                             @PathVariable String petId,
                                                             @Validated(ModifyPetProfileGroup.class) @RequestBody PetRequestDTO newPetProfile) {
        return petService.getModifyPetProfileResponse(token, petId, newPetProfile);
    }

    @PatchMapping("/pets/{petId}/avatar")
    @ApiOperation("修改宠物头像接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header"),
            @ApiImplicitParam(name = "petId", value = "宠物id", required = true,
                    dataTypeClass = String.class, paramType = "path"),
            @ApiImplicitParam(name = "image", value = "图片文件", required = true,
                    dataTypeClass = MultipartFile.class, paramType = "form")
    })
    public ReactiveResponse<PetRequestData> uploadAvatar(@RequestHeader("token") String token,
                                                         @PathVariable("petId") String petId,
                                                         @RequestParam("image") MultipartFile image) {
        return petService.getUploadAvatarResponse(token, petId, image);
    }


    @DeleteMapping("/pets/{petId}")
    @ApiOperation("删除指定宠物接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header"),
            @ApiImplicitParam(name = "petId", value = "宠物id", required = true,
                    dataTypeClass = String.class, paramType = "path")
    })
    public ReactiveResponse<PetRequestData> removePet(@RequestHeader("token") String token,
                                                      @PathVariable("petId") String petId) {
        return petService.getRemovePetResponse(token, petId);
    }

}
