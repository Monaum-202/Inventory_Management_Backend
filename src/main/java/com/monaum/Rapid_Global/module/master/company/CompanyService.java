package com.monaum.Rapid_Global.module.master.company;

import com.monaum.Rapid_Global.config.SecurityUtil;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.personnel.user.User;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Service
@RequiredArgsConstructor
public class CompanyService {

    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private SecurityUtil securityUtil;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(CreateCompanyReqDto reqDto) {

        Company company = companyMapper.toEntity(reqDto);
        company = companyRepo.save(company);

        return ResponseUtils.SuccessResponseWithData(companyMapper.toDto(company));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {

        Company company = companyRepo.findById(id).orElseThrow(() -> new CustomException("Company not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(companyMapper.toDto(company));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAllByUser(Pageable pageable) {
        User currentUser = securityUtil.getCurrentUser()
                .orElseThrow(() -> new CustomException("User not authenticated", HttpStatus.UNAUTHORIZED));

        Page<CompanyResDto> companies = companyRepo.findAllByCreatedBy(currentUser, pageable).map(companyMapper::toDto);
        CustomPageResponseDTO<CompanyResDto> paginatedResponse = PaginationUtil.buildPageResponse(companies, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }


    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(UpdateCompanyReqDto dto) {

        Company company = companyRepo.getReferenceById(dto.getId());

        companyMapper.toEntity(dto, company);
        Company updated = companyRepo.save(company);

        return ResponseUtils.SuccessResponseWithData(companyMapper.toDto(updated));
    }


    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {

        Company company = companyRepo.findById(id).orElseThrow(() -> new CustomException("Company not found", HttpStatus.NOT_FOUND));
        companyRepo.delete(company);

        return ResponseUtils.SuccessResponseWithData(companyMapper.toDto(company));
    }
}
