package com.monaum.Rapid_Global.module.company;

import com.monaum.Rapid_Global.config.SecurityUtil;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Service
@RequiredArgsConstructor
public class CompanyService {

    @Autowired private CompanyRepo companyRepo;
    @Autowired private CompanyMapper companyMapper;
    @Autowired private SecurityUtil securityUtil;

    @Transactional
    public CompanyResDto create(CreateCompanyReqDto reqDto) {
            Company company = companyMapper.toEntity(reqDto);
            company = companyRepo.save(company);

            return companyMapper.toDto(company);
    }

    public CompanyResDto getById(Long id) throws CustomException {
        Company company = companyRepo.findById(id).orElseThrow(() -> new CustomException("Company not found", HttpStatus.NOT_FOUND));

        return companyMapper.toDto(company);
    }

    public Page<CompanyResDto> getAllByUser(Pageable pageable) {
        User currentUser = securityUtil.getCurrentUser().orElseThrow(() -> new CustomException("User not authenticated", HttpStatus.UNAUTHORIZED));

        Page<Company> transactions = companyRepo.findAllByCreatedBy(currentUser, pageable);
        return transactions.map(companyMapper::toDto);
    }

    @Transactional
    public CompanyResDto update(UpdateCompanyReqDto dto) {
        Company company = companyRepo.getReferenceById(dto.getId());

        companyMapper.toEntity(dto, company);

        Company updated = companyRepo.save(company);

        return companyMapper.toDto(updated);
    }

    
    public void delete(Long id) throws CustomException {
        Company company = companyRepo.findById(id).orElseThrow(() -> new CustomException("Company not found", HttpStatus.NOT_FOUND));
        companyRepo.delete(company);
    }
}
