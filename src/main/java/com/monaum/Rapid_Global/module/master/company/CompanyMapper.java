package com.monaum.Rapid_Global.module.master.company;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toEntity(CreateCompanyReqDto dto);

    void toEntity(UpdateCompanyReqDto dto, @MappingTarget Company company);

    CompanyResDto toDto(Company company);
}
